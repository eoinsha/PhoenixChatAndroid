package org.phoenixframework.channels.sample.chat.org.phoenixframework.channels.sample.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Utils {

    private Context context;
    private SharedPreferences sharedPref;

    private static final String KEY_SHARED_PREF = "PHOENIX_CHAT_SAMPLE";
    private static final int KEY_MODE_PRIVATE = 0;
    private static final String KEY_TOPIC = "topic";
    private static final String KEY_URL = "url";

    public Utils(Context context) {
        this.context = context;
        sharedPref = this.context.getSharedPreferences(KEY_SHARED_PREF, KEY_MODE_PRIVATE);
    }

    public void storeChannelDetails(final String url, final String topic) {
        Editor editor = sharedPref.edit();
        editor.putString(KEY_URL, url);
        editor.putString(KEY_TOPIC, topic);
        editor.commit();
    }

    public String getTopic() {
        return sharedPref.getString(KEY_TOPIC, null);
    }

    public String getUrl() {
        return sharedPref.getString(KEY_URL, null);
    }
}