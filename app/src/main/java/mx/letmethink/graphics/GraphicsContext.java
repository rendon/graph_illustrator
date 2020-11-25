package mx.letmethink.graphics;

import mx.letmethink.util.MathUtils;

public class GraphicsContext {
    public static final double DEFAULT_REAL_WIDTH   = 50;
    public static final double DEFAULT_REAL_HEIGHT  = 50;

    private final double[] factors;
    private double pixelSize;
    private double realWidth;
    private double realHeight;
    private double gridInterval;

    private int factorIndex;
    private int centerX;
    private int centerY;
    private int maxX;
    private int maxY;

    public GraphicsContext()
    {
        setRealWidth(DEFAULT_REAL_WIDTH);
        setRealHeight(DEFAULT_REAL_HEIGHT);

        // Grid drawing settings
        gridInterval = 0.5;

        factors = new double[] {2, 2, 2.5};
        factorIndex = 0;
    }

    /**
     * Initialize the variables needed to use isotropic mapping mode
     * (Computer Graphics for Java Programmers, 2nd. Edition, Leen Ammeraaland,
     * Kang Zhang).
     **/
    public void init(int panelWidth, int panelHeight)
    {
        maxX = panelWidth - 1;
        maxY = panelHeight - 1;

        centerX = maxX / 2;
        centerY = maxY / 2;

        pixelSize = realWidth / Math.max(maxX, maxY);

        factorIndex = 0;
        gridInterval = 0.5;

        int k = factors.length;
        int w = ix(gridInterval) - ix(0);
        while (w < 50 || w > 150) {
            if (w < 50) {
                gridInterval *= factors[factorIndex];
                factorIndex = (factorIndex + 1) % k;
            } else if (w > 150) {
                factorIndex = (factorIndex - 1 + k) % k;
                gridInterval /= factors[factorIndex];
            }

            w = ix(gridInterval) - ix(0);
        }
    }

    /**
     * Returns the device-coordinate of x.
     *
     * @param x x-coordinate in logical-coordinates
     * @return an integer with the device-coordinate of x
     */
    public int ix(double x)
    {
        return MathUtils.round(centerX + x / pixelSize);
    }

    /**
     * Returns the device-coordinate of y.
     *
     * @param y y-coordinate in logical-coordinates
     * @return an integer with the device-coordinate of y
     */
    public int iy(double y)
    {
        return MathUtils.round(centerY - y / pixelSize);
    }

    /**
     * Returns the device-coordinate of x using a particular pixel size.
     *
     * @param x  x-coordinate in logical-coordinates
     * @param ps pixel size
     * @return an integer with the device-coordinate of x
     */
    public int ix(double x, double ps)
    {
        return MathUtils.round(centerX + x / ps);
    }

    /**
     * Returns the device-coordinate of y using a particular pixel size.
     *
     * @param y  y-coordinate in logical-coordinates
     * @param ps pixel size
     * @return an integer with the device-coordinate of y
     */
    public int iy(double y, double ps)
    {
        return MathUtils.round(centerY - y / ps);
    }

    /**
     * Returns the logical-coordinate of x.
     *
     * @param x x-coordinate in device-coordinates
     * @return double, logical coordinate of x
     */
    public double fx(int x)
    {
        return (double) (x - centerX) * pixelSize;
    }

    /**
     * Returns the logical-coordinate of y.
     *
     * @param y y-coordinate in device-coordinates
     * @return double, logical coordinate of y
     */
    public double fy(int y)
    {
        return (double) (centerY - y) * pixelSize;
    }

    /**
     * Zooms out the plane ten percent with origin in mouse click.
     *
     * @param mx X coordinate of mouse click.
     * @param my Y coordinate of mouse click.
     */
    public void zoomOut(int mx, int my)
    {
        if (pixelSize > 0.5) {
            return;
        }

        double ps = pixelSize;
        Point2D previous = new Point2D(fx(mx), fy(my));

        pixelSize += pixelSize / 10;

        int dx = ix(previous.x()) - ix(previous.x(), ps);
        int dy = iy(previous.y()) - iy(previous.y(), ps);

        centerX -= dx;
        centerY -= dy;
    }

    /**
     * Zooms in the plane ten percent with origin in mouse click.
     *
     * @param mx X coordinate of mouse click.
     * @param my Y coordinate of mouse click.
     */
    public void zoomIn(int mx, int my)
    {
        if (pixelSize < 1e-2) {
            return;
        }

        double ps = pixelSize;
        Point2D previous = new Point2D(fx(mx), fy(my));
        pixelSize -= pixelSize / 10;

        int dx = ix(previous.x()) - ix(previous.x(), ps);
        int dy = iy(previous.y()) - iy(previous.y(), ps);

        centerX -= dx;
        centerY -= dy;
    }

    /**
     * Restore the original scale.
     */
    public void resetZoom(int width, int height)
    {
        setRealWidth(DEFAULT_REAL_WIDTH);
        setRealWidth(DEFAULT_REAL_HEIGHT);
        init(width, height);
    }

    public void setRealWidth(double rw)
    {
        realWidth = rw;
    }

    public void setRealHeight(double rh)
    {
        realHeight = rh;
    }

    public int getCenterX()
    {
        return centerX;
    }

    public int getCenterY()
    {
        return centerY;
    }

    public void setCenterX(int value)
    {
        centerX = value;
    }

    public void setCenterY(int value)
    {
        centerY = value;
    }

    public double[] computeGridParams(int panelWidth, int panelHeight)
    {
        double left = fx(0);
        double top = fy(0);
        double right = fx(panelWidth - 1);
        double bottom = fy(panelHeight - 1);

        int w = ix(gridInterval) - ix(0);
        if (w < 50) {
            gridInterval *= factors[factorIndex];
            factorIndex = (factorIndex + 1) % factors.length;
        } else if (w > 150) {
            factorIndex = (factorIndex - 1 + factors.length) % factors.length;
            gridInterval /= factors[factorIndex];
        }

        int cX = ix(0);
        int interval = java.lang.Math.max(1, ix(gridInterval) - cX);
        int mod = cX % interval;
        double startX = fx(mod) - (fx(mod) % gridInterval) - gridInterval;

        int cY = iy(0);
        interval = java.lang.Math.max(1, iy(gridInterval) - cY);
        mod = cY % interval;
        double startY = fy(mod) - (fy(mod) % gridInterval) + gridInterval;

        double[] params = new double[7];
        params[0] = gridInterval;
        params[1] = left;
        params[2] = right;
        params[3] = top;
        params[4] = bottom;
        params[5] = startX;
        params[6] = startY;

        return params;
    }

}
