package robobattle.game;

import robobattle.strategy.RobotStrategy;

import java.awt.*;

public class Turn {
    public static final int MOVE = 0, SHOOT = 1, MAKE = 2, SKIP = 3;

    public Point coords;
    public int type, range, health, damage;
    public RobotStrategy strat;
    public int speed;
}
