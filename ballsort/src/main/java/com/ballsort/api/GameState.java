
package com.ballsort.api;

import com.ballsort.model.Ball;
import com.ballsort.model.Tube;
import com.ballsort.game.Game;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private List<List<String>> tubes;
    private int moveCount;
    private boolean complete;

    public GameState() {
        Game game = Game.getInstance();
        this.tubes = new ArrayList<>();
        
        for (Tube tube : game.getBoard().getTubes()) {
            List<String> tubeBalls = new ArrayList<>();
            for (Ball ball : tube.getBalls()) {
                tubeBalls.add(ball.getColor().toString());
            }
            tubes.add(tubeBalls);
        }
        
        this.moveCount = game.getMoveCount();
        this.complete = game.isComplete();
    }

    public List<List<String>> getTubes() {
        return tubes;
    }

    public void setTubes(List<List<String>> tubes) {
        this.tubes = tubes;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
