package edu.inforscience.util;
public class MathUtils {
    /**
     * Returns n rounded to the nearest integer.
     *
     * Q: Why not Math.round()?
     * A: I need n to be a double and the result to be an int, not long.
     * @param n a double number
     * @return an integer rounded to the nearest integer
     */
    public static int round(double n)
    {
        return (int) Math.floor(n + 0.5);
    }
}
