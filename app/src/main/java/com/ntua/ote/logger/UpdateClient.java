package com.ntua.ote.logger;

import android.os.AsyncTask;
import android.util.Log;

import com.ntua.ote.logger.models.rs.AsyncResponseUpdateDetails;
import com.ntua.ote.logger.utils.AsyncResponse;
import com.ntua.ote.logger.utils.Constants;
import com.ntua.ote.logger.utils.RequestType;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateClient extends AsyncTask<Object, String, AsyncResponseUpdateDetails> {

    private static final String TAG = UpdateClient.class.getName();

    public AsyncResponse delegate = null;

    public UpdateClient(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected AsyncResponseUpdateDetails doInBackground(Object[] params){
        RequestType requestType = (RequestType) params[0];

        try {
            HttpURLConnection urlConn;
            URL url = new URL (Constants.SERVER_URL + requestType.endpoint);
            urlConn = (HttpURLConnection) url.openConnection();
            if(requestType == RequestType.CHECK_VERSION) {
                urlConn.setRequestProperty("Accept", "text/plain");
            } else {
                urlConn.setRequestProperty("Accept", "application/octet-stream");
            }
            urlConn.setRequestMethod("GET");
            urlConn.setConnectTimeout(5000);
            urlConn.connect();

            int statusCode = urlConn.getResponseCode();
            Log.i(statusCode + "", TAG);

            if (statusCode == HttpURLConnection.HTTP_OK) {
                if(requestType == RequestType.CHECK_VERSION) {
                    StringBuilder sb = new StringBuilder();
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            urlConn.getInputStream()));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();
                    Log.i(sb.toString() , TAG);
                    return new AsyncResponseUpdateDetails(sb.toString(), requestType);
                } else {
                    BufferedInputStream bufferinstream = new BufferedInputStream(urlConn.getInputStream());
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    byte[] data = new byte[500];
                    int current = 0;

                    while((current = bufferinstream.read(data,0,data.length)) != -1){
                        buffer.write(data,0,current);
                    }

                    AsyncResponseUpdateDetails details = new AsyncResponseUpdateDetails(data, RequestType.UPDATE);
                    details.setContent(buffer.toByteArray());
                    return details;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(e.getMessage(), TAG);
        }

        return null;
    }


    @Override
    protected void onPostExecute(AsyncResponseUpdateDetails result) {
        delegate.processFinish(result);
    }

}