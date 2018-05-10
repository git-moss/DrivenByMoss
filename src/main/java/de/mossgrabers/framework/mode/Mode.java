// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode;

import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Interface to a mode. A mode has a display and two rows of buttons to navigate it.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface Mode
{
    /**
     * Called when a mode is activated.
     */
    void onActivate ();


    /**
     * Called when a mode is deactivated.
     */
    void onDeactivate ();


    /**
     * Get if this is a mode which is only temporarily displayed.
     *
     * @return True if temporary
     */
    boolean isTemporary ();


    /**
     * Update the display.
     */
    void updateDisplay ();


    /**
     * Update the first row buttons.
     */
    void updateFirstRow ();


    /**
     * Update the second row buttons.
     */
    void updateSecondRow ();


    /**
     * A knob has been used.
     *
     * @param index The index of the knob
     * @param value The value the knob sent
     */
    void onValueKnob (int index, int value);


    /**
     * A knob has been touched.
     *
     * @param index The index of the knob
     * @param isTouched True if the knob has been touched
     */
    void onValueKnobTouch (final int index, final boolean isTouched);


    /**
     * A row button has been pressed.
     *
     * @param row The number of the button row
     * @param index The index of the button
     * @param event The button event
     */
    void onRowButton (int row, int index, ButtonEvent event);
}
