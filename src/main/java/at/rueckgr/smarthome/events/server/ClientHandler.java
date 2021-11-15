package at.rueckgr.smarthome.events.server;

import at.rueckgr.smarthome.events.metrics.MetricsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger();

    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final ClientHandlerCallback callback;
    private final Long handlerId;
    private final MetricsService metricsService;


    public ClientHandler(final Socket socket, final Long handlerId, final ClientHandlerCallback callback) throws IOException {
        this.socket = socket;
        this.callback = callback;
        this.handlerId = handlerId;

        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();

        metricsService = new MetricsService();
    }

    public void run() {
        logger.info("New client connection from {}:{}", socket.getInetAddress().getHostAddress(), socket.getPort());

        try {
            doWork();
        }
        finally {
            cleanup();
            callback.deregister(handlerId);
        }

        logger.info("Connection from {}:{} closed", socket.getInetAddress().getHostAddress(), socket.getPort());
    }

    private void doWork() {

        final Scanner scanner = new Scanner(inputStream);
        final PrintWriter printWriter = new PrintWriter(outputStream);

        final ClientCommandProcessor clientCommandProcessor = new ClientCommandProcessor();

        while (scanner.hasNextLine()) {
            final String line = scanner.nextLine();

            logger.debug("Request: {}", line);

            final long startTime = System.currentTimeMillis();
            final String response = clientCommandProcessor.processCommand(line);
            metricsService.recordExecution(startTime);

            if (StringUtils.isNotBlank(response)) {
                logger.debug("Response: {}", response);

                printWriter.println(response);
                printWriter.flush();
            }
        }

        scanner.close();
        printWriter.close();
    }

    public void cleanup() {
        try {
            socket.close();
        }
        catch (IOException e) {
            // ignore
        }
    }
}
