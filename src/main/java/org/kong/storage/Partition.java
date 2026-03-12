package org.kong.storage;

import java.io.File;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * 分区类，用于管理特定主题分区的日志存储
 * 每个分区包含多个日志段，支持自动滚动创建新段
 */
public class Partition {

    /** 分区 ID */
    private final int partitionId;
    
    /** 日志段最大大小（10MB），超过此值将滚动创建新段 */
    private static final long SEGMENT_MAX_SIZE = 10 *  1024;

    /** 分区数据存储目录路径 */
    private final String dir;
    
    // private final LogStorage logStorage;
    
    /** 日志段列表，包含该分区的所有日志段 */
    private final List<LogSegment> segments = new ArrayList<>();

    /** 当前活跃的日志段，用于写入操作 */
    private LogSegment activeSegment;

    /**
     * 构造分区对象
     * @param topic 主题名称
     * @param partitionId 分区 ID
     * @throws Exception 目录创建或日志段初始化时的异常
     */
    public Partition(String topic, int partitionId) throws Exception {
        this.partitionId = partitionId;
        this.dir = "data/" + topic + "-" + partitionId;
    
        File d = new File(dir);
            
    
        // 确保分区目录存在，如不存在则创建
        if (!d.exists()) {
            File parent = d.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            d.mkdirs();
        }
        // 初始化第一个日志段
        rollSegment();
    }

    /**
     * 向分区追加消息数据
     * 如果当前活跃段达到大小限制，会自动滚动创建新段
     * @param message 要追加的消息字节数组
     * @return 消息的偏移量
     * @throws Exception 消息写入时的异常
     */
    public synchronized long append(byte[] message) throws Exception {

        // 检查当前段是否已达到大小限制
        if (activeSegment.size() >= SEGMENT_MAX_SIZE) {
            rollSegment();
        }

        // 将消息追加到活跃段
        return activeSegment.append(message);
    }

    /**
     * 滚动创建新的日志段
     * 新段的基础偏移量为前一段基础偏移量加上段大小
     * @throws Exception 日志段创建时的异常
     */
    private void rollSegment() throws Exception {

        long baseOffset = 0;

        // 计算新段的基础偏移量
        if (activeSegment != null) {
            baseOffset = activeSegment.getBaseOffset() + activeSegment.size();
        }

        // 创建新的日志段并设置为活跃段
        LogSegment newSegment = new LogSegment(dir, baseOffset);

        segments.add(newSegment);

        activeSegment = newSegment;

        System.out.println("new segment: " + baseOffset);
    }

    /**
     * 获取分区 ID
     * @return 分区 ID
     */
    public int getPartitionId() {
        return partitionId;
    }

    public FetchResult fetch(long offset) throws Exception {

        for (LogSegment segment : segments) {
            long base = segment.getBaseOffset();
            long end = base + segment.size();

            if (offset >= base && offset < end) {
                int position = segment.positionForOffset(offset);
                segment.getFileChannel().position(position);
                int length = segment.getFileChannel()
                        .map(FileChannel.MapMode.READ_ONLY, position, 4)
                        .getInt();

                return new FetchResult(segment.getFileChannel(), position + 4, length);
            }

        }

        return null;
    }
}

