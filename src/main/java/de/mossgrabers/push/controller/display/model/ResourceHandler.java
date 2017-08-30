// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.controller.display.model;

import com.bitwig.extension.api.Image;
import com.bitwig.extension.controller.api.ControllerHost;

import java.util.HashMap;
import java.util.Map;


/**
 * Get and cache some resources like SVG images.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public final class ResourceHandler
{
    private static final Map<String, Image> CACHE = new HashMap<> ();
    private static ControllerHost           theHost;


    /**
     * Initialise the handler.
     *
     * @param host The controller host
     */
    public static void init (final ControllerHost host)
    {
        theHost = host;
    }


    /**
     * Get a SVG image as an Image object.
     *
     * @param imageName The name of the image
     * @return The buffered image
     */
    public static Image getSVGImage (final String imageName)
    {
        return CACHE.get (imageName);
    }


    /**
     * Load and cache an image.
     *
     * @param imageName The name (absolute path) of the image
     */
    public static void addSVGImage (final String imageName)
    {
        CACHE.put (imageName, theHost.loadSVG (imageName, 1));
    }


    /**
     * Private due to helper class.
     */
    private ResourceHandler ()
    {
        // Intentionally empty
    }
}
