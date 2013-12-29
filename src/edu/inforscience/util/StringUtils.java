package edu.inforscience.util;

import javax.swing.text.StyleConstants;

public class StringUtils {

    public static String align(String text, int alignment)
    {
        if (alignment == StyleConstants.ALIGN_CENTER)
            return justifyCenter(text);
        else if (alignment == StyleConstants.ALIGN_RIGHT)
            return justifyRight(text);
        else
            return text;

    }
    public static String justifyCenter(String text)
    {
        String[] lines = text.split("\n");
        int n = lines.length;
        int maxLength = 0;
        for (int i = 0; i < n; i++) {
            lines[i] = lines[i].trim();
            maxLength = Math.max(maxLength, lines[i].length());
        }

        for (int i = 0; i < n; i++) {
            int spaces = (maxLength - lines[i].length()) / 2;
            String s = "";
            for (int j = 0; j < spaces; j++)
                s += " ";
            lines[i] = s + lines[i];
        }

        String centered = "";
        for (int i = 0; i < n; i++) {
            centered += lines[i];
            if (i < n - 1)
                centered += "\n";
        }

        return centered;
    }

    public static String justifyRight(String text)
    {
        String[] lines = text.split("\n");
        int n = lines.length;
        int maxLength = 0;
        for (int i = 0; i < n; i++) {
            lines[i] = lines[i].trim();
            maxLength = Math.max(maxLength, lines[i].length());
        }

        for (int i = 0; i < n; i++) {
            String s = "";
            while (s.length() + lines[i].length() < maxLength)
                s += " ";

            lines[i] = s + lines[i];
        }

        String right = "";
        for (int i = 0; i < n; i++) {
            right += lines[i];
            if (i < n - 1)
                right += "\n";
        }

        return right;
    }
}
