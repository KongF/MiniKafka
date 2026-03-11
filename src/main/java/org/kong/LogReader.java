package org.kong;

import java.io.RandomAccessFile;


/**
 * 日志文件读取器，支持按偏移量随机读取文件行
 */
public class LogReader {

    /**
     * 随机访问文件对象，用于读取日志文件
     */
    private final RandomAccessFile raf;

    /**
     * 构造日志读取器实例
     *
     * @param path 日志文件路径
     * @throws Exception 文件打开失败时抛出异常
     */
    public LogReader(String path) throws Exception {
        this.raf = new RandomAccessFile(path, "r");
    }

    /**
     * 从指定偏移量位置读取一行日志
     *
     * @param offset 文件中的字节偏移量
     * @return 读取到的文本行
     * @throws Exception 文件读取失败或到达文件末尾时抛出异常
     */
    public String read(long offset) throws Exception {
        raf.seek(offset);
        return raf.readLine();
    }

}
