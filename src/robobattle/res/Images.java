package robobattle.res;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.HashMap;

public class Images {

    private static final String[] list = new String[]{"body-medium.png",
            "body-strong.png",
            "body-weak.png",
            "gun-medium-black.png",
            "gun-medium-blue.png",
            "gun-medium-brown.png",
            "gun-medium-green.png",
            "gun-medium-red.png",
            "gun-medium-yellow.png",
            "gun-strong-black.png",
            "gun-strong-blue.png",
            "gun-strong-brown.png",
            "gun-strong-green.png",
            "gun-strong-red.png",
            "gun-strong-yellow.png",
            "gun-weak-black.png",
            "gun-weak-blue.png",
            "gun-weak-brown.png",
            "gun-weak-green.png",
            "gun-weak-red.png",
            "gun-weak-yellow.png",
            "mainframe-base.png",
            "mainframe-black.png",
            "mainframe-blue.png",
            "mainframe-green.png",
            "mainframe-red.png",
            "mainframe-yellow.png",
            "icon.png"};

    private static HashMap<String, Image> images;

    public static void load() {
        images = new HashMap<String, Image>();
        try {
            for (String s : list) {
                Image img = ImageIO.read(Images.class.getResource(s));
                s = s.substring(0, s.lastIndexOf('.'));
                images.put(s, img);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Image getImage(String key) {
        return images.get(key);
    }

}
