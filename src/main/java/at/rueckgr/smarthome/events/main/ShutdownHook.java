package at.rueckgr.smarthome.events.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShutdownHook extends Thread {
    private static final Logger logger = LogManager.getLogger();

    @Override
    public void run() {
        logger.info("Shutting down...");
    }
}
