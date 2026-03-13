package org.kong.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ResponseEncoder extends MessageToByteEncoder<Response> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Response msg, ByteBuf out) {
        byte[] body = msg.body();
        int length = 4 + body.length;
        out.writeInt(length);
        out.writeInt(body.length);
        out.writeBytes(body);
    }
}
