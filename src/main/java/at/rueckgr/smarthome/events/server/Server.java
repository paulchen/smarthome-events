package at.rueckgr.smarthome.events.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

    public void run() {
        try {
            final ServerSocket serverSocket = new ServerSocket(9999);
            for (; ; ) {
                final Socket socket = serverSocket.accept();
                final ClientHandler clientHandler = new ClientHandler(socket);
                final Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }
        catch (IOException e) {
            // TODO
        }
    }
}
