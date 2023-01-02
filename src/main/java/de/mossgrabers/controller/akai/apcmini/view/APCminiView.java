// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apcmini.view;

import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Additional interface for APCmini views.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface APCminiView
{
    /**
     * A track button was pressed
     *
     * @param index The index of the button
     * @param event The event
     */
    void onSelectTrack (int index, ButtonEvent event);


    /**
     * Get the track button color.
     *
     * @param index The index of the button
     * @return The color
     */
    int getTrackButtonColor (int index);
}
