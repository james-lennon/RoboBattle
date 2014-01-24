package robobattle.main;

import com.apple.eawt.Application;
import robobattle.game.GameManager;
import robobattle.res.Images;
import robobattle.strategy.RobotStrategy;

import javax.swing.*;
import java.awt.*;

public class RoboBattleArena {
    private JPanel panel;
    private ArenaPanel ap;
    private GameManager gm;
    private JFrame frame;

    public RoboBattleArena(int w, int h) {
        frame = new JFrame("RoboBattle Arena");
        frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new JPanel(new CardLayout());
        ap = new ArenaPanel(w, h);
        gm = new GameManager(w, h, ap);
        ap.setGameManager(gm);
        panel.add(ap, "arena");

        JPanel end = new JPanel();
        end.add(new JLabel("Game Over!"));
        panel.add(end,"end");
        setCard("arena");
        frame.add(panel);
        Application.getApplication().setDockIconImage(Images.getImage("icon"));
    }

    private void setCard(String string) {
        ((CardLayout) panel.getLayout()).show(panel, string);
    }

    public void show() {
        frame.setVisible(true);
    }

    public void addMainframe(String string, Point loc, RobotStrategy mfs) {
        gm.addMainFrame(string, loc.x, loc.y, mfs);
    }

    public void addRobot(String string, Point loc, int damage, int health, int range, int speed, RobotStrategy rs) {
        gm.addRobot(string, loc.x, loc.y, damage, health, range, speed, rs);
    }
}
