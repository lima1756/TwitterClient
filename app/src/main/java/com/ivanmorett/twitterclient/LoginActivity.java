package com.ivanmorett.twitterclient;

import android.content.Intent;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ivanmorett.twitterclient.R;
import com.ivanmorett.twitterclient.models.SampleModel;
import com.ivanmorett.twitterclient.models.SampleModelDao;
import com.codepath.oauth.OAuthLoginActionBarActivity;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        final SampleModel sampleModel = new SampleModel();
        sampleModel.setName("CodePath");

        final SampleModelDao sampleModelDao = ((TwitterApp) getApplicationContext()).getMyDatabase().sampleModelDao();

        AsyncTask<SampleModel, Void, Void> task = new AsyncTask<SampleModel, Void, Void>() {
            @Override
            protected Void doInBackground(SampleModel... sampleModels) {
                sampleModelDao.insertModel(sampleModels);
                return null;
            };
        };
        task.execute(sampleModel);
        Button btn = findViewById(R.id.btnLogin);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getClient().connect();
            }
        });
    }


    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    // OAuth authenticated successfully, launch primary authenticated activity
    // i.e Display application "homepage"
    @Override
    public void onLoginSuccess() {
         Intent i = new Intent(this, TimelineActivity.class);
         startActivity(i);
         this.finish();
    }

    // OAuth authentication flow failed, handle the error
    // i.e Display an error dialog or toast
    @Override
    public void onLoginFailure(Exception e) {
        e.printStackTrace();
    }

    // Click handler method for the button used to start OAuth flow
    // Uses the client to initiate OAuth authorization
    // This should be tied to a button used to login

}
