package mx.letmethink.util;

import java.awt.Color;

public class Utils {
    public static Color decode(String hex) {
        if (hex == null) {
            return null;
        }

        if (!hex.matches("0x[0-9a-fA-F]{6,6}([0-9a-fA-F]{2,2})?")) {
            return null;
        }

        int r = Integer.parseInt("" + hex.charAt(2) + hex.charAt(3), 16);
        int g = Integer.parseInt("" + hex.charAt(4) + hex.charAt(5), 16);
        int b = Integer.parseInt("" + hex.charAt(6) + hex.charAt(7), 16);
        int a = 255;
        if (hex.length() == 10) {
            a = Integer.parseInt("" + hex.charAt(8) + hex.charAt(9), 16);
        }

        return new Color(r, g, b, a);
    }

    public static String encode(Color color) {
        if (color == null) {
            return null;
        }

        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int a = color.getAlpha();
        return String.format("0x%02x%02x%02x%02x", r, g, b, a);
    }
}
