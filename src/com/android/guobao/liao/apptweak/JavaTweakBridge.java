package com.android.guobao.liao.apptweak;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import android.util.Log;
import com.android.guobao.liao.apptweak.util.ReflectUtil;

public class JavaTweakBridge {
    static {
        System.loadLibrary("tweakjar");
    }
    static private final ConcurrentHashMap<String, JavaTweakHook> backupMethods = new ConcurrentHashMap<String, JavaTweakHook>();

    static public void writeToLogcat(int prio, String msg) {
        Log.println(prio, "TweakJar", msg + "\r\n");
    }

    static public void writeToLogcat(int prio, String format, Object... args) {
        writeToLogcat(prio, String.format(format, args));
    }

    static private native Method nativeHookMethod(Class<?> hook_class, String hook_method, Object hook_data, boolean can_hook_chain);

    synchronized //lock
    static public boolean hookJavaMethod(Class<?> hook_class, String hook_method, JavaTweakHook hook_data) {
        try {
            boolean can_hook_chain = false;
            if (hook_class == null || hook_method == null || hook_method.equals("")) {
                return false;
            }
            Member hook_member = ReflectUtil.findClassMember(hook_class, hook_method, false);
            if (hook_member == null) {
                writeToLogcat(Log.ERROR, "hookJavaMethod: method<%s> no exist.", hook_method);
                return false;
            }
            String method_decl = ReflectUtil.getMemberDeclare(hook_member, false);
            String method_key = String.format("%s@%x", method_decl, hook_class.getClassLoader().hashCode());
            if (!can_hook_chain && backupMethods.containsKey(method_key)) {
                //writeToLogcat(Log.WARN, "hookJavaMethod: method<%s> hook repeat.", hook_method);
                return false;
            }
            if (hook_data == null) {
                hook_data = JavaTweakHook.onlyLogHook();
            }
            Method m = nativeHookMethod(hook_class, hook_method, hook_data, can_hook_chain);
            if (m == null) {
                writeToLogcat(Log.ERROR, "hookJavaMethod: method<%s> hook error.", hook_method);
                return false;
            }
            hook_data.setBackup(m);
            backupMethods.put(method_key, hook_data);
            writeToLogcat(Log.INFO, "hookJavaMethod: method<%s> hook ok.", hook_method);
            return true;
        } catch (Throwable e) {
            writeToLogcat(Log.ERROR, "hookJavaMethod: method<%s> hook exception: %s.", hook_method, e);
            return false;
        }
    }

    static public boolean hookJavaMethod(Class<?> hook_class, String hook_method) {
        return hookJavaMethod(hook_class, hook_method, (JavaTweakHook) null);
    }

    static public boolean hookAllJavaMethods(Class<?> hook_class, String method_name, JavaTweakHook hook_data) {
        if (hook_class == null) {
            return false;
        }
        Method[] ms = hook_class.getDeclaredMethods();
        for (int i = 0; i < ms.length; i++) {
            if (method_name.equals("") || ms[i].getName().equals(method_name)) {
                hookJavaMember(ms[i], hook_data);
            }
        }
        return true;
    }

    static public boolean hookAllJavaMethods(Class<?> hook_class, String method_name) {
        return hookAllJavaMethods(hook_class, method_name, null);
    }

    static public boolean hookJavaClass(Class<?> hook_class, JavaTweakHook hook_data) {
        hookAllJavaMethods(hook_class, "", hook_data);
        hookAllJavaConstructors(hook_class, hook_data);
        return true;
    }

    static public boolean hookJavaClass(Class<?> hook_class) {
        return hookJavaClass(hook_class, null);
    }

    static public boolean hookAllJavaConstructors(Class<?> hook_class, JavaTweakHook hook_data) {
        if (hook_class == null) {
            return false;
        }
        Constructor<?>[] cs = hook_class.getDeclaredConstructors();
        for (int i = 0; i < cs.length; i++) {
            if (true) {
                hookJavaMember(cs[i], hook_data);
            }
        }
        return true;
    }

    static public boolean hookAllJavaConstructors(Class<?> hook_class) {
        return hookAllJavaConstructors(hook_class, null);
    }

    static private boolean hookJavaMember(Member hook_member, JavaTweakHook hook_data) {
        Class<?> clazz = hook_member.getDeclaringClass();
        String decl = ReflectUtil.getMemberDeclare(hook_member, true);
        if (hook_data == null) {
            hookJavaMethod(clazz, decl);
            return true;
        }
        Constructor<?> hook_constr = hook_data.getClass().getDeclaredConstructors()[0];
        String cons = ReflectUtil.getMemberDeclare(hook_constr, true);

        Class<?>[] ts = hook_constr.getParameterTypes();
        String zero_ = ts.length > 0 ? ts[0].getName() : "";
        String this_ = zero_.equals("int") || zero_.equals("java.lang.String") ? "" : zero_;

        Object thiz_ = ReflectUtil.getObjectField(hook_data, this_);
        Object name_ = ReflectUtil.getObjectField(hook_data, "name_");
        Object flags_ = ReflectUtil.getObjectField(hook_data, "flags_");

        ArrayList<Object> args = new ArrayList<Object>();
        for (int i = 0; i < ts.length; i++) {
            String t = ts[i].getName();
            if (i == 0 && thiz_ != null) {
                args.add(thiz_);
            } else if (t.equals("java.lang.String")) {
                args.add(name_);
            } else if (t.equals("int")) {
                args.add(flags_);
            }
        }
        Object data = ReflectUtil.newClassInstance(hook_data.getClass(), cons, args.toArray());
        hookJavaMethod(clazz, decl, (JavaTweakHook) data);
        return true;
    }

    static public boolean hookJavaMethod(ClassLoader hook_loader, String hook_class, String hook_method, JavaTweakHook hook_data) {
        return hookJavaMethod(ReflectUtil.classForName(hook_loader, hook_class), hook_method, hook_data);
    }

    static public boolean hookJavaMethod(ClassLoader hook_loader, String hook_class, String hook_method) {
        return hookJavaMethod(hook_loader, hook_class, hook_method, (JavaTweakHook) null);
    }

    static public boolean hookJavaMethod(ClassLoader hook_loader, String hook_class, String hook_method, Object... opts) {
        return hookJavaMethod(ReflectUtil.classForName(hook_loader, hook_class), hook_method, opts);
    }

    static public boolean hookJavaMethod(Class<?> hook_class, String hook_method, Object... opts) {
        return hookJavaMethod(hook_class, hook_method, JavaTweakHook.onlyLogHook(opts));
    }
}