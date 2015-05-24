package org.phoenixframework.channels.sample.chat;

import java.util.Date;

class ReceivedMessage {
    private String text;
    private Date msgDate;
    private boolean fromMe = false;

    ReceivedMessage(final String text, final Date msgDate) {
        this.text = text;
        this.msgDate = msgDate;
    }

    String getText() {
        return text;
    }

    Date getMsgDate() {
        return msgDate;
    }

    void setFromMe(boolean fromMe) {
        this.fromMe = fromMe;
    }

    boolean isFromMe() {
        return fromMe;
    }

    @Override
    public String toString() {
        return "ReceivedMessage{" +
                "text='" + text + '\'' +
                ", msgDate=" + msgDate +
                ", fromMe=" + fromMe +
                '}';
    }
}
