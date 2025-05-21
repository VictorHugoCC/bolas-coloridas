
package com.ballsort.server;

import com.ballsort.api.GameState;
import com.ballsort.api.MoveRequest;
import com.ballsort.api.MoveResponse;
import com.ballsort.game.Game;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Manipulador de requisições HTTP para a API do jogo.
 */
public class ApiHandler implements HttpHandler {
    private final ObjectMapper objectMapper;
    private final Game game;

    public ApiHandler() {
        this.objectMapper = new ObjectMapper();
        this.game = Game.getInstance();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            if (path.equals("/api/game/state") && method.equals("GET")) {
                handleGetGameState(exchange);
            } else if (path.equals("/api/game/restart") && method.equals("POST")) {
                handleRestartGame(exchange);
            } else if (path.equals("/api/game/move") && method.equals("POST")) {
                handleMoveBall(exchange);
            } else {
                sendResponse(exchange, 404, "Not Found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
        }
    }

    /**
     * Manipula a requisição para obter o estado atual do jogo.
     */
    private void handleGetGameState(HttpExchange exchange) throws IOException {
        GameState gameState = new GameState();
        String response = objectMapper.writeValueAsString(gameState);
        sendJsonResponse(exchange, 200, response);
    }

    /**
     * Manipula a requisição para reiniciar o jogo.
     */
    private void handleRestartGame(HttpExchange exchange) throws IOException {
        game.restart();
        GameState gameState = new GameState();
        String response = objectMapper.writeValueAsString(gameState);
        sendJsonResponse(exchange, 200, response);
    }

    /**
     * Manipula a requisição para mover uma bola.
     */
    private void handleMoveBall(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        MoveRequest moveRequest = objectMapper.readValue(requestBody, MoveRequest.class);
        
        boolean success = game.moveBall(moveRequest.getFromTube(), moveRequest.getToTube());
        String message = success ? "Movimento realizado com sucesso" : "Movimento inválido";
        
        MoveResponse moveResponse = new MoveResponse(success, message, new GameState());
        String response = objectMapper.writeValueAsString(moveResponse);
        
        sendJsonResponse(exchange, 200, response);
    }

    /**
     * Envia uma resposta HTTP com o conteúdo especificado.
     */
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * Envia uma resposta HTTP com conteúdo JSON.
     */
    private void sendJsonResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}
