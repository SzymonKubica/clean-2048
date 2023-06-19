package clean2048.controller;

public enum Direction {
    LEFT, RIGHT, UP, DOWN;

    public boolean isVertical() {
        return this == UP || this == DOWN;
    }
}
