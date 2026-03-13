package org.kong.protocol;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 请求类，用于封装客户端发送的各种操作请求
 * 支持序列化以便在网络中传输
 */
public class Request implements Serializable {

    /** 请求类型，标识具体的操作（如 produce、consume 等） */
    private String type;
    
    /** 主题名称，指定消息要发送到的主题 */
    private String topic;
    
    /** 分区 ID，指定消息所在的分区 */
    private int partition;
        
    /** 消费者组 ID，用于群组管理和偏移量跟踪 */
    private String groupId;
        
    /** 消费者 ID，标识特定的消费者实例 */
    private String consumerId;

    private Map<String, List<Integer>> assignment;

    /** 消息键，用于消息的路由和索引 */
    private String key;
    
    /** 消息体内容，存储实际的数据负载 */
    private byte[] body;
    
    /** 消息偏移量，用于指定或返回消息的位置 */
    private long offset;
    private int generationId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public void setAssignment(Map<String, List<Integer>> assignment) {
        this.assignment = assignment;
    }

    public Map<String, List<Integer>> getAssignment() {
        return assignment;
    }

    public int getGenerationId() {
        return generationId;
    }

    public void setGenerationId(int generationId) {
        this.generationId = generationId;
    }
}
