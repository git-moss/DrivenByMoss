// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.definition;

import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.LightInfo;
import de.mossgrabers.framework.daw.midi.IMidiOutput;

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
     * Does it provide support for fader simulation?
     *
     * @return True if supported
     */
    boolean hasFaderSupport ();


    /**
     * Get the MIDI system exclusive header of the specific Launchpad.
     *
     * @return The hader as formatted string with hex values
     */
    String getSysExHeader ();


    /**
     * Get the command to switch to standalone mode.
     *
     * @return The commad as formatted string with hex values
     */
    String getStandaloneModeCommand ();


    /**
     * Get the command to switch to program mode.
     *
     * @return The commad as formatted string with hex values
     */
    String getProgramModeCommand ();


    /**
     * Get the command to switch to fader mode.
     *
     * @return The commad as formatted string with hex values
     */
    String getFaderModeCommand ();


    /**
     * Get the command to switch to pan mode.
     *
     * @return The commad as formatted string with hex values
     */
    String getPanModeCommand ();


    /**
     * Are Scene buttons using MIDI CC or notes?
     *
     * @return True if CC otherwise false for notes
     */
    boolean sceneButtonsUseCC ();


    /**
     * Set the given pad/note to blink.
     *
     * @param output The output where to send to
     * @param note The note
     * @param blinkColor The color to use for blinking
     * @param fast Blink fast or slow
     */
    void sendBlinkState (final IMidiOutput output, final int note, final int blinkColor, final boolean fast);


    /**
     * Get the IDs of common buttons.
     *
     * @return The CCs
     */
    Map<ButtonID, Integer> getButtonIDs ();


    /**
     * Create an update sysex string for all given pads.
     *
     * @param padInfos The info how to update the pads
     * @return The sysex string
     */
    List<String> buildLEDUpdate (Map<Integer, LightInfo> padInfos);
}
