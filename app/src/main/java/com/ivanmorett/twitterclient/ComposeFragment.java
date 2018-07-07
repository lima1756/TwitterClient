package com.ivanmorett.twitterclient;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ivanmorett.twitterclient.controllers.ProgressBarController;
import com.ivanmorett.twitterclient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class ComposeFragment extends DialogFragment implements View.OnClickListener{

    @BindView(R.id.btnTweet) Button btnTweet;
    @BindView(R.id.pbRemainingChars) ProgressBar pbRemainingChars;
    @BindView(R.id.etTweetBody) EditText etTweetBody;
    @BindView(R.id.tvRemainingChars) TextView tvRemainingChars;
    @BindView(R.id.tvReply) TextView tvReply;
    @BindView(R.id.ivUser) ImageView ivUser;
    private Context context;
    private boolean replying;
    private Tweet tweet;

    ProgressBarController pbController;

    public ComposeFragment(){}

    public static ComposeFragment newInstance(Context context, Tweet tweet, boolean replying) {
        ComposeFragment frag = new ComposeFragment();
        Bundle args = new Bundle();
        frag.replying = replying;
        frag.tweet = tweet;
        frag.context = context;
        frag.setArguments(args);
        return frag;
    }

    public static ComposeFragment newInstance(Context context, Tweet tweet){
        return newInstance(context, tweet, false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose, container);
        ButterKnife.bind(this, view);

        GlideApp.with(context)
                .load(TwitterClient.USER_IMG)
                .circleCrop()
                .into(ivUser);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Toolbar toolbar = view.findViewById(R.id.tbCompose);

        if(!this.replying) {
            tvReply.setVisibility(View.GONE);
        }
        else {
            etTweetBody.setText("@"+tweet.getUser().getScreenName());
            tvReply.setText("Replying to: @" + tweet.getUser().getScreenName());
        }

        // Setting the controller
        pbController = new ProgressBarController(pbRemainingChars);
        pbController.restartProgress();


        btnTweet.setOnClickListener(this);

        etTweetBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(pbController.verifyStatus(context, s.length())){
                    String remaining = (240-s.length())+"";
                    tvRemainingChars.setText(remaining);
                }
                else{
                    tvRemainingChars.setText("");
                }
                pbController.updateSize(s.length());
            }

        });
    }

    @Override
    public void onClick(View v) {
        if(pbController.isOk()){
            if(replying){
                new TwitterClient(context).replyTweet(etTweetBody.getText().toString(), tweet.getUid(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            Tweet tweet = Tweet.fromJSON(response);
                            ((TimelineActivity) getActivity()).addTweet(tweet);
                            dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        try {
                            Toast.makeText(context,
                                    errorResponse.getJSONArray("errors").getJSONObject(0).getString("message"),
                                    Toast.LENGTH_LONG).show();
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
            else {
                new TwitterClient(context).sendTweet(etTweetBody.getText().toString(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            Tweet tweet = Tweet.fromJSON(response);
                            ((TimelineActivity) getActivity()).addTweet(tweet);
                            dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        try {
                            Toast.makeText(context,
                                    errorResponse.getJSONArray("errors").getJSONObject(0).getString("message"),
                                    Toast.LENGTH_LONG).show();
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        }
    }
}
