package org.kong.storage.log;

import org.kong.storage.LogSegment;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LogManager {
    private final Map<String, Log> logs = new ConcurrentHashMap<>();

    public Log getOrCreateLog(String topic, int partition) {
        String key = topic + "-" + partition;
        return logs.computeIfAbsent(key, k -> {
                    try {
                        return new Log(topic, partition);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }
    public void flush() {
        for (LogSegment segment : segments) {
            segment.flush();
        }
    }

}
