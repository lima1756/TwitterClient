package com.ivanmorett.twitterclient;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ComposeActivity extends AppCompatActivity {

    @BindView(R.id.btnTweet) Button btnTweet;
    @BindView(R.id.pbRemainingChars) ProgressBar pbRemainingChars;
    @BindView(R.id.etTweetBody) EditText etTweetBody;
    @BindView(R.id.tvRemainingChars) TextView tvRemainingChars;

    ProgressBarController pbController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.tbCompose);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Setting the controller
        pbController = new ProgressBarController(pbRemainingChars);
        pbController.restartProgress();


        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pbController.isOk()){
                    // TODO send tweet
                }
            }
        });

        etTweetBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(pbController.verifyStatus(getApplicationContext(), s.length())){
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.compose_menu, menu);
        return true;
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
