package robobattle.game;

import robobattle.grid.GridManager;
import robobattle.main.ArenaPanel;
import robobattle.robot.MainFrame;
import robobattle.robot.Robot;
import robobattle.strategy.RobotStrategy;

import java.awt.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

public class GameManager {

    private static final int AMT = 2;
    private int numTeams;
    private final ArenaPanel ap;
    private GridManager gm;
    private TreeMap<String, TeamInfo> map;
    private String[] colors = new String[]{"blue", "green", "black",
            "yellow", "red"};
    private int i = 0;
    private boolean paused = true;
    private String key;
    private long lastturn = 0;

    public GameManager(int w, int h, ArenaPanel ap) {
        key = generateKey();
        gm = new GridManager(w, h, key);
        this.ap = ap;
        map = new TreeMap<String, GameManager.TeamInfo>();
    }

    private String generateKey() {
        String k = "";
        for (int i = 0; i < 10; i++) {
            k += (char) ((int) (Math.random() * 100) + '0');
        }
        return k;
    }

    private String nextColor() {
        String s = colors[i];
        i++;
        return s;
    }

    public void addMainFrame(String string, int x, int y, RobotStrategy rs) {
        if (!map.containsKey(string)) {
            map.put(string, new TeamInfo(nextColor(), string));
        }
        MainFrame mf = new MainFrame(string, x, y, rs, map.get(string).c, key);
        gm.add(mf);
        ap.initializeTeams(map);
    }

    public void addRobot(String string, int x, int y, int damage, int health,
                         int range, int speed, RobotStrategy rs) {
        if (!map.containsKey(string)) {
            map.put(string, new TeamInfo(nextColor(), string));
        }
        Robot r = new Robot(string, x, y, damage, health, range, speed, rs,
                map.get(string).c, key);
        gm.add(r);
        ap.initializeTeams(map);
    }

    public void start() {
        ap.initializeTeams(map);
        numTeams = map.size();
        if (!paused)
            return;
        paused = false;
        while (!isOver()) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            doTurn();
        }
    }

    public void stop() {
        paused = true;
    }

    private boolean isOver() {
        int teams = 0;
        ArrayList<String> remove = new ArrayList<String>();
        for (String t : map.keySet()) {
            ArrayList<Robot> team = gm.getRobotsByTeamName(t);
            if (team.size() != 0) {
                teams++;
            }
        }
        for (String s : remove) {
            map.remove(s);
        }
        return paused;
        // return map.size() <= 1;
    }

    public void doTurn() {
        if (System.currentTimeMillis() - lastturn < 1000) {
            try {
                Thread.sleep(1000 - System.currentTimeMillis() + lastturn);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        lastturn = System.currentTimeMillis();
        for (String k : map.keySet()) {
            final TeamInfo inf = map.get(k);
            ArrayList<Robot> lis = gm.getRobotsByTeamName(k);
            if (lis.size() > 0)
                inf.score++;
            final Robot[][] reg = gm.getVisibleRegionByTeam(k);
            for (int i = 0; i < lis.size(); i++) {
                Turn m = null;
                final Robot r = lis.get(i);
                if (r.getHealth() <= 0) {
                    gm.remove(r);
                    continue;
                }
                m = r.doTurn(this, gm.getVisibleRegion(r), inf.points);
                if (m == null)
                    m = new SkipTurn();
                if (m.type == Turn.MOVE) {
                    Point dst = new Point(r.getX() + m.coords.x * r.getSpeed(),
                            r.getY() + m.coords.y * r.getSpeed());
                    Point tar = gm.checkMove(r.getX(), r.getY(), dst.x, dst.y);
                    if (tar != null)
                        animateMove(r, r.getX(), r.getY(), tar.x, tar.y);
                } else if (m.type == Turn.MAKE) {
                    if (r instanceof MainFrame) {
                        Point dst = m.coords;
                        int points = map.get(r.getTeam()).points;
                        int total = m.damage + m.range + m.health + m.speed;
                        if (points >= total) {
                            if (gm.make(r.getTeam(), dst.x, dst.y, m.damage,
                                    m.health, m.range, m.speed, m.strat,
                                    map.get(r.getTeam()).c)) {
                                map.get(r.getTeam()).points -= total;
                                inf.score += total;
                            }
                        } else {
                            Errors.log("Not enough Points!");
                        }
                    } else {
                        Errors.log("Only MainFrames can make robots!");
                    }
                } else if (m.type == Turn.SHOOT) {
                    if (Math.abs(m.coords.x - r.getX()) <= r.getRange()
                            && Math.abs(m.coords.y - r.getY()) <= r.getRange()) {
                        Point tar = gm.shoot(r.getX(), r.getY(), m.coords.x,
                                m.coords.y, r.getDamage());
                        animateShoot(r.getX(), r.getY(), tar.x, tar.y,
                                r.getDamage(), map.get(r.getTeam()).c);
                        Robot vic = gm.getGrid()[tar.x][tar.y];
                        if (vic != null) {
                            vic.removeHealth(r.getDamage(), key);
                            vic.getStrategy(key).onHit(vic.getHealth(), r.getLoc());
                            if (!vic.getTeam().equals(r.getTeam()))
                                inf.score += Math.min(r.getDamage(), vic.getHealth());
                        }
                        if (vic.getHealth() <= 0) {
                            gm.remove(vic);
                            checkTeams();
                            continue;
                        }
                    } else {
                        Errors.log("Shoot target out of range!");
                    }
                }
            }
            inf.points += AMT;
        }
        ap.incrementTurn();
        ap.repaint();
    }

    private void checkTeams() {
        Set<String> iter = map.keySet();
        for (String s : iter) {
            TeamInfo inf = map.get(s);
            if (gm.getRobotsByTeamName(s).size() <= 0 && !inf.dead) {
                inf.dead = true;
                numTeams--;
            }
        }
        if (numTeams <= 1) {
            endGame();
        }
    }

    private void endGame() {
        stop();
    }

    public void transmitMessage(String team, Point p, String msg, Point point) {
        for (Robot r : gm.getAllRobots()) {
            if (r.getTeam().equals(team) && (r.getX() != p.x || r.getY() != p.y)) {
                r.getStrategy(key).receiveMessage(msg, point);
            }
        }
    }

    private void animateShoot(int x, int y, int x2, int y2, int dmg, String c) {
        ap.drawShoot(x, y, x2, y2, dmg, c);
        ap.repaint();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ap.repaint();
        ap.hideShot();
    }

    private void animateMove(Robot r, int x1, int y1, int x2, int y2) {

        int dx = 0, dy = 0;
        Point p = ap.expand(x1, y1), p1 = ap.expand(x2, y2);
        int speed = (Math.abs(p.x - p1.x) + Math.abs(p.y - p1.y)) / 30 + 1;
        int x3 = p.x;
        int y3 = p.y;
        int x4 = p1.x;
        int y4 = p1.y;
        int rot = 0;
        if (x3 < x4) {
            dx = speed;
            rot = 90;
        } else if (x3 > x4) {
            dx = -speed;
            rot = 270;
        } else if (y3 < y4) {
            dy = speed;
            rot = 180;
        } else if (y3 > y4) {
            dy = -speed;
            rot = 0;
        }
        r.rotate(rot, key);
        while (Math.abs(x4 - x3) > speed || Math.abs(y4 - y3) > speed) {
            x3 += dx;
            y3 += dy;
            r.setOffsetX(r.getOffsetX() + dx, key);
            r.setOffsetY(r.getOffsetY() + dy, key);
            ap.repaint();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        r.setOffsetX(0, key);
        r.setOffsetY(0, key);
        gm.move(r, x2, y2);
        ap.repaint();
    }

    public Robot[][] getGrid() {
        return gm.getGrid();
    }

    public class TeamInfo {

        public int points, score;
        public String c, team;
        public boolean dead;

        public TeamInfo(String color, String t) {
            c = color;
            team = t;
            points = 0;
            score = 0;
            dead = false;
        }

        public String toString() {
            return "Points: " + points + ", Color: " + c;
        }
    }
}
