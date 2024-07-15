package com.android.guobao.liao.apptweak;

@SuppressWarnings("unused")
public class JavaTweakCallback {
    static private Object handleHookedMethod(Object thiz, Object[] args, Object data) throws Throwable {
        return ((JavaTweakHook) data).handleHookedMethod(thiz, args);
    }
}
