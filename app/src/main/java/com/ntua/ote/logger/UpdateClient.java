package com.ntua.ote.logger;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ntua.ote.logger.models.rs.AsyncResponseUpdateDetails;
import com.ntua.ote.logger.models.rs.AuthenticationRequest;
import com.ntua.ote.logger.models.rs.Version;
import com.ntua.ote.logger.utils.AsyncResponse;
import com.ntua.ote.logger.utils.CommonUtils;
import com.ntua.ote.logger.utils.Constants;
import com.ntua.ote.logger.utils.RequestType;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

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
            Gson gson = new GsonBuilder().create();
            AuthenticationRequest authRequest = new AuthenticationRequest();
            authRequest.setUserName(Constants.SERVER_USERNAME);
            authRequest.setPassword(Constants.SERVER_PASSWORD);
            String json= gson.toJson(authRequest);
            HttpsURLConnection urlConn;
            URL url = new URL (Constants.SERVER_URL + requestType.endpoint);
            urlConn = (HttpsURLConnection) url.openConnection();
            urlConn.setDoInput (true);
            urlConn.setDoOutput (true);
            urlConn.setUseCaches(false);
            urlConn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            if(requestType == RequestType.CHECK_VERSION) {
                urlConn.setRequestProperty("Accept", "application/json");
            } else {
                urlConn.setRequestProperty("Accept", "application/octet-stream");
            }
            urlConn.setRequestMethod("POST");
            urlConn.setConnectTimeout(5000);
            urlConn.setSSLSocketFactory(CommonUtils.configureSSL((Context) params[1]).getSocketFactory());
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
                    Gson builder = new GsonBuilder().create();
                    Version version = builder.fromJson(sb.toString(), Version.class);
                    return new AsyncResponseUpdateDetails(version, requestType);
                } else {
                    BufferedInputStream bufferinstream = new BufferedInputStream(urlConn.getInputStream());
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    byte[] data = new byte[500];
                    int current;

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