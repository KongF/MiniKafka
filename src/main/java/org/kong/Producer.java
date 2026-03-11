package org.kong;

public class Producer {

    private final MessageQueue queue;

    public Producer(MessageQueue queue) {
        this.queue = queue;
    }

    public void send(String topic, String msg) {
        queue.send(new Message(topic, msg.getBytes()));
    }

}
