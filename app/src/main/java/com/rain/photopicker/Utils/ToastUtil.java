package com.rain.photopicker.Utils;

import android.widget.Toast;

import com.rain.photopicker.MyApplication;

/**
 * Describe:
 * Created by Rain on 2018/4/26.
 */

public class ToastUtil {
    private static Toast mToast;

    public static void showToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(MyApplication.getContext(), text, Toast.LENGTH_SHORT);
        }
        mToast.setText(text);
        mToast.show();
    }
}
