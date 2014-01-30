import robobattle.game.MoveTurn;
import robobattle.game.ShootTurn;
import robobattle.game.Turn;
import robobattle.robot.Robot;
import robobattle.strategy.RobotState;
import robobattle.strategy.RobotStrategy;

import java.awt.*;
import java.util.ArrayList;

public class CenterStrategy implements RobotStrategy {

    private RobotState s;

    private Turn checkAndShoot() {
        ArrayList<Robot> range = s.getRobotsInRange();
        for (Robot r : range) {
            if (!r.getTeam().equals(s.getTeam())) {
                return new ShootTurn(r.getX(), r.getY());
            }
        }
        return null;
    }

    @Override
    public Turn getTurn(RobotState state) {
        s = state;
        int x = state.getX(), y = state.getY();
        int cx = state.getWidth() / 2, cy = state.getHeight() / 2;
        Turn t = checkAndShoot();
        if (t != null) {
            return t;
        }
        if (x > cx && state.isValid(x - 1, y)) {
            return new MoveTurn(MoveTurn.LEFT);
        } else if (x < cx && s.isValid(x + 1, y)) {
            return new MoveTurn(MoveTurn.RIGHT);
        } else if (y < cy && s.isValid(x, y + 1)) {
            return new MoveTurn(MoveTurn.DOWN);
        } else if (y > cy && s.isValid(x, y - 1)) {
            return new MoveTurn(MoveTurn.UP);
        }
        return null;
    }

    @Override
    public void receiveMessage(String s, Point point) {
        //Do nothing
    }

    @Override
    public void onHit(int i, Point point) {
        //Do nothing
    }
}