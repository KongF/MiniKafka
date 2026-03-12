package org.kong.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.kong.protocol.Response;

public class ClientHandler extends SimpleChannelInboundHandler<String> {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

        Response response = mapper.readValue(msg, Response.class);

        if (response.getBody() != null) {
            String message = new String(response.getBody());
            System.out.println("consume: " + message+"/////"+response.toString());
        } else {
            System.out.println(response.getMessage());
        }

    }

}
