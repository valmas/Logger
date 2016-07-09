package com.ntua.ote.logger.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    private boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static Date stringToDate(String dateString){
        DateFormat df = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
        Date d;
        try {
            d = df.parse(dateString);
            return d;
        } catch (ParseException e) {
            Log.e(TAG, "parse date exception");
            e.printStackTrace();
        }
        return null;
    }

    public static String dateToString(Date date, String format){
        DateFormat df = new SimpleDateFormat(format);
        String strDate = df.format(date);
        return strDate;
    }

    public static String getOutputDuration(int duration){
        int hours = duration / 3600;
        int minutes = (duration % 3600) / 60;
        int seconds = duration % 60;
        String durStr = (hours != 0 ? hours + "hr " : "") +
                (minutes != 0 ? minutes + "min " : "" ) +
                (seconds != 0 ? seconds + "sec" : "");
        return durStr;
    }
}
