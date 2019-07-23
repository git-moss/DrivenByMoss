// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.display;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.resource.ResourceHandler;
import de.mossgrabers.framework.graphics.Align;
import de.mossgrabers.framework.graphics.IBitmap;
import de.mossgrabers.framework.graphics.IGraphicsConfiguration;
import de.mossgrabers.framework.graphics.IGraphicsDimensions;
import de.mossgrabers.framework.graphics.grid.IGridElement;

import java.util.List;


/**
 * Draws the content of the display based on the model into a bitmap.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class VirtualDisplay
{
    private final DisplayModel           model;
    private final IBitmap                image;
    private final IGraphicsConfiguration configuration;
    private final IGraphicsDimensions    dimensions;


    /**
     * Constructor.
     *
     * @param host The controller host
     * @param model Stores the data for drawing the display
     * @param configuration The configuration to use for drawing, e.g. colors
     * @param dimensions The pre-calculated grid dimension
     * @param windowTitle The title for the preview window
     */
    public VirtualDisplay (final IHost host, final DisplayModel model, final IGraphicsConfiguration configuration, final IGraphicsDimensions dimensions, final String windowTitle)
    {
        this.model = model;
        this.configuration = configuration;
        this.dimensions = dimensions;

        ResourceHandler.init (host);

        this.image = host.createBitmap (dimensions.getWidth (), dimensions.getHeight ());
        this.image.setDisplayWindowTitle (windowTitle);

        this.model.addGridElementChangeListener (this::drawGrid);
    }


    /**
     * Get the drawn image.
     *
     * @return The image
     */
    public IBitmap getImage ()
    {
        return this.image;
    }


    /**
     * Draws the N grid elements of the grid.
     */
    private void drawGrid ()
    {
        this.image.render (gc -> {
            final int width = this.dimensions.getWidth ();
            final int height = this.dimensions.getHeight ();
            final double separatorSize = this.dimensions.getSeparatorSize ();

            // Clear display
            final ColorEx colorBorder = this.configuration.getColorBorder ();
            gc.fillRectangle (0, 0, width, height, colorBorder);

            final ModelInfo info = this.model.getInfo ();
            final List<IGridElement> elements = info.getElements ();
            final int size = elements.size ();
            if (size == 0)
                return;
            final int gridWidth = width / size;
            final double paintWidth = gridWidth - separatorSize;
            final double offsetX = separatorSize / 2.0;

            for (int i = 0; i < size; i++)
                elements.get (i).draw (gc, this.configuration, this.dimensions, i * gridWidth + offsetX, paintWidth, height);

            final String notification = info.getNotification ();
            if (notification == null)
                return;

            final ColorEx colorText = this.configuration.getColorText ();
            gc.drawTextInBounds (notification, 0, 0, width, height, Align.CENTER, colorText, colorBorder, height / 4.0);
        });
    }
}
