// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.canvas.component;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.graphics.Align;
import de.mossgrabers.framework.graphics.IGraphicsConfiguration;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.graphics.IGraphicsDimensions;
import de.mossgrabers.framework.graphics.IGraphicsInfo;

import java.util.Arrays;
import java.util.List;


/**
 * A component which contains several text items. Each item can be selected.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SceneListGridElement implements IComponent
{
    private final ColorEx [] colors;
    private final String []  names;
    private final boolean [] exists;
    private final boolean [] isSelecteds;


    /**
     * Constructor.
     *
     * @param scenes The scenes
     */
    public SceneListGridElement (final List<IScene> scenes)
    {
        final int size = scenes.size ();

        this.colors = new ColorEx [size];
        this.names = new String [size];
        this.exists = new boolean [size];
        this.isSelecteds = new boolean [size];

        for (int i = 0; i < size; i++)
        {
            final IScene scene = scenes.get (i);
            this.colors[i] = scene.getColor ();
            this.names[i] = scene.getName ();
            this.exists[i] = scene.doesExist ();
            this.isSelecteds[i] = scene.isSelected ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsInfo info)
    {
        final IGraphicsContext gc = info.getContext ();
        final IGraphicsDimensions dimensions = info.getDimensions ();
        final IGraphicsConfiguration configuration = info.getConfiguration ();
        final double left = info.getBounds ().left ();
        final double width = info.getBounds ().width ();
        final double height = info.getBounds ().height ();

        final double separatorSize = dimensions.getSeparatorSize ();
        final double inset = dimensions.getInset ();

        final int size = this.colors.length;
        final double itemLeft = left + separatorSize;
        final double itemWidth = width - separatorSize;
        final double itemHeight = height / size;

        final ColorEx textColor = configuration.getColorText ();
        final ColorEx borderColor = configuration.getColorBackgroundLighter ();

        for (int i = 0; i < size; i++)
        {
            final double itemTop = i * itemHeight;

            final ColorEx backgroundColor = this.colors[i];
            gc.fillRectangle (itemLeft, itemTop + separatorSize, itemWidth, itemHeight - 2 * separatorSize, backgroundColor);
            if (this.exists[i])
                gc.drawTextInBounds (this.names[i], itemLeft + inset, itemTop - 1, itemWidth - 2 * inset, itemHeight, Align.LEFT, ColorEx.calcContrastColor (backgroundColor), itemHeight / 2);
            gc.strokeRectangle (itemLeft, itemTop + separatorSize, itemWidth, itemHeight - 2 * separatorSize, this.isSelecteds[i] ? textColor : borderColor, this.isSelecteds[i] ? 2 : 1);
        }
    }


    /** {@inheritDoc} */
    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode (this.colors);
        result = prime * result + Arrays.hashCode (this.exists);
        result = prime * result + Arrays.hashCode (this.isSelecteds);
        result = prime * result + Arrays.hashCode (this.names);
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
        final SceneListGridElement other = (SceneListGridElement) obj;
        if (!Arrays.equals (this.colors, other.colors) || !Arrays.equals (this.exists, other.exists) || !Arrays.equals (this.isSelecteds, other.isSelecteds))
            return false;
        return Arrays.equals (this.names, other.names);
    }
}
