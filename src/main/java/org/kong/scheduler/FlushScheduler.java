package org.kong.scheduler;

import org.kong.storage.log.LogManager;

public class FlushScheduler implements Runnable {

    private final LogManager logManager;

    public FlushScheduler(LogManager logManager) {
        this.logManager = logManager;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(5000);
                logManager.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
