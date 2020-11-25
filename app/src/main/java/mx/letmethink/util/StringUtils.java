package mx.letmethink.util;

import javax.swing.text.StyleConstants;

public class StringUtils {

    public static String align(String text, int alignment) {
        if (alignment == StyleConstants.ALIGN_CENTER) {
            return justifyCenter(text);
        } else if (alignment == StyleConstants.ALIGN_RIGHT) {
            return justifyRight(text);
        } else {
            return text;
        }

    }
    public static String justifyCenter(String text) {
        String[] lines = text.split("\n", -1);
        int n = lines.length;
        int maxLength = 0;
        for (int i = 0; i < n; i++) {
            maxLength = Math.max(maxLength, lines[i].length());
        }

        char[][] chars = new char[n][maxLength];
        for (int i = 0; i < n; i++) {
            int spaces = (maxLength - lines[i].length()) / 2;
            int pos = 0;
            while (pos < spaces) {
                chars[i][pos++] = ' ';
            }

            for (int j = 0; j < lines[i].length(); j++) {
                chars[i][pos++] = lines[i].charAt(j);
            }

            while (pos < maxLength) {
                chars[i][pos++] = ' ';
            }
        }

        StringBuilder centered = new StringBuilder();
        for (int i = 0; i < n; i++) {
            centered.append(chars[i]);
            if (i < n - 1) {
                centered.append('\n');
            }
        }

        return centered.toString();
    }

    public static String justifyRight(String text) {
        String[] lines = text.split("\n", -1);
        int n = lines.length;
        int maxLength = 0;
        for (int i = 0; i < n; i++) {
            lines[i] = lines[i].trim();
            maxLength = Math.max(maxLength, lines[i].length());
        }

        char[][] chars = new char[n][maxLength];
        for (int i = 0; i < n; i++) {
            int spaces = maxLength - lines[i].length();
            int pos = 0;
            while (pos < spaces) {
                chars[i][pos++] = ' ';
            }

            for (int j = 0; j < lines[i].length(); j++) {
                chars[i][pos++] = lines[i].charAt(j);
            }
        }

        StringBuilder right = new StringBuilder();
        for (int i = 0; i < n; i++) {
            right.append(chars[i]);
            if (i < n - 1) {
                right.append('\n');
            }
        }

        return right.toString();
    }
}
