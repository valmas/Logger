package com.ntua.ote.logger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ntua.ote.logger.db.CallLogDbHelper;
import com.ntua.ote.logger.models.LogDetails;
import com.ntua.ote.logger.utils.CommonUtils;
import com.ntua.ote.logger.utils.Constants;
import com.ntua.ote.logger.utils.Direction;

import java.util.Date;


public class ViewEntryActivity extends AppCompatActivity {


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
            LogDetails logDetails = CallLogDbHelper.getInstance(this).getAllData(id);
            TextView aView = (TextView) findViewById(R.id.external_number);
            aView.setText(logDetails.getExternalNumber());

            String dateTime = CommonUtils.dateToString(logDetails.getDateTime(), Constants.DATE_TIME_OUTPUT_WITH_SEC_FORMAT);
            aView = (TextView) findViewById(R.id.date_time);
            aView.setText(dateTime);

            aView = (TextView) findViewById(R.id.duration);
            aView.setText(CommonUtils.getOutputDuration(logDetails.getDuration()));

            int directionCode = logDetails.getDirection().code;
            Direction direction = Direction.parseCode(directionCode);
            ImageView iv = (ImageView) findViewById(R.id.direction);
            if(Direction.OUTGOING == direction) {
                iv.setImageDrawable(getResources().getDrawable(R.drawable.outgoing));
            } else if(Direction.INCOMING == direction) {
                iv.setImageDrawable(getResources().getDrawable(R.drawable.incoming));
            }

            if(logDetails.getLatitude() == 0.0 || logDetails.getLongitude() == 0.0) {
                Button btn = (Button) findViewById(R.id.show_location_btn);
                btn.setEnabled(false);
            }
        }
    }

}
