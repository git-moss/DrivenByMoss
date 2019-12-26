// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mikro.mk3.mode;

import de.mossgrabers.controller.maschine.mikro.mk3.controller.MaschineMikroMk3ControlSurface;
import de.mossgrabers.framework.daw.IModel;


/**
 * The Play Cursor Position mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PositionMode extends BaseMode
{
    private boolean isSlow = false;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public PositionMode (final MaschineMikroMk3ControlSurface surface, final IModel model)
    {
        super ("Position", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final double speed = this.model.getValueChanger ().calcKnobSpeed (value);
        this.model.getTransport ().changePosition (speed > 0, this.isSlow);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        if (isTouched)
            this.toggleSpeed ();
    }


    /**
     * Toggle the scroll speed.
     */
    public void toggleSpeed ()
    {
        this.isSlow = !this.isSlow;
    }
}