package ru.otus.java.basic.http.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    private int port;
    private Dispatcher dispatcher;

    public HttpServer(int port) {
        this.port = port;
        this.dispatcher = new Dispatcher();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту: " + port);
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                new Thread(() -> {
                    try {
                        byte[] buffer = new byte[8192];
                        int n = socket.getInputStream().read(buffer);
                        if (n > 0) {
                            String rawRequest = new String(buffer, 0, n);
                            HttpRequest request = new HttpRequest(rawRequest);
                            request.printInfo(true);
                            dispatcher.execute(request, socket.getOutputStream());
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
