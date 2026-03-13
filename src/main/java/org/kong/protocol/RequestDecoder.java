package org.kong.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class RequestDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int length = in.readInt();
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }
        Request req = new Request();
        req.setApiKey(in.readShort());

        int topicLen = in.readInt();
        byte[] topicBytes = new byte[topicLen];
        in.readBytes(topicBytes);
        req.setTopic(new String(topicBytes));
        req.setPartition(in.readInt());
        req.setOffset(in.readLong());
        int keyLen = in.readInt();
        byte[] key = new byte[keyLen];
        in.readBytes(key);
        int bodyLen = in.readInt();
        if (bodyLen > 0) {
            byte[] body = new byte[bodyLen];
            in.readBytes(body);
            req.setBody(body);
        }

        int groupLen = in.readInt();
        if (groupLen > 0) {
            byte[] groupBytes = new byte[groupLen];
            in.readBytes(groupBytes);
            req.setGroupId(new String(groupBytes));
        }

        int memberLen = in.readInt();
        if (memberLen > 0) {
            byte[] memberBytes = new byte[memberLen];
            in.readBytes(memberBytes);
            req.setMemberId(new String(memberBytes));
        }
        req.setGenerationId(in.readInt());
        out.add(req);
    }
}