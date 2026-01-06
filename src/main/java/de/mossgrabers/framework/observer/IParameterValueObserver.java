// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.observer;

/**
 * An observer for value changes of parameters.
 *
 * @author Jürgen Moßgraber
 */
@FunctionalInterface
public interface IParameterValueObserver
{
    /**
     * Called if a parameter value has changed.
     *
     * @param page The page of the parameter
     * @param index The index of the parameter
     */
    void update (int page, int index);
}
