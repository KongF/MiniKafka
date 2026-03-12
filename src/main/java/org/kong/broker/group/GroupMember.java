package org.kong.broker.group;

public class GroupMember {

    /** 消费者的唯一标识符 */
    private final String consumerId;

    /** 最后一次心跳时间 */
    private long lastHeartbeat;

    public GroupMember(String consumerId) {
        this.consumerId = consumerId;
        this.lastHeartbeat = System.currentTimeMillis();
    }

    public String getConsumerId() {
        return consumerId;
    }

    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void heartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
    }
}
