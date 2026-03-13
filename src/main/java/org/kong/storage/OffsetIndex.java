package org.kong.storage;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * 偏移量索引类，用于维护消息偏移量到物理位置的映射关系
 * 支持二分查找以快速定位指定偏移量的消息位置
 */
public class OffsetIndex {

    /** 索引文件对象 */
    private final File file;

    /** 随机访问文件对象，用于读写索引数据 */
    private final RandomAccessFile raf;

    /** 索引条目列表，存储在内存中的索引数据 */
    private final List<IndexEntry> entries = new ArrayList<>();

    /**
     * 构造偏移量索引对象
     * @param dir 索引文件存储目录
     * @param baseOffset 基础偏移量，用于命名索引文件
     * @throws Exception 文件创建或加载时的异常
     */
    public OffsetIndex(String dir, long baseOffset) throws Exception {

        // 创建索引文件
        this.file = new File(dir, baseOffset + ".index");

        // 确保文件存在，如不存在则创建
        if (!file.exists()) {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            file.createNewFile();
        }

        // 打开随机访问文件并加载现有索引数据
        this.raf = new RandomAccessFile(file, "rw");

        load();
    }

    /**
     * 从磁盘文件加载索引数据到内存
     * 按顺序读取文件中的所有索引条目（每个条目包含偏移量和位置）
     * @throws Exception 文件读取时的异常
     */
    private void load() throws Exception {

        // 从文件开头开始读取
        raf.seek(0);

        // 循环读取直到文件末尾
        while (raf.getFilePointer() < raf.length()) {

            // 读取偏移量
            int offset = raf.readInt();

            // 读取位置
            int position = raf.readInt();

            // 添加到内存索引列表
            entries.add(new IndexEntry(offset, position));
        }
    }

    /**
     * 向索引中追加新的条目
     * 同时将数据写入磁盘文件和内存列表
     * @param offset 消息偏移量
     * @param position 消息在日志文件中的物理位置
     * @throws Exception 文件写入时的异常
     */
    public synchronized void append(int offset, int position)
            throws Exception {

        // 定位到文件末尾
        raf.seek(raf.length());

        // 写入偏移量和位置数据
        raf.writeInt(offset);

        raf.writeInt(position);

        // 添加到内存索引列表
        entries.add(new IndexEntry(offset, position));
    }

    /**
     * 根据目标偏移量查找对应的物理位置
     * 使用二分查找算法提高查找效率
     * 如果找不到精确匹配，则返回小于目标偏移量的最大偏移量对应的位置
     * @param targetOffset 目标偏移量
     * @return 对应的物理位置，如果未找到则返回 0
     */
    public int lookup(int targetOffset) {
        int low = 0;
        int high = entries.size() - 1;
        // 二分查找
        while (low <= high) {
            int mid = (low + high) / 2;
            IndexEntry entry = entries.get(mid);
            // 找到精确匹配
            if (entry.offset == targetOffset) {
                return entry.position;
            }
            // 调整查找范围
            if (entry.offset < targetOffset) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }

        }

        // 未找到精确匹配时，返回小于目标偏移量的最大偏移量对应的位置
        if (high >= 0) {
            return entries.get(high).position;
        }

        // 默认返回 0
        return 0;
    }

}