
package com.ballsort.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Manipulador de requisições HTTP para arquivos estáticos.
 */
public class StaticFileHandler implements HttpHandler {
    private final String resourcesPath;
    private final Map<String, String> mimeTypes;

    public StaticFileHandler(String resourcesPath) {
        this.resourcesPath = resourcesPath;
        this.mimeTypes = new HashMap<>();
        initMimeTypes();
    }

    private void initMimeTypes() {
        mimeTypes.put("html", "text/html");
        mimeTypes.put("css", "text/css");
        mimeTypes.put("js", "application/javascript");
        mimeTypes.put("json", "application/json");
        mimeTypes.put("png", "image/png");
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("jpeg", "image/jpeg");
        mimeTypes.put("gif", "image/gif");
        mimeTypes.put("svg", "image/svg+xml");
        mimeTypes.put("ico", "image/x-icon");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        
        // Se o caminho for a raiz, redireciona para index.html
        if (requestPath.equals("/")) {
            requestPath = "/index.html";
        }

        // Constrói o caminho do recurso
        String resourcePath = resourcesPath + requestPath;
        
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                // Recurso não encontrado
                String response = "File not found: " + requestPath;
                exchange.sendResponseHeaders(404, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                return;
            }

            // Define o tipo MIME com base na extensão do arquivo
            String extension = getFileExtension(requestPath);
            String contentType = mimeTypes.getOrDefault(extension, "application/octet-stream");
            exchange.getResponseHeaders().set("Content-Type", contentType);

            // Lê o conteúdo do recurso
            byte[] bytes = is.readAllBytes();
            exchange.sendResponseHeaders(200, bytes.length);
            
            // Envia o conteúdo
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    private String getFileExtension(String path) {
        int dotIndex = path.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < path.length() - 1) {
            return path.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }
}
