// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Interface for transposing up and down.
 *
 * @author Jürgen Moßgraber
 */
public interface TransposeView
{
    /**
     * Trigger to display 1 octave lower.
     *
     * @param event The button event
     */
    void onOctaveDown (final ButtonEvent event);


    /**
     * Trigger to display 1 octave higher.
     *
     * @param event The button event
     */
    void onOctaveUp (final ButtonEvent event);


    /**
     * Test if the up button is enabled.
     *
     * @return True if enabled
     */
    boolean isOctaveUpButtonOn ();


    /**
     * Test if the up button is enabled.
     *
     * @return True if enabled
     */
    boolean isOctaveDownButtonOn ();
}
