package com.ntua.ote.logger;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ntua.ote.logger.db.CallLogDbHelper;
import com.ntua.ote.logger.models.LogDetails;
import com.ntua.ote.logger.utils.CommonUtils;
import com.ntua.ote.logger.utils.Constants;
import com.ntua.ote.logger.utils.Direction;
import com.ntua.ote.logger.utils.LogType;

public class ViewEntryActivity extends AppCompatActivity {

    private LogDetails logDetails;
    private Dialog smsContentDialog;

    /** On activity creation parameterize and displays the layout the details of the Call/SMS */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entry);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();
        long id = b == null ? -1 : b.getLong(Constants.VIEW_ENTRY_KEY);

        if(id > -1) {
            logDetails = CallLogDbHelper.getInstance(this).getAllData(id);
            TextView aView = (TextView) findViewById(R.id.external_number);
            aView.setText(logDetails.getExternalNumber());

            String dateTime = CommonUtils.dateToString(logDetails.getDateTime(), Constants.DATE_TIME_OUTPUT_WITH_SEC_FORMAT);
            aView = (TextView) findViewById(R.id.date_time);
            aView.setText(dateTime);
            if(logDetails.getType() == LogType.CALL) {
                aView = (TextView) findViewById(R.id.duration_content_lbl);
                aView.setText(R.string.duration);
                aView = (TextView) findViewById(R.id.duration);
                aView.setVisibility(View.VISIBLE);
                aView.setText(CommonUtils.getOutputDuration(logDetails.getDuration()));
                aView = (TextView) findViewById(R.id.content);
                aView.setVisibility(View.GONE);
            } else if(logDetails.getType() == LogType.SMS) {
                aView = (TextView) findViewById(R.id.duration_content_lbl);
                aView.setText(R.string.content);
                aView = (TextView) findViewById(R.id.content);
                aView.setVisibility(View.VISIBLE);
                String smsContent = logDetails.getSmsContent();
                if(smsContent.length() > 28) {
                    smsContent = smsContent.substring(0, 25) + "...";
                }
                aView.setText(smsContent);
                aView = (TextView) findViewById(R.id.duration);
                aView.setVisibility(View.GONE);
            }
            int directionCode = logDetails.getDirection().code;
            Direction direction = Direction.parseCode(directionCode);
            ImageView iv = (ImageView) findViewById(R.id.direction);
            if(Direction.OUTGOING == direction) {
                if(logDetails.getType() == LogType.CALL) {
                    iv.setImageDrawable(CommonUtils.getDrawable(R.drawable.outgoing, getResources()));
                } else {
                    iv.setImageDrawable(CommonUtils.getDrawable(R.drawable.smsoutgoing, getResources()));
                }
            } else if(Direction.INCOMING == direction) {
                if(logDetails.getType() == LogType.CALL) {
                    iv.setImageDrawable(CommonUtils.getDrawable(R.drawable.incoming, getResources()));
                } else {
                    iv.setImageDrawable(CommonUtils.getDrawable(R.drawable.smsincoming, getResources()));
                }
            }
            if(logDetails.getLatitude() == 0.0 || logDetails.getLongitude() == 0.0) {
                Button btn = (Button) findViewById(R.id.show_location_btn);
                btn.setEnabled(false);
            }

            aView = (TextView) findViewById(R.id.cell_id);
            aView.setText(String.valueOf(logDetails.getCellId()));
            aView = (TextView) findViewById(R.id.lac);
            aView.setText(String.valueOf(logDetails.getLac()));
            aView = (TextView) findViewById(R.id.rat);
            aView.setText(String.valueOf(logDetails.getRat()));
            aView = (TextView) findViewById(R.id.rssi);
            aView.setText(String.valueOf(logDetails.getRssi()));
            aView = (TextView) findViewById(R.id.rsrp);
            aView.setText(String.valueOf(logDetails.getLTE_rsrp()));
            aView = (TextView) findViewById(R.id.rsrq);
            aView.setText(String.valueOf(logDetails.getLTE_rsrq()));
            aView = (TextView) findViewById(R.id.rssnr);
            aView.setText(String.valueOf(logDetails.getLTE_rssnr()));
            aView = (TextView) findViewById(R.id.cqi);
            aView.setText(String.valueOf(logDetails.getLTE_cqi()));
        }
    }

    /** Redirects the user to the map screen upon selecting to display the location of the log */
    public void showLocation(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        Bundle b = new Bundle();
        b.putDouble(Constants.LATITUDE_KEY, logDetails.getLatitude());
        b.putDouble(Constants.LONGITUDE_KEY, logDetails.getLongitude());
        b.putString(Constants.EXTERNAL_NUMBER_KEY, logDetails.getExternalNumber());
        intent.putExtras(b);
        startActivity(intent);
    }

    /** Displays a dialog to the user with the SMS content if the SMS content is too large to
     * be displayed int the layout */
    public void showSmsContent(View view) {
        if(logDetails.getSmsContent().length() > 28) {
            smsContentDialog = new Dialog(this);
            smsContentDialog.setContentView(R.layout.sms_content_popup);
            TextView txt = (TextView) smsContentDialog.findViewById(R.id.smsContentPopup);
            txt.setText(logDetails.getSmsContent());
            smsContentDialog.show();
        }
    }

    public void closeDialog(View view) {
        smsContentDialog.dismiss();
    }
}
