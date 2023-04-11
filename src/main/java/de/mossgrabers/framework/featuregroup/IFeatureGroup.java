// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.featuregroup;

import de.mossgrabers.framework.controller.ButtonID;


/**
 * Interface to a feature group. A feature group has a number of controls (knobs, buttons, etc.) and
 * output elements like a display or LEDs. A feature group has a name and can be de-/activated.
 *
 * @author Jürgen Moßgraber
 */
public interface IFeatureGroup
{
    /**
     * Get the name of the feature group.
     *
     * @return The name
     */
    String getName ();


    /**
     * Called when a feature group is activated.
     */
    void onActivate ();


    /**
     * Called when a feature group is deactivated.
     */
    void onDeactivate ();


    /**
     * Get the color for a button, which is controlled by the feature group.
     *
     * @param buttonID The ID of the button
     * @return A color index
     */
    int getButtonColor (ButtonID buttonID);
}
