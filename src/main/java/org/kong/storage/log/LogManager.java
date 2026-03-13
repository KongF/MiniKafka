package org.kong.storage.log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LogManager {
    private final Map<String, Log> logs = new ConcurrentHashMap<>();

    public Log getOrCreateLog(String topic, int partition) {
        String key = topic + "-" + partition;
        return logs.computeIfAbsent(key, k -> new Log(topic, partition)
        );
    }
}
