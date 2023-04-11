// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.resource;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.graphics.IImage;

import java.util.HashMap;
import java.util.Map;


/**
 * Get and cache some resources like SVG images.
 *
 * @author Jürgen Moßgraber
 */
public final class ResourceHandler
{
    private static final Map<String, IImage> CACHE = new HashMap<> ();
    private static IHost                     theHost;


    /**
     * Initialize the handler.
     *
     * @param host The controller host
     */
    public static void init (final IHost host)
    {
        theHost = host;

        addSVGImage ("channel/mute.svg");
        addSVGImage ("channel/record_arm.svg");
        addSVGImage ("channel/solo.svg");
        addSVGImage ("channel/solo.svg");

        addSVGImage ("track/audio_track.svg");
        addSVGImage ("track/crossfade_a.svg");
        addSVGImage ("track/crossfade_ab.svg");
        addSVGImage ("track/crossfade_b.svg");
        addSVGImage ("track/group_track.svg");
        addSVGImage ("track/group_track_open.svg");
        addSVGImage ("track/hybrid_track.svg");
        addSVGImage ("track/instrument_track.svg");
        addSVGImage ("track/master_track.svg");
        addSVGImage ("track/multi_layer.svg");
        addSVGImage ("track/return_track.svg");

        addSVGImage ("device/device_analysis.svg");
        addSVGImage ("device/device_audio.svg");
        addSVGImage ("device/device_container.svg");
        addSVGImage ("device/device_drum_machine.svg");
        addSVGImage ("device/device_drum_module.svg");
        addSVGImage ("device/device_generic.svg");
        addSVGImage ("device/device_instrument.svg");
        addSVGImage ("device/device_io.svg");
        addSVGImage ("device/device_note.svg");
        addSVGImage ("device/device_plugin.svg");

        addSVGImage ("pin.svg");
        addSVGImage ("user.svg");
    }


    /**
     * Get a SVG image as an Image object.
     *
     * @param imageName The name of the image
     * @return The buffered image
     */
    public static IImage getSVGImage (final String imageName)
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
