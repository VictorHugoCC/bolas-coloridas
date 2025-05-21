
package com.ballsort.model;

import java.util.*;

public class Board {
    private final List<Tube> tubes;
    private final int tubeCapacity;
    private int moveCount;

    public Board(int numTubes, int tubeCapacity) {
        this.tubes = new ArrayList<>(numTubes);
        this.tubeCapacity = tubeCapacity;
        this.moveCount = 0;

        for (int i = 0; i < numTubes; i++) {
            tubes.add(new Tube(tubeCapacity));
        }
    }

    public int getMoveCount() {
        return moveCount;
    }

    public List<Tube> getTubes() {
        return Collections.unmodifiableList(tubes);
    }

    public boolean moveBall(int fromIndex, int toIndex) {
        if (fromIndex < 0 || fromIndex >= tubes.size() || toIndex < 0 || toIndex >= tubes.size() || fromIndex == toIndex) {
            return false;
        }

        Tube fromTube = tubes.get(fromIndex);
        Tube toTube = tubes.get(toIndex);

        if (fromTube.isEmpty()) {
            return false;
        }

        if (toTube.isFull()) {
            return false;
        }

        Ball topBall = fromTube.peekBall();

        if (toTube.isEmpty() || toTube.peekBall().getColor() == topBall.getColor()) {
            Ball ball = fromTube.removeBall();
            toTube.addBall(ball);
            moveCount++;
            return true;
        }

        return false;
    }

    public boolean isComplete() {
        for (Tube tube : tubes) {
            if (!tube.isEmpty() && !tube.isSorted()) {
                return false;
            }
        }
        return true;
    }

    public void distributeRandomly(int numColors) {
        for (Tube tube : tubes) {
            while (!tube.isEmpty()) {
                tube.removeBall();
            }
        }

        moveCount = 0;

        int numTubesWithBalls = tubes.size() - 1;

        if (numColors > numTubesWithBalls) {
            numColors = numTubesWithBalls;
        }

        List<Ball> allBalls = new ArrayList<>();
        for (int i = 0; i < numColors; i++) {
            Ball.Color color = Ball.Color.values()[i];
            for (int j = 0; j < tubeCapacity; j++) {
                allBalls.add(new Ball(color));
            }
        }

        Collections.shuffle(allBalls);

        int ballIndex = 0;
        for (int i = 0; i < numTubesWithBalls; i++) {
            Tube tube = tubes.get(i);
            int ballsToAdd = Math.min(tubeCapacity, allBalls.size() - ballIndex);
            
            for (int j = 0; j < ballsToAdd; j++) {
                tube.addBall(allBalls.get(ballIndex++));
            }
        }

        if (!isSolvable()) {
            distributeRandomly(numColors);
        }
    }

    private boolean isSolvable() {
        boolean hasEmptyTube = false;
        for (Tube tube : tubes) {
            if (tube.isEmpty()) {
                hasEmptyTube = true;
                break;
            }
        }

        if (!hasEmptyTube) {
            return false;
        }

        Map<Ball.Color, Integer> colorCount = new HashMap<>();
        for (Tube tube : tubes) {
            for (Ball ball : tube.getBalls()) {
                Ball.Color color = ball.getColor();
                colorCount.put(color, colorCount.getOrDefault(color, 0) + 1);
            }
        }

        for (Integer count : colorCount.values()) {
            if (count != tubeCapacity) {
                return false;
            }
        }

        return true;
    }
}
