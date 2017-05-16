// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apcmini.mode;

import de.mossgrabers.apcmini.controller.APCminiControlSurface;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.TrackBankProxy;


/**
 * The send mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendMode extends BaseMode
{
    private int sendIndex;


    /**
     * Constructor.
     *
     * @param sendIndex The index of the send
     * @param surface The control surface
     * @param model The model
     */
    public SendMode (final int sendIndex, final APCminiControlSurface surface, final Model model)
    {
        super (surface, model);
        this.sendIndex = sendIndex;
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        final AbstractTrackBankProxy currentTrackBank = this.model.getCurrentTrackBank ();
        if (currentTrackBank instanceof TrackBankProxy)
            ((TrackBankProxy) currentTrackBank).setSend (index, this.sendIndex, value);
    }
}