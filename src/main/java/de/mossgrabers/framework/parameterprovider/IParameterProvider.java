// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider;

import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.observer.IParametersAdjustObserver;

import java.util.Set;


/**
 * Interface to get a number of parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IParameterProvider
{
    /**
     * Get the number of available parameters.
     *
     * @return The number of available parameters
     */
    int size ();


    /**
     * Get a parameter
     *
     * @param index The index of the parameter
     * @return The parameter
     */
    IParameter get (int index);


    /**
     * Registers a parameters adjustment observer.
     *
     * @param observer The observer to register
     */
    void addParametersObserver (IParametersAdjustObserver observer);


    /**
     * Unregisters a parameters adjustment observer.
     *
     * @param observer The observer to unregister
     */
    void removeParametersObserver (IParametersAdjustObserver observer);


    /**
     * Unregisters all parameters adjustment observers.
     *
     * @return The previously registered observers
     */
    Set<IParametersAdjustObserver> removeParametersObservers ();
}
