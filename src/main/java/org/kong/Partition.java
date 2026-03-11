package org.kong;


public class Partition {

    private final int partitionId;

    private final LogStorage logStorage;

    public Partition(int partitionId, String topic) throws Exception {
        this.partitionId = partitionId;
        String path = "data/" + topic + "-" + partitionId + ".log";
        this.logStorage = new LogStorage(path);
    }

    public int getPartitionId() {
        return partitionId;
    }

    public long append(byte[] message) throws Exception {
        return logStorage.append(message);
    }

}
