// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mikro.mk3.mode;

import de.mossgrabers.controller.maschine.mikro.mk3.controller.MaschineMikroMk3ControlSurface;
import de.mossgrabers.framework.daw.IModel;


/**
 * The Tempo mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TempoMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public TempoMode (final MaschineMikroMk3ControlSurface surface, final IModel model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        this.model.getTransport ().changeTempo (value > 0);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (int index, boolean isTouched)
    {
        if (isTouched)
            this.model.getTransport ().tapTempo ();
    }
}