package com.ivanmorett.twitterclient.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel
public class Entities {
    private ArrayList<String> urls,  hashtags, media;

    public Entities(){
        urls = new ArrayList<String>();
        hashtags = new ArrayList<String>();
        media = new ArrayList<String>();
    }

    public ArrayList<String> getUrls(){return this.urls; }
    public ArrayList<String> getHashtags(){ return this.hashtags; }
    public ArrayList<String> getMedias(){ return this.media; }

    public static Entities fromJSON(JSONObject object) throws JSONException{
        JSONArray urlsArray = object.has("urls")? object.getJSONArray("urls"): new JSONArray();
        JSONArray hashtagsArray = object.has("hashtags")? object.getJSONArray("hashtags"): new JSONArray();
        JSONArray mediaArray = object.has("media")? object.getJSONArray("media"): new JSONArray();
        Entities entities = new Entities();
        for(int i = 0; i < urlsArray.length(); i++){
            entities.urls.add(urlsArray.getString(i));
        }
        for(int i = 0; i < hashtagsArray.length(); i++){
            entities.hashtags.add(hashtagsArray.getString(i));
        }
        for(int i = 0; i < mediaArray.length(); i++){
            entities.media.add(mediaArray.getJSONObject(i).getString("media_url_https"));
        }
        return entities;
    }
}