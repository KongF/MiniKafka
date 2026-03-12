package org.kong.broker.group;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GroupCoordinator {

    private final Map<String, ConsumerGroup> groups = new ConcurrentHashMap<>();

    public ConsumerGroup joinGroup(String groupId, String consumerId) {

        ConsumerGroup group = groups.computeIfAbsent(groupId, ConsumerGroup::new);

        group.addMember(consumerId);

        return group;
    }

    public ConsumerGroup getGroup(String groupId) {
        return groups.get(groupId);
    }
}