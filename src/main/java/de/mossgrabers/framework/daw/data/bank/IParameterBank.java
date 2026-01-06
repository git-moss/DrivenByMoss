// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.bank;

import de.mossgrabers.framework.observer.IParameterValueObserver;
import de.mossgrabers.framework.parameter.IParameter;


/**
 * Interface to a parameter bank.
 *
 * @author Jürgen Moßgraber
 */
public interface IParameterBank extends IBank<IParameter>
{
    /**
     * Get the parameter page bank.
     *
     * @return The bank
     */
    IParameterPageBank getPageBank ();


    /**
     * Add an observer for value changes of the parameters on the active page. Must only be called
     * on startup!
     *
     * @param observer The observer to notify on a value change, the parameters are the index of the
     *            active page and the index of the parameter
     */
    void addValueObserver (final IParameterValueObserver observer);
}