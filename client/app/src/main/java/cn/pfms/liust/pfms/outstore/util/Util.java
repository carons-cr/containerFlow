package cn.pfms.liust.pfms.outstore.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.EditText;

/**
 * Created by liust on 17-10-6.
 */

public class Util {
    long lastTime;
    int DEFAULT_TIME =3500;
    //editview获取焦点
    public void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    //dialogdismiss显示
    public void dialogDismiss(final ProgressDialog dialog) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                int progress = 0;
                while (System.currentTimeMillis() - startTime < 1000) {
                    try {
                        progress += 10;
                        dialog.setProgress(progress);
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        dialog.dismiss();
                    }
                }
                dialog.dismiss();
            }
        }).start();
    }

    public boolean isSingle() {
        boolean isSingle;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime <= DEFAULT_TIME) {
            isSingle = true;
        } else {
            isSingle = false;
        }
        lastTime = currentTime;
        return isSingle;
    }

}
