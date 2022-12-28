// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one;

import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.parameter.PlayPositionParameter;


/**
 * A parameter implementation for changing the play position. Additionally, converts an absolute
 * knob into relative movements.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ElectraOnePlayPositionParameter extends PlayPositionParameter
{
    private double lastScrollValue = 0.5;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param transport The transport
     * @param surface The control surface
     */
    public ElectraOnePlayPositionParameter (final IValueChanger valueChanger, final ITransport transport, final IControlSurface<?> surface)
    {
        super (valueChanger, transport, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void setNormalizedValue (final double value)
    {
        final double diff = value - this.lastScrollValue;
        this.lastScrollValue = value;

        this.transport.changePosition (diff > 0, this.surface.isKnobSensitivitySlow ());

        if (value == 0 || value == 1)
        {
            this.surface.getMidiOutput ().sendCCEx (15, 25, 64);
            this.lastScrollValue = 64;
        }
    }
}
