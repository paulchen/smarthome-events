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
    private final Object lock;
    private Map<Long, ClientHandler> clientHandlers = new HashMap<>();
    private volatile boolean failure = false;
    private volatile boolean running = false;

    public Server(final int port, final Object lock) {
        this.port = port;
        this.lock = lock;
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(port);
        }
        catch (IOException e) {
            logger.error("Error creating server socket", e.getMessage());
            synchronized (lock) {
                failure = true;
                lock.notifyAll();
            }
            return;
        }

        synchronized (lock) {
            running = true;
            lock.notifyAll();
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
            if(serverSocket != null) {
                serverSocket.close();
            }
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

    public boolean isFailure() {
        return failure;
    }

    public boolean isRunning() {
        return running;
    }
}
