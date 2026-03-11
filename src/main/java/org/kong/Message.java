package org.kong;

public class Message {
    private final long offset;
    private final String topic;
    private final byte[] body;

    public Message(long offset,String topic, byte[] body) {
        this.offset = offset;
        this.topic = topic;
        this.body = body;
    }
    public long getOffset() {
        return offset;
    }

    public String getTopic() {
        return topic;
    }

    public byte[] getBody() {
        return body;
    }
}
