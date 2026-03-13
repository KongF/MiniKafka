package org.kong.broker.group;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GroupCoordinator {

    private final Map<String, ConsumerGroup> groups = new ConcurrentHashMap<>();

    public ConsumerGroup joinGroup(String groupId, String consumerId) {

        ConsumerGroup group = groups.computeIfAbsent(groupId, ConsumerGroup::new);

        group.addMember(consumerId);

        return group;
    }
    public void checkTimeout(long timeout) {

        long now = System.currentTimeMillis();

        for (ConsumerGroup group : groups.values()) {
            Iterator<String> it = group.members().iterator();

            while (it.hasNext()) {
                String memberId = it.next();
                GroupMember member = group.getMembers().get(memberId);

                if (now - member.getLastHeartbeat() > timeout) {
                    it.remove();
                    group.removeMember(memberId);
                }

            }

        }

    }
    public ConsumerGroup getGroup(String groupId) {
        return groups.get(groupId);
    }
}