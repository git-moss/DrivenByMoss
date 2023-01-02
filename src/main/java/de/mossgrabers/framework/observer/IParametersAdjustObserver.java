// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.observer;

/**
 * Callback interface for observing the adjustment of a group of parameters, e.g. a page in a
 * parameters bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@FunctionalInterface
public interface IParametersAdjustObserver
{
    /**
     * The callback function.
     */
    void parametersAdjusted ();
}
