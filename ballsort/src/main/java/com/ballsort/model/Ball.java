
package com.ballsort.model;

public class Ball {
    private final Color color;

    public Ball(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return color.toString();
    }

    public enum Color {
        RED, BLUE, GREEN, YELLOW, PURPLE, ORANGE;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
}
