package robobattle.game;

import java.awt.*;

public class MoveTurn extends Turn {

    public static final int UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3;

    public MoveTurn(int direction) {
        int x = 0, y = 0;
        switch (direction) {
            case UP:
                y = -1;
                break;
            case DOWN:
                y = 1;
                break;
            case RIGHT:
                x = 1;
                break;
            case LEFT:
                x = -1;
                break;
            default:
                Errors.log("Invalid direction!");
        }
        this.coords = new Point(x, y);
        this.type = Turn.MOVE;
    }

}
