package at.rueckgr.smarthome.events.server;

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

    public ClientHandler(final Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
    }

    public void run() {
        logger.info("New client connection from {}:{}", socket.getInetAddress().getHostAddress(), socket.getPort());

        final Scanner scanner = new Scanner(inputStream);
        final PrintWriter printWriter = new PrintWriter(outputStream);

        final ClientCommandProcessor clientCommandProcessor = new ClientCommandProcessor();

        while(scanner.hasNextLine()) {
            final String line = scanner.nextLine();

            logger.debug("Request: {}", line);

            final String response = clientCommandProcessor.processCommand(line);
            if(StringUtils.isNotBlank(response)) {
                logger.debug("Response: {}", response);

                printWriter.println(response);
                printWriter.flush();
            }
        }

        scanner.close();
        printWriter.close();
        try {
            socket.close();
        }
        catch (IOException e) {
            // ignore
        }

        logger.info("Connection from {}:{} closed", socket.getInetAddress().getHostAddress(), socket.getPort());
    }
}
