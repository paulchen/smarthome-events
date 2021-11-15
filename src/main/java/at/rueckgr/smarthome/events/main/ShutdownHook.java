package at.rueckgr.smarthome.events.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ShutdownHook extends Thread {
    private static final Logger logger = LogManager.getLogger();

    private final List<Shutdownable> shutdownables = new ArrayList<>();

    @Override
    public void run() {
        logger.info("Shutting down...");

        for (final Shutdownable shutdownable : shutdownables) {
            shutdownable.shutdown();
        }

        logger.info("Shutdown complete");
    }

    public void addShutdownable(final Shutdownable shutdownable) {
        shutdownables.add(shutdownable);
    }
}
