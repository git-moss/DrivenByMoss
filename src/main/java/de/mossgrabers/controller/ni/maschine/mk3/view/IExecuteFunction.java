// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.view;

import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Make the main functions of a Maschine view available for some additional buttons of the Maschine
 * Studio.
 *
 * @author Jürgen Moßgraber
 */
public interface IExecuteFunction
{
    /**
     * Implement to execute whatever function the view has.
     *
     * @param padIndex The index of the pressed pad (0-15)
     * @param buttonEvent Down or up
     */
    void executeFunction (int padIndex, ButtonEvent buttonEvent);
}
