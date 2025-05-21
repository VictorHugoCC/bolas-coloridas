
package com.ballsort.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Tube {
    private final Stack<Ball> balls;
    private final int capacity;

    public Tube(int capacity) {
        this.capacity = capacity;
        this.balls = new Stack<>();
    }

    public boolean addBall(Ball ball) {
        if (balls.size() < capacity) {
            balls.push(ball);
            return true;
        }
        return false;
    }

    public Ball removeBall() {
        if (balls.isEmpty()) {
            return null;
        }
        return balls.pop();
    }

    public boolean isEmpty() {
        return balls.isEmpty();
    }

    public boolean isFull() {
        return balls.size() == capacity;
    }

    public Ball peekBall() {
        if (balls.isEmpty()) {
            return null;
        }
        return balls.peek();
    }

    public boolean isSorted() {
        if (balls.isEmpty()) {
            return true;
        }
        
        Ball.Color color = balls.get(0).getColor();
        for (Ball ball : balls) {
            if (ball.getColor() != color) {
                return false;
            }
        }
        return true;
    }

    public int size() {
        return balls.size();
    }

    public int getCapacity() {
        return capacity;
    }

    public List<Ball> getBalls() {
        return new ArrayList<>(balls);
    }
}
