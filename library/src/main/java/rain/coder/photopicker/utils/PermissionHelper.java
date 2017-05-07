package rain.coder.photopicker.utils;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.zhy.m.permission.MPermissions;

import rain.coder.library.R;

/**
 * Descriptions :Android6.0权限相关帮助类
 * Created by Rain on 16-12-8.
 */
public class PermissionHelper {

    public static final String TAG = "PermissionHelper";

    public static final int PERMISSION_REFUSE = 500;//权限不足，权限被拒绝的时候
    public static final String PACKAGE_URL_SCHEME = "package:";//权限方案

    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    public static final String PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String PERMISSION_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;


    public static boolean checkPermission(Activity activity, String permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, permissions) == PackageManager.PERMISSION_GRANTED)
                return true;
            else return false;
        }
        return false;
    }

    /**
     * 请求权限
     *
     * @param permissions
     */
    public static void requestPermission(Activity activity, int requestCode, String permissions) {
        //检查权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(activity, permissions) != PackageManager.PERMISSION_GRANTED)
                MPermissions.requestPermissions(activity, requestCode, permissions);
        }
    }

    /**
     * 请求权限
     *
     * @param permissions
     */
    public static void requestPermission(Fragment fragment, int requestCode, String permissions) {
        //检查权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(fragment.getContext(), permissions) != PackageManager.PERMISSION_GRANTED)
                MPermissions.requestPermissions(fragment, requestCode, permissions);
        }
    }

    public static void showSystemSettingDialog(final Activity activity, String message) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("温馨提示");
        builder.setMessage(message);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                activity.finish();
            }
        });
        builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startSystemSettingActivity(activity);
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    /**
     * 打开应用程序设置
     *
     * @param activity
     */
    public static void startSystemSettingActivity(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + activity.getPackageName()));
        activity.startActivity(intent);
    }
}
