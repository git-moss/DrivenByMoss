// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
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
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        this.model.getTransport ().changePosition (value > 0, this.isSlow);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (int index, boolean isTouched)
    {
        if (isTouched)
            this.isSlow = !this.isSlow;
    }
}