package com.ntua.ote.logger.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.ntua.ote.logger.PermissionsMapping;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CommonUtils {

    private static final String TAG = CommonUtils.class.getName();

    public static boolean isServiceRunning(Class serviceClass, Activity act) {
        ActivityManager manager = (ActivityManager) act.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean havePermissions(PermissionsMapping pm, Activity act){
        for(String perm : pm.permission) {
            if (ContextCompat.checkSelfPermission(act, perm)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean deniedPermissionExists(int[] grantResults){
        for(int res : grantResults) {
            if(res == PackageManager.PERMISSION_DENIED) {
                return true;
            }
        }
        return false;
    }

    public static void requestPermission(PermissionsMapping pm, Activity act){
        List<String> deniedPermissions = new ArrayList<>();
        for(String perm : pm.permission) {
            if (ContextCompat.checkSelfPermission(act, perm)
                    != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(perm);
            }
        }
        if(!deniedPermissions.isEmpty()) {
            String[] stringArray = new String[deniedPermissions.size()];
            ActivityCompat.requestPermissions(act, deniedPermissions.toArray(stringArray), pm.requestCode);
        }
    }

    public static String getDetailedOsVersion(){
        StringBuilder builder = new StringBuilder();
        builder.append(Build.VERSION.RELEASE);

        int sdk = Build.VERSION.SDK_INT;

        AndroidVersions av = AndroidVersions.valueOf("S" + sdk);
        String versionName = av == null ? "" : av.versionName;

        builder.append(" ").append(versionName).append(" ");
        builder.append("(SDK ").append(sdk).append(")");

        return builder.toString();
    }
}
