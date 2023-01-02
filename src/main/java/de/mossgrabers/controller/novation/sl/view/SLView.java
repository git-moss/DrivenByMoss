// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.sl.view;

import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Interface to SL specific view methods.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface SLView
{
    /**
     * A button of the first row was pressed or released.
     *
     * @param index The index of the button
     * @param event The button event
     */
    void onButtonRow1 (final int index, final ButtonEvent event);


    /**
     * A button of the second row was pressed or released.
     *
     * @param index The index of the button
     * @param event The button event
     */
    void onButtonRow2 (final int index, final ButtonEvent event);


    /**
     * A button of the third row was pressed or released.
     *
     * @param index The index of the button
     * @param event The button event
     */
    void onButtonRow3 (final int index, final ButtonEvent event);


    /**
     * A button of the fourth row was pressed or released.
     *
     * @param index The index of the button
     * @param event The button event
     */
    void onButtonRow4 (final int index, final ButtonEvent event);


    /**
     * A button of the fifth row (this is the fourth row with the transport button enabled) was
     * pressed or released.
     *
     * @param index The index of the button
     * @param event The button event
     */
    void onButtonRow5 (final int index, final ButtonEvent event);


    /**
     * The first button row was selected.
     */
    void onButtonRow1Select ();


    /**
     * The second button row was selected.
     */
    void onButtonRow2Select ();


    /**
     * The P1 button up or down was pressed
     *
     * @param isUp The up button was pressed if true
     * @param event The button event
     */
    void onButtonP1 (final boolean isUp, final ButtonEvent event);
}
