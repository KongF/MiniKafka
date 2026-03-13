package org.kong.protocol;

import org.kong.broker.group.GroupMember;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Response implements Serializable {

    private boolean success;

    private String message;

    private byte[] body;

    private long offset;

    private List<Integer> partitions;
    private boolean leader;
    private Map<String, GroupMember> members;
    private int generationId;

    public Response(){}

    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    public Response(boolean success, String message, byte[] body) {
        this.success = success;
        this.message = message;
        this.body = body;
    }

    public Response(byte[] body, long offset) {
        this.success = true;
        this.body = body;
        this.offset = offset;
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

    public List<Integer> getPartitions() {
        return partitions;
    }

    public void setPartitions(List<Integer> partitions) {
        this.partitions = partitions;
    }

    public void setLeader(boolean leader) {
        this.leader = leader;
    }

    public boolean isLeader() {
        return leader;
    }

    public void setMembers(Map<String, GroupMember> members) {
        this.members = members;
    }

    public Map<String, GroupMember> getMembers() {
        return members;
    }

    public void setGenerationId(int generationId) {
        this.generationId = generationId;
    }

    public int getGenerationId() {
        return generationId;
    }
}