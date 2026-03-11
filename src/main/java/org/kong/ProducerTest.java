package org.kong;

import org.kong.client.ProducerClient;

public class ProducerTest {

    public static void main(String[] args) throws Exception {
        ProducerClient producer = new ProducerClient("localhost", 9092);
        for (int i = 0; i < 10; i++) {
            producer.send("order", "user1", "order-" + i);
        }
    }
}
