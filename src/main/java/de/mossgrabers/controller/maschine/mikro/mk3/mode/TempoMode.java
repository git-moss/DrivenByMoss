// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
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
        super ("Tempo", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final double speed = this.model.getValueChanger ().calcKnobSpeed (value);
        this.model.getTransport ().changeTempo (speed > 0);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        if (isTouched)
            this.model.getTransport ().tapTempo ();
    }
}