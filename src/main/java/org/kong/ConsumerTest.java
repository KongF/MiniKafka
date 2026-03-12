package org.kong;

import org.kong.client.ConsumerClient;

public class ConsumerTest {

    public static void main(String[] args) throws Exception {

        ConsumerClient consumer =
                new ConsumerClient(
                        "localhost",
                        9092,
                        "order",
                        1
                );

        while (true) {
            consumer.poll();
            Thread.sleep(1000);

        }
    }
}
