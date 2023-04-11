// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchkey.maxi.controller;

import de.mossgrabers.controller.novation.launchkey.maxi.LaunchkeyMk3Configuration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.DeviceInquiry;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.framework.view.Views;


/**
 * The Launchkey Mk3 control surface.
 *
 * @author Jürgen Moßgraber
 */
@SuppressWarnings("javadoc")
public class LaunchkeyMk3ControlSurface extends AbstractControlSurface<LaunchkeyMk3Configuration>
{
    /** Device family code of Launchkey 25 model. */
    public static final int   LAUNCHKEY_25            = 52;
    /** Device family code of Launchkey 37 model. */
    public static final int   LAUNCHKEY_37            = 53;
    /** Device family code of Launchkey 49 model. */
    public static final int   LAUNCHKEY_49            = 54;
    /** Device family code of Launchkey 61 model. */
    public static final int   LAUNCHKEY_61            = 55;

    // Buttons & Knobs

    public static final int   LAUNCHKEY_VIEW_SELECT   = 0x03;
    public static final int   LAUNCHKEY_MODE_SELECT   = 0x09;
    public static final int   LAUNCHKEY_FADER_SELECT  = 0x0A;
    public static final int   LAUNCHKEY_TOUCH_EVENTS  = 0x0B;
    public static final int   LAUNCHKEY_DAW_ONLINE    = 0x0C;

    public static final int   LAUNCHKEY_KNOB_1        = 0x15;
    public static final int   LAUNCHKEY_KNOB_2        = 0x16;
    public static final int   LAUNCHKEY_KNOB_3        = 0x17;
    public static final int   LAUNCHKEY_KNOB_4        = 0x18;
    public static final int   LAUNCHKEY_KNOB_5        = 0x19;
    public static final int   LAUNCHKEY_KNOB_6        = 0x1A;
    public static final int   LAUNCHKEY_KNOB_7        = 0x1B;
    public static final int   LAUNCHKEY_KNOB_8        = 0x1C;

    public static final int   LAUNCHKEY_SELECT1       = 0x25;
    public static final int   LAUNCHKEY_SELECT2       = 0x26;
    public static final int   LAUNCHKEY_SELECT3       = 0x27;
    public static final int   LAUNCHKEY_SELECT4       = 0x28;
    public static final int   LAUNCHKEY_SELECT5       = 0x29;
    public static final int   LAUNCHKEY_SELECT6       = 0x2A;
    public static final int   LAUNCHKEY_SELECT7       = 0x2B;
    public static final int   LAUNCHKEY_SELECT8       = 0x2C;
    public static final int   LAUNCHKEY_TOGGLE_SELECT = 0x2D;

    public static final int   LAUNCHKEY_DEVICE_SELECT = 0x33;
    public static final int   LAUNCHKEY_DEVICE_LOCK   = 0x34;

    public static final int   LAUNCHKEY_FADER_1       = 0x35;
    public static final int   LAUNCHKEY_FADER_2       = 0x36;
    public static final int   LAUNCHKEY_FADER_3       = 0x37;
    public static final int   LAUNCHKEY_FADER_4       = 0x38;
    public static final int   LAUNCHKEY_FADER_5       = 0x39;
    public static final int   LAUNCHKEY_FADER_6       = 0x3A;
    public static final int   LAUNCHKEY_FADER_7       = 0x3B;
    public static final int   LAUNCHKEY_FADER_8       = 0x3C;
    public static final int   LAUNCHKEY_FADER_MASTER  = 0x3D;

    public static final int   LAUNCHKEY_CAPTURE_MIDI  = 0x4A;
    public static final int   LAUNCHKEY_QUANTIZE      = 0x4B;
    public static final int   LAUNCHKEY_CLICK         = 0x4C;
    public static final int   LAUNCHKEY_UNDO          = 0x4D;

    public static final int   LAUNCHKEY_TRACK_RIGHT   = 0x66;
    public static final int   LAUNCHKEY_TRACK_LEFT    = 0x67;

    public static final int   LAUNCHKEY_SCENE1        = 0x68;
    public static final int   LAUNCHKEY_SCENE2        = 0x69;

    public static final int   LAUNCHKEY_ARROW_UP      = 0x6A;
    public static final int   LAUNCHKEY_ARROW_DOWN    = 0x6B;

    public static final int   LAUNCHKEY_SHIFT         = 0x6C;
    public static final int   LAUNCHKEY_PLAY          = 0x73;
    public static final int   LAUNCHKEY_STOP          = 0x74;
    public static final int   LAUNCHKEY_RECORD        = 0x75;
    public static final int   LAUNCHKEY_LOOP          = 0x76;

    // Fader Modes
    public static final int   FADER_MODE_CUSTOM       = 0;
    public static final int   FADER_MODE_VOLUME       = 1;
    public static final int   FADER_MODE_PARAMS       = 2;
    public static final int   FADER_MODE_SEND1        = 4;
    public static final int   FADER_MODE_SEND2        = 5;
    public static final int   FADER_MODE_CUSTOM1      = 6;
    public static final int   FADER_MODE_CUSTOM2      = 7;
    public static final int   FADER_MODE_CUSTOM3      = 8;
    public static final int   FADER_MODE_CUSTOM4      = 9;

    // Knob Modes
    public static final int   KNOB_MODE_CUSTOM        = 0;
    public static final int   KNOB_MODE_VOLUME        = 1;
    public static final int   KNOB_MODE_PARAMS        = 2;
    public static final int   KNOB_MODE_PAN           = 3;
    public static final int   KNOB_MODE_SEND1         = 4;
    public static final int   KNOB_MODE_SEND2         = 5;
    public static final int   KNOB_MODE_CUSTOM1       = 6;
    public static final int   KNOB_MODE_CUSTOM2       = 7;
    public static final int   KNOB_MODE_CUSTOM3       = 8;
    public static final int   KNOB_MODE_CUSTOM4       = 9;

    // Pad Modes
    public static final int   PAD_MODE_CUSTOM         = 0;
    public static final int   PAD_MODE_DRUM           = 1;
    public static final int   PAD_MODE_SESSION        = 2;
    public static final int   PAD_MODE_SCALE_CHORDS   = 3;
    public static final int   PAD_MODE_USER_CHORDS    = 4;
    public static final int   PAD_MODE_CUSTOM_MODE0   = 5;
    public static final int   PAD_MODE_CUSTOM_MODE1   = 6;
    public static final int   PAD_MODE_CUSTOM_MODE2   = 7;
    public static final int   PAD_MODE_CUSTOM_MODE3   = 8;
    public static final int   PAD_MODE_DEVICE_SELECT  = 9;
    public static final int   PAD_MODE_NAVIGATION     = 10;

    private final ModeManager faderModeManager        = new ModeManager ();
    private boolean           isDAWConnected          = false;
    private boolean           hasFaders               = true;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The DAW MIDI output
     * @param input The DAW MIDI input
     * @param definition The Launchpad definition
     */
    public LaunchkeyMk3ControlSurface (final IHost host, final ColorManager colorManager, final LaunchkeyMk3Configuration configuration, final IMidiOutput output, final IMidiInput input)
    {
        super (host, configuration, colorManager, output, input, new LaunchkeyPadGrid (colorManager, output), 1400, 600);

        this.input.setSysexCallback (this::handleSysEx);
        this.output.sendSysex (DeviceInquiry.createQuery ());
    }


    /** {@inheritDoc} */
    @Override
    protected void createPads ()
    {
        ((LaunchkeyPadGrid) this.padGrid).setView (Views.SESSION);

        super.createPads ();

        // Map alternative MIDI notes for grid...
        final int size = this.padGrid.getRows () * this.padGrid.getCols ();
        final int startNote = this.padGrid.getStartNote ();
        for (int i = 0; i < size; i++)
        {
            final int note = startNote + i;

            final ButtonID buttonID = ButtonID.get (ButtonID.PAD17, i);
            IHwButton pad = this.createButton (buttonID, "D " + (i + 1));
            pad.addLight (this.surfaceFactory.createLight (this.surfaceID, null, () -> this.padGrid.getLightInfo (note).getEncoded (), state -> this.padGrid.sendState (note), colorIndex -> this.colorManager.getColor (colorIndex, buttonID), null));
            int [] translated = LaunchkeyPadGrid.translateToController (Views.DRUM, note);
            pad.bind (this.input, BindType.NOTE, translated[0], translated[1]);
            pad.bind ( (event, velocity) -> this.handleGridNote (event, note, velocity));

            final ButtonID buttonID2 = ButtonID.get (ButtonID.PAD33, i);
            pad = this.createButton (buttonID2, "DS " + (i + 1));
            pad.addLight (this.surfaceFactory.createLight (this.surfaceID, null, () -> this.padGrid.getLightInfo (note).getEncoded (), state -> this.padGrid.sendState (note), colorIndex -> this.colorManager.getColor (colorIndex, buttonID2), null));
            translated = LaunchkeyPadGrid.translateToController (Views.DEVICE, note);
            pad.bind (this.input, BindType.NOTE, translated[0], translated[1]);
            pad.bind ( (event, velocity) -> this.handleGridNote (event, note, velocity));
        }
    }


    /**
     * Get the mode manager for the faders.
     *
     * @return The fader mode manager
     */
    public ModeManager getFaderModeManager ()
    {
        return this.faderModeManager;
    }


    /** {@inheritDoc} */
    @Override
    protected void updateViewControls ()
    {
        super.updateViewControls ();

        if (this.hasFaders ())
        {
            final IMode m = this.faderModeManager.getActive ();
            if (m != null)
                m.updateDisplay ();
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void internalShutdown ()
    {
        this.setLaunchpadToDAW (false);
    }


    /**
     * Set the launchkey to DAW mode.
     */
    public void setLaunchpadToDAW (final boolean enable)
    {
        this.output.sendNoteEx (0x0F, LAUNCHKEY_DAW_ONLINE, enable ? 0x7F : 0x00);
    }


    /** {@inheritDoc} */
    @Override
    public LaunchkeyPadGrid getPadGrid ()
    {
        return (LaunchkeyPadGrid) super.getPadGrid ();
    }


    /**
     * Activate a Launchkey knob mode.
     *
     * @param knobMode A knob mode (use KNOB_MODE_* constants)
     */
    public void setKnobMode (final int knobMode)
    {
        this.output.sendCCEx (0x0F, 0x09, knobMode);
    }


    /**
     * Activate a Launchkey pad mode.
     *
     * @param padMode A pad mode (use PAD_MODE_* constants)
     */
    public void setPadMode (final int padMode)
    {
        this.output.sendCCEx (0x0F, 0x03, padMode);
    }


    /**
     * True if the DAW is online.
     *
     * @return True if the DAW is online.
     */
    public boolean isDAWConnected ()
    {
        return this.isDAWConnected;
    }


    /**
     * Set if the DAW is online.
     *
     * @param isDAWConnected True to set the online state
     */
    public void setDAWConnected (final boolean isDAWConnected)
    {
        this.isDAWConnected = isDAWConnected;
    }


    /**
     * Does the model have faders (only on 49 and 61 models).
     *
     * @return True if it has faders
     */
    public boolean hasFaders ()
    {
        return this.hasFaders;
    }


    private void handleSysEx (final String data)
    {
        final int [] byteData = StringUtils.fromHexStr (data);

        final DeviceInquiry deviceInquiry = new DeviceInquiry (byteData);
        if (deviceInquiry.isValid ())
            this.handleDeviceInquiryResponse (deviceInquiry);
    }


    /**
     * Handle the response of a device inquiry.
     *
     * @param deviceInquiry The parsed response
     */
    private void handleDeviceInquiryResponse (final DeviceInquiry deviceInquiry)
    {
        final int [] revisionLevel = deviceInquiry.getRevisionLevel ();
        final String firmwareVersion = String.format ("%d%d%d%d", Integer.valueOf (revisionLevel[0]), Integer.valueOf (revisionLevel[1]), Integer.valueOf (revisionLevel[2]), Integer.valueOf (revisionLevel[3]));
        this.host.println ("Firmware version: " + (firmwareVersion.charAt (0) == '0' ? firmwareVersion.substring (1) : firmwareVersion));

        final int [] deviceFamilyCode = deviceInquiry.getDeviceFamilyCode ();
        this.hasFaders = deviceFamilyCode[0] == LAUNCHKEY_49 || deviceFamilyCode[0] == LAUNCHKEY_61;
    }
}