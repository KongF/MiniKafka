package org.kong.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class ResponseDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(
            ChannelHandlerContext ctx,
            ByteBuf in,
            List<Object> out) {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int length = in.readInt();
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }
        int bodyLen = in.readInt();
        byte[] body = new byte[bodyLen];
        in.readBytes(body);
        Response response = new Response(body);
        out.add(response);
    }
}