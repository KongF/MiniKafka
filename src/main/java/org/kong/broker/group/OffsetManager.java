package org.kong.broker.group;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 偏移量管理器类，用于管理消费者组的消费偏移量
 * 支持提交和获取指定主题分区的消费进度
 */
public class OffsetManager {

    /** 偏移量存储映射，使用线程安全的 ConcurrentHashMap
     * key 格式：groupId-topic-partition
     * value: 已提交的偏移量
     */
    private final Map<String, Long> offsets = new ConcurrentHashMap<>();

    /**
     * 提交消费者的消费偏移量
     * @param groupId 消费者组 ID
     * @param topic 主题名称
     * @param partition 分区 ID
     * @param offset 要提交的消费偏移量
     */
    public void commit(String groupId,
                       String topic,
                       int partition,
                       long offset) {
        // 构建唯一键并存储偏移量
        String key = groupId + "-" + topic + "-" + partition;
        offsets.put(key, offset);
    }

    /**
     * 获取消费者组的消费偏移量
     * @param groupId 消费者组 ID
     * @param topic 主题名称
     * @param partition 分区 ID
     * @return 已提交的偏移量，如果未找到则返回 0
     */
    public long getOffset(String groupId,
                          String topic,
                          int partition) {

        // 构建唯一键并查找偏移量
        String key = groupId + "-" + topic + "-" + partition;

        return offsets.getOrDefault(key, 0L);
    }
}
