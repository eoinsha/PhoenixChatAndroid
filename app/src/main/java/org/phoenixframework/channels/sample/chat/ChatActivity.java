package org.phoenixframework.channels.sample.chat;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.phoenixframework.channels.Channel;
import org.phoenixframework.channels.Envelope;
import org.phoenixframework.channels.IErrorCallback;
import org.phoenixframework.channels.IMessageCallback;
import org.phoenixframework.channels.ISocketCloseCallback;
import org.phoenixframework.channels.ISocketOpenCallback;
import org.phoenixframework.channels.Payload;
import org.phoenixframework.channels.Socket;
import org.phoenixframework.channels.sample.chat.org.phoenixframework.channels.sample.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = ChatActivity.class.getSimpleName();

    private Button btnSend;
    private EditText messageField;
    private ListView messagesListView;
    private MessageArrayAdapter listAdapter;
    private Socket socket;
    private Channel channel;

    private List<Envelope> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);

        btnSend = (Button) findViewById(R.id.button_send);
        btnSend.setEnabled(false);
        messageField = (EditText) findViewById(R.id.message_text);
        messagesListView = (ListView) findViewById(R.id.messages_list_view);
        messagesListView.setDivider(null);
        messagesListView.setDividerHeight(0);
        listAdapter = new MessageArrayAdapter(this, android.R.layout.simple_list_item_1);
        messagesListView.setAdapter(listAdapter);

        Utils utils = new Utils(getApplicationContext());
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
                        channel.join().receive("ok", new IMessageCallback() {
                            @Override
                            public void onMessage(final Envelope envelope) {
                                addToList("You have joined '" + topic + "'");
                            }
                        });
                        channel.on("message_feed", new IMessageCallback() {
                            @Override
                            public void onMessage(final Envelope envelope) {
                                final List<Map> messages = (List<Map>) envelope.getPayload().get("messages");
                                Log.i(TAG, "MESSAGES: " + messages);
                                if(messages != null) {
                                    for (final Map< String, Object> message : messages) {
                                        addToList((String)message.get("body"));
                                    }
                                }
                            }
                        }).on("new_msg", new IMessageCallback() {
                            @Override
                            public void onMessage(final Envelope envelope) {
                                final String messageText = (String) envelope.getPayload().get("body");
                                Log.i(TAG, "MESSAGES: " + messageText);
                                addToList(messageText);
                                notifyMessageReceived();
                            }
                        });
                    } catch (Exception e) {
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnSend.setEnabled(true);
                        }
                    });
                }
            })
                    .onClose(new ISocketCloseCallback() {
                        @Override
                        public void onClose() {
                            showToast("Closed");
                        }
                    })
                    .onError(new IErrorCallback() {
                        @Override
                        public void onError(final String reason) {
                            handleTerminalError(reason);
                        }
                    })
                    .connect();

        } catch (Exception e) {
            Log.e(TAG, "Failed to connect", e);
            handleTerminalError(e);
        }
    }

    private void sendMessage() {
        final String message = messageField.getText().toString();
        if (channel != null && channel.canPush()) {
            final Payload payload = new Payload();
            payload.set("body", message);
            try {
                channel.push("new_msg", payload)
                        .receive("ok", new IMessageCallback() {
                            @Override
                            public void onMessage(Envelope envelope) {
                                final String body = (String) envelope.getPayload().getResponse().get("body");
                                Log.i(TAG, "MESSAGE[ME]: " + body);
                                addToList("ME " + body);
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
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleTerminalError(final Throwable t) {
        handleTerminalError(t.toString());
    }

    private void handleTerminalError(final String s) {
        addToList(s);
        showToast(s);
    }

    private void addToList(final String s) {
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              listAdapter.add(s);
                          }
                      }
        );
    }
}
