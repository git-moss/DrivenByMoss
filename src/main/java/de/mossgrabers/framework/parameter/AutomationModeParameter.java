// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameter;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.constants.AutomationMode;


/**
 * A parameter implementation for changing the automation mode.
 *
 * @author Jürgen Moßgraber
 */
public class AutomationModeParameter extends AbstractParameterImpl
{
    protected final ITransport transport;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param transport The transport
     */
    public AutomationModeParameter (final IValueChanger valueChanger, final ITransport transport)
    {
        super (valueChanger, 0);

        this.transport = transport;
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
        this.inc (valueChanger.isIncrease (value) ? 1 : -1);
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
        if (increment > 0)
            this.transport.nextAutomationWriteMode (false);
        else
            this.transport.previousAutomationWriteMode ();
    }


    /** {@inheritDoc} */
    @Override
    public void resetValue ()
    {
        this.transport.setAutomationWriteMode (AutomationMode.READ);
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue ()
    {
        return this.transport.getAutomationWriteMode ().getLabel ();
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
        return "Automation Mode";
    }
}
