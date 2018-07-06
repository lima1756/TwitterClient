package com.ivanmorett.twitterclient.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel
public class Tweet {

    private String body;
    private long uid;
    private User user;
    private String createdAt;
    private Integer retweetCount, favoriteCount;
    private boolean retweeted, favorited;
    private Entities entities;

    public Tweet(){}

    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException{
        Tweet tweet = new Tweet();

        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.retweetCount = jsonObject.getInt("retweet_count");
        tweet.favoriteCount = jsonObject.getInt("favorite_count");
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        tweet.favorited = jsonObject.getBoolean("favorited");
        tweet.entities = Entities.fromJSON(jsonObject.getJSONObject("entities"));
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));

        return tweet;
    }

    public static ArrayList<Tweet> fromJSON(JSONArray jsonArray) throws JSONException{
        ArrayList<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            Tweet tweet = Tweet.fromJSON(jsonArray.getJSONObject(i));
            tweets.add(tweet);
        }
        return tweets;
    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public User getUser() {
        return user;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getRetweetCount() {
        return retweetCount.toString();
    }

    public String getFavoriteCount() {
        return favoriteCount.toString();
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void changeFavorite(){
        if(favorited)
            favoriteCount--;
        else
            favoriteCount++;
        favorited=!favorited;
    }


    public Entities getEntities() {
        return entities;
    }
}
