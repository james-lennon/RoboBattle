package robobattle.main;

import robobattle.game.GameManager;
import robobattle.game.GameManager.TeamInfo;
import robobattle.res.Images;
import robobattle.robot.Robot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.*;

public class ArenaPanel extends JPanel {

    private final GridPanel gp;
    private final InfoPanel ip;
    private GameManager gm;
    private int w, h;
    private double tw, th;
    private Point p2;
    private Point p1;
    private int shotWidth;
    private Color shotColor;
    private boolean r = false;

    public ArenaPanel(int w, int h) {
        Images.load();
        setLayout(new GridBagLayout());
        this.w = w;
        this.h = h;
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 1;
        gp = new GridPanel();
        gp.setPreferredSize(new Dimension(700, 700));
        this.add(gp, c);
        c.gridx = 1;
        c.weightx = c.weighty = 0;
        ip = new InfoPanel();
        add(ip, c);
    }

    public void setGameManager(GameManager g) {
        gm = g;
    }

    public void start() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                gm.start();
            }
        });
        t.start();
    }

    public void step() {
        if (r)
            return;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                r = true;
                gm.doTurn();
                r = false;
            }
        });
        t.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.black);
        int val = Math.min(getWidth() - ip.getWidth() - 10, getHeight());
        gp.setPreferredSize(new Dimension(val, val));
        gp.revalidate();
        g.fillRect(0, 0, getWidth(), getHeight());
        //paintComponents(g);
    }

    public Point expand(int x, int y) {
        return new Point((int) (x * tw), (int) (y * th));
    }

    private Color getColor(String c) {
        Color col = null;
        if (c.equals("red")) {
            col = Color.red;
        } else if (c.equals("blue")) {
            col = Color.blue;
        } else if (c.equals("green")) {
            col = Color.green;
        } else if (c.equals("black")) {
            col = Color.black;
        } else if (c.equals("yellow")) {
            col = Color.yellow;
        } else {
            col = Color.white;
        }
        return col;
    }

    public void drawShoot(int x, int y, int x2, int y2, int dmg, String c) {
        p1 = new Point((int) (x * tw + tw / 2), (int) (y * th + th / 2));
        p2 = new Point((int) (x2 * tw + tw / 2), (int) (y2 * th + th / 2));
        shotWidth = dmg * 3 + 2;
        shotColor = getColor(c);
    }

    public void incrementTurn() {
        ip.incrementTurn();
    }

    public void hideShot() {
        p1 = null;
        p2 = null;
    }

    private class GridPanel extends JPanel {

        public void paint(Graphics g) {
            int cw = getWidth();
            int ch = getHeight();
            g.setColor(Color.gray);
            g.fillRect(0, 0, cw, ch);
            tw = (double) cw / w;
            th = (double) ch / h;
            g.setColor(Color.black);
            for (int i = 0; i < w; i++) {
                g.drawLine((int) (i * tw), 0, (int) (i * tw), ch);
            }
            for (int i = 0; i < h; i++) {
                g.drawLine(0, (int) (i * th), cw, (int) (i * th));
            }
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    Robot t = gm.getGrid()[i][j];
                    if (t != null) {
                        Point p = expand(i, j);
                        g.drawImage(t.getImage(), p.x + t.getOffsetX(),
                                p.y + t.getOffsetY(), (int) tw, (int) th, null);
                    }
                }
            }
            if (p1 != null) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(shotColor);
                g2.setStroke(new BasicStroke(shotWidth));
                g2.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }
    }

    private class InfoPanel extends JPanel implements ActionListener {

        private JButton start, stop, step;
        private JLabel turns;
        private int numTurns = 0;
        private GridBagConstraints gbc;
        private ArrayList<TeamPanel> tpanels;
        private ArrayList<TeamInfo> infolist;
        private Comparator<TeamInfo> cmp = new Comparator<TeamInfo>() {
            @Override
            public int compare(TeamInfo a, TeamInfo b) {
                int c = b.score - a.score;
                if (c == 0) {
                    return a.team.compareTo(b.team);
                }
                return c;
            }
        };

        public InfoPanel() {
            JPanel buttons = new JPanel();
            setLayout(new GridBagLayout());
            buttons.setLayout(new GridBagLayout());
            gbc = new GridBagConstraints();
            start = new JButton("Start");
            gbc.gridy = 0;
            gbc.gridx = 1;
            turns = new JLabel("Turn 1");
            buttons.add(turns, gbc);
            start.addActionListener(this);
            gbc.gridy = 1;
            gbc.gridx = 0;
            buttons.add(start, gbc);
            step = new JButton("Step");
            step.addActionListener(this);
            gbc.gridx++;
            buttons.add(step, gbc);
            stop = new JButton("Stop");
            stop.addActionListener(this);
            gbc.gridx++;
            buttons.add(stop, gbc);
            gbc.gridx = 0;
            gbc.gridy = 0;
            add(buttons, gbc);
            tpanels = new ArrayList<TeamPanel>();
            infolist = new ArrayList<TeamInfo>();
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getSource() == start) {
                start();
            } else if (actionEvent.getSource() == stop) {
                gm.stop();
            } else if (actionEvent.getSource() == step) {
                step();
            }
        }

        public void incrementTurn() {
            numTurns++;
            turns.setText("Turn " + numTurns);
        }

        public void initializeTeams(Map<String, TeamInfo> m) {
            Set<String> k = m.keySet();
            for (TeamPanel tp : tpanels) {
                this.remove(tp);
            }
            tpanels.clear();
            infolist.clear();
            gbc.gridy = 1;
            int i = 0;
            for (String s : k) {
                infolist.add(m.get(s));
                TeamPanel tp = new TeamPanel(i++);
                add(tp, gbc);
                gbc.gridy++;
                tpanels.add(tp);
            }
            this.revalidate();
        }

        public void paint(Graphics g) {
            Collections.sort(infolist, cmp);
            super.paint(g);
        }
    }

    private class TeamPanel extends JPanel {
        private final JLabel nameLabel;
        private final JLabel iconLabel;
        private Color col;
        private JLabel scoreLabel;
        private int index;

        public TeamPanel(int i) {
            this.index = i;
            col = getColor(ip.infolist.get(index).c);
            super.setLayout(new FlowLayout());
            BufferedImage ico = new BufferedImage(20, 20, BufferedImage.TYPE_3BYTE_BGR);
            Graphics g = ico.getGraphics();
            g.setColor(col);
            g.fillRect(0, 0, 20, 20);
            iconLabel = new JLabel(new ImageIcon(ico));
            add(iconLabel);
            nameLabel = new JLabel(ip.infolist.get(index).team);
            this.add(nameLabel);
            scoreLabel = new JLabel("" + ip.infolist.get(index).score);
            add(scoreLabel);
        }

        public void paint(Graphics g) {
            TeamInfo inf = ip.infolist.get(index);
            col = getColor(inf.c);
            BufferedImage ico = new BufferedImage(20, 20, BufferedImage.TYPE_3BYTE_BGR);
            Graphics gr = ico.getGraphics();
            gr.setColor(col);
            gr.fillRect(0, 0, 20, 20);
            if (inf.dead) {
                if (!inf.c.equals("red"))
                    gr.setColor(Color.RED);
                else
                    gr.setColor(Color.black);
                gr.drawString("X", 5, 15);
            }
            iconLabel.setIcon(new ImageIcon(ico));
            nameLabel.setText(inf.team);
            scoreLabel.setText("Score: " + inf.score);
            this.paintComponents(g);
        }
    }

    public void initializeTeams(Map<String, TeamInfo> m) {
        ip.initializeTeams(m);
    }
}
