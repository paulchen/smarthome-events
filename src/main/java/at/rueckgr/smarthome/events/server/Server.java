package at.rueckgr.smarthome.events.server;

import at.rueckgr.smarthome.events.main.Shutdownable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server implements Runnable, Shutdownable, ClientHandlerCallback {
    private static final Logger logger = LogManager.getLogger();

    private ServerSocket serverSocket;
    private final int port;
    private Map<Long, ClientHandler> clientHandlers = new HashMap<>();

    public Server(final int port) {
        this.port = port;
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(port);
        }
        catch (IOException e) {
            logger.error("Error creating server socket", e.getMessage());
            return;
        }

        Long clientId = 0L;
        for (; ; ) {
            final ClientHandler clientHandler;
            try {
                clientId++;
                final Socket socket = serverSocket.accept();
                clientHandler = new ClientHandler(socket, clientId, this);
                clientHandlers.put(clientId, clientHandler);
            }
            catch (IOException e) {
                logger.error("Error accepting client connection", e.getMessage());
                break;
            }
            final Thread thread = new Thread(clientHandler);
            thread.start();
        }
    }

    @Override
    public void shutdown() {
        try {
            serverSocket.close();
        }
        catch (IOException e) {
            // ignore
        }

        for (final ClientHandler clientHandler : clientHandlers.values()) {
            clientHandler.cleanup();
        }
    }

    @Override
    public void deregister(final Long handlerId) {
        clientHandlers.remove(handlerId);
    }
}
