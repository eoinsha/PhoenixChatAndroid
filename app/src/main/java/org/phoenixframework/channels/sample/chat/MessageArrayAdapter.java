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
        final ReceivedMessage message = getItem(position);

        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(message.isFromMe() ? R.layout.my_message_layout : R.layout.message_layout, parent, false);
        textView = (TextView) row.findViewById(R.id.singleMessageText);
        textView.setText(message.getBody());
        msgDateLabel = (TextView) row.findViewById(R.id.messageDate);
        if(message.getInsertedDate() != null) {
            msgDateLabel.setVisibility(View.VISIBLE);
            msgDateLabel.setText(DateUtils.getRelativeTimeSpanString(message.getInsertedDate().getTime()));
        }
        else {
            msgDateLabel.setVisibility(View.INVISIBLE);
        }
        return row;
    }
}