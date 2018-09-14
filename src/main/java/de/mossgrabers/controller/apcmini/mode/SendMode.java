// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apcmini.mode;

import de.mossgrabers.controller.apcmini.controller.APCminiControlSurface;
import de.mossgrabers.framework.daw.IModel;


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
    public SendMode (final int sendIndex, final APCminiControlSurface surface, final IModel model)
    {
        super (surface, model);
        this.sendIndex = sendIndex;
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        this.model.getCurrentTrackBank ().getItem (index).getSendBank ().getItem (this.sendIndex).setValue (value);
    }
}