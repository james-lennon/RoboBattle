import robobattle.main.RoboBattleArena;

import java.awt.*;

/**
 * Created by jameslennon on 1/30/14.
 */
public class Main {

    public static void main(String[] args) {
        //Initialize the Arena
        RoboBattleArena rb = new RoboBattleArena(8, 8);
        rb.addMainframe("James", new Point(0, 0), new CowardStrategy());
        rb.addMainframe("Alex", new Point(7, 7), new SimpleStrategy());
        //Show the Arena
        rb.show();
    }
}
