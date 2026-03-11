package org.kong.storage;

import java.io.File;
import java.io.RandomAccessFile;

public class LogSegment {

    private final long baseOffset;

    private final File logFile;

    private final RandomAccessFile raf;

    private long writePosition = 0;

    public LogSegment(String dir, long baseOffset) throws Exception {

        this.baseOffset = baseOffset;

        this.logFile =  new File(dir + "/" + baseOffset + ".log");

        this.raf = new RandomAccessFile(logFile, "rw");

        this.writePosition = raf.length();

    }

    public synchronized long append(byte[] data)
            throws Exception {

        long offset = baseOffset + writePosition;

        raf.seek(writePosition);

        raf.writeInt(data.length);

        raf.write(data);

        writePosition += 4 + data.length;

        return offset;

    }

}
