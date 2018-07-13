// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.controller.display;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.display.grid.GridElement;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.resource.ResourceHandler;
import de.mossgrabers.framework.graphics.Align;
import de.mossgrabers.framework.graphics.IBitmap;

import java.util.List;


/**
 * Draws the content of the display based on the model into a bitmap.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class VirtualDisplay
{
    private static final int        DISPLAY_WIDTH  = 960;
    private static final int        DISPLAY_HEIGHT = 160;

    private final DisplayModel      model;
    private final IBitmap           image;
    private final PushConfiguration configuration;


    /**
     * Constructor.
     *
     * @param host The controller host
     * @param model Stores the data for drawing the display
     * @param configuration The configuration to use for drawing, e.g. colors
     */
    public VirtualDisplay (final IHost host, final DisplayModel model, final PushConfiguration configuration)
    {
        this.model = model;

        ResourceHandler.init (host);

        this.image = host.createBitmap (DISPLAY_WIDTH, DISPLAY_HEIGHT);
        this.image.setDisplayWindowTitle ("Push 2 Display");

        this.model.addGridElementChangeListener (this::redrawGrid);
        this.configuration = configuration;
    }


    /**
     * Redraw the display.
     */
    public void redrawGrid ()
    {
        this.drawGrid (this.image);
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
     *
     * @param bitmap The bitmap to draw to
     */
    private void drawGrid (final IBitmap bitmap)
    {
        bitmap.render (gc -> {
            // Clear display
            final ColorEx colorBorder = this.configuration.getColorBorder ();
            gc.fillRectangle (0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT, colorBorder);

            final ModelInfo info = this.model.getInfo ();
            final List<GridElement> elements = info.getElements ();
            final int size = elements.size ();
            if (size == 0)
                return;
            final int gridWidth = DISPLAY_WIDTH / size;
            final double paintWidth = gridWidth - GridElement.SEPARATOR_SIZE;
            final double offsetX = GridElement.SEPARATOR_SIZE / 2.0;

            for (int i = 0; i < size; i++)
                elements.get (i).draw (gc, i * gridWidth + offsetX, paintWidth, DISPLAY_HEIGHT, this.configuration);

            final String notification = info.getNotification ();
            if (notification == null)
                return;

            final ColorEx colorText = this.configuration.getColorText ();
            gc.drawTextInBounds (notification, 0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT, Align.CENTER, colorText, colorBorder, DISPLAY_HEIGHT / 4.0);
        });
    }
}
