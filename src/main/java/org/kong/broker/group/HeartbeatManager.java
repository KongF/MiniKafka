package org.kong.broker.group;

public class HeartbeatManager implements Runnable {

    private final GroupCoordinator coordinator;

    private final long timeout = 10000;

    public HeartbeatManager(GroupCoordinator coordinator) {
        this.coordinator = coordinator;
    }

    @Override
    public void run() {

        while (true) {

            try {

                Thread.sleep(3000);

                coordinator.checkTimeout(timeout);

            } catch (Exception e) {

                e.printStackTrace();
            }

        }

    }

}
