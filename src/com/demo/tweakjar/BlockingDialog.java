package com.demo.tweakjar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class BlockingDialog {
    private int mWhich;
    static private Handler mHandler;

    static public int showBlockingDialog(Activity context, String title, String msg) {
        mHandler = new Handler(new Handler.Callback() {
            public boolean handleMessage(Message msg) {
                throw new RuntimeException();
            }
        });
        return new BlockingDialog(context, title, msg).mWhich;
    }

    private BlockingDialog(Context context, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("确定", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mWhich = which;
                mHandler.sendMessage(mHandler.obtainMessage());
            }
        });
        builder.setNegativeButton("取消", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mWhich = which;
                mHandler.sendMessage(mHandler.obtainMessage());
            }
        });
        builder.setTitle(title).setMessage(msg).create().show();
        try {
            Looper.loop();
        } catch (Exception e) {
        }
    }
}