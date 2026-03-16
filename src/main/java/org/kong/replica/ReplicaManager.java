package org.kong.replica;

import org.kong.storage.PartitionState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReplicaManager {

    private final Map<String, PartitionState> partitions = new ConcurrentHashMap<>();

    public PartitionState getPartition(String topic, int partition) {
        String key = topic + "-" + partition;
        return partitions.get(key);
    }

    public void createPartition(String topic, int partition, int leaderId) {
        String key = topic + "-" + partition;
        partitions.put(key, new PartitionState(topic, partition, leaderId));
    }

}