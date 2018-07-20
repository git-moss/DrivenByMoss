// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apc.mode;

import de.mossgrabers.controller.apc.controller.APCControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;


/**
 * Panorama knob mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PanMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public PanMode (final APCControlSurface surface, final IModel model)
    {
        super (surface, model, 3, 64);
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final int index, final int value)
    {
        this.model.getCurrentTrackBank ().getItem (index).setPan (value);
    }


    /** {@inheritDoc} */
    @Override
    public Integer getValue (final int index)
    {
        final ITrack track = this.model.getCurrentTrackBank ().getItem (index);
        return track.doesExist () ? Integer.valueOf (track.getPan ()) : null;
    }
}