package com.demo.tweakjar;

import com.android.guobao.liao.apptweak.JavaTweakBridge;
import com.android.guobao.liao.apptweak.JavaTweakHook;
import com.android.guobao.liao.apptweak.JavaTweakReplace;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_one).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //hook类的构造方法
                //()                          不带参数的构造函数，如果构造函数有参数，hook时需要指定参数列表类型
                //(int)                         带一个int参数的构造函数
                //(int,java.lang.String,byte[]) 带三个参数的构造函数，参数与参数之间不能有空格，如果是普通类需要指定类的全路径(包名.类名)
                JavaTweakBridge.hookJavaMethod(Activity.class, "()");
            }
        });
        findViewById(R.id.btn_two).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //hook类的非构造方法
                //非构造方法与构造方法HOOK写法的不同之处在于参数列表前多了方法名
                //如果某个方法没有重载方法的话，带参数列表与不带参数列表的hook写法等价
                //即JavaTweakBridge.hookJavaMethod(Activity.class, "isChild")和JavaTweakBridge.hookJavaMethod(Activity.class, "isChild()")写法等价
                JavaTweakBridge.hookJavaMethod(Activity.class, "startActivityForResult(android.content.Intent,int,android.os.Bundle)");
            }
        });
        findViewById(R.id.btn_three).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //hook类的所有重载方法
                //hook的写法上只需要填上方法名即可，无需填写参数列表
                JavaTweakBridge.hookAllJavaMethods(Activity.class, "startActivityForResult");
            }
        });
        findViewById(R.id.btn_four).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //hook类的所有构造方法
                JavaTweakBridge.hookAllJavaConstructors(Activity.class);
            }
        });
        findViewById(R.id.btn_five).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //hook类的所有非构造方法
                JavaTweakBridge.hookAllJavaMethods(Activity.class, "");
            }
        });
        findViewById(R.id.btn_six).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //hook类的所有方法
                JavaTweakBridge.hookJavaClass(Activity.class);
            }
        });
        findViewById(R.id.btn_seven).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //微调方法逻辑
                JavaTweakBridge.hookJavaMethod(Activity.class, "startActivityForResult(android.content.Intent,int,android.os.Bundle)", new JavaTweakHook() {
                    protected void beforeHookedMethod(Object thiz, Object[] args) {
                        BlockingDialog.showBlockingDialog((Activity) thiz, "提示", "创建Activity前");
                    }

                    protected void afterHookedMethod(Object thiz, Object[] args) {
                        BlockingDialog.showBlockingDialog((Activity) thiz, "提示", "创建Activity后");
                    }
                });
            }
        });
        findViewById(R.id.btn_eight).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //无条件替换方法逻辑
                JavaTweakBridge.hookJavaMethod(Activity.class, "startActivityForResult(android.content.Intent,int,android.os.Bundle)", new JavaTweakReplace() {
                    protected Object replaceHookedMethod(Object thiz, Object[] args) {
                        BlockingDialog.showBlockingDialog((Activity) thiz, "提示", "不再创建Activity");
                        return null;
                    }
                });
            }
        });
        findViewById(R.id.btn_nine).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //有条件替换逻辑
                JavaTweakBridge.hookJavaMethod(Activity.class, "startActivityForResult(android.content.Intent,int,android.os.Bundle)", new JavaTweakHook() {
                    protected void beforeHookedMethod(Object thiz, Object[] args) {
                        int which = BlockingDialog.showBlockingDialog((Activity) thiz, "提示", "点击确定会继续创建Activity\n点击取消会不再创建Activity");
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            setResult(null);
                        }
                    }
                });
            }
        });
        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //自动打印调用堆栈日志(默认不打印，下面的两种写法任选其一)
                //JavaTweakBridge.hookJavaMethod(Activity.class, "()", true);
                //JavaTweakBridge.hookJavaMethod(Activity.class, "()", JavaTweakHook.HOOK_FLAG_STACK_TRACE);

                //禁止打印调用参数日志(默认会打印)
                //JavaTweakBridge.hookJavaMethod(Activity.class, "()", JavaTweakHook.HOOK_FLAG_NO_CALL_LOG);

                //为被混淆的方法设置别名(优化日志输出)
                //JavaTweakBridge.hookJavaMethod(Activity.class, "findViewById", "Activity_findViewById");

                //创建新的Activity以测试hook效果
                startActivity(new Intent(MainActivity.this, MainActivity.class));
            }
        });
    }
}
