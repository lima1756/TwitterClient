package com.ivanmorett.twitterclient;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ivanmorett.twitterclient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetAdapter adapter;
    private static final String TAG  = "TimelineActivity";
    private RecyclerView.SmoothScroller smoothScroller;

    @BindView(R.id.rvTweet) RecyclerView rvTweet;
    @BindView(R.id.fabComposeTweet) FloatingActionButton fabComposeTweet;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        ButterKnife.bind(this);

        tweets = new ArrayList<>();

        client = TwitterApp.getRestClient(getApplicationContext());

        adapter = new TweetAdapter(tweets, this, client);

        rvTweet.setLayoutManager(new LinearLayoutManager( this));
        rvTweet.setAdapter(adapter );

        fabComposeTweet.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
//                Intent i = new Intent(getApplicationContext(), ComposeFragment.class);
////                startActivityForResult(i, 1);
                showEditDialog(ComposeFragment.newInstance(getApplicationContext(), null));
            }
        });


        populateTimeline();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // Your code to refresh the list here.

                // Make sure you call swipeContainer.setRefreshing(false)

                // once the network request has completed successfully.

                fetchTimelineAsync(0);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Configure the scroll toTop
        smoothScroller = new LinearSmoothScroller(getApplicationContext()) {
            @Override protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
        smoothScroller.setTargetPosition(0);
    }

    private void populateTimeline(){
        client.getHomeTimeline(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                try {
                    adapter.addAll(Tweet.fromJSON(response));
                } catch (JSONException e) {
                    e.printStackTrace();
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

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        client.getHomeTimeline(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                adapter.clear();

                // Adding items
                try {
                    adapter.addAll(Tweet.fromJSON(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, responseString);
                swipeContainer.setRefreshing(false);
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e(TAG, errorResponse.toString());
                swipeContainer.setRefreshing(false);
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, errorResponse.toString());
                swipeContainer.setRefreshing(false);
                throwable.printStackTrace();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public void addTweet(Tweet tweet){
        //Toast.makeText(getApplicationContext(), "Tweet submitted", Toast.LENGTH_LONG).show();
        tweets.add(0, tweet);
        adapter.notifyItemInserted(0);
        scrollToTop();
    }
    public void scrollToTop(){
        rvTweet.smoothScrollToPosition(0);
        //rvTweet.getLayoutManager().startSmoothScroll(smoothScroller);
    }

    public void showEditDialog(ComposeFragment composeFragment) {

        FragmentManager fm = getSupportFragmentManager();

        ComposeFragment editNameDialogFragment = composeFragment;

        editNameDialogFragment.show(fm, "fragment_edit_name");

    }


}
