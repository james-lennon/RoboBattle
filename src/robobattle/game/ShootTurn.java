package robobattle.game;

import java.awt.*;

public class ShootTurn extends Turn {

    public ShootTurn(int x, int y) {
        this(new Point(x, y));
    }

    public ShootTurn(Point p) {
        this.coords = p;
        this.type = Turn.SHOOT;
    }

}
