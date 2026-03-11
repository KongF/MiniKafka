package org.kong;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageQueue {

    private final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();

    public void send(Message message) {
        queue.add(message);
    }

    public Message poll() throws InterruptedException {
        return queue.take();
    }

}
