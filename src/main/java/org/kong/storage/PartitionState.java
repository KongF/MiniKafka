package org.kong.storage;

import java.util.HashSet;
import java.util.Set;

public class PartitionState {

    private final String topic;
    private final int partition;
    private int leaderId;
    private final Set<Integer> replicas = new HashSet<>();
    private final Set<Integer> isr = new HashSet<>();
    private long highWatermark = 0;

    public PartitionState(String topic, int partition, int leaderId) {
        this.topic = topic;
        this.partition = partition;
        this.leaderId = leaderId;

        replicas.add(leaderId);
        isr.add(leaderId);
    }

    public boolean isLeader(int brokerId) {
        return leaderId == brokerId;
    }

    public void addReplica(int brokerId) {
        replicas.add(brokerId);
    }

    public void addToIsr(int brokerId) {
        isr.add(brokerId);
    }

    public void removeFromIsr(int brokerId) {
        isr.remove(brokerId);
    }

    public Set<Integer> getIsr() {
        return isr;
    }

    public int getLeader() {
        return leaderId;
    }

    public void setLeader(int leaderId) {
        this.leaderId = leaderId;
    }

}
