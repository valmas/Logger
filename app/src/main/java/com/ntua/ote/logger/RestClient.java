package com.ntua.ote.logger;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ntua.ote.logger.models.AsyncResponseLogDetails;
import com.ntua.ote.logger.utils.AsyncResponse;
import com.ntua.ote.logger.utils.CommonUtils;
import com.ntua.ote.logger.utils.Constants;
import com.ntua.ote.logger.utils.RequestType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class RestClient extends AsyncTask<Object, String, AsyncResponseLogDetails> {

    public AsyncResponse delegate = null;

    private static final String TAG = RestClient.class.getName();

    public RestClient(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected AsyncResponseLogDetails doInBackground(Object[] params){
        long responseCode = -1;
        boolean success;
        RequestType requestType = (RequestType) params[0];

        try {
            Gson gson = new GsonBuilder()
                    .setDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").create();
            String json= gson.toJson(params[1]);
            Log.i("data map", json);
            HttpsURLConnection urlConn;
            URL url = new URL (Constants.SERVER_URL + requestType.endpoint);
            urlConn = (HttpsURLConnection) url.openConnection();
            urlConn.setDoInput (true);
            urlConn.setDoOutput (true);
            urlConn.setUseCaches(false);
            urlConn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            urlConn.setRequestProperty("Accept", "application/json;charset=utf-8");
            urlConn.setRequestMethod("POST");
            urlConn.setConnectTimeout(5000);
            urlConn.setSSLSocketFactory(CommonUtils.configureSSL((Context) params[3]).getSocketFactory());
            urlConn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            urlConn.connect();

            OutputStream os = urlConn.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            osw.write(json);
            osw.flush ();
            osw.close ();

            int statusCode = urlConn.getResponseCode();
            Log.i(statusCode + "", TAG);
            if (statusCode == HttpURLConnection.HTTP_OK) {
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        urlConn.getInputStream(),"utf-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                responseCode = Long.parseLong(sb.toString());
                Log.i(responseCode + "", TAG);
                success = true;
            } else {
                success = false;
            }

        } catch (Exception e) {
            success = false;
            e.printStackTrace();
            Log.i(e.getMessage(), TAG);
        }

        return new AsyncResponseLogDetails((long)params[2], responseCode, success, requestType);
    }


    @Override
    protected void onPostExecute(AsyncResponseLogDetails result) {
        delegate.processFinish(result);
    }

}