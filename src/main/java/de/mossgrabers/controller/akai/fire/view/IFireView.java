// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.view;

/**
 * Extension to all fire views e.g. the select knob.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IFireView
{
    /**
     * The Select knob has been turned.
     *
     * @param value The value the knob sent
     */
    void onSelectKnobValue (int value);


    /**
     * Get the color index of the solo buttons.
     *
     * @param index The index of the solo button 0-3
     * @return 00 Off, 01 Dull red, 02 Dull green, 03 High red, 04 High green
     */
    int getSoloButtonColor (final int index);
}
