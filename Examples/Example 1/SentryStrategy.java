import robobattle.game.ShootTurn;
import robobattle.game.Turn;
import robobattle.robot.Robot;
import robobattle.strategy.RobotState;
import robobattle.strategy.RobotStrategy;

import java.awt.*;
import java.util.ArrayList;

public class SentryStrategy implements RobotStrategy {

    @Override
    public Turn getTurn(RobotState state) {
        ArrayList<Robot> range = state.getRobotsInRange();
        for (Robot r : range) {
            if (!r.getTeam().equals(state.getTeam())) {
                if (!state.isFriendlyFire(new Point(r.getX(), r.getY())))
                    return new ShootTurn(r.getX(), r.getY());
            }
        }
        return null;
    }

    @Override
    public void receiveMessage(String s, Point point) {

    }

    @Override
    public void onHit(int i, Point point) {

    }
}
