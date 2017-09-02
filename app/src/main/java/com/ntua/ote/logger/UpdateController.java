package com.ntua.ote.logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.ntua.ote.logger.models.rs.AsyncResponseUpdateDetails;
import com.ntua.ote.logger.utils.AsyncResponse;
import com.ntua.ote.logger.utils.CommonUtils;
import com.ntua.ote.logger.utils.Constants;
import com.ntua.ote.logger.utils.PermissionsMapping;
import com.ntua.ote.logger.utils.RequestType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class UpdateController implements AsyncResponse<AsyncResponseUpdateDetails> {
    private static final String TAG = UpdateController.class.getName();

    private Activity context;

    private static UpdateController ourInstance;

    public static UpdateController getInstance(Activity context) {
        if(ourInstance == null) {
            ourInstance = new UpdateController(context);
        } else {
            ourInstance.context = context;
        }
        return ourInstance;
    }

    private UpdateController(Activity context) {
        this.context = context;
    }

    /** Gets the latest available version from the server */
    public void checkVersion(){
        if(CommonUtils.haveNetworkConnection(context)) {
            new UpdateClient(this).execute(RequestType.CHECK_VERSION, context);
        } else {
            Toast.makeText(context, "Please enable internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    /** Informs the user of the newest version and its the changeLog.
     * Asks the user to download the newest version */
    private void newVersionAlert(Context context, final String filename, final String changeLog){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("   A Newer Version is Available (" + filename + ").\n\n   " +
                "Change Log:\n    " + changeLog + "\n\n   Do you Want to Download it?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        alertOnClick();
                    }
                });

        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /** Requests DOWNLOAD_PERMISSIONS from user in order to download the newest version */
    private void alertOnClick(){
        if(CommonUtils.havePermissions(PermissionsMapping.DOWNLOAD_PERMISSIONS, context)) {
            download();
        } else {
            CommonUtils.requestPermission(PermissionsMapping.DOWNLOAD_PERMISSIONS, context);
        }
    }

    /** Downloads the newest version */
    public void download() {

        if(CommonUtils.haveNetworkConnection(context)) {
            new UpdateClient(this).execute(RequestType.UPDATE, context);
        } else {
            Toast.makeText(context, "Please enable internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    /** A callback method that is invoked from the UpdateClient when the latest version is retrieved
     * or the download has been completed */
    @Override
    public void processFinish(AsyncResponseUpdateDetails output) {
        if(output != null) {
            switch (output.getRequestType()) {
                case CHECK_VERSION: {
                    if (output.getVersion() != null && !TextUtils.isEmpty(output.getVersion().getVersionNumber()) &&
                            !ApplicationController.VERSION.equals(output.getVersion().getVersionNumber())) {
                        newVersionAlert(context, output.getVersion().getVersionNumber(), output.getVersion().getChangeLog());
                    } else {
                        Toast.makeText(context, "You Have the Most Recent Version", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case UPDATE: {
                    try {
                        Log.i(context.getFilesDir().getAbsolutePath(), TAG);
                        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        File file = new File(path, Constants.FILE_NAME);
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(output.getContent());
                        fos.close();
                        Toast.makeText(context, "Download completed", Toast.LENGTH_SHORT).show();
                        //install_apk();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        } else {
            Toast.makeText(context, "Connection to server failed. Please try again", Toast.LENGTH_SHORT).show();
        }
    }
}
