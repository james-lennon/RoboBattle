package robobattle.grid;

import robobattle.game.Errors;
import robobattle.robot.Robot;
import robobattle.strategy.RobotStrategy;

import java.awt.*;
import java.util.ArrayList;

@SuppressWarnings("RedundantIfStatement")
public class GridManager {

    private Robot[][] grid;
    private int w, h;
    private String key;

    public GridManager(int w, int h, String auth) {
        grid = new Robot[w][h];
        this.w = w;
        this.h = h;
        key = auth;
    }

    public Robot[][] getGrid() {
        return grid;
    }

    public void add(Robot r) {
        grid[r.getX()][r.getY()] = r;
    }

    public ArrayList<Robot> getAllRobots() {
        ArrayList<Robot> lis = new ArrayList<Robot>();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (grid[i][j] != null) {
                    lis.add(grid[i][j]);
                }
            }
        }
        return lis;
    }

    public ArrayList<Robot> getRobotsByTeamName(String n) {
        ArrayList<Robot> all = getAllRobots();
        for (int i = 0; i < all.size(); i++) {
            if (!all.get(i).getTeam().equals(n)) {
                all.remove(i);
                i--;
            }
        }
        return all;
    }

    public boolean isValid(int x, int y) {
        if (x < 0 || x >= w || y < 0 || y >= h) {
            return false;
        }
        return grid[x][y] == null;
    }

    public void move(Robot src, int x, int y) {
        if (x < 0 || x >= w || y < 0 || y >= h) {
            Errors.log("Invalid move destination!");
            return;
        }
        if (grid[x][y] != null) {
            Errors.log("Can't move to occupied location!");
            return;
        }
        grid[src.getX()][src.getY()] = null;
        src.move(x, y, key);
        grid[x][y] = src;
    }

    public Robot[][] getVisibleRegion(Robot r) {
        Robot[][] region = new Robot[w][h];
        for (int i = r.getX() - r.getRange(); i <= r.getX() + r.getRange(); i++) {
            for (int j = r.getY() - r.getRange(); j <= r.getY() + r.getRange(); j++) {
                if (i < 0 || i >= w || j < 0 || j >= h) {
                    continue;
                }
                region[i][j] = grid[i][j];
            }
        }
        return region;
    }

    public Robot[][] getVisibleRegionByTeam(String t) {
        ArrayList<Robot> lis = getRobotsByTeamName(t);
        Robot[][] region = new Robot[w][h];
        for (Robot r : lis) {
            int ran = r.getRange();
            for (int i = r.getX() - ran; i <= r.getX() + ran; i++) {
                for (int j = r.getY() - ran; j <= r.getY() + ran; j++) {
                    if (i < 0 || i >= w || j < 0 || j >= h) {
                        continue;
                    }
                    region[i][j] = grid[i][j];
                }
            }
        }
        return region;
    }

    public boolean make(String team, int x, int y, int damage, int health,
                        int range, int speed, RobotStrategy strat, String col) {
        if (x < 0 || x >= w || y < 0 || y >= h) {
            Errors.log("Invalid make location!");
            return false;
        }
        if (grid[x][y] != null) {
            Errors.log("Can't make a robot in an occupied location!");
            return false;
        }
        add(new Robot(team, x, y, damage, health, range, speed, strat, col, key));
        return true;
    }

    public Point shoot(int x1, int y1, int x2, int y2, int dmg) {
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
            if (grid[x1][y1] != null) {
                if (grid[x1][y1].getHealth() <= 0)
                    remove(grid[x1][y1]);
                return new Point(x1, y1);
            }
        }
        return new Point(x2, y2);
    }

    public void remove(Robot r) {
        grid[r.getX()][r.getY()] = null;
    }

    public Point checkMove(int x, int y, int x1, int y1) {
        if ((x1 - x != 0) && (y1 - y != 0))
            return null;
        while (x != x1 || y != y1) {
            int dx = 0, dy = 0;
            if (x < x1) {
                dx++;
            } else if (x > x1) {
                dx--;
            } else if (y < y1) {
                dy++;
            } else if (y > y1) {
                dy--;
            }
            if (!isValid(x + dx, y + dy) || grid[x + dx][y + dy] != null) {
                return new Point(x, y);
            }
            x += dx;
            y += dy;
        }
        return new Point(x1, y1);
    }
}
