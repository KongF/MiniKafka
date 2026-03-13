package org.kong.broker.group;

/**
 * 消费者消费组的状态枚举
 * 定义了消费组在生命周期中可能处于的不同状态：
 * - PREPARING_REBALANCE: 准备进行再平衡，通常在组成员变化或分区分配需要调整时进入此状态
 * - STABLE: 稳定状态，表示消费组正常运行，成员和分区分配已确定
 */
public enum GroupState {

    /** 准备再平衡状态，表示消费组即将执行分区重新分配 */
    PREPARING_REBALANCE,

    /** 稳定状态，表示消费组正常运行中 */
    STABLE

}
