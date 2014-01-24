package robobattle.robot;

import robobattle.strategy.RobotStrategy;

public class MainFrame extends Robot {

    public MainFrame(String name, int x, int y, RobotStrategy s, String c, String key) {
        super(name, x, y, 1, 10, 1, 1, s, c, key);
    }
}
