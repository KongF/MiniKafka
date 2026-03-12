package org.kong.storage;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * 日志段类，用于管理单个日志文件的存储操作
 * 每个日志段由基础偏移量标识，支持追加写入数据
 */
public class LogSegment {


    /** 基础偏移量，用于计算消息的全局偏移量 */
    private final long baseOffset;

    /** 随机访问文件对象，用于实际的读写操作 */
    private final RandomAccessFile raf;

    /** 偏移量索引对象，用于维护消息偏移量到物理位置的映射 */
    private final OffsetIndex index;

    /** 当前写入位置，从文件开头计算的字节偏移量 */
    private long writePosition = 0;

    /** 消息计数器，用于控制索引条目的创建频率 */
    private int messageCount = 0;

    /** 索引间隔，每 100 条消息创建一个索引条目 */
    private static final int INDEX_INTERVAL = 100;

    /**
     * 构造日志段对象
     * @param dir 日志文件存储目录
     * @param baseOffset 基础偏移量，用于命名文件和计算消息偏移量
     * @throws Exception 文件创建或打开时的异常
     */
    public LogSegment(String dir, long baseOffset) throws Exception {

        this.baseOffset = baseOffset;
        File logFile = new File(dir, baseOffset + ".log");
        if (!logFile.exists()) {
            logFile.createNewFile();
        }
        this.raf = new RandomAccessFile(logFile, "rw");

        this.writePosition = raf.length();
        this.index = new OffsetIndex(dir, baseOffset);

    }

    /**
     * 向日志段追加数据
     * 数据格式：4 字节长度 + 实际数据内容
     * @param data 要写入的数据字节数组
     * @return 消息的全局偏移量（baseOffset + 写入位置）
     * @throws Exception 文件写入时的异常
     */
    public synchronized long append(byte[] data) throws Exception {

        // 计算消息的全局偏移量
        long offset = baseOffset + writePosition;

        // 定位到当前写入位置
        raf.seek(writePosition);

        // 先写入数据长度（4 字节），再写入实际数据
        raf.writeInt(data.length);

        raf.write(data);
        if(messageCount % INDEX_INTERVAL == 0){
            index.append((int)offset, (int)writePosition);
        }
        // 更新写入位置
        writePosition += 4 + data.length;
        messageCount++;
        return offset;

    }
    /**
     * 根据偏移量读取消息数据
     * 通过索引快速定位到消息的物理位置，然后读取完整的数据
     * @param offset 要读取的消息偏移量
     * @return 消息的字节数组数据
     * @throws Exception 文件读取时的异常
     */
    public byte[] read(long offset) throws Exception {

        // 通过索引查找消息对应的物理位置
        int position = index.lookup((int) offset);

        // 定位到消息的物理位置
        raf.seek(position);

        // 读取数据长度
        int length = raf.readInt();

        // 创建缓冲区并读取完整的数据
        byte[] data = new byte[length];

        raf.readFully(data);

        return data;
    }

    /**
     * 获取日志段的基础偏移量
     * @return 基础偏移量
     */
    public long getBaseOffset() {
        return baseOffset;
    }

    /**
     * 获取日志段的当前大小（已写入的字节数）
     * @return 写入位置，即日志段大小
     */
    public long size() {
        return writePosition;
    }

}
