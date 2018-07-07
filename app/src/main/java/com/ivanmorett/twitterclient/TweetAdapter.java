package com.ivanmorett.twitterclient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private ArrayList<Tweet> mTweets;
    private Context context;
    private TimelineActivity timelineActivity;
    private TwitterClient client;

    public TweetAdapter(ArrayList<Tweet> tweets, TimelineActivity timelineActivity, TwitterClient client){
        mTweets = tweets;
        this.timelineActivity = timelineActivity;
        this.client = client;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_tweet, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Tweet tweet = mTweets.get(position);

        // Set item views based on your views and data model
        viewHolder.tvBody.setText(tweet.getBody());
        viewHolder.tvUserName.setText("@"+tweet.getUser().getScreenName());
        viewHolder.tvName.setText(tweet.getUser().getName());
        viewHolder.tvTimeStamp.setText(TimeStampController.getRelativeTimeAgo(tweet.getCreatedAt()));
        viewHolder.tvFavorite.setText(tweet.getFavoriteCount());
        viewHolder.tvRetweet.setText(tweet.getRetweetCount());
        changeButtonColor(viewHolder.btnFavorite, viewHolder.tvFavorite, !tweet.isFavorited(), R.color.colorFavorite);
        changeButtonColor(viewHolder.btnRetweet, viewHolder.tvRetweet, !tweet.isRetweeted(), R.color.colorRetweet);

        GlideApp.with(context)
                .load(tweet.getUser().getProfileImageUrl())
                .circleCrop()
                .into(viewHolder.ivProfileImage);

        if(tweet.getEntities().getMedias().size()>0){
            GlideApp.with(context)
                    .load(tweet.getEntities().getMedias().get(0))
                    .into(viewHolder.ivTweet);
        }
        else{
            viewHolder.ivTweet.setVisibility(View.GONE);
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.tvBody) TextView tvBody;
        @BindView(R.id.tvUserName) TextView tvUserName;
        @BindView(R.id.tvTimeStamp) TextView tvTimeStamp;
        @BindView(R.id.btnReply) Button btnReply;
        @BindView(R.id.btnFavorite) Button btnFavorite;
        @BindView(R.id.btnRetweet) Button btnRetweet;
        @BindView(R.id.tvRetweet) TextView tvRetweet;
        @BindView(R.id.tvFavorite) TextView tvFavorite;
        @BindView(R.id.ivTweet) ImageView ivTweet;

        public ViewHolder(View itemView){
            super(itemView);

            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            btnReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    // make sure the position is valid, i.e. actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        Tweet tweet = mTweets.get(position);
                        timelineActivity.showEditDialog(ComposeFragment.newInstance(context, tweet, true));
                    }
                }
            });
            btnRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Tweet tweet = isTweetClicked(getAdapterPosition());
                    if(tweet==null)
                        return;
                    client.retweet(tweet.getUid(), new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                Tweet tweet2 = Tweet.fromJSON(response);
                                changeButtonColor(btnRetweet, tvRetweet, false, R.color.colorRetweet);
                                tvRetweet.setText(tweet.getRetweetCount()+1);
                                timelineActivity.addTweet(tweet2);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.e("Adapter", errorResponse.toString());
                            try {
                                Toast.makeText(context,
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
                    final Tweet tweet = isTweetClicked(getAdapterPosition());
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
                                    Toast.makeText(context,
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
                                    Toast.makeText(context,
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

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                Tweet tweet = mTweets.get(position);
                Intent i = new Intent(context, TweetDetailsActivity.class);
                i.putExtra("tweet", Parcels.wrap(tweet));
                context.startActivity(i);
            }
        }
    }

    private Tweet isTweetClicked(int position){
        if (position != RecyclerView.NO_POSITION)
            return mTweets.get(position);
        else
            return null;
    }


    private void changeButtonColor(Button btn, TextView text, boolean clicked, int color){
        if(clicked) {
            text.setTextColor(context.getColor(R.color.colorUnClicked));
            btn.setBackgroundTintList(context.getResources().getColorStateList(R.color.colorUnClicked));
        }
        else{
            text.setTextColor(context.getColor(color));
            btn.setBackgroundTintList(context.getResources().getColorStateList(color));
        }

    }
    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }



    // Add a list of items -- change to type used
    public void addAll(ArrayList<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }


}
