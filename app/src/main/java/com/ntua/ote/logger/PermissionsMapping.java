package com.ntua.ote.logger;

import android.Manifest;

public enum PermissionsMapping {

    READ_CALL_LOG(Manifest.permission.READ_CALL_LOG, 1),
    READ_SMS_LOG(Manifest.permission.READ_SMS, 2);

    public final String permission;
    public final int requestCode;

    PermissionsMapping(String permission, int requestCode) {
        this.permission = permission;
        this.requestCode = requestCode;
    }

}
