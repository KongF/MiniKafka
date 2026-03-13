package org.kong.broker.group;

import java.util.*;

/**
 * 消费者组类，用于管理一组消费者的分区分配
 * 支持动态添加成员和重新平衡分区分配
 */
public class ConsumerGroup {

    /** 消费者组的唯一标识符 */
    private final String groupId;

    /** 消费者组成员集合 */
    private final Map<String,GroupMember> members = new HashMap();

    private GroupState state = GroupState.PREPARING_REBALANCE;

    private int generationId = 0;
    /** 分区分配结果，映射每个消费者到其负责的分区列表 */
    private final Map<String, List<Integer>> assignment = new HashMap<>();

    /** 当前消费者组的领导者，用于处理元数据同步请求 */
    private String leaderId;

    /**
     * 构造消费者组对象
     * @param groupId 消费者组的唯一标识符
     */
    public ConsumerGroup(String groupId) {
        this.groupId = groupId;
    }

    /**
     * 向消费者组添加新成员
     * @param consumerId 消费者的唯一标识符
     */
    public synchronized void addMember(String consumerId) {
        if (!members.containsKey(consumerId)) {

            members.put(consumerId, new GroupMember(consumerId));

            if (leaderId == null) {
                leaderId = consumerId;
            }

            triggerRebalence();
        }
    }
    public synchronized void removeMember(String consumerId) {
        members.remove(consumerId);
        triggerRebalence();
    }
    public synchronized void heartbeat(String consumerId) {

        GroupMember member = members.get(consumerId);

        if (member != null) {
            member.heartbeat();
        }
    }
    private void triggerRebalence() {
        state = GroupState.PREPARING_REBALANCE;
        generationId++;
        assignment.clear();
    }
    public synchronized void completeRebalance(Map<String, List<Integer>> newAssignment) {
        assignment.clear();
        assignment.putAll(newAssignment);
        state = GroupState.STABLE;
    }
    public synchronized boolean isLeader(String consumerId) {
        return consumerId.equals(leaderId);
    }

    public int generationId(){
        return generationId;
    }
    public GroupState state(){
        return state;
    }

    public Set<String> members(){
        return members.keySet();
    }

    /**
     * 执行分区重新平衡，将所有分区均匀分配给组内消费者
     * 使用轮询算法按顺序将分区分配给排序后的消费者列表
     * @param partitionCount 分区总数
     * @return 分区分配结果，键为消费者 ID，值为该消费者分配的分区列表
     */
//    public synchronized Map<String, List<Integer>> rebalance(int partitionCount) {
//
//        // 创建排序后的消费者列表，确保分配结果的一致性
//        List<String> consumers = new ArrayList<>(members.keySet());
//
//        Collections.sort(consumers);
//
//        // 初始化结果映射，为每个消费者创建空的分区列表
//        Map<String, List<Integer>> result = new HashMap<>();
//
//        for (String c : consumers) {
//            result.put(c, new ArrayList<>());
//        }
//
//        // 使用轮询算法分配所有分区
//        for (int i = 0; i < partitionCount; i++) {
//
//            // 计算当前分区应该分配给的消费者索引
//            String consumer = consumers.get(i % consumers.size());
//            result.get(consumer).add(i);
//        }
//        // 更新内部的分区分配结果
//        assignment.clear();
//        assignment.putAll(result);
//
//        return assignment;
//    }

    /**
     * 获取指定消费者分配的分区列表
     * @param consumerId 消费者的唯一标识符
     * @return 该消费者分配的分区列表，如果未找到则返回空列表
     */
    public List<Integer> partitions(String consumerId) {
        return assignment.getOrDefault(consumerId, List.of());
    }

    public Map<String, GroupMember> getMembers() {
        return members;
    }
    public Map<String, List<Integer>> getAssignment() {
        return assignment;
    }
}