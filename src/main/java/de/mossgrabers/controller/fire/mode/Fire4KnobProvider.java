// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.fire.mode;

import de.mossgrabers.controller.fire.controller.FireControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.observer.IParametersAdjustObserver;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;


/**
 * Provide access to 8 parameters. If the ALT button is pressed the parameters 5-8 are provided
 * otherwise 1-4.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Fire4KnobProvider implements IParameterProvider
{
    private final FireControlSurface surface;
    private final IParameterProvider provider;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param provider The provider to wrap
     */
    public Fire4KnobProvider (final FireControlSurface surface, final IParameterProvider provider)
    {
        this.surface = surface;
        this.provider = provider;
    }


    /** {@inheritDoc} */
    @Override
    public int size ()
    {
        return 4;
    }


    /** {@inheritDoc} */
    @Override
    public IParameter get (final int index)
    {
        return this.provider.get (this.surface.isPressed (ButtonID.ALT) ? 4 + index : index);
    }


    /** {@inheritDoc} */
    @Override
    public void addParametersObserver (final IParametersAdjustObserver observer)
    {
        this.provider.addParametersObserver (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void removeParametersObserver (final IParametersAdjustObserver observer)
    {
        this.provider.removeParametersObserver (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void notifyParametersObservers ()
    {
        this.provider.notifyParametersObservers ();
    }
}
