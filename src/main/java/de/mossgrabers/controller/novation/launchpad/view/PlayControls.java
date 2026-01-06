// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.view;

import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.MidiConstants;
import de.mossgrabers.framework.scale.Scales;


/**
 * Additional play controls for the first row of the play views.
 *
 * @author Jürgen Moßgraber
 */
public class PlayControls
{
    private static final int []           MODULATION_INTENSITIES =
    {
        0,
        32,
        64,
        92,
        127
    };

    private final LaunchpadControlSurface surface;
    private final Scales                  scales;

    private boolean                       isActive               = false;
    private boolean                       isSustain              = false;
    private boolean                       isPitchDown            = false;
    private boolean                       isPitchUp              = false;
    private int                           isModulation           = 0;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param scales The scales
     */
    public PlayControls (final LaunchpadControlSurface surface, final Scales scales)
    {
        this.surface = surface;
        this.scales = scales;
    }


    /**
     * Get the button color for the button which is used to toggle the play controls
     *
     * @return The color index
     */
    public int getToggleButtonColor ()
    {
        return this.isActive ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_LO;
    }


    /**
     * Toggle the play controls.
     */
    public void toggle ()
    {
        this.isActive = !this.isActive;
    }


    /**
     * Are the play controls active?
     *
     * @return True if active
     */
    public boolean isActive ()
    {
        return this.isActive;
    }


    /**
     * Draw (light) the play controls if they are active
     */
    public void draw ()
    {
        if (!this.isActive)
            return;

        final IPadGrid padGrid = this.surface.getPadGrid ();
        final int startNote = this.scales.getStartNote ();
        padGrid.light (startNote, this.isSustain ? LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW_LO);
        padGrid.light (startNote + 1, this.isPitchDown ? LaunchpadColorManager.LAUNCHPAD_COLOR_CYAN_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_CYAN_LO);
        padGrid.light (startNote + 2, this.isPitchUp ? LaunchpadColorManager.LAUNCHPAD_COLOR_CYAN_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_CYAN_LO);
        for (int i = 0; i < 5; i++)
            padGrid.light (startNote + 3 + i, this.isModulation == i ? LaunchpadColorManager.LAUNCHPAD_COLOR_MAGENTA_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_MAGENTA_LO);
    }


    /**
     * Handle the grid notes to trigger the play controls actions.
     *
     * @param key The pressed/released key
     * @param velocity The velocity of the key
     * @return True if handled
     */
    public boolean handleGridNotes (final int key, final int velocity)
    {
        if (!this.isActive)
            return false;

        final int pos = key - this.scales.getStartNote ();
        if (pos >= 8)
            return false;

        final boolean isDown = velocity > 0;

        final IMidiInput midiInput = this.surface.getMidiInput ();
        switch (pos)
        {
            // Sustain
            case 0:
                this.isSustain = isDown;
                midiInput.sendRawMidiEvent (MidiConstants.CMD_CC, 64, this.isSustain ? 127 : 0);
                break;

            // Pitch
            case 1:
                this.isPitchDown = isDown;
                midiInput.sendRawMidiEvent (MidiConstants.CMD_PITCHBEND, 0, this.isPitchDown ? Math.abs (velocity / 2 - 63) : 64);
                break;
            case 2:
                this.isPitchUp = isDown;
                midiInput.sendRawMidiEvent (MidiConstants.CMD_PITCHBEND, 0, this.isPitchUp ? 64 + velocity / 2 : 64);
                break;

            // Modulation
            default:
                if (isDown)
                {
                    this.isModulation = pos - 3;
                    midiInput.sendRawMidiEvent (MidiConstants.CMD_CC, 1, MODULATION_INTENSITIES[this.isModulation]);
                }
                break;
        }

        return true;
    }
}
