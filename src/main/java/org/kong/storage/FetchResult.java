package org.kong.storage;


import java.nio.channels.FileChannel;

public class FetchResult {

    private final FileChannel channel;

    private final long position;

    private final int length;

    public FetchResult(FileChannel channel, long position, int length) {
        this.channel = channel;
        this.position = position;
        this.length = length;
    }

    public FileChannel getChannel() {
        return channel;
    }

    public long getPosition() {
        return position;
    }

    public int getLength() {
        return length;
    }
}
