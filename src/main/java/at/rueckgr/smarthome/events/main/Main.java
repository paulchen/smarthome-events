package at.rueckgr.smarthome.events.main;

import at.rueckgr.smarthome.events.server.Database;
import at.rueckgr.smarthome.events.server.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        if(args.length != 1) {
            logger.error("Invalid number of arguments");
            System.exit(1);
        }

        final String propertiesFilename = args[0];
        final Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(propertiesFilename));
        }
        catch (IOException e) {
            logger.error("Unable to read properties file", e);
            System.exit(2);
        }

        final DatabaseCredentials databaseCredentials = new DatabaseCredentials(properties.getProperty(PropertyKey.URL),
                properties.getProperty(PropertyKey.USERNAME),
                properties.getProperty(PropertyKey.PASSWORD),
                properties.getProperty(PropertyKey.DRIVER));
        Database.setCredentials(databaseCredentials);

        logger.info("Server startup");

        ShutdownHook hook = new ShutdownHook();
        Runtime.getRuntime().addShutdownHook(hook);

        final Server server = new Server(Integer.valueOf(properties.getProperty(PropertyKey.PORT)));
        hook.addShutdownable(server);

        final Thread thread = new Thread(server);
        thread.start();

        // just to initialize JPA stuff
        Database.getEm();

        try {
            Thread.sleep(5000L);
        }
        catch (InterruptedException e) {
            // ignore;
        }

        if (!server.isRunning() || server.isFailure()) {
            logger.error("Error starting server");
            hook.run();
            return;
        }

        logger.info("Server running");

        final Scanner scanner = new Scanner(System.in);
        while(scanner.hasNextLine()) {
            final String input = scanner.nextLine();
            if(input.equalsIgnoreCase("quit")) {
                hook.run();
                break;
            }
        }
    }
}
