package ru.otus.java.basic.http.server;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private static final Logger logger = LogManager.getLogger(HttpServer.class);
    private int port;
    private Dispatcher dispatcher;
    private ExecutorService executorService;


    public HttpServer(int port) {
        this.port = port;
        this.dispatcher = new Dispatcher();
        this.executorService = Executors.newFixedThreadPool(10);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Сервер запущен на порту: {}", port);
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                executorService.submit(() -> handleRequest(socket));
            }
        } catch (IOException e) {
            logger.error("Ошибка http сервера: ", e);
        } finally {
            executorService.shutdown();
        }
    }
    private void handleRequest(Socket socket) {
        try (socket) {
            byte[] buffer = new byte[8192];
            int n = socket.getInputStream().read(buffer);
            if (n < 1) {
                return;
            }
            String rawRequest = new String(buffer, 0, n);
            HttpRequest request = new HttpRequest(rawRequest);
            request.printInfo(true);
            dispatcher.execute(request, socket.getOutputStream());
        } catch (IOException e) {
            logger.error("Ошибка при обработке запроса: ", e);
        }
    }
}
