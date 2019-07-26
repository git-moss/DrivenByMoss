// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apc.controller;

import de.mossgrabers.controller.apc.APCConfiguration;
import de.mossgrabers.framework.command.ContinuousCommandID;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.View;

import java.util.Arrays;


/**
 * The APC 1 and APC 2 control surface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class APCControlSurface extends AbstractControlSurface<APCConfiguration>
{
    // Midi Notes
    public static final int         APC_BUTTON_RECORD_ARM      = 0x30;
    public static final int         APC_BUTTON_SOLO            = 0x31;
    public static final int         APC_BUTTON_ACTIVATOR       = 0x32;
    public static final int         APC_BUTTON_TRACK_SELECTION = 0x33;
    public static final int         APC_BUTTON_CLIP_STOP       = 0x34;
    public static final int         APC_BUTTON_CLIP_LAUNCH_1   = 0x35;
    public static final int         APC_BUTTON_CLIP_LAUNCH_2   = 0x36;
    public static final int         APC_BUTTON_CLIP_LAUNCH_3   = 0x37;
    public static final int         APC_BUTTON_CLIP_LAUNCH_4   = 0x38;
    public static final int         APC_BUTTON_CLIP_LAUNCH_5   = 0x39;
    public static final int         APC_BUTTON_CLIP_TRACK      = 0x3A;
    public static final int         APC_BUTTON_DEVICE_ON_OFF   = 0x3B;
    public static final int         APC_BUTTON_DEVICE_LEFT     = 0x3C;
    public static final int         APC_BUTTON_DEVICE_RIGHT    = 0x3D;
    public static final int         APC_BUTTON_DETAIL_VIEW     = 0x3E;
    public static final int         APC_BUTTON_REC_QUANT       = 0x3F;
    public static final int         APC_BUTTON_MIDI_OVERDUB    = 0x40;
    public static final int         APC_BUTTON_METRONOME       = 0x41;
    public static final int         APC_BUTTON_A_B             = 0x42; // mkII
    public static final int         APC_BUTTON_MASTER          = 0x50;
    public static final int         APC_BUTTON_STOP_ALL_CLIPS  = 0x51;
    public static final int         APC_BUTTON_SCENE_LAUNCH_1  = 0x52;
    public static final int         APC_BUTTON_SCENE_LAUNCH_2  = 0x53;
    public static final int         APC_BUTTON_SCENE_LAUNCH_3  = 0x54;
    public static final int         APC_BUTTON_SCENE_LAUNCH_4  = 0x55;
    public static final int         APC_BUTTON_SCENE_LAUNCH_5  = 0x56;
    public static final int         APC_BUTTON_PAN             = 0x57;
    public static final int         APC_BUTTON_SEND_A          = 0x58;
    public static final int         APC_BUTTON_SEND_B          = 0x59;
    public static final int         APC_BUTTON_SEND_C          = 0x5A;
    public static final int         APC_BUTTON_PLAY            = 0x5B;
    public static final int         APC_BUTTON_STOP            = 0x5C;
    public static final int         APC_BUTTON_RECORD          = 0x5D;
    public static final int         APC_BUTTON_UP              = 0x5E;
    public static final int         APC_BUTTON_DOWN            = 0x5F;
    public static final int         APC_BUTTON_RIGHT           = 0x60;
    public static final int         APC_BUTTON_LEFT            = 0x61;
    public static final int         APC_BUTTON_SHIFT           = 0x62;
    public static final int         APC_BUTTON_TAP_TEMPO       = 0x63;
    public static final int         APC_BUTTON_NUDGE_PLUS      = 0x64;
    public static final int         APC_BUTTON_NUDGE_MINUS     = 0x65;
    public static final int         APC_BUTTON_SESSION         = 0x66; // mkII
    public static final int         APC_BUTTON_BANK            = 0x67; // mkII

    // Midi CC
    public static final int         APC_KNOB_TRACK_LEVEL       = 0x07;
    public static final int         APC_KNOB_TEMPO             = 0x0D; // mkII
    public static final int         APC_KNOB_MASTER_LEVEL      = 0x0E;
    public static final int         APC_KNOB_CROSSFADER        = 0x0F;
    public static final int         APC_KNOB_DEVICE_KNOB_1     = 0x10;
    public static final int         APC_KNOB_DEVICE_KNOB_2     = 0x11;
    public static final int         APC_KNOB_DEVICE_KNOB_3     = 0x12;
    public static final int         APC_KNOB_DEVICE_KNOB_4     = 0x13;
    public static final int         APC_KNOB_DEVICE_KNOB_5     = 0x14;
    public static final int         APC_KNOB_DEVICE_KNOB_6     = 0x15;
    public static final int         APC_KNOB_DEVICE_KNOB_7     = 0x16;
    public static final int         APC_KNOB_DEVICE_KNOB_8     = 0x17;
    public static final int         APC_KNOB_DEVICE_KNOB_LED_1 = 0x18;
    public static final int         APC_KNOB_DEVICE_KNOB_LED_2 = 0x19;
    public static final int         APC_KNOB_DEVICE_KNOB_LED_3 = 0x1A;
    public static final int         APC_KNOB_DEVICE_KNOB_LED_4 = 0x1B;
    public static final int         APC_KNOB_DEVICE_KNOB_LED_5 = 0x1C;
    public static final int         APC_KNOB_DEVICE_KNOB_LED_6 = 0x1D;
    public static final int         APC_KNOB_DEVICE_KNOB_LED_7 = 0x1E;
    public static final int         APC_KNOB_DEVICE_KNOB_LED_8 = 0x1F;
    public static final int         APC_KNOB_CUE_LEVEL         = 0x2F;
    public static final int         APC_KNOB_TRACK_KNOB_1      = 0x30;
    public static final int         APC_KNOB_TRACK_KNOB_2      = 0x31;
    public static final int         APC_KNOB_TRACK_KNOB_3      = 0x32;
    public static final int         APC_KNOB_TRACK_KNOB_4      = 0x33;
    public static final int         APC_KNOB_TRACK_KNOB_5      = 0x34;
    public static final int         APC_KNOB_TRACK_KNOB_6      = 0x35;
    public static final int         APC_KNOB_TRACK_KNOB_7      = 0x36;
    public static final int         APC_KNOB_TRACK_KNOB_8      = 0x37;
    public static final int         APC_KNOB_TRACK_KNOB_LED_1  = 0x38;
    public static final int         APC_KNOB_TRACK_KNOB_LED_2  = 0x39;
    public static final int         APC_KNOB_TRACK_KNOB_LED_3  = 0x3A;
    public static final int         APC_KNOB_TRACK_KNOB_LED_4  = 0x3B;
    public static final int         APC_KNOB_TRACK_KNOB_LED_5  = 0x3C;
    public static final int         APC_KNOB_TRACK_KNOB_LED_6  = 0x3D;
    public static final int         APC_KNOB_TRACK_KNOB_LED_7  = 0x3E;
    public static final int         APC_KNOB_TRACK_KNOB_LED_8  = 0x3F;
    public static final int         APC_FOOTSWITCH_1           = 0x40;
    public static final int         APC_FOOTSWITCH_2           = 0x43;

    private static final boolean [] APC_BUTTON_UPDATE;
    static
    {
        APC_BUTTON_UPDATE = new boolean [128];
        Arrays.fill (APC_BUTTON_UPDATE, true);
        APC_BUTTON_UPDATE[APC_BUTTON_RECORD_ARM] = false;
        APC_BUTTON_UPDATE[APC_BUTTON_SOLO] = false;
        APC_BUTTON_UPDATE[APC_BUTTON_ACTIVATOR] = false;
        APC_BUTTON_UPDATE[APC_BUTTON_TRACK_SELECTION] = false;
        APC_BUTTON_UPDATE[APC_BUTTON_CLIP_TRACK] = false;
        APC_BUTTON_UPDATE[APC_BUTTON_DEVICE_ON_OFF] = false;
        APC_BUTTON_UPDATE[APC_BUTTON_DEVICE_LEFT] = false;
        APC_BUTTON_UPDATE[APC_BUTTON_DEVICE_RIGHT] = false;
        APC_BUTTON_UPDATE[APC_BUTTON_DETAIL_VIEW] = false;
        APC_BUTTON_UPDATE[APC_BUTTON_REC_QUANT] = false;
        APC_BUTTON_UPDATE[APC_BUTTON_MIDI_OVERDUB] = false;
        APC_BUTTON_UPDATE[APC_BUTTON_METRONOME] = false;
        APC_BUTTON_UPDATE[APC_BUTTON_A_B] = false;
        APC_BUTTON_UPDATE[APC_BUTTON_MASTER] = false;
        APC_BUTTON_UPDATE[APC_BUTTON_PAN] = false;
        APC_BUTTON_UPDATE[APC_BUTTON_SEND_A] = false;
        APC_BUTTON_UPDATE[APC_BUTTON_SEND_B] = false;
        APC_BUTTON_UPDATE[APC_BUTTON_SEND_C] = false;
        APC_BUTTON_UPDATE[APC_BUTTON_PLAY] = false;
        APC_BUTTON_UPDATE[APC_BUTTON_RECORD] = false;
        APC_BUTTON_UPDATE[APC_BUTTON_SESSION] = false;
        APC_BUTTON_UPDATE[APC_BUTTON_BANK] = false;
    }

    private static final String ID_APC_40      = "73";
    private static final String ID_APC_40_MKII = "29";

    private boolean             isMkII;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The midi output
     * @param input The midi input
     * @param isMkII
     */
    public APCControlSurface (final IHost host, final ColorManager colorManager, final APCConfiguration configuration, final IMidiOutput output, final IMidiInput input, final boolean isMkII)
    {
        super (host, configuration, colorManager, output, input, new APCPadGrid (colorManager, output, isMkII));

        this.isMkII = isMkII;

        this.shiftButtonId = APC_BUTTON_SHIFT;
        this.leftButtonId = APC_BUTTON_LEFT;
        this.rightButtonId = APC_BUTTON_RIGHT;
        this.upButtonId = APC_BUTTON_UP;
        this.downButtonId = APC_BUTTON_DOWN;

        // Set Mode 2
        this.output.sendSysex ("F0 47 7F " + (isMkII ? ID_APC_40_MKII : ID_APC_40) + " 60 00 04 41 08 02 01 F7");
    }


    public boolean isMkII ()
    {
        return this.isMkII;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isGridNote (final int note)
    {
        return note <= 39 && super.isGridNote (36 + note);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleGridNote (final int note, final int velocity)
    {
        super.handleGridNote (36 + note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public int getSceneTrigger (final int index)
    {
        return APC_BUTTON_SCENE_LAUNCH_1 + index;
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final int channel, final int cc, final int state)
    {
        this.output.sendNoteEx (channel, cc, state);
    }


    /** {@inheritDoc} */
    @Override
    public void setContinuous (final int channel, final int cc, final int state)
    {
        this.output.sendNoteEx (channel, cc, state);
    }


    public void setLED (final int knob, final int value)
    {
        this.output.sendCC (knob, value);
    }


    /**
     * Check if a button should be updated by the main update routine.
     *
     * @param button The button to check
     * @return True if it should be updated
     */
    public boolean shouldUpdateButton (final int button)
    {
        return APC_BUTTON_UPDATE[button];
    }


    /** {@inheritDoc} */
    @Override
    protected void handleMidi (final int status, final int data1, final int data2)
    {
        final int code = status & 0xF0;
        final int channel = status & 0xF;

        switch (code)
        {
            // Note on/off
            case 0x80:
            case 0x90:
                int note = data1;
                if (!this.isMkII && data1 >= 53 && data1 <= 57)
                    note = (4 - (data1 - APC_BUTTON_CLIP_LAUNCH_1)) * 8 + channel;
                if (this.isGridNote (note))
                    this.handleGridNote (note, code == 0x80 ? 0 : data2);
                else
                    this.handleCC (channel, note, code == 0x80 ? 0 : data2);
                break;

            // CC
            case 0xB0:
                final View view = this.viewManager.getActiveView ();
                if (view == null)
                    return;
                final ContinuousCommandID commandID = this.getContinuousCommand (channel, data1);
                if (commandID != null)
                    view.executeContinuousCommand (commandID, data2);
                if (data1 == APCControlSurface.APC_FOOTSWITCH_2)
                    view.executeTriggerCommand (this.getTriggerCommand (APCControlSurface.APC_FOOTSWITCH_2), data2 > 0 ? ButtonEvent.DOWN : ButtonEvent.UP);
                break;

            default:
                this.host.println ("Unhandled midi status: " + status);
                break;
        }
    }
}