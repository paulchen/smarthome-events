package at.rueckgr.smarthome.events.main;

import at.rueckgr.smarthome.events.server.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

public class Main {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        logger.info("Server startup");

        ShutdownHook hook = new ShutdownHook();
        Runtime.getRuntime().addShutdownHook(hook);

        final Server server = new Server(9999);
        hook.addShutdownable(server);

        final Thread thread = new Thread(server);
        thread.start();

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
