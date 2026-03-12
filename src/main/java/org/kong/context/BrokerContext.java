package org.kong.context;

import org.kong.TopicManager;
import org.kong.broker.group.GroupCoordinator;
import org.kong.broker.group.OffsetManager;

/**
 * Broker 上下文类，提供全局单例的管理组件
 * 包含主题管理、群组协调和偏移量管理的静态实例
 */
public class BrokerContext {

    /** 主题管理器实例，负责主题的创建、删除和管理 */
    public static final TopicManager TOPIC_MANAGER = new TopicManager();
    
    /** 群组协调器实例，负责消费者群组的协调和管理 */
    public static final GroupCoordinator GROUP_COORDINATOR = new GroupCoordinator();
    
    /** 偏移量管理器实例，负责消费偏移量的提交和查询 */
    public static final OffsetManager OFFSET_MANAGER = new OffsetManager();

}
