// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mikro.mk3.mode;

import de.mossgrabers.controller.maschine.mikro.mk3.controller.MaschineMikroMk3ControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;


/**
 * Mode for editing Send volumes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendMode extends BaseMode
{
    private int sendIndex;


    /**
     * Constructor.
     * 
     * @param sendIndex The send index
     * @param surface The control surface
     * @param model The model
     */
    public SendMode (final int sendIndex, final MaschineMikroMk3ControlSurface surface, final IModel model)
    {
        super (surface, model);
        this.sendIndex = sendIndex;
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (selectedTrack != null)
            selectedTrack.getSendBank ().getItem (this.sendIndex).changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (final int index, final boolean isTouched)
    {
        if (!isTouched)
            return;
        final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (selectedTrack != null)
            selectedTrack.getSendBank ().getItem (this.sendIndex).resetValue ();
    }
}