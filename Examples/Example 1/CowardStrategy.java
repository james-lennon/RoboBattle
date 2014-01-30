import robobattle.game.MakeTurn;
import robobattle.game.MoveTurn;
import robobattle.game.Turn;
import robobattle.strategy.RobotState;
import robobattle.strategy.RobotStrategy;

import java.awt.*;

public class CowardStrategy implements RobotStrategy {

    private boolean cent = false;

    public int distSum(RobotState s, int x, int y) {
        int sum = 0;
        for (int i = 0; i < s.getWidth(); i++) {
            for (int j = 0; j < s.getHeight(); j++) {
                if (s.get(i, j) != null && !s.get(i, j).getTeam().equals(s.getTeam())) {
                    sum += Math.abs(i - x) + Math.abs(j - y);
                }
            }
        }
        return sum;
    }

    public Turn findSafe(RobotState s) {
        int max = 0;
        MoveTurn turn = null;
        int d = distSum(s, s.getX() + 1, s.getY());
        if (d > max) {
            max = d;
            turn = new MoveTurn(MoveTurn.RIGHT);
        }
        d = distSum(s, s.getX() - 1, s.getY());
        if (d > max) {
            max = d;
            turn = new MoveTurn(MoveTurn.LEFT);
        }
        d = distSum(s, s.getX(), s.getY() + 1);
        if (d > max) {
            max = d;
            turn = new MoveTurn(MoveTurn.DOWN);
        }
        d = distSum(s, s.getX(), s.getY() - 1);
        if (d > max) {
            max = d;
            turn = new MoveTurn(MoveTurn.UP);
        }
        if (turn != null)
            return turn;
        return s.randomValidMove();
    }

    @Override
    public Turn getTurn(RobotState s) {
        int p = s.getResourcePoints();
        if (p >= 12) {
            cent = true;

            return new MakeTurn(s.randomValidAdjacentLocation(), 4, 4, 4, 0, new SentryStrategy());
        }
//        if (!cent) {
//            int tarx = s.getWidth() / 2;
//            int tary = s.getHeight() / 2;
//            if (s.getX() < tarx) {
//                return new MoveTurn(MoveTurn.RIGHT);
//            } else if (s.getX() > tarx) {
//                return new MoveTurn(MoveTurn.LEFT);
//            }
//            if (s.getY() < tary) {
//                return new MoveTurn(MoveTurn.DOWN);
//            } else if (s.getY() > tary) {
//                return new MoveTurn(MoveTurn.UP);
//            }
//        }
        return findSafe(s);
    }

    @Override
    public void receiveMessage(String s, Point point) {

    }

    @Override
    public void onHit(int i, Point point) {

    }
}
