package de.mossgrabers.push.controller.display.model;

import de.mossgrabers.push.controller.display.model.grid.GridElement;

import com.bitwig.extension.api.Bitmap;
import com.bitwig.extension.api.BitmapFormat;
import com.bitwig.extension.api.GraphicsOutput;
import com.bitwig.extension.api.GraphicsOutput.AntialiasMode;
import com.bitwig.extension.controller.api.ControllerHost;

import java.awt.Color;
import java.io.IOException;
import java.util.List;


/**
 * Draws the content of the display based on the model into a bitmap.
 *
 * Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class VirtualDisplay
{
    private static final int     DISPLAY_WIDTH     = 960;
    private static final int     DISPLAY_HEIGHT    = 160;

    private final DisplayModel   model;
    private final Bitmap         image1;
    private final Bitmap         image2;
    private Bitmap               currentImage;
    private final Object         imageExchangeLock = new Object ();
    private final LayoutSettings layoutSettings;
    private final GraphicsOutput graphicsOutput1;
    private final GraphicsOutput graphicsOutput2;


    /**
     * Constructor.
     * 
     * @param host The controller host
     * @param model Stores the data for drawing the display
     * @param layoutSettings The layout settings to use for drawing
     */
    public VirtualDisplay (final ControllerHost host, final DisplayModel model, final LayoutSettings layoutSettings)
    {
        this.model = model;

        ResourceHandler.init (host);
        ResourceHandler.addSVGImage ("channel/mute.svg");
        ResourceHandler.addSVGImage ("channel/record_arm.svg");
        ResourceHandler.addSVGImage ("channel/solo.svg");
        ResourceHandler.addSVGImage ("channel/solo.svg");
        ResourceHandler.addSVGImage ("track/audio_track.svg");
        ResourceHandler.addSVGImage ("track/crossfade_a.svg");
        ResourceHandler.addSVGImage ("track/crossfade_ab.svg");
        ResourceHandler.addSVGImage ("track/crossfade_b.svg");
        ResourceHandler.addSVGImage ("track/group_track.svg");
        ResourceHandler.addSVGImage ("track/hybrid_track.svg");
        ResourceHandler.addSVGImage ("track/instrument_track.svg");
        ResourceHandler.addSVGImage ("track/master_track.svg");
        ResourceHandler.addSVGImage ("track/multi_layer.svg");
        ResourceHandler.addSVGImage ("track/return_track.svg");

        this.image1 = host.createBitmap (DISPLAY_WIDTH, DISPLAY_HEIGHT, BitmapFormat.ARGB32);
        this.image2 = host.createBitmap (DISPLAY_WIDTH, DISPLAY_HEIGHT, BitmapFormat.ARGB32);
        this.currentImage = this.image1;

        this.graphicsOutput1 = this.image1.createGraphicsOutput ();
        this.graphicsOutput2 = this.image2.createGraphicsOutput ();

        configureGraphics (this.graphicsOutput1);
        configureGraphics (this.graphicsOutput2);

        this.model.addGridElementChangeListener (this::redrawGrid);
        this.layoutSettings = layoutSettings;

        // TODO
        // this.layoutSettings.addFontChangeListener ( (observable, oldValue, newValue) ->
        // this.redrawGrid ());
        // this.layoutSettings.addColorChangeListener ( (observable, oldValue, newValue) ->
        // this.redrawGrid ());
    }


    /**
     * Redraw the display.
     */
    public void redrawGrid ()
    {
        synchronized (this.imageExchangeLock)
        {
            final boolean isOne = this.currentImage == this.image1;
            final Bitmap drawImage = isOne ? this.image2 : this.image1;
            this.drawGrid (isOne ? this.graphicsOutput1 : this.graphicsOutput2);
            this.currentImage = drawImage;
        }
    }


    /**
     * Get the drawn image.
     *
     * @return The image
     */
    public Bitmap getImage ()
    {
        return this.currentImage;
    }


    /**
     * Draws the N grid elements of the grid.
     *
     * @param gc The graphics context to draw into
     */
    public void drawGrid (final GraphicsOutput gc)
    {
        // Clear display
        final Color borderColor = this.layoutSettings.getBorderColor ();
        gc.setColor (borderColor.getRed () / 255.0, borderColor.getGreen () / 255.0, borderColor.getBlue () / 255.0);

        // gc.setColor (0.6, 0.6, 0.6, 1);

        gc.paint ();

        final List<GridElement> elements = this.model.getGridElements ();
        final int size = elements.size ();
        if (size == 0)
            return;
        final int gridWidth = DISPLAY_WIDTH / size;
        final double paintWidth = gridWidth - GridElement.SEPARATOR_SIZE;
        final double offsetX = GridElement.SEPARATOR_SIZE / 2.0;

        try
        {
            for (int i = 0; i < size; i++)
                elements.get (i).draw (gc, i * gridWidth + offsetX, paintWidth, DISPLAY_HEIGHT, this.layoutSettings);
        }
        catch (final IOException ex)
        {
            // TODO
            // this.model.addLogMessage ("Could not load SVG image: " + ex.getLocalizedMessage ());
        }
    }


    /**
     * Makes several graphic settings on the graphics output.
     *
     * @param graphicsOutput The graphics output to configure
     */
    private static void configureGraphics (final GraphicsOutput graphicsOutput)
    {
        graphicsOutput.setAntialias (AntialiasMode.BEST);
        // TODO
        // g.setRenderingHint (RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        // g.setRenderingHint (RenderingHints.KEY_FRACTIONALMETRICS,
        // RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }
}
