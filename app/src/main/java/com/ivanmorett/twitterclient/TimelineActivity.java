package com.ivanmorett.twitterclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient(getApplicationContext());
        populateTimeline();
    }

    private void populateTimeline(){
         client.getHomeTimeline(new JsonHttpResponseHandler(){
             private String TAG = "TwitterClient";
             @Override
             public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                 Log.d(TAG, response.toString());
             }

             @Override
             public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                 Log.d(TAG, response.toString());
             }

             @Override
             public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                 Log.e(TAG, responseString);
                 throwable.printStackTrace();
             }

             @Override
             public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                 Log.e(TAG, errorResponse.toString());
                 throwable.printStackTrace();
             }

             @Override
             public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                 Log.e(TAG, errorResponse.toString());
                 throwable.printStackTrace();
             }
         });
    }
}
