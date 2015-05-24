package org.phoenixframework.channels.sample.chat;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MessageArrayAdapter extends ArrayAdapter<ReceivedMessage> {
    public MessageArrayAdapter(Context context, int resource) {
        super(context, resource);
    }

    private LinearLayout messageContainer;
    private TextView textView;
    private TextView msgDateLabel;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.message_layout, parent, false);
        }
        messageContainer = (LinearLayout) row.findViewById(R.id.messageContainer);
        final ReceivedMessage message = getItem(position);
        textView = (TextView) row.findViewById(R.id.singleMessageText);
        textView.setText(message.getText());
        msgDateLabel = (TextView) row.findViewById(R.id.messageDate);
        if(message.getMsgDate() != null) {
            msgDateLabel.setVisibility(View.VISIBLE);
            msgDateLabel.setText(DateUtils.getRelativeTimeSpanString(message.getMsgDate().getTime()));
        }
        else {
            msgDateLabel.setVisibility(View.INVISIBLE);
        }

        messageContainer.setGravity(message.getText().startsWith("ME ") ? Gravity.RIGHT : Gravity.LEFT);
        return row;
    }
}