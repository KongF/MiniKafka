package org.kong;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TopicManager {

    private final Map<String, Topic> topics = new ConcurrentHashMap<>();

    public Topic createTopic(String name, int partitionCount) throws Exception {

        Topic topic = new Topic(name, partitionCount);

        topics.put(name, topic);

        return topic;
    }

    public Topic getTopic(String name) {
        return topics.get(name);
    }

}
