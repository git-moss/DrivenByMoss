// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.canvas.component;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.graphics.Align;
import de.mossgrabers.framework.graphics.IGraphicsConfiguration;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.graphics.IGraphicsDimensions;
import de.mossgrabers.framework.graphics.IGraphicsInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * A component which contains several text items. Each item can be selected.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SceneListGridElement implements IComponent
{
    private final List<IScene> scenes;


    /**
     * Constructor.
     *
     * @param scenes The scenes
     */
    public SceneListGridElement (final List<IScene> scenes)
    {
        this.scenes = new ArrayList<> (scenes);
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsInfo info)
    {
        final IGraphicsContext gc = info.getContext ();
        final IGraphicsDimensions dimensions = info.getDimensions ();
        final IGraphicsConfiguration configuration = info.getConfiguration ();
        final double left = info.getBounds ().getLeft ();
        final double width = info.getBounds ().getWidth ();
        final double height = info.getBounds ().getHeight ();

        final double separatorSize = dimensions.getSeparatorSize ();
        final double inset = dimensions.getInset ();

        final int size = this.scenes.size ();
        final double itemLeft = left + separatorSize;
        final double itemWidth = width - separatorSize;
        final double itemHeight = height / size;

        final ColorEx textColor = configuration.getColorText ();
        final ColorEx borderColor = configuration.getColorBackgroundLighter ();

        for (int i = 0; i < size; i++)
        {
            final double itemTop = i * itemHeight;

            final IScene scene = this.scenes.get (i);
            final ColorEx backgroundColor = new ColorEx (scene.getColor ());
            gc.fillRectangle (itemLeft, itemTop + separatorSize, itemWidth, itemHeight - 2 * separatorSize, backgroundColor);
            if (scene.doesExist ())
                gc.drawTextInBounds (scene.getName (), itemLeft + inset, itemTop - 1, itemWidth - 2 * inset, itemHeight, Align.LEFT, ColorEx.calcContrastColor (backgroundColor), itemHeight / 2);
            gc.strokeRectangle (itemLeft, itemTop + separatorSize, itemWidth, itemHeight - 2 * separatorSize, scene.isSelected () ? textColor : borderColor, scene.isSelected () ? 2 : 1);
        }
    }
}
