// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mikro.mk3.mode;

import de.mossgrabers.controller.maschine.mikro.mk3.controller.MaschineMikroMk3ControlSurface;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;


/**
 * The Play Cursor Position mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PositionMode extends BaseMode
{
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
        final IValueChanger valueChanger = this.model.getValueChanger ();
        final double speed = valueChanger.calcKnobSpeed (value);
        this.model.getTransport ().changePosition (speed > 0, valueChanger.isSlow ());
    }
}