package com.ivanmorett.twitterclient;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.ivanmorett.twitterclient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetAdapter adapter;
    @BindView(R.id.rvTweet) RecyclerView rvTweet;
    @BindView(R.id.fabComposeTweet) FloatingActionButton fabComposeTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        ButterKnife.bind(this);

        tweets = new ArrayList<>();

        adapter = new TweetAdapter(tweets);

        rvTweet.setLayoutManager(new LinearLayoutManager( this));
        rvTweet.setAdapter(adapter );

        fabComposeTweet.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ComposeActivity.class);
                startActivity(i);
            }
        });

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

                 for(int i = 0; i < response.length(); i++){
                     try {
                         Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                         tweets.add(tweet);
                         adapter.notifyItemInserted(tweets.size()-1);
                     } catch(JSONException ex){
                         Log.e(TAG, ex.getMessage() );
                         ex.printStackTrace();
                     }
                 }

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
