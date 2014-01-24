package robobattle.strategy;

import robobattle.game.GameManager;
import robobattle.game.MoveTurn;
import robobattle.game.SkipTurn;
import robobattle.game.Turn;
import robobattle.robot.Robot;

import java.awt.*;
import java.util.ArrayList;

public class RobotState {

    private int[] dx = new int[]{0, 1, 0, -1},
            dy = new int[]{-1, 0, 1, 0};
    private Robot[][] grid;
    private Point loc;
    private int points;
    private String team;
    private GameManager gm;
    private Robot mRobot;

    public RobotState(String team, Point p, int points, Robot[][] g, GameManager gm) {
        this.team = team;
        this.loc = p;
        this.points = points;
        this.gm = gm;
        grid = new Robot[g.length][g[0].length];
        for (int i = 0; i < g.length; i++) {
            for (int j = 0; j < g[0].length; j++) {
                if (g[i][j] != null) {
                    grid[i][j] = new Robot(g[i][j]);
                }
            }
        }
        mRobot = grid[p.x][p.y];
    }

    public Robot get(int x, int y) {
        return grid[x][y];
    }

    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < grid.length && y >= 0 && y < grid[0].length;
    }

    public boolean isInRange(int x, int y) {
        return Math.abs(x - loc.x) < mRobot.getRange() && Math.abs(y - loc.y) < mRobot.getRange();
    }

    public boolean isValid(int x, int y) {
        return isInBounds(x, y) && grid[x][y] == null;
    }

    public int getResourcePoints() {
        return points;
    }

    public ArrayList<Robot> getRobotsInRange() {
        ArrayList<Robot> lis = new ArrayList<Robot>();
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                if (get(i, j) != null) {
                    lis.add(get(i, j));
                }
            }
        }
        return lis;
    }

    public boolean isFriendlyFire(Point p){
        int x1 = getX(), y1 = getY(), x2 = p.x, y2 = p.y;
        while (x1 != x2 || y1 != y2) {
            if (x1 > x2) {
                x1--;
            } else if (x2 > x1) {
                x1++;
            }
            if (y1 > y2) {
                y1--;
            } else if (y2 > y1) {
                y1++;
            }
            if (grid[x1][y1] != null && grid[x1][y1].getTeam().equals(getTeam())) {
                return true;
            }
        }
        return false;
    }

    public Point getLocation(){
        return loc;
    }

    public Turn randomValidMove() {
        int i = (int) (Math.random() * 4);
        for (int j = 0; j < 4; j++) {
            int index = (i + j) % 4;
            if (isValid(loc.x + dx[index], loc.y + dy[index])) {
                return new MoveTurn(index);
            }
        }
        return new SkipTurn();
    }

    public Point randomValidAdjacentLocation() {
        int i = (int) (Math.random() * 4);
        for (int j = 0; j < 4; j++) {
            int index = (i + j) % 4;
            if (isValid(loc.x + dx[index], loc.y + dy[index])) {
                return new Point(loc.x + dx[index], loc.y + dy[index]);
            }
        }
        return null;
    }

    public int getX() {
        return loc.x;
    }

    public String getTeam() {
        return team;
    }

    public int getY() {
        return loc.y;
    }

    public void transmitMessage(String s, Point p) {
        gm.transmitMessage(team, loc, s, p);
    }

    public int getWidth() {
        return grid.length;
    }

    public int getHeight() {
        return grid[0].length;
    }

    public Robot getRobot() {
        return mRobot;
    }
}
