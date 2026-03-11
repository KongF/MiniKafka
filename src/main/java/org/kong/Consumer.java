package org.kong;

public class Consumer {

    private final LogReader logReader;
    private long offset = 0;

    public Consumer(String path) throws Exception {
        this.logReader = new LogReader(path);
    }

    public void poll() throws Exception {
        while (true) {
            String msg = logReader.read(offset);

            if (msg != null) {
                System.out.println("consume:" + msg);
                offset += msg.getBytes().length + 1;
            }
            Thread.sleep(1000);
        }
    }
}
