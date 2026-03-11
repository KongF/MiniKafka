package org.kong;

public class Producer {

    private final TopicManager topicManager;

    public Producer(TopicManager topicManager) {
        this.topicManager = topicManager;
    }

    public void send(String topicName, String key, String message) throws Exception {
        Topic topic = topicManager.getTopic(topicName);

        int partitionIndex = Math.abs(key.hashCode()) % topic.getPartitions().size();

        Partition partition = topic.getPartitions().get(partitionIndex);

        long offset = partition.append(message.getBytes());

        System.out.println("message append offset=" + offset);
    }

}
