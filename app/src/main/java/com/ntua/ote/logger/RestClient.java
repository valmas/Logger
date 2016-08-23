package com.ntua.ote.logger;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ntua.ote.logger.models.AsyncResponseDetails;
import com.ntua.ote.logger.utils.AsyncResponse;
import com.ntua.ote.logger.utils.RequestType;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class RestClient extends AsyncTask<Object, String, AsyncResponseDetails> {

    public AsyncResponse delegate = null;

    private static final String TAG = RestClient.class.getName();

    private static final String URL = "http://192.168.1.3:8080/logger/server/rest/log/";

    public RestClient(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected AsyncResponseDetails doInBackground(Object[] params){
        long responseCode = -1;
        boolean success = false;
        RequestType requestType = (RequestType) params[0];
        HttpPost httpost = new HttpPost(URL + requestType.endpoint);

        try {
            Gson gson = new GsonBuilder()
                    .setDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").create();
            Log.d("data  map", "data map------" + gson.toJson(params[1]));
            httpost.setEntity(new StringEntity(gson.toJson(params[1])));
            httpost.setHeader("Accept", "application/json");
            httpost.setHeader("Content-type", "application/json");
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(httpost);
            int statusCode = response.getStatusLine().getStatusCode();
            Log.d("response code", "----------------" + statusCode);

            if (statusCode == 200) {
                String responseStr = EntityUtils.toString(response.getEntity());
                responseCode = Long.parseLong(responseStr);
                success = true;
            } else {
                success = false;
            }

        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }

        return new AsyncResponseDetails((long)params[2], responseCode, success, requestType);
    }

    @Override
    protected void onPostExecute(AsyncResponseDetails result) {
        delegate.processFinish(result);
    }

}