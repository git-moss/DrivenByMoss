// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.controller.display.model;

import de.mossgrabers.framework.daw.resource.ResourceHandler;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.display.model.grid.GridElement;

import com.bitwig.extension.api.Bitmap;
import com.bitwig.extension.api.BitmapFormat;
import com.bitwig.extension.api.GraphicsOutput;
import com.bitwig.extension.api.GraphicsOutput.AntialiasMode;
import com.bitwig.extension.controller.api.ControllerHost;

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
    private final Bitmap            image;
    private final PushConfiguration configuration;


    /**
     * Constructor.
     *
     * @param host The controller host
     * @param model Stores the data for drawing the display
     * @param configuration The configuration to use for drawing, e.g. colors
     */
    public VirtualDisplay (final ControllerHost host, final DisplayModel model, final PushConfiguration configuration)
    {
        this.model = model;

        ResourceHandler.init (host);

        this.image = host.createBitmap (DISPLAY_WIDTH, DISPLAY_HEIGHT, BitmapFormat.ARGB32);
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
    public Bitmap getImage ()
    {
        return this.image;
    }


    /**
     * Draws the N grid elements of the grid.
     *
     * @param image The image to draw to
     */
    public void drawGrid (final Bitmap image)
    {
        image.render (gc -> {
            configureGraphics (gc);

            // Clear display
            gc.setColor (this.configuration.getColorBorder ());
            gc.rectangle (0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT);
            gc.fill ();

            final List<GridElement> elements = this.model.getGridElements ();
            final int size = elements.size ();
            if (size == 0)
                return;
            final int gridWidth = DISPLAY_WIDTH / size;
            final double paintWidth = gridWidth - GridElement.SEPARATOR_SIZE;
            final double offsetX = GridElement.SEPARATOR_SIZE / 2.0;

            for (int i = 0; i < size; i++)
                elements.get (i).draw (gc, i * gridWidth + offsetX, paintWidth, DISPLAY_HEIGHT, this.configuration);
        });
    }


    /**
     * Makes several graphic settings on the graphics output.
     *
     * @param graphicsOutput The graphics output to configure
     */
    private static void configureGraphics (final GraphicsOutput graphicsOutput)
    {
        graphicsOutput.setAntialias (AntialiasMode.BEST);
    }
}
