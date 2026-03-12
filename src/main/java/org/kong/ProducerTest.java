package org.kong;

import org.kong.client.ProducerClient;

public class ProducerTest {

    public static void main(String[] args) throws Exception {
        ProducerClient producer = new ProducerClient("localhost", 9092);
        for (int i = 0; i < 10000; i++) {
            producer.send("order", "user1", "order-" + i);
            producer.send("order", "user2", "order-" + i);
            producer.send("customer", "byd", "cus-" + i);
        }
    }
}
