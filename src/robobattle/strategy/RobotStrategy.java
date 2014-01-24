package robobattle.strategy;

import robobattle.game.Turn;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: James Lennon
 * Date: 10/14/13
 * Time: 4:06 PM
 * To change this template use File | Settings | File Templates.
 */
public interface RobotStrategy {

    public Turn getTurn(RobotState state);

    public void receiveMessage(String msg, Point p);

    public void onHit(int health, Point source);
}
