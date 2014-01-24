package robobattle.robot;

import robobattle.game.GameManager;
import robobattle.game.Turn;
import robobattle.res.Images;
import robobattle.strategy.RobotState;
import robobattle.strategy.RobotStrategy;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class Robot {

    private int range, health, damage, speed;
    private String team, key, color;
    private Image img;
    private Point loc;
    private RobotStrategy strategy;
    private int rotation = 0;
    private int offsetX = 0, offsetY = 0;
    private Turn turn;

    public Robot(String name, int x, int y, int d, int h, int r, int speed,
                 RobotStrategy s, String color, String auth) {
        loc = new Point(x, y);
        if (this instanceof MainFrame) {
            img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
            Graphics g = img.getGraphics();
            g.drawImage(Images.getImage("mainframe-base"), 0, 0, null);
            g.drawImage(Images.getImage("mainframe-" + color), 0, 0, null);
            g.drawImage(Images.getImage("gun-weak-" + color), 0, 0, null);
        } else {
            img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
            Graphics g = img.getGraphics();
            g.drawImage(Images.getImage("body-" + getStrength(h)), 0, 0, null);
            g.drawImage(Images.getImage("gun-" + getStrength(d) + "-" + color),
                    0, 0, null);
        }
        range = r;
        health = h;
        damage = d;
        strategy = s;
        team = name;
        this.speed = speed;
        key = auth;
        this.color = color;
    }

    public Robot(Robot robot) {
        this(robot.team, robot.getX(), robot.getY(), robot.getDamage(), robot.getHealth(), robot.getRange(), robot.getSpeed(), robot.strategy, robot.color, robot.key);
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX, String auth) {
        if (checkAuth(auth)) {
            this.offsetX = offsetX;
        }
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY, String auth) {
        if (checkAuth(auth)) {
            this.offsetY = offsetY;
        }
    }

    private String getStrength(int val) {
        String strength = "";
        if (val < 3) {
            strength = "weak";
        } else if (val < 5) {
            strength = "medium";
        } else {
            strength = "strong";
        }
        return strength;
    }

    public String getTeam() {
        return team;
    }

    public Image getImage() {
        return img;
    }

    public int getX() {
        return loc.x;
    }

    public int getY() {
        return loc.y;
    }

    public final Point getLoc() {
        return (Point) loc.clone();
    }

    public void move(int x, int y, String auth) {
        if (!checkAuth(auth)) {
            return;
        }
        loc.x = x;
        loc.y = y;
    }

    public void rotate(int rot, String auth) {
        if (!checkAuth(auth)) {
            return;
        }
        double amt = Math.toRadians(rot - rotation);
        int x = img.getWidth(null) / 2, y = img.getHeight(null) / 2;
        AffineTransform tx = AffineTransform.getRotateInstance(amt, x, y);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage newimg = new BufferedImage(2 * x, 2 * y, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) newimg.getGraphics();
        g2d.drawImage(op.filter((BufferedImage) img, null), 0, 0, null);
        rotation = rot;
        img = newimg;
    }

    public int getRange() {
        return range;
    }

    public int getHealth() {
        return health;
    }

    public int getDamage() {
        return damage;
    }

    public RobotStrategy getStrategy(String auth) {
        if (!checkAuth(auth)) {
            return null;
        }
        return strategy;
    }

    public void removeHealth(int dmg, String auth) {
        if (!checkAuth(auth)) {
            return;
        }
        health -= dmg;
        if (health < 0) {
            health = 0;
        }
    }

    public int getSpeed() {
        return speed;
    }

    public Turn doTurn(GameManager gm, Robot[][] reg, int points) {
        final RobotStrategy s = getStrategy(key);
        RobotState rs = new RobotState(team, loc, points, reg, gm);
        Turn t = s.getTurn(rs);
        // long startTime = System.currentTimeMillis();
        // while (System.currentTimeMillis() - startTime <= 1000) {
        // t = s.nextTurn();
        // if (t != null)// || !tmp.isAlive())
        // break;
        // try {
        // Thread.sleep(20);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // }
        return t;
    }

    public Turn getTurn(String auth) {
        if (!checkAuth(auth)) {
            return null;
        }
        Turn tmp = turn;
        turn = null;
        return tmp;
    }

    private boolean checkAuth(String auth) {
        return key.equals(auth);
    }
}
