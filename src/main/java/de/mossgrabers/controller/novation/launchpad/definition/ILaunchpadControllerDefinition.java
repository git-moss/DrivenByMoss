// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.definition;

import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.controller.novation.launchpad.definition.button.ButtonSetup;
import de.mossgrabers.framework.controller.grid.LightInfo;

import java.util.List;
import java.util.Map;


/**
 * Additional configuration options for the different Launchpad models.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface ILaunchpadControllerDefinition
{
    /**
     * Is this device a Pro model with additional buttons?
     *
     * @return True if it is a pro version
     */
    boolean isPro ();


    /**
     * Does the device have dedicated buttons to select the tracks?
     *
     * @return True if supported
     */
    boolean hasTrackSelectionButtons ();


    /**
     * Get the MIDI system exclusive header of the specific Launchpad.
     *
     * @return The header as formatted string with hex values
     */
    String getSysExHeader ();


    /**
     * Get the command to switch to standalone mode.
     *
     * @return The command as formatted string with hex values
     */
    String getStandaloneModeCommand ();


    /**
     * Get the command to switch to program mode.
     *
     * @return The command as formatted string with hex values
     */
    String getProgramModeCommand ();


    /**
     * Reset the mode to use the device without the DAW.
     *
     * @param surface The control surface
     */
    void resetMode (LaunchpadControlSurface surface);


    /**
     * Are Scene buttons using MIDI CC or notes?
     *
     * @return True if CC otherwise false for notes
     */
    boolean sceneButtonsUseCC ();


    /**
     * Set the logo or front color depending on the model.
     *
     * @param surface The control surface
     * @param color The color index
     */
    void setLogoColor (LaunchpadControlSurface surface, int color);


    /**
     * Get the setup information for the non-grid buttons.
     *
     * @return The setup
     */
    ButtonSetup getButtonSetup ();


    /**
     * Create an update system exclusive string for all given pads.
     *
     * @param padInfos The info how to update the pads
     * @return The system exclusive string
     */
    List<String> buildLEDUpdate (Map<Integer, LightInfo> padInfos);
}
