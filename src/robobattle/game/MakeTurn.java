package robobattle.game;

import robobattle.strategy.RobotStrategy;

import java.awt.*;

public class MakeTurn extends Turn {

    public MakeTurn(Point loc, int damage, int health, int range, int speed, RobotStrategy strategy) {
        this.type = Turn.MAKE;
        this.coords = loc;
        this.damage = damage;
        this.range = range;
        this.health = health;
        this.speed = speed;
        this.strat = strategy;
    }

}
