package com.ntua.ote.logger;

import android.app.ListFragment;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ntua.ote.logger.db.CallLogDbHelper;
import com.ntua.ote.logger.db.CallLogDbSchema;
import com.ntua.ote.logger.utils.CommonUtils;
import com.ntua.ote.logger.utils.Constants;
import com.ntua.ote.logger.utils.Direction;
import com.ntua.ote.logger.utils.LogType;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CallsViewActivity extends AppCompatActivity {

    private static final String TAG = CallsViewActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_calls);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initFromPreferences();

        getFragmentManager().beginTransaction()
                .add(R.id.list_view_layout, new CallListFragment())
                .commit();
    }

    public void initFromPreferences(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean value = sharedPref.getBoolean(SettingsActivity.KEY_PREF_KEEP_SCREEN_ON, false);
        this.findViewById(R.id.view_calls_layout).setKeepScreenOn(value);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    public static class CallListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

        public SimpleCursorAdapter mAdapter;

        private ProgressBar progressBar;

        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            progressBar = new ProgressBar(getActivity());
            progressBar.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
            progressBar.setIndeterminate(true);


            Bundle b = getActivity().getIntent().getExtras();;
            final LogType type = b == null ? null :
                    LogType.parseCode(b.getInt(Constants.LOG_TYPE_KEY));

            // Must add the progress bar to the root of the layout
            ViewGroup root = (ViewGroup) getActivity().findViewById(android.R.id.content);
            root.addView(progressBar);

            // For the cursor adapter, specify which columns go into which views
            String[] fromColumns = {CallLogDbSchema.CallLogEntry.COLUMN_NAME_EXT_NUM,
                    CallLogDbSchema.CallLogEntry.COLUMN_NAME_DATE,
                    CallLogDbSchema.CallLogEntry.COLUMN_NAME_DURATION,
                    CallLogDbSchema.CallLogEntry.COLUMN_NAME_DIRECTION};

            int[] toViews = {R.id.external_number, R.id.date_time, R.id.duration, R.id.direction};


            // Create an empty adapter we will use to display the loaded data.
            // We pass null for the cursor, then update it in onLoadFinished()
            mAdapter = new SimpleCursorAdapter(getActivity(),
                    R.layout.view_calls_list, null,
                    fromColumns, toViews, 0);

            mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

                public boolean setViewValue(View aView, Cursor aCursor, int aColumnIndex) {

                    switch (aColumnIndex) {
                        case 1:
                            ((TextView) aView).setText(aCursor.getString(aColumnIndex));
                            return true;
                        case 2:
                            String dateTime = aCursor.getString(aColumnIndex);
                            Date d = CommonUtils.stringToDate(dateTime);
                            dateTime = CommonUtils.dateToString(d, Constants.DATE_TIME_OUTPUT_FORMAT);
                            TextView textView = (TextView) aView;
                            textView.setText(dateTime);
                            return true;
                        case 3:
                            textView = (TextView) aView;
                            int duration = aCursor.getInt(aColumnIndex);
                            textView.setText(CommonUtils.getOutputDuration(duration));
                            return true;
                        case 4:
                            int directionCode = aCursor.getInt(aColumnIndex);
                            Direction direction = Direction.parseCode(directionCode);
                            ImageView iv = (ImageView) aView;
                            if(type == LogType.CALL) {
                                if (Direction.OUTGOING == direction) {
                                    iv.setImageDrawable(getResources().getDrawable(R.drawable.outgoing));
                                } else if (Direction.INCOMING == direction) {
                                    iv.setImageDrawable(getResources().getDrawable(R.drawable.incoming));
                                }
                            } else if (type == LogType.SMS) {
                                if (Direction.OUTGOING == direction) {
                                    iv.setImageDrawable(getResources().getDrawable(R.drawable.smsoutgoing));
                                } else if (Direction.INCOMING == direction) {
                                    iv.setImageDrawable(getResources().getDrawable(R.drawable.smsincoming));
                                }
                            }
                            return true;

                    }

                    return false;
                }
            });
            setListAdapter(mAdapter);

            getListView().setEmptyView(getActivity().findViewById(android.R.id.empty));

            // Prepare the loader.  Either re-connect with an existing one,
            // or start a new one.
            getLoaderManager().initLoader(0, b, this);

        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            LogType type = args == null ? null :
                    LogType.parseCode(args.getInt(Constants.LOG_TYPE_KEY));
            return new CursorLoader(getActivity(), null,
                    CallLogDbHelper.LIST_PROJECTION, CallLogDbHelper.selectionByType, new String[]{type.code + ""}, null)
            {
                @Override
                public Cursor loadInBackground()
                {
                    Cursor c = CallLogDbHelper.getInstance(getContext()).getDataForList(getProjection(), getSelection(), getSelectionArgs());
                    if(c == null || c.isAfterLast()) {
                        Log.d("empty", TAG);
                    }
                    return c;
                }
            };

        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            progressBar.setVisibility(View.GONE);
            mAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mAdapter.swapCursor(null);

        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            super.onListItemClick(l, v, position, id);
            Intent intent = new Intent(getActivity(), ViewEntryActivity.class);
            Bundle b = new Bundle();
            b.putLong(Constants.VIEW_ENTRY_KEY, id);
            intent.putExtras(b);
            startActivity(intent);
        }
    }
}