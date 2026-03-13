package org.kong.protocol;

import org.kong.broker.group.GroupMember;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Response implements Serializable {

    private byte[] body;

    public Response() {
    }

    public Response(byte[] body) {
        this.body = body;
    }

    public byte[] body() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}