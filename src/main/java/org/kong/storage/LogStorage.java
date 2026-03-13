package org.kong.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.atomic.AtomicLong;

public class LogStorage {
    private final File file;

    private final AtomicLong offset = new AtomicLong(0);

    public LogStorage(String path) throws Exception {

        this.file = new File(path);

        if (!file.exists()) {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            file.createNewFile();
        }

        offset.set(file.length());
    }
    public synchronized long append(byte[] data) throws Exception {

        long currentOffset = offset.get();

        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            fos.write(data);
            fos.write('\n');
        }

        offset.addAndGet(data.length + 1);

        return currentOffset;
    }
}
