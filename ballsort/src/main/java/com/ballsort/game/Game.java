
package com.ballsort.game;

import com.ballsort.model.Board;

public class Game {
    private static final int NUM_TUBES = 7;
    private static final int TUBE_CAPACITY = 7;
    private static final int NUM_COLORS = 6;

    private final Board board;
    private static Game instance;

    private Game() {
        this.board = new Board(NUM_TUBES, TUBE_CAPACITY);
        this.board.distributeRandomly(NUM_COLORS);
    }

    public static synchronized Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public void restart() {
        board.distributeRandomly(NUM_COLORS);
    }

    public Board getBoard() {
        return board;
    }

    public boolean moveBall(int fromIndex, int toIndex) {
        return board.moveBall(fromIndex, toIndex);
    }

    public boolean isComplete() {
        return board.isComplete();
    }

    public int getMoveCount() {
        return board.getMoveCount();
    }
}
