package com.ivanmorett.twitterclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivanmorett.twitterclient.controllers.TimeStampController;
import com.ivanmorett.twitterclient.models.Tweet;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetDetailsActivity extends AppCompatActivity {

    @BindView(R.id.tvTimeStamp) TextView tvTimeStamp;
    @BindView(R.id.tvUserName) TextView tvUserName;
    @BindView(R.id.tvBody) TextView tvBody;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.tbDetails) Toolbar tbDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        ButterKnife.bind(this);

        Intent i = getIntent();
        Tweet tweet = Parcels.unwrap(i.getParcelableExtra("tweet"));

        setSupportActionBar(tbDetails);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set item views based on your views and data model
        tvBody.setText(tweet.getBody());
        tvUserName.setText("@"+tweet.getUser().getScreenName());
        tvName.setText(tweet.getUser().getName());
        tvTimeStamp.setText(TimeStampController.getDate(tweet.getCreatedAt()));
        GlideApp.with(getApplicationContext())
                .load(tweet.getUser().getProfileImageUrl())
                .circleCrop()
                .into(ivProfileImage);

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
