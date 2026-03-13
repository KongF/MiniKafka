package org.kong.storage.log;

import org.kong.storage.LogSegment;

import java.util.ArrayList;
import java.util.List;

public class Log {

    private final String topic;

    private final int partition;

    private final List<LogSegment> segments = new ArrayList<>();

    private LogSegment activeSegment;

    public Log(String topic, int partition) {
        this.topic = topic;
        this.partition = partition;
        this.activeSegment = new LogSegment(0);

        segments.add(activeSegment);
    }

    public synchronized long append(byte[] message) {

        long offset = activeSegment.nextOffset();
        activeSegment.append(message);
        if (activeSegment.isFull()) {
            rollSegment();
        }

        return offset;
    }

    private void rollSegment() {
        long baseOffset = activeSegment.nextOffset();
        activeSegment = new LogSegment(baseOffset);
        segments.add(activeSegment);
    }

}
