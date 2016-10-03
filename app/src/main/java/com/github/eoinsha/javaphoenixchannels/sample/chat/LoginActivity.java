package com.github.eoinsha.javaphoenixchannels.sample.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.eoinsha.javaphoenixchannels.sample.util.Utils;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity  {

    // UI references.
    private TextView mUrlView;
    private EditText mTopicView;

    private Utils utils = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        utils = new Utils(getApplicationContext());

        // Set up the login form.
        mUrlView = (TextView) findViewById(R.id.url);
        mUrlView.setText(utils.getUrl());

        mTopicView = (EditText) findViewById(R.id.topic);
        mTopicView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    startChat();
                    return true;
                }
                return false;
            }
        });
        mTopicView.setText(utils.getTopic());

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startChat();
            }
        });

        signInButton.requestFocus();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void startChat() {
        // Reset errors.
        mUrlView.setError(null);
        mTopicView.setError(null);

        // Store values at the time of the login attempt.
        String url = mUrlView.getText().toString();
        String topic = mTopicView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(url)) {
            mUrlView.setError(getString(R.string.error_field_required));
            focusView = mUrlView;
            cancel = true;
        } else if (!isUrlValid(url)) {
            mUrlView.setError(getString(R.string.error_invalid_url));
            focusView = mUrlView;
            cancel = true;
        }
        // Check for a valid email address.
        else if (!isRoomValid(topic)) {
            mTopicView.setError(getString(R.string.error_invalid_topic));
            focusView = mTopicView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            utils.storeChannelDetails(topic);
            final Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
        }
    }

    private boolean isUrlValid(final String uri) {
        try {
            new URL(uri);
            return true;
        }
        catch(MalformedURLException e){
            return false;
        }
    }

    private boolean isRoomValid(final String topic) {
        return topic.length() > 1;
    }
}
