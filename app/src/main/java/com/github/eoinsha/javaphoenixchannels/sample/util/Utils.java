package com.github.eoinsha.javaphoenixchannels.sample.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.github.eoinsha.javaphoenixchannels.sample.chat.R;

public class Utils {

    private Context context;
    private SharedPreferences sharedPref;

    private static final String KEY_SHARED_PREF = "PHOENIX_CHAT_SAMPLE";
    private static final int KEY_MODE_PRIVATE = 0;
    private static final String KEY_TOPIC = "topic";

    public Utils(Context context) {
        this.context = context;
        sharedPref = this.context.getSharedPreferences(KEY_SHARED_PREF, KEY_MODE_PRIVATE);
    }

    public void storeChannelDetails(final String topic) {
        Editor editor = sharedPref.edit();
        editor.putString(KEY_TOPIC, topic);
        editor.commit();
    }

    public String getTopic() {
        final String prevTopic = sharedPref.getString(KEY_TOPIC, null);
        return prevTopic != null ? prevTopic : context.getText(R.string.default_topic).toString();
    }

    public String getUrl() {
        return context.getText(R.string.default_url).toString();
    }
}