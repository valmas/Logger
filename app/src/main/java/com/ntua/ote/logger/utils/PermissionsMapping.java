package com.ntua.ote.logger.utils;

import android.Manifest;

public enum PermissionsMapping {

    LOGGER_PERMISSIONS(new String[]{Manifest.permission.READ_CALL_LOG,
                               Manifest.permission.READ_SMS,
                               Manifest.permission.PROCESS_OUTGOING_CALLS,
                               Manifest.permission.RECEIVE_SMS,
                               Manifest.permission.ACCESS_COARSE_LOCATION,
                               Manifest.permission.ACCESS_FINE_LOCATION,
                               Manifest.permission.INTERNET,
                               Manifest.permission.ACCESS_NETWORK_STATE}, 1),
    REBOOT_NOTIF(new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED}, 0),
    DOWNLOAD_PERMISSIONS(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 3),
    INIT_PERMISSIONS(new String[]{Manifest.permission.READ_PHONE_STATE}, 2);

    public final String[] permission;
    public final int requestCode;

    PermissionsMapping(String[] permission, int requestCode) {
        this.permission = permission;
        this.requestCode = requestCode;
    }

}
