package org.kong;

import org.kong.broker.BrokerContext;
import org.kong.broker.BrokerServer;

public class BrokerMain {

    public static void main(String[] args) throws Exception {

        BrokerContext.TOPIC_MANAGER.createTopic("order", 3);

        new BrokerServer(9092).start();

    }

}
