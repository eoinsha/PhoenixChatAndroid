package org.phoenixframework.channels.sample.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
class ReceivedMessage {
    private String userId;
    private String body;
    private Date insertedDate;
    private Date updatedDate;
    private boolean fromMe = false;

    @SuppressWarnings("unused")
    public ReceivedMessage() {
    }

    @JsonProperty("user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(final String body) {
        this.body = body;
    }

    @JsonProperty("inserted_at")
    public Date getInsertedDate() {
        return insertedDate;
    }

    public void setInsertedDate(final Date insertedDate) {
        this.insertedDate = insertedDate;
    }

    @JsonProperty("updated_at")
    @SuppressWarnings("unused")
    public Date getUpdatedDate() {
        return updatedDate;
    }

    @SuppressWarnings("unused")
    public void setUpdatedDate(final Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    void setFromMe(final boolean fromMe) {
        this.fromMe = fromMe;
    }

    boolean isFromMe() {
        return fromMe;
    }

    @Override
    public String toString() {
        return "ReceivedMessage{" +
                "userId='" + userId + '\'' +
                ", body='" + body + '\'' +
                ", insertedDate=" + insertedDate +
                ", updatedDate=" + updatedDate +
                ", fromMe=" + fromMe +
                '}';
    }
}
