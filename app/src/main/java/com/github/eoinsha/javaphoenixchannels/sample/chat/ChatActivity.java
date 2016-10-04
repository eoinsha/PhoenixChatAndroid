package com.github.eoinsha.javaphoenixchannels.sample.chat;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.phoenixframework.channels.Channel;
import org.phoenixframework.channels.Envelope;
import org.phoenixframework.channels.IErrorCallback;
import org.phoenixframework.channels.IMessageCallback;
import org.phoenixframework.channels.ISocketCloseCallback;
import org.phoenixframework.channels.ISocketOpenCallback;
import org.phoenixframework.channels.ITimeoutCallback;
import org.phoenixframework.channels.Socket;

import com.github.eoinsha.javaphoenixchannels.sample.util.Utils;

import java.io.IOException;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = ChatActivity.class.getSimpleName();

    private Button btnSend;
    private EditText messageField;
    private MessageArrayAdapter listAdapter;
    private Socket socket;
    private Channel channel;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);

        btnSend = (Button) findViewById(R.id.button_send);
        btnSend.setEnabled(false);
        messageField = (EditText) findViewById(R.id.message_text);
        final ListView messagesListView = (ListView) findViewById(R.id.messages_list_view);
        messagesListView.setDivider(null);
        messagesListView.setDividerHeight(0);
        listAdapter = new MessageArrayAdapter(this, android.R.layout.simple_list_item_1);
        messagesListView.setAdapter(listAdapter);

        final Utils utils = new Utils(getApplicationContext());
        final String url = utils.getUrl();
        final String topic = utils.getTopic();

        try {
            socket = new Socket(url.toString());

            socket.onOpen(new ISocketOpenCallback() {
                @Override
                public void onOpen() {
                    showToast("Connected");
                    channel = socket.chan(topic, null);

                    try {
                        channel.join().receive("ok", new IMessageCallback() {
                            @Override
                            public void onMessage(final Envelope envelope) {
                                showToast("You have joined '" + topic + "'");
                            }
                        });
                        channel.on("user:entered", new IMessageCallback() {
                            @Override
                            public void onMessage(final Envelope envelope) {
                                final JsonNode user = envelope.getPayload().get("user");
                                if (user == null || user instanceof NullNode) {
                                    showToast("An anonymous user entered");
                                }
                                else {
                                    showToast("User '" + user.toString() + "' entered");
                                }
                            }
                        }).on("new:msg", new IMessageCallback() {
                            @Override
                            public void onMessage(final Envelope envelope) {
                                final ChatMessage message;
                                try {
                                    message = objectMapper.treeToValue(envelope.getPayload(), ChatMessage.class);
                                    Log.i(TAG, "MESSAGE: " + message);
                                    if (message.getUserId() != null && !message.getUserId().equals("SYSTEM")) {
                                        addToList(message);
                                        notifyMessageReceived();
                                    }
                                } catch (JsonProcessingException e) {
                                    Log.e(TAG, "Unable to parse message", e);
                                }
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
        final String messageBody = messageField.getText().toString().trim();
        if (channel != null) {
            final ObjectNode payload = objectMapper.createObjectNode();
            payload.put("body", messageBody);
            try {
                final Date pushDate = new Date();
                channel.push("new:msg", payload)
                        .receive("ok", new IMessageCallback() {
                            @Override
                            public void onMessage(final Envelope envelope) {
                                final ChatMessage message = new ChatMessage();
                                message.setBody(messageBody);
                                message.setInsertedDate(pushDate);
                                message.setFromMe(true);
                                Log.i(TAG, "MESSAGE: " + message);
                                addToList(message);
                            }
                        })
                        .timeout(new ITimeoutCallback() {
                            @Override
                            public void onTimeout() {
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
        showToast(s);
    }

    private void addToList(final ChatMessage message) {
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              listAdapter.add(message);
                          }
                      }
        );
    }
}
