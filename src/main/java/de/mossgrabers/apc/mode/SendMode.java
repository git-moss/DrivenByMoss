// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apc.mode;

import de.mossgrabers.apc.controller.APCControlSurface;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;


/**
 * Panorama knob mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendMode extends BaseMode
{
    private int sendIndex;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param sendIndex The index of the send
     */
    public SendMode (final APCControlSurface surface, final IModel model, final int sendIndex)
    {
        super (surface, model, 2, 0);
        this.sendIndex = sendIndex;
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final int index, final int value)
    {
        final IChannelBank currentTrackBank = this.model.getCurrentTrackBank ();
        if (currentTrackBank instanceof ITrackBank)
            ((ITrackBank) currentTrackBank).setSend (index, this.sendIndex, value);
    }


    /** {@inheritDoc} */
    @Override
    public Integer getValue (final int index)
    {
        if (this.model.isEffectTrackBankActive ())
            return Integer.valueOf (0);
        final ITrack track = this.model.getCurrentTrackBank ().getTrack (index);
        return track.doesExist () ? Integer.valueOf (track.getSends ()[this.sendIndex].getValue ()) : null;
    }
}
