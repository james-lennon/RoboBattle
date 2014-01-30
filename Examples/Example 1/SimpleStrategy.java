import robobattle.game.MakeTurn;
import robobattle.game.Turn;
import robobattle.strategy.RobotState;
import robobattle.strategy.RobotStrategy;

import java.awt.*;

public class SimpleStrategy implements RobotStrategy {
    @Override
    public Turn getTurn(RobotState state) {
        if (state.getResourcePoints() >= 8) {
            return new MakeTurn(state.randomValidAdjacentLocation(), 2, 2, 2, 2,new CenterStrategy());
        }
        return state.randomValidMove();
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
