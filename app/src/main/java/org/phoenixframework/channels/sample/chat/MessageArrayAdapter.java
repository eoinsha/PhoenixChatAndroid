package org.phoenixframework.channels.sample.chat;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MessageArrayAdapter extends ArrayAdapter<String> {
    public MessageArrayAdapter(Context context, int resource) {
        super(context, resource);
    }

    private LinearLayout messageContainer;
    private TextView textView;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.message_layout, parent, false);
        }
        messageContainer = (LinearLayout) row.findViewById(R.id.messageContainer);
        final String messageText = getItem(position);
        textView = (TextView) row.findViewById(R.id.singleMessage);
        textView.setText(messageText);
//        textView.setBackgroundResource(chatMessageObj.left ? R.drawable.bubble_a : R.drawable.bubble_b);
        messageContainer.setGravity(messageText.startsWith("ME ") ? Gravity.RIGHT : Gravity.LEFT);
        return row;
    }
}
