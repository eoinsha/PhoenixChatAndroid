package org.phoenixframework.channels.sample.chat;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.phoenixframework.channels.Channel;
import org.phoenixframework.channels.Envelope;
import org.phoenixframework.channels.IMessageCallback;
import org.phoenixframework.channels.ISocketCloseCallback;
import org.phoenixframework.channels.ISocketOpenCallback;
import org.phoenixframework.channels.Payload;
import org.phoenixframework.channels.Socket;
import org.phoenixframework.channels.sample.chat.org.phoenixframework.channels.sample.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.DeploymentException;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = ChatActivity.class.getSimpleName();

    private Utils utils;
    private Button btnSend;
    private EditText messageField;
    private ListView messagesListView;

    private Socket socket;
    private Channel channel;

    private List<Envelope> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        btnSend = (Button) findViewById(R.id.button_send);
        btnSend.setEnabled(false);
        messageField = (EditText) findViewById(R.id.message_text);
        messagesListView = (ListView) findViewById(R.id.messages_list_view);

        utils = new Utils(getApplicationContext());
        final String url = utils.getUrl();
        final String topic = utils.getTopic();

        try {
            socket = new Socket(url);

            socket.onOpen(new ISocketOpenCallback() {
                @Override
                public void onOpen() {
                    showToast("Connected");
                    channel = socket.chan(topic, null);

                    try {
                        channel.join().receive("messages", new IMessageCallback() {
                            @Override
                            public void onMessage(Envelope envelope) {
                                Log.i(TAG, "MESSAGES: " + envelope.getPayload().get("body"));
                            }
                        });

                        channel.join().receive("new_msg", new IMessageCallback() {
                            @Override
                            public void onMessage(Envelope envelope) {
                                Log.i(TAG, "MESSAGE: " + envelope.getPayload().get("body"));
                            }
                        });
                    }
                    catch(Exception e) {
                        Log.e(TAG, "Failed to join channel " + topic, e);
                        handleTerminalError(e);
                    }
                    btnSend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendMessage();
                            messageField.setText("");
                        }
                    });
                    btnSend.setEnabled(true);
                }
            });
            socket.onClose(new ISocketCloseCallback() {
                @Override
                public void onClose() {
                    showToast("Closed");
                }
            });

        }
        catch (Exception e) {
            Log.e(TAG, "Failed to connect", e);
            handleTerminalError(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendMessage() {
        final String message = messageField.getText().toString();
        if(channel != null && channel.canPush()) {
            final Payload payload = new Payload();
            payload.set("body", message);
            try {
                channel.push("new_msg", payload)
                        .receive("ok", new IMessageCallback() {
                            @Override
                            public void onMessage(Envelope envelope) {
                                Log.i(TAG, "MESSAGE[ME]: " + envelope.getPayload().get("body"));
                            }
                        })
                        .after(500, new Runnable() {
                            @Override
                            public void run() {
                                Log.w(TAG, "MESSAGE timed out");
                            }
                        });
            } catch (IOException e) {
                Log.e(TAG, "Failed to send", e);
                showToast("Failed to send");
            }
        }
    }

    private void showToast(final String toastText) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void notifyMessageReceived() {
        try {
            Uri notification = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleTerminalError(final Throwable t) {
        showToast(t.getLocalizedMessage());
    }
}
