// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apcmini.controller;

import de.mossgrabers.controller.apcmini.APCminiConfiguration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
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
    // Midi Notes
    public static final int    APC_BUTTON_TRACK_BUTTON1 = 64;
    public static final int    APC_BUTTON_TRACK_BUTTON2 = 65;
    public static final int    APC_BUTTON_TRACK_BUTTON3 = 66;
    public static final int    APC_BUTTON_TRACK_BUTTON4 = 67;
    public static final int    APC_BUTTON_TRACK_BUTTON5 = 68;
    public static final int    APC_BUTTON_TRACK_BUTTON6 = 69;
    public static final int    APC_BUTTON_TRACK_BUTTON7 = 70;
    public static final int    APC_BUTTON_TRACK_BUTTON8 = 71;
    public static final int    APC_BUTTON_SCENE_BUTTON1 = 82;
    public static final int    APC_BUTTON_SCENE_BUTTON2 = 83;
    public static final int    APC_BUTTON_SCENE_BUTTON3 = 84;
    public static final int    APC_BUTTON_SCENE_BUTTON4 = 85;
    public static final int    APC_BUTTON_SCENE_BUTTON5 = 86;
    public static final int    APC_BUTTON_SCENE_BUTTON6 = 87;
    public static final int    APC_BUTTON_SCENE_BUTTON7 = 88;
    public static final int    APC_BUTTON_SCENE_BUTTON8 = 89;
    public static final int    APC_BUTTON_SHIFT         = 98;

    // Midi CC
    public static final int    APC_KNOB_TRACK_LEVEL1    = 48;
    public static final int    APC_KNOB_TRACK_LEVEL2    = 49;
    public static final int    APC_KNOB_TRACK_LEVEL3    = 50;
    public static final int    APC_KNOB_TRACK_LEVEL4    = 51;
    public static final int    APC_KNOB_TRACK_LEVEL5    = 52;
    public static final int    APC_KNOB_TRACK_LEVEL6    = 53;
    public static final int    APC_KNOB_TRACK_LEVEL7    = 54;
    public static final int    APC_KNOB_TRACK_LEVEL8    = 55;
    public static final int    APC_KNOB_MASTER_LEVEL    = 56;

    public static final int [] APC_BUTTONS_ALL          =
    {
        APC_BUTTON_TRACK_BUTTON1,
        APC_BUTTON_TRACK_BUTTON2,
        APC_BUTTON_TRACK_BUTTON3,
        APC_BUTTON_TRACK_BUTTON4,
        APC_BUTTON_TRACK_BUTTON5,
        APC_BUTTON_TRACK_BUTTON6,
        APC_BUTTON_TRACK_BUTTON7,
        APC_BUTTON_TRACK_BUTTON8,
        APC_BUTTON_SCENE_BUTTON1,
        APC_BUTTON_SCENE_BUTTON2,
        APC_BUTTON_SCENE_BUTTON3,
        APC_BUTTON_SCENE_BUTTON4,
        APC_BUTTON_SCENE_BUTTON5,
        APC_BUTTON_SCENE_BUTTON6,
        APC_BUTTON_SCENE_BUTTON7,
        APC_BUTTON_SCENE_BUTTON8,
        APC_BUTTON_SHIFT
    };

    public static final int    APC_BUTTON_STATE_OFF     = 0;
    public static final int    APC_BUTTON_STATE_ON      = 1;
    public static final int    APC_BUTTON_STATE_BLINK   = 2;

    public static final int    TRACK_STATE_CLIP_STOP    = 0;
    public static final int    TRACK_STATE_SOLO         = 1;
    public static final int    TRACK_STATE_REC_ARM      = 2;
    public static final int    TRACK_STATE_MUTE         = 3;
    public static final int    TRACK_STATE_SELECT       = 4;

    private int                trackState               = TRACK_STATE_CLIP_STOP;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The midi output
     * @param input The midi input
     */
    public APCminiControlSurface (final IHost host, final ColorManager colorManager, final APCminiConfiguration configuration, final IMidiOutput output, final IMidiInput input)
    {
        super (host, configuration, colorManager, output, input, new APCminiPadGrid (colorManager, output), APC_BUTTONS_ALL);

        this.shiftButtonId = APC_BUTTON_SHIFT;

        for (int i = 0; i < 64; i++)
            this.gridNotes[i] = i;
    }


    /** {@inheritDoc} */
    @Override
    public void setButton (final int button, final int state)
    {
        this.output.sendNote (button, state);
    }


    /** {@inheritDoc} */
    @Override
    public void setButtonEx (final int button, final int channel, final int state)
    {
        this.output.sendNoteEx (channel, button, state);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleGridNote (final int note, final int velocity)
    {
        super.handleGridNote (this.pads.translateToGrid (note), velocity);
    }


    public int getTrackState ()
    {
        return this.trackState;
    }


    public void setTrackState (final int trackState)
    {
        this.trackState = trackState;
    }
}