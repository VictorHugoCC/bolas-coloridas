
package com.ballsort;

import com.ballsort.server.ApiHandler;
import com.ballsort.server.StaticFileHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class HttpServerMain {
    private static final int PORT = 8090;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/api", new ApiHandler());

        server.createContext("/", new StaticFileHandler("public"));

        server.setExecutor(Executors.newFixedThreadPool(10));

        server.start();
        
        System.out.println("Servidor iniciado na porta " + PORT);
        System.out.println("Acesse http://localhost:" + PORT + " para jogar");
    }
}
