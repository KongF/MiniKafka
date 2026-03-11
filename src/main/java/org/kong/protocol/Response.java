package org.kong.protocol;

import java.io.Serializable;

public class Response implements Serializable {

    private boolean success;

    private String message;

    private byte[] body;

    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    public Response(boolean success, String message, byte[] body) {
        this.success = success;
        this.message = message;
        this.body = body;
    }

    public Response(byte[] body) {
        this.success = true;
        this.body = body;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}