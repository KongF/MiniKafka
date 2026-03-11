package org.kong;

public class Message {

    private final String topic;
    private final byte[] body;

    public Message(String topic, byte[] body) {
        this.topic = topic;
        this.body = body;
    }

    public String getTopic() {
        return topic;
    }

    public byte[] getBody() {
        return body;
    }
}
