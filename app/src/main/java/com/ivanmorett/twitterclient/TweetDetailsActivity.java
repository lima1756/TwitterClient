package com.ivanmorett.twitterclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ivanmorett.twitterclient.controllers.TimeStampController;
import com.ivanmorett.twitterclient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TweetDetailsActivity extends AppCompatActivity {

    @BindView(R.id.tvTimeStamp) TextView tvTimeStamp;
    @BindView(R.id.tvUserName) TextView tvUserName;
    @BindView(R.id.tvBody) TextView tvBody;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.tbDetails) Toolbar tbDetails;
    @BindView(R.id.btnReply) Button btnReply;
    @BindView(R.id.btnFavorite) Button btnFavorite;
    @BindView(R.id.btnRetweet) Button btnRetweet;
    @BindView(R.id.tvRetweet) TextView tvRetweet;
    @BindView(R.id.tvFavorite) TextView tvFavorite;
    @BindView(R.id.ivTweet) ImageView ivTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        ButterKnife.bind(this);

        Intent i = getIntent();
        final Tweet tweet = Parcels.unwrap(i.getParcelableExtra("tweet"));

        setSupportActionBar(tbDetails);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set item views based on your views and data model
        tvBody.setText(tweet.getBody());
        tvUserName.setText("@"+tweet.getUser().getScreenName());
        tvName.setText(tweet.getUser().getName());
        tvTimeStamp.setText(TimeStampController.getDate(tweet.getCreatedAt()));
        tvFavorite.setText(tweet.getFavoriteCount());
        tvRetweet.setText(tweet.getRetweetCount());
        GlideApp.with(getApplicationContext())
                .load(tweet.getUser().getProfileImageUrl())
                .circleCrop()
                .into(ivProfileImage);
        final TwitterClient client = new TwitterClient(getApplicationContext());

//        btnReply.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int position = getAdapterPosition();
//                // make sure the position is valid, i.e. actually exists in the view
//                if (position != RecyclerView.NO_POSITION) {
//                    Tweet tweet = mTweets.get(position);
//                    timelineActivity.showEditDialog(ComposeFragment.newInstance(context, tweet, true));
//                }
//            }
//        });
        btnRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tweet==null)
                    return;
                client.retweet(tweet.getUid(), new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            Tweet tweet2 = Tweet.fromJSON(response);
                            changeButtonColor(btnRetweet, tvRetweet, false, R.color.colorRetweet);
                            tvRetweet.setText(tweet.getRetweetCount()+1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e("Adapter", errorResponse.toString());
                        try {
                            Toast.makeText(getApplicationContext(),
                                    errorResponse.getJSONArray("errors").getJSONObject(0).getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });



            }
        });
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tweet==null)
                    return;
                if(tweet.isFavorited()){
                    client.unlikeTweet(tweet.getUid(), new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            changeButtonColor(btnFavorite, tvFavorite, tweet.isFavorited(), R.color.colorFavorite);
                            tweet.changeFavorite();
                            tvFavorite.setText(tweet.getFavoriteCount());
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.e("Adapter", errorResponse.toString());
                            changeButtonColor(btnFavorite, tvFavorite, tweet.isFavorited(), R.color.colorFavorite);
                            tweet.changeFavorite();
                            tvFavorite.setText(tweet.getFavoriteCount());
                            try {
                                Toast.makeText(getApplicationContext(),
                                        errorResponse.getJSONArray("errors").getJSONObject(0).getString("message"),
                                        Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                else{
                    client.likeTweet(tweet.getUid(), new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            changeButtonColor(btnFavorite, tvFavorite, tweet.isFavorited(), R.color.colorFavorite);
                            tweet.changeFavorite();
                            tvFavorite.setText(tweet.getFavoriteCount());
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.e("Adapter", errorResponse.toString());
                            changeButtonColor(btnFavorite, tvFavorite, tweet.isFavorited(), R.color.colorFavorite);
                            tweet.changeFavorite();
                            tvFavorite.setText(tweet.getFavoriteCount());
                            try {
                                Toast.makeText(getApplicationContext(),
                                        errorResponse.getJSONArray("errors").getJSONObject(0).getString("message"),
                                        Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

            }
        });

    }

    private void changeButtonColor(Button btn, TextView text, boolean clicked, int color){
        if(clicked) {
            text.setTextColor(getApplicationContext().getColor(R.color.colorUnClicked));
            btn.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.colorUnClicked));
        }
        else{
            text.setTextColor(getApplicationContext().getColor(color));
            btn.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(color));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // This is the up button
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;

            default:
                return true;

        }

    }
}
