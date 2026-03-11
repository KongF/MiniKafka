package org.kong;

public class Consumer {

    private final MessageQueue queue;

    public Consumer(MessageQueue queue) {
        this.queue = queue;
    }

    public void start() {
        new Thread(() -> {
            while (true) {
                try {
                    Message msg = queue.poll();
                    System.out.println(new String(msg.getBody()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
