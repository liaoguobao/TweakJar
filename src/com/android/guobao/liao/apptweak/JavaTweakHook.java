package com.android.guobao.liao.apptweak;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import android.util.Log;

import com.android.guobao.liao.apptweak.util.*;

public abstract class JavaTweakHook {
    static public final int HOOK_FLAG_NO_CALL_LOG = 0x00000001; //不打印调用参数日志
    static public final int HOOK_FLAG_STACK_TRACE = 0x00000002; //  打印调用堆栈日志

    private Method backup_;
    private Object result_;
    private Throwable except_;
    private boolean return_;
    private int flags_;
    private String name_;

    public JavaTweakHook() {
        this(0, "");
    }

    public JavaTweakHook(int flags) {
        this(flags, "");
    }

    public JavaTweakHook(String name) {
        this(0, name);
    }

    public JavaTweakHook(int flags, String name) {
        backup_ = null;
        result_ = null;
        except_ = null;
        return_ = false;
        flags_ = flags;
        name_ = name == null ? "" : name;
    }

    protected void beforeHookedMethod(Object thiz, Object[] args) {
    }

    protected void afterHookedMethod(Object thiz, Object[] args) {
    }

    synchronized //lock
    protected final Object handleHookedMethod(Object thiz, Object[] args) throws Throwable {
        result_ = null;
        except_ = null;
        return_ = false;
        if ((flags_ & HOOK_FLAG_STACK_TRACE) != 0) {
            JavaTweakBridge.writeToLogcat(Log.INFO, Log.getStackTraceString(new Throwable()));
        }
        if (!return_) {
            try {
                beforeHookedMethod(thiz, args);
            } catch (Throwable e) {
                JavaTweakBridge.writeToLogcat(Log.ERROR, "beforeHookedMethod: %s: %s", backup_, e);
            }
        }
        if (!return_) {
            try {
                result_ = backup_.invoke(thiz, args);
            } catch (InvocationTargetException e) {
                except_ = e.getCause();
                JavaTweakBridge.writeToLogcat(Log.WARN, "invoke: %s: %s", backup_, except_);
            }
        }
        if (!return_) {
            try {
                afterHookedMethod(thiz, args);
            } catch (Throwable e) {
                JavaTweakBridge.writeToLogcat(Log.ERROR, "afterHookedMethod: %s: %s", backup_, e);
            }
        }
        if ((flags_ & HOOK_FLAG_NO_CALL_LOG) == 0) {
            JavaTweakBridge.writeToLogcat(Log.INFO, paramsToString(name_, backup_, result_, thiz, args));
        }
        if (except_ != null) {
            throw except_;
        }
        return result_;
    }

    private String paramsToString(String name, Method m, Object hr, Object thiz, Object[] args) {
        Class<?> type = m.getReturnType();
        Class<?>[] types = m.getParameterTypes();

        String log = String.format("%s::%s%s->{\r\n", m.getDeclaringClass().getName(), m.getName(), !name.equals("") && !m.getName().equals(name) ? "@" + name : "");
        log += String.format("\t_this_ = %s->%s\r\n", m.getDeclaringClass().getName(), thiz);
        for (int i = 0; i < args.length; i++) {
            log += String.format("\tparam%d = %s->%s\r\n", i + 1, types[i].getName(), ReflectUtil.peekValue(args[i]));
        }
        log += String.format("\treturn = %s->%s\r\n}\r\n", type.getName(), ReflectUtil.peekValue(hr));
        return log;
    }

    public Method getBackup() {
        return backup_;
    }

    public void setBackup(Method backup) {
        backup_ = backup;
    }

    public Object getResult() {
        return result_;
    }

    public void setResult(Object result) {
        result_ = result;
        except_ = null;
        return_ = true;
    }

    public Throwable getThrowable() {
        return except_;
    }

    public void setThrowable(Throwable except) {
        result_ = null;
        except_ = except;
        return_ = true;
    }

    public static JavaTweakHook onlyLogHook(Object... opts) {
        int hook_flags_for_log = 0;
        String method_name_for_log = "";
        for (int i = 0; i < opts.length; i++) {
            Object o = opts[i];
            if (String.class.isInstance(o)) {
                method_name_for_log = (String) o;
            } else if (Integer.class.isInstance(o)) {
                hook_flags_for_log = (Integer) o;
            } else if (Boolean.class.isInstance(o)) {
                hook_flags_for_log |= (Boolean) o ? JavaTweakHook.HOOK_FLAG_STACK_TRACE : 0;
            }
        }
        return new JavaTweakHook(hook_flags_for_log, method_name_for_log) {
        };
    }
}
