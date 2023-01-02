// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apcmini.controller;

import de.mossgrabers.controller.akai.apcmini.APCminiConfiguration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;


/**
 * The APC 1 and APC 2 control surface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class APCminiControlSurface extends AbstractControlSurface<APCminiConfiguration>
{
    // MIDI Notes
    public static final int APC_BUTTON_TRACK_BUTTON1 = 64;
    public static final int APC_BUTTON_TRACK_BUTTON2 = 65;
    public static final int APC_BUTTON_TRACK_BUTTON3 = 66;
    public static final int APC_BUTTON_TRACK_BUTTON4 = 67;
    public static final int APC_BUTTON_TRACK_BUTTON5 = 68;
    public static final int APC_BUTTON_TRACK_BUTTON6 = 69;
    public static final int APC_BUTTON_TRACK_BUTTON7 = 70;
    public static final int APC_BUTTON_TRACK_BUTTON8 = 71;
    public static final int APC_BUTTON_SCENE_BUTTON1 = 82;
    public static final int APC_BUTTON_SCENE_BUTTON2 = 83;
    public static final int APC_BUTTON_SCENE_BUTTON3 = 84;
    public static final int APC_BUTTON_SCENE_BUTTON4 = 85;
    public static final int APC_BUTTON_SCENE_BUTTON5 = 86;
    public static final int APC_BUTTON_SCENE_BUTTON6 = 87;
    public static final int APC_BUTTON_SCENE_BUTTON7 = 88;
    public static final int APC_BUTTON_SCENE_BUTTON8 = 89;
    public static final int APC_BUTTON_SHIFT         = 98;

    // MIDI CC
    public static final int APC_KNOB_TRACK_LEVEL1    = 48;
    public static final int APC_KNOB_TRACK_LEVEL2    = 49;
    public static final int APC_KNOB_TRACK_LEVEL3    = 50;
    public static final int APC_KNOB_TRACK_LEVEL4    = 51;
    public static final int APC_KNOB_TRACK_LEVEL5    = 52;
    public static final int APC_KNOB_TRACK_LEVEL6    = 53;
    public static final int APC_KNOB_TRACK_LEVEL7    = 54;
    public static final int APC_KNOB_TRACK_LEVEL8    = 55;
    public static final int APC_KNOB_MASTER_LEVEL    = 56;

    public static final int APC_BUTTON_STATE_OFF     = 0;
    public static final int APC_BUTTON_STATE_ON      = 1;
    public static final int APC_BUTTON_STATE_BLINK   = 2;

    public static final int TRACK_STATE_CLIP_STOP    = 0;
    public static final int TRACK_STATE_SOLO         = 1;
    public static final int TRACK_STATE_REC_ARM      = 2;
    public static final int TRACK_STATE_MUTE         = 3;
    public static final int TRACK_STATE_SELECT       = 4;

    private int             trackState               = TRACK_STATE_CLIP_STOP;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The MIDI output
     * @param input The MIDI input
     */
    public APCminiControlSurface (final IHost host, final ColorManager colorManager, final APCminiConfiguration configuration, final IMidiOutput output, final IMidiInput input)
    {
        super (host, configuration, colorManager, output, input, new APCminiPadGrid (colorManager, output), 110, 106);
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final BindType bindType, final int channel, final int cc, final int state)
    {
        // Shift has no light
        if (cc == APC_BUTTON_SHIFT)
            return;

        this.output.sendNoteEx (channel, cc, state);
    }


    /**
     * Get the mode of the track button (Select, Record Arm, Solo, Mute, ...)
     *
     * @return The new state
     */
    public int getTrackState ()
    {
        return this.trackState;
    }


    /**
     * Sets the mode of the track button (Select, Record Arm, Solo, Mute, ...)
     *
     * @param trackState The new state
     */
    public void setTrackState (final int trackState)
    {
        this.trackState = trackState;
    }
}