// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.canvas.component;

import java.util.Arrays;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.graphics.IGraphicsConfiguration;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.graphics.IGraphicsInfo;


/**
 * A graph component which is drawn on top of all other components.
 *
 * @author Jürgen Moßgraber
 */
/**
 *
 */
public class GraphOverlayComponent implements IComponent
{
    private final double  x;
    private final double  y;
    private final double  width;
    private final double  height;
    private final ColorEx color;
    private final int []  data;
    private final int     maxValue;


    /**
     * Constructor.
     *
     * @param x The left side of the bounding box
     * @param y The upper side of the bounding box
     * @param width The width of the bounding box
     * @param height The height of the bounding box
     * @param color The color of the line
     * @param data The data to draw
     * @param maxValue The maximum y-value of the data
     */
    public GraphOverlayComponent (final double x, final double y, final double width, final double height, final ColorEx color, final int [] data, final int maxValue)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.data = data;
        this.maxValue = maxValue;
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsInfo info)
    {
        final IGraphicsContext gc = info.getContext ();
        final IGraphicsConfiguration configuration = info.getConfiguration ();

        gc.fillRectangle (this.x - 1, this.y - 1, this.width + 2, this.height + 2, configuration.getColorBorder ());

        // Step sizes for scaling
        final double xStep = this.width / (this.data.length - 1);
        final double yScale = this.height / this.maxValue;

        // Previous point (scaled)
        double prevX = this.x;
        double prevY = this.y + this.height - Math.round (this.data[0] * yScale);

        for (int i = 1; i < this.data.length; i++)
        {
            final double currX = this.x + Math.round (i * xStep);
            final double currY = this.y + this.height - Math.round (this.data[i] * yScale);
            gc.drawLine (prevX, prevY, currX, currY, this.color);
            prevX = currX;
            prevY = currY;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.color == null ? 0 : this.color.hashCode ());
        result = prime * result + Arrays.hashCode (this.data);
        long temp;
        temp = Double.doubleToLongBits (this.height);
        result = prime * result + (int) (temp ^ temp >>> 32);
        result = prime * result + this.maxValue;
        temp = Double.doubleToLongBits (this.width);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits (this.x);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits (this.y);
        result = prime * result + (int) (temp ^ temp >>> 32);
        return result;
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals (final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null || this.getClass () != obj.getClass ())
            return false;
        final GraphOverlayComponent other = (GraphOverlayComponent) obj;
        if (this.color == null)
        {
            if (other.color != null)
                return false;
        }
        else if (!this.color.equals (other.color))
            return false;
        if (!Arrays.equals (this.data, other.data) || Double.doubleToLongBits (this.height) != Double.doubleToLongBits (other.height) || this.maxValue != other.maxValue || Double.doubleToLongBits (this.width) != Double.doubleToLongBits (other.width))
            return false;
        if ((Double.doubleToLongBits (this.x) != Double.doubleToLongBits (other.x)) || (Double.doubleToLongBits (this.y) != Double.doubleToLongBits (other.y)))
            return false;
        return true;
    }
}
