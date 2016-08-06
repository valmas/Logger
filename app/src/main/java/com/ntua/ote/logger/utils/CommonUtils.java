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
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.ntua.ote.logger.models.StrengthDetails;

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

    public static int getCelliId(Context context){
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        GsmCellLocation cellLocation = (GsmCellLocation)telephonyManager.getCellLocation();
        if(cellLocation != null) {
            int cellId = cellLocation.getCid();
            int networkType = telephonyManager.getNetworkType();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    break;
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    break;
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    break;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    cellId = transform(cellId);
                    break;
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    break;
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    cellId = transform(cellId);
                    break;
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    cellId = transform(cellId);
                    break;
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    cellId = transform(cellId);
                    break;
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    cellId = transform(cellId);
                    break;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    cellId = transformLTE(cellId);
                    break;
            }
            return cellId;
        }
        return -1;
    }

    private static int transform( int cellid){
        String cidhexS = Integer.toHexString( cellid );
        if (cidhexS.length() == 7) {
            int length_cidhexS = cidhexS.length();
            String cidhexSlast4S = cidhexS.substring( length_cidhexS - 4 );
            int cid2 = Integer.parseInt( cidhexSlast4S, 16 );
            return cid2;
        } else {
            int cid3 = -1;
            Log.e("1234", "LM | (<7 digits) cid = "+ cid3);
            return cid3;
        }

    }

    private static int transformLTE( int cellid){
        String cidhexS = Integer.toHexString( cellid );
        int length_cidhexS = cidhexS.length();
        String cidhexSfirst3S = cidhexS.substring(0, length_cidhexS-2);
        String cidhexSlast2S = cidhexS.substring(length_cidhexS-2,length_cidhexS);
        int LTEcid1 = Integer.parseInt( cidhexSfirst3S, 16 )*10;
        int LTEcid2 = Integer.parseInt( cidhexSlast2S, 16 );
        int LTEcid = LTEcid1 + LTEcid2;
        return LTEcid;
    };

    public static int getLat(Context context){
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        GsmCellLocation cellLocation = (GsmCellLocation)telephonyManager.getCellLocation();

        return cellLocation == null ? -1 : cellLocation.getLac();
    }

    public static void getRS(SignalStrength signalStrength, TelephonyManager tm, StrengthDetails strengthDetails){
        int networkType = tm.getNetworkType();
        String LTErsrp = "";
        String LTErsrq = "";
        String LTErssnr = "";
        String LTEcqi = "";
        int rssi = 0;
        String[] temp = signalStrength.toString().split(" ");

        if (Build.VERSION.SDK_INT >= 20) {
            if(TelephonyManager.NETWORK_TYPE_UNKNOWN != networkType) {
                if (((Integer.parseInt(temp[8]) != -1) && (Integer.parseInt(temp[8]) != 99)) || ((Integer.parseInt(temp[8]) != -1) &&(Integer.parseInt(temp[8]) == 0))) {
                    LTErsrp = temp[9];
                    rssi = Integer.parseInt(LTErsrp);
                    LTErsrq = temp[10];
                    LTErssnr = temp[11];
                    LTEcqi = temp[12];
                    if (LTErsrp.equalsIgnoreCase("2147483647") || LTErsrp.equalsIgnoreCase("47483647")) {
                        LTErsrp = "-1";
                    }
                    if (LTErsrq.equalsIgnoreCase("2147483647") ) {
                        LTErsrq = "-1";
                    }
                    if (LTErssnr.equalsIgnoreCase("2147483647")) {
                        LTErssnr = "-1";
                    }
                    if (LTEcqi.equalsIgnoreCase("2147483647") ) {
                        LTEcqi = "-1";
                    }
                }  else {
                    rssi = 2 * Integer.parseInt(temp[1]) - 113;
                }

                // Sony Z4 format if temp[9] == 99 -> 2G/3G
                if (Integer.parseInt(temp[1]) == 99 && Integer.parseInt(temp[9]) != 99 && Integer.parseInt(temp[8]) == -1) {
                    LTErsrp = temp[10];
                    rssi = Integer.parseInt(LTErsrp);
                    LTErsrq = temp[11];
                    LTErssnr = temp[12];
                    LTEcqi = temp[13];
                    if (LTErsrp.equalsIgnoreCase("2147483647") || LTErsrp.equalsIgnoreCase("47483647")) {
                        LTErsrp = "-1";
                    }
                    if (LTErsrq.equalsIgnoreCase("2147483647") ) {
                        LTErsrq = "-1";
                    }
                    if (LTErssnr.equalsIgnoreCase("2147483647")) {
                        LTErssnr = "-1";
                    }
                    if (LTEcqi.equalsIgnoreCase("2147483647") ) {
                        LTEcqi = "-1";
                    }
                }  else if (Integer.parseInt(temp[9]) == 99) {
                    rssi = 2 * Integer.parseInt(temp[1]) - 113;
                }
            }
        } else {   // SDK_INT <=19
            if(TelephonyManager.NETWORK_TYPE_UNKNOWN != networkType){
                if (Integer.parseInt(temp[1]) != 99) {
                    rssi = 2 * Integer.parseInt(temp[1]) - 113;
                }  else {
                    LTErsrp = temp[9];
                    rssi = Integer.parseInt(LTErsrp);
                    LTErsrq = temp[10];
                    LTErssnr = temp[11];
                    LTEcqi = temp[12];
                    if (LTErsrp.equalsIgnoreCase("2147483647") || LTErsrp.equalsIgnoreCase("47483647")) {
                        LTErsrp = "-1";
                    }
                    if (LTErsrq.equalsIgnoreCase("2147483647") ) {
                        LTErsrq = "-1";
                    }
                    if (LTErssnr.equalsIgnoreCase("2147483647")) {
                        LTErssnr = "-1";
                    }
                    if (LTEcqi.equalsIgnoreCase("2147483647") ) {
                        LTEcqi = "-1";
                    }
                    if (Integer.parseInt(temp[8]) == 99) {
                        rssi = 2 * Integer.parseInt(temp[1]) - 113;
                    } else {
                        LTErsrp = temp[9];
                        rssi = Integer.parseInt(LTErsrp);
                    }
                }
            }
        }
        if(TelephonyManager.NETWORK_TYPE_UNKNOWN != networkType){
            if (android.os.Build.MODEL.equalsIgnoreCase("GT-I9305")) {
                if (Integer.parseInt(temp[1]) != 99) {
                    rssi = 2 * Integer.parseInt(temp[1]) - 113;
                }  else {
                    LTErsrp = temp[9];
                    rssi = Integer.parseInt(LTErsrp);
                    LTErsrq = temp[10];
                    LTErssnr = temp[11];
                    LTEcqi = temp[12];

                    if (LTErsrp.equalsIgnoreCase("2147483647") || LTErsrp.equalsIgnoreCase("47483647")) {
                        LTErsrp = "-1";
                    }
                    if (LTErsrq.equalsIgnoreCase("2147483647") ) {
                        LTErsrq = "-1";
                    }
                    if (LTErssnr.equalsIgnoreCase("2147483647")) {
                        LTErssnr = "-1";
                    }
                    if (LTEcqi.equalsIgnoreCase("2147483647") ) {
                        LTEcqi = "-1";
                    }

                    if (Integer.parseInt(temp[8]) == 99) {
                        rssi = 2 * Integer.parseInt(temp[1]) - 113;
                    } else {
                        LTErsrp = temp[9];
                        rssi = Integer.parseInt(LTErsrp);
                    }
                }
            }
        }
        strengthDetails.setRssi(rssi);
        strengthDetails.setLTE_rsrp(LTErsrp);
        strengthDetails.setLTE_rsrq(LTErsrq);
        strengthDetails.setLTE_rssnr(LTErssnr);
        strengthDetails.setLTE_cqi(LTEcqi);
    }
}
