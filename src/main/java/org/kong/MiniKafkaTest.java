package org.kong;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class MiniKafkaTest {
    public static void main(String[] args) throws Exception {

        TopicManager topicManager = new TopicManager();

        Topic topic = topicManager.createTopic("order", 1);

        Producer producer = new Producer(topicManager);

        producer.send("order", "u1", "{\"name\":\"张三\"}");

        producer.send("order", "u1", "order2");

        producer.send("order", "u1", "order3");

        Consumer consumer = new Consumer("data/order-0.log");

        consumer.poll();

    }
}