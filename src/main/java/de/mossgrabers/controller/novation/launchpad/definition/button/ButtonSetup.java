// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.definition.button;

import java.util.EnumMap;
import java.util.Map;


/**
 * Button configuration for a specific Launchpad version.
 *
 * @author Jürgen Moßgraber
 */
public class ButtonSetup
{
    private final Map<LaunchpadButton, LaunchpadButtonInfo> buttonIDs = new EnumMap<> (LaunchpadButton.class);


    /**
     * Add a physical button to the configuration.
     *
     * @param button The launchpad button ID
     * @param midiControl The MIDI control number
     */
    public void setButton (final LaunchpadButton button, final int midiControl)
    {
        this.setButton (button, midiControl, false);
    }


    /**
     * Add a button to the configuration.
     *
     * @param button The launchpad button ID
     * @param midiControl The MIDI control number
     * @param isShifted True if it needs to be combined with the shift key
     */
    public void setButton (final LaunchpadButton button, final int midiControl, final boolean isShifted)
    {
        this.buttonIDs.put (button, new LaunchpadButtonInfo (midiControl, isShifted));
    }


    /**
     * Get a button info.
     *
     * @param buttonID The ID of the button for which to get the info
     * @return The info
     */
    public LaunchpadButtonInfo get (final LaunchpadButton buttonID)
    {
        final LaunchpadButtonInfo info = this.buttonIDs.get (buttonID);
        return info == null ? LaunchpadButtonInfo.VIRTUAL : info;
    }
}
