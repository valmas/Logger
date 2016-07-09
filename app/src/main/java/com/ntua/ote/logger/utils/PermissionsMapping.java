package com.ntua.ote.logger.utils;

import android.Manifest;

public enum PermissionsMapping {

    LOGGER_PERMISSIONS(new String[]{Manifest.permission.READ_CALL_LOG,
                               Manifest.permission.READ_SMS,
                               Manifest.permission.PROCESS_OUTGOING_CALLS,
                               Manifest.permission.RECEIVE_SMS,
                               Manifest.permission.ACCESS_COARSE_LOCATION,
                               Manifest.permission.ACCESS_FINE_LOCATION}, 1),
    REBOOT_NOTIF(new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED}, 0),
    INIT_PERMISSIONS(new String[]{Manifest.permission.READ_PHONE_STATE}, 2);

    public final String[] permission;
    public final int requestCode;

    PermissionsMapping(String[] permission, int requestCode) {
        this.permission = permission;
        this.requestCode = requestCode;
    }

}
