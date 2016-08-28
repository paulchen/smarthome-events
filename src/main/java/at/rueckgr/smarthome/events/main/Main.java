package at.rueckgr.smarthome.events.main;

import at.rueckgr.smarthome.events.model.Sensor;
import at.rueckgr.smarthome.events.server.Server;
import at.rueckgr.smarthome.events.server.SystemStateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;

public class Main {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        logger.info("Server startup");

        Runtime.getRuntime().addShutdownHook(new ShutdownHook());

        final SystemStateManager systemStateManager = SystemStateManager.getInstance();

        final Sensor sensor = new Sensor(1L, 1L, "Test");
        systemStateManager.setSensorData(Collections.singletonList(sensor));

        final Server server = new Server();
        final Thread thread = new Thread(server);
        thread.start();

        logger.info("Server running");

        // TODO
    }
}
