package org.kong.protocol;

import java.nio.channels.FileChannel;

public class FetchResult {

    private final FileChannel fileChannel;

    private final long position;

    private final long length;

    public FetchResult(
            FileChannel fileChannel,
            long position,
            long length) {

        this.fileChannel = fileChannel;
        this.position = position;
        this.length = length;
    }

    public FileChannel fileChannel() {
        return fileChannel;
    }

    public long position() {
        return position;
    }

    public long length() {
        return length;
    }
}
