package org.kong.protocol;


public class Request {

    private short apiKey;

    private String topic;

    private int partition;
    private long offset;
    private String key;
    private byte[] body;
    private String groupId;

    private String memberId;

    private int generationId;

    public short apiKey() {
        return apiKey;
    }

    public void setApiKey(short apiKey) {
        this.apiKey = apiKey;
    }

    public String topic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int partition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public long offset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public byte[] body() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public int getGenerationId() {
        return generationId;
    }

    public void setGenerationId(int generationId) {
        this.generationId = generationId;
    }
    public String key() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
}
