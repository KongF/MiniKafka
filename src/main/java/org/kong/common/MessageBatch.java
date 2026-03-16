package org.kong.common;

import java.util.ArrayList;
import java.util.List;

public class MessageBatch {

    private final List<byte[]> messages =
            new ArrayList<>();

    public void add(byte[] msg) {

        messages.add(msg);
    }

    public List<byte[]> messages() {

        return messages;
    }

}
