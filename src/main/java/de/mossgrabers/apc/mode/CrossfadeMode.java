// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apc.mode;

import de.mossgrabers.apc.controller.APCControlSurface;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.daw.data.TrackData;


/**
 * Crossfade A|B knob mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CrossfadeMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public CrossfadeMode (final APCControlSurface surface, final Model model)
    {
        super (surface, model, 3, 64);
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final int index, final int value)
    {
        this.model.getCurrentTrackBank ().setPan (index, value);
    }


    /** {@inheritDoc} */
    @Override
    public Integer getValue (final int index)
    {
        final TrackData track = this.model.getCurrentTrackBank ().getTrack (index);
        if (!track.doesExist ())
            return null;

        final String crossfadeMode = track.getCrossfadeMode ();
        if ("A".equals (crossfadeMode))
            return Integer.valueOf (0);
        if ("B".equals (crossfadeMode))
            return Integer.valueOf (127);
        return Integer.valueOf (64);
    }
}