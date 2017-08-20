package de.mossgrabers.push.controller.display.model;

import com.bitwig.extension.api.Image;
import com.bitwig.extension.controller.api.ControllerHost;

import java.util.HashMap;
import java.util.Map;


public class ResourceHandler
{
    private static final Map<String, Image> CACHE = new HashMap<> ();
    private static ControllerHost           theHost;


    public static void init (final ControllerHost host)
    {
        theHost = host;
    }


    /**
     * TODO
     * 
     * Get a SVG image as a buffered image. The image is expected to be monochrome: 1 color and the
     * a transparent background. The given color replaces the color of the image. The images are
     * cached by name and color.
     *
     * @param imageName The name (absolute path) of the image
     * @return The buffered image
     */
    public static Image getSVGImage (final String imageName)
    {
        return CACHE.get (imageName);
    }


    public static void addSVGImage (final String imageName)
    {
        final Image image = theHost.loadSVG (imageName, 1);
        CACHE.put (imageName, image);
    }


    private ResourceHandler ()
    {
    }
}
