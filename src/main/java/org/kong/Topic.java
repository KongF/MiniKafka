package org.kong;

import org.kong.storage.Partition;

import java.util.ArrayList;
import java.util.List;

public class Topic {

    private final String name;

    private final List<Partition> partitions;

    public Topic(String name, int partitionCount) throws Exception {

        this.name = name;

        this.partitions = new ArrayList<>();

        for (int i = 0; i < partitionCount; i++) {
            partitions.add(new Partition(name,i));
        }
    }

    public String getName() {
        return name;
    }

    public List<Partition> getPartitions() {
        return partitions;
    }

}
