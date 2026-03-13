package org.kong.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RequestEncoder extends MessageToByteEncoder<Request> {

    @Override
    protected void encode(
            ChannelHandlerContext ctx,
            Request msg,
            ByteBuf out) {

        byte[] topicBytes = msg.topic().getBytes();
        byte[] keyBytes = msg.key().getBytes();
        byte[] body = msg.body();
        int length = 2 + 4 + topicBytes.length + 4 + 8+keyBytes.length + 4 +
                        (body == null ? 0 : body.length);
        out.writeInt(length);
        out.writeShort(msg.apiKey());
        out.writeInt(topicBytes.length);
        out.writeBytes(topicBytes);
        out.writeInt(msg.partition());
        out.writeLong(msg.offset());
        out.writeInt(keyBytes.length);
        out.writeBytes(keyBytes);
        if (body != null) {
            out.writeInt(body.length);
            out.writeBytes(body);
        } else {
            out.writeInt(0);
        }
    }
}