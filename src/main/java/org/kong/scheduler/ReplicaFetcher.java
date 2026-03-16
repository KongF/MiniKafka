package org.kong.scheduler;

import org.kong.storage.PartitionState;

public class ReplicaFetcher implements Runnable {

    private final int followerId;

    private final PartitionState partition;

    public ReplicaFetcher(int followerId, PartitionState partition) {
        this.followerId = followerId;
        this.partition = partition;
    }

    @Override
    public void run() {
        while (true) {
            try {
                fetchFromLeader();
                Thread.sleep(500);
            } catch (Exception e) {
                System.out.println("ReplicaFetcher error");
                throw new RuntimeException(e);
            }

        }

    }

    private void fetchFromLeader() {
        int leader = partition.getLeader();
        // 向 leader 发送 fetch 请求

        // 更新 follower offset

    }
}