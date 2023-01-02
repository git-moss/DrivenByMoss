// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchcontrol.controller;

import de.mossgrabers.controller.novation.launchcontrol.LaunchControlXLConfiguration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Arrays;


/**
 * The LaunchControl XL control surface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class LaunchControlXLControlSurface extends AbstractControlSurface<LaunchControlXLConfiguration>
{
    // Knobs & Faders - MIDI CC

    public static final int     LAUNCHCONTROL_KNOB_SEND_A_1   = 0x0D;
    public static final int     LAUNCHCONTROL_KNOB_SEND_A_2   = 0x0E;
    public static final int     LAUNCHCONTROL_KNOB_SEND_A_3   = 0x0F;
    public static final int     LAUNCHCONTROL_KNOB_SEND_A_4   = 0x10;
    public static final int     LAUNCHCONTROL_KNOB_SEND_A_5   = 0x11;
    public static final int     LAUNCHCONTROL_KNOB_SEND_A_6   = 0x12;
    public static final int     LAUNCHCONTROL_KNOB_SEND_A_7   = 0x13;
    public static final int     LAUNCHCONTROL_KNOB_SEND_A_8   = 0x14;

    public static final int     LAUNCHCONTROL_KNOB_SEND_B_1   = 0x1D;
    public static final int     LAUNCHCONTROL_KNOB_SEND_B_2   = 0x1E;
    public static final int     LAUNCHCONTROL_KNOB_SEND_B_3   = 0x1F;
    public static final int     LAUNCHCONTROL_KNOB_SEND_B_4   = 0x20;
    public static final int     LAUNCHCONTROL_KNOB_SEND_B_5   = 0x21;
    public static final int     LAUNCHCONTROL_KNOB_SEND_B_6   = 0x22;
    public static final int     LAUNCHCONTROL_KNOB_SEND_B_7   = 0x23;
    public static final int     LAUNCHCONTROL_KNOB_SEND_B_8   = 0x24;

    public static final int     LAUNCHCONTROL_KNOB_PAN_1      = 0x31;
    public static final int     LAUNCHCONTROL_KNOB_PAN_2      = 0x32;
    public static final int     LAUNCHCONTROL_KNOB_PAN_3      = 0x33;
    public static final int     LAUNCHCONTROL_KNOB_PAN_4      = 0x34;
    public static final int     LAUNCHCONTROL_KNOB_PAN_5      = 0x35;
    public static final int     LAUNCHCONTROL_KNOB_PAN_6      = 0x36;
    public static final int     LAUNCHCONTROL_KNOB_PAN_7      = 0x37;
    public static final int     LAUNCHCONTROL_KNOB_PAN_8      = 0x38;

    public static final int     LAUNCHCONTROL_FADER_1         = 0x4D;
    public static final int     LAUNCHCONTROL_FADER_2         = 0x4E;
    public static final int     LAUNCHCONTROL_FADER_3         = 0x4F;
    public static final int     LAUNCHCONTROL_FADER_4         = 0x50;
    public static final int     LAUNCHCONTROL_FADER_5         = 0x51;
    public static final int     LAUNCHCONTROL_FADER_6         = 0x52;
    public static final int     LAUNCHCONTROL_FADER_7         = 0x53;
    public static final int     LAUNCHCONTROL_FADER_8         = 0x54;

    // Buttons - MIDI CC

    public static final int     LAUNCHCONTROL_SEND_PREV       = 0x68;
    public static final int     LAUNCHCONTROL_SEND_NEXT       = 0x69;
    public static final int     LAUNCHCONTROL_TRACK_PREV      = 0x6A;
    public static final int     LAUNCHCONTROL_TRACK_NEXT      = 0x6B;

    // Buttons - MIDI Notes

    public static final int     LAUNCHCONTROL_DEVICE          = 0x69;
    public static final int     LAUNCHCONTROL_MUTE            = 0x6A;
    public static final int     LAUNCHCONTROL_SOLO            = 0x6B;
    public static final int     LAUNCHCONTROL_RECORD_ARM      = 0x6C;

    public static final int     LAUNCHCONTROL_TRACK_FOCUS_1   = 0x29;
    public static final int     LAUNCHCONTROL_TRACK_FOCUS_2   = 0x2A;
    public static final int     LAUNCHCONTROL_TRACK_FOCUS_3   = 0x2B;
    public static final int     LAUNCHCONTROL_TRACK_FOCUS_4   = 0x2C;
    public static final int     LAUNCHCONTROL_TRACK_FOCUS_5   = 0x39;
    public static final int     LAUNCHCONTROL_TRACK_FOCUS_6   = 0x3A;
    public static final int     LAUNCHCONTROL_TRACK_FOCUS_7   = 0x3B;
    public static final int     LAUNCHCONTROL_TRACK_FOCUS_8   = 0x3C;

    public static final int     LAUNCHCONTROL_TRACK_CONTROL_1 = 0x49;
    public static final int     LAUNCHCONTROL_TRACK_CONTROL_2 = 0x4A;
    public static final int     LAUNCHCONTROL_TRACK_CONTROL_3 = 0x4B;
    public static final int     LAUNCHCONTROL_TRACK_CONTROL_4 = 0x4C;
    public static final int     LAUNCHCONTROL_TRACK_CONTROL_5 = 0x59;
    public static final int     LAUNCHCONTROL_TRACK_CONTROL_6 = 0x5A;
    public static final int     LAUNCHCONTROL_TRACK_CONTROL_7 = 0x5B;
    public static final int     LAUNCHCONTROL_TRACK_CONTROL_8 = 0x5C;

    private static final int [] TEMPLATE_CHANGE_HEADER        =
    {
        0xF0,
        0x00,
        0x20,
        0x29,
        0x02,
        0x11,
        0x77
    };

    private final ModeManager   trackButtonModeManager        = new ModeManager ();
    private final ModeManager   faderModeManager              = new ModeManager ();


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The DAW MIDI output
     * @param input The DAW MIDI input
     */
    public LaunchControlXLControlSurface (final IHost host, final ColorManager colorManager, final LaunchControlXLConfiguration configuration, final IMidiOutput output, final IMidiInput input)
    {
        super (host, configuration, colorManager, output, input, null, 650, 450);

        this.input.setSysexCallback (this::handleSysEx);
    }


    /**
     * Get the track mode manager.
     *
     * @return The track mode manager
     */
    public ModeManager getTrackButtonModeManager ()
    {
        return this.trackButtonModeManager;
    }


    /**
     * Get the fader mode manager.
     *
     * @return The track mode manager
     */
    public ModeManager getFaderModeManager ()
    {
        return this.faderModeManager;
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final BindType bindType, final int channel, final int noteOrCC, final int state)
    {
        if (bindType == BindType.NOTE)
            this.output.sendNoteEx (channel, noteOrCC, state);
        else
            this.output.sendCCEx (channel, noteOrCC, state);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleCC (final int data1, final int data2)
    {
        // Ignore 'not bound' warnings for user modes
    }


    /**
     * Set the LED of a knob for the factory templates.
     *
     * @param row The row of the knob (0-2)
     * @param column The column of the knob (0-7)
     * @param green The intensity of green (0-3)
     * @param red The intensity of red (0-3)
     */
    public void setKnobLEDColor (final int row, final int column, final int green, final int red)
    {
        final int midiChannel = this.configuration.getTemplate ();
        if (midiChannel >= 8)
            this.setKnobLED (midiChannel, 13 + row + column * 16, green, red);
    }


    /**
     * Set the LED of a knob.
     *
     * @param channel The MIDI channel (number of the template)
     * @param note The note number of the knob
     * @param green The green intensity 0-3
     * @param red The red intensity 0-3
     */
    private void setKnobLED (final int channel, final int note, final int green, final int red)
    {
        final int g = Math.min (3, Math.max (0, green));
        final int r = Math.min (3, Math.max (0, red));
        // Red is in bit 0 and 1. Green is in bit 4 and 5.
        this.output.sendNoteEx (channel, note, 12 + r + (g << 4));
    }


    /**
     * Handle system exclusive messages.
     *
     * @param data The received message
     */
    private void handleSysEx (final String data)
    {
        final int [] byteData = StringUtils.fromHexStr (data);

        // Handle template change - F0h 00h 20h 29h 02h 11h 77h Template F7h
        if (byteData.length == 9 && Arrays.equals (byteData, 0, TEMPLATE_CHANGE_HEADER.length, TEMPLATE_CHANGE_HEADER, 0, TEMPLATE_CHANGE_HEADER.length))
        {
            final int templateID = byteData[7];

            // Already active?
            if (this.configuration.getTemplate () == templateID)
                return;

            this.configuration.setTemplate (templateID);
            return;
        }

        this.errorln ("Unhandled Sysex: " + data);
    }


    /**
     * Sends a sysex message to the device to activate a template.
     *
     * @param templateID The index of the template
     */
    public void selectTemplate (final int templateID)
    {
        final byte [] data = new byte [TEMPLATE_CHANGE_HEADER.length + 2];
        for (int i = 0; i < TEMPLATE_CHANGE_HEADER.length; i++)
            data[i] = (byte) TEMPLATE_CHANGE_HEADER[i];
        data[data.length - 2] = (byte) templateID;
        data[data.length - 1] = (byte) 0xF7;
        this.output.sendSysex (data);
    }
}