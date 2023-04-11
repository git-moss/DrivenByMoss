// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameter;

import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.ITransport;


/**
 * A parameter implementation for changing the play position.
 *
 * @author Jürgen Moßgraber
 */
public class PlayPositionParameter extends AbstractParameterImpl
{
    protected final ITransport         transport;
    protected final IControlSurface<?> surface;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param transport The transport
     * @param surface The control surface
     */
    public PlayPositionParameter (final IValueChanger valueChanger, final ITransport transport, final IControlSurface<?> surface)
    {
        super (valueChanger, 0);

        this.transport = transport;
        this.surface = surface;
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        throw new UnsupportedOperationException ();
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final IValueChanger valueChanger, final int value)
    {
        throw new UnsupportedOperationException ();
    }


    /** {@inheritDoc} */
    @Override
    public void setNormalizedValue (final double value)
    {
        throw new UnsupportedOperationException ();
    }


    /** {@inheritDoc} */
    @Override
    public void changeValue (final IValueChanger valueChanger, final int value)
    {
        this.transport.changePosition (valueChanger.isIncrease (value), this.surface.isKnobSensitivitySlow ());
    }


    /** {@inheritDoc} */
    @Override
    public void setValueImmediatly (final int value)
    {
        this.setValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void inc (final double increment)
    {
        this.transport.changePosition (increment > 0, this.surface.isKnobSensitivitySlow ());
    }


    /** {@inheritDoc} */
    @Override
    public void resetValue ()
    {
        this.setValue (0);
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue ()
    {
        return this.transport.getPositionText ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return "Play Position";
    }
}
