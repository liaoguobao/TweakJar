package com.android.guobao.liao.apptweak;

import android.util.Log;

public abstract class JavaTweakReplace extends JavaTweakHook {
    public JavaTweakReplace() {
        super();
    }

    public JavaTweakReplace(int flags) {
        super(flags);
    }

    public JavaTweakReplace(String name) {
        super(name);
    }

    public JavaTweakReplace(int flags, String name) {
        super(flags, name);
    }

    @Override
    protected final void beforeHookedMethod(Object thiz, Object[] args) {
        try {
            setResult(replaceHookedMethod(thiz, args));
        } catch (Throwable e) {
            JavaTweakBridge.writeToLogcat(Log.ERROR, "replaceHookedMethod: %s: %s", getBackup(), e);
        }
    }

    @Override
    protected final void afterHookedMethod(Object thiz, Object[] args) {
    }

    protected abstract Object replaceHookedMethod(Object thiz, Object[] args);

    public static JavaTweakReplace constReturnReplace(final Object result) {
        return new JavaTweakReplace() {
            @Override
            protected Object replaceHookedMethod(Object thiz, Object[] args) {
                return result;
            }
        };
    }

    public static JavaTweakReplace nullReturnReplace() {
        return constReturnReplace(null);
    }
}
