// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.controller;

import de.mossgrabers.controller.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.launchpad.definition.ILaunchpadControllerDefinition;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The Launchpad control surface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class LaunchpadControlSurface extends AbstractControlSurface<LaunchpadConfiguration>
{
    public static final int                      LAUNCHPAD_BUTTON_SCENE1            = 89;                      // 1/4
    public static final int                      LAUNCHPAD_BUTTON_SCENE2            = 79;
    public static final int                      LAUNCHPAD_BUTTON_SCENE3            = 69;
    public static final int                      LAUNCHPAD_BUTTON_SCENE4            = 59;
    public static final int                      LAUNCHPAD_BUTTON_SCENE5            = 49;                      // ...
    public static final int                      LAUNCHPAD_BUTTON_SCENE6            = 39;
    public static final int                      LAUNCHPAD_BUTTON_SCENE7            = 29;
    public static final int                      LAUNCHPAD_BUTTON_SCENE8            = 19;                      // 1/32T

    public static final int                      LAUNCHPAD_FADER_1                  = 21;
    public static final int                      LAUNCHPAD_FADER_2                  = 22;
    public static final int                      LAUNCHPAD_FADER_3                  = 23;
    public static final int                      LAUNCHPAD_FADER_4                  = 24;
    public static final int                      LAUNCHPAD_FADER_5                  = 25;
    public static final int                      LAUNCHPAD_FADER_6                  = 26;
    public static final int                      LAUNCHPAD_FADER_7                  = 27;
    public static final int                      LAUNCHPAD_FADER_8                  = 28;

    public static final int                      LAUNCHPAD_BUTTON_STATE_OFF         = 0;
    public static final int                      LAUNCHPAD_BUTTON_STATE_ON          = 1;
    public static final int                      LAUNCHPAD_BUTTON_STATE_HI          = 4;

    public static final int                      CONTROL_MODE_OFF                   = 0;
    public static final int                      CONTROL_MODE_REC_ARM               = 1;
    public static final int                      CONTROL_MODE_TRACK_SELECT          = 2;
    public static final int                      CONTROL_MODE_MUTE                  = 3;
    public static final int                      CONTROL_MODE_SOLO                  = 4;
    public static final int                      CONTROL_MODE_STOP_CLIP             = 5;

    public static final String                   LAUNCHPAD_PRO_PRG_MODE             = "2C 03";
    public static final String                   LAUNCHPAD_PRO_FADER_MODE           = "2C 02";
    public static final String                   LAUNCHPAD_PRO_PAN_MODE             = LAUNCHPAD_PRO_FADER_MODE;

    public static final String                   LAUNCHPAD_MKII_PRG_MODE            = "22 00";
    public static final String                   LAUNCHPAD_MKII_FADER_MODE          = "22 04";
    public static final String                   LAUNCHPAD_MKII_PAN_MODE            = "22 05";

    private static final byte []                 LAUNCHPAD_VERSION_INQUIRY          =
    {
        (byte) 0xF0,
        (byte) 0x00,
        (byte) 0x20,
        (byte) 0x29,
        (byte) 0x00,
        (byte) 0x70,
        (byte) 0xF7
    };

    private static final int []                  LAUNCHPAD_VERSION_INQUIRY_RESPONSE =
    {
        0xF0,
        0x00,
        0x20,
        0x29,
        0x00,
        0x70
    };

    private final ILaunchpadControllerDefinition definition;
    private final String                         prgMode;
    private final String                         faderMode;
    private final String                         panMode;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The midi output
     * @param input The midi input
     * @param definition The Launchpad definition
     */
    public LaunchpadControlSurface (final IHost host, final ColorManager colorManager, final LaunchpadConfiguration configuration, final IMidiOutput output, final IMidiInput input, final ILaunchpadControllerDefinition definition)
    {
        super (host, configuration, colorManager, output, input, new LaunchpadPadGrid (colorManager, output, definition));

        this.definition = definition;

        // TODO
        this.prgMode = this.definition.isPro () ? LAUNCHPAD_PRO_PRG_MODE : LAUNCHPAD_MKII_PRG_MODE;
        this.faderMode = this.definition.isPro () ? LAUNCHPAD_PRO_FADER_MODE : LAUNCHPAD_MKII_FADER_MODE;
        this.panMode = this.definition.isPro () ? LAUNCHPAD_PRO_PAN_MODE : LAUNCHPAD_MKII_PAN_MODE;

        this.buttonIDs.putAll (this.definition.getButtonIDs ());

        this.input.setSysexCallback (this::handleSysEx);
        this.output.sendSysex (LAUNCHPAD_VERSION_INQUIRY);
    }


    /**
     * Is the user button pressed (mixer on MkII)?
     *
     * @return True if pressed
     */
    public boolean isUserPressed ()
    {
        return this.isPressed (ButtonID.USER);
    }


    /**
     * Set the launchpad to standalone mode.
     */
    public void setLaunchpadToStandalone ()
    {
        this.sendLaunchpadSysEx ("21 01");
    }


    /**
     * Set the launchpad to program mode. All pads can freely controlled.
     */
    public void setLaunchpadToPrgMode ()
    {
        this.sendLaunchpadSysEx (this.prgMode);
        // Ensure that grid gets redrawn, switch modes is especially very slow on the MkII
        this.host.scheduleTask (this.getPadGrid ()::forceFlush, 200);
    }


    /**
     * Set the launchpad to panorama mode. 8 groups of 8 vertical pads are used as a fader.
     */
    public void setLaunchpadToFaderMode ()
    {
        this.sendLaunchpadSysEx (this.faderMode);
        // Ensure that grid gets redrawn, switch modes is especially very slow on the MkII
        this.host.scheduleTask (this.getPadGrid ()::forceFlush, 200);
    }


    /**
     * Set the launchpad to panorama mode. 8 groups of 8 vertical pads are used to control panorama.
     */
    public void setLaunchpadToPanMode ()
    {
        this.sendLaunchpadSysEx (this.panMode);
        // Ensure that grid gets redrawn, switch modes is especially very slow on the MkII
        this.host.scheduleTask (this.getPadGrid ()::forceFlush, 200);
    }


    /**
     * Set the color of a fader (8 vertical pads).
     *
     * @param number The number of the fader (0-7)
     * @param color The color to set
     */
    public void setupFader (final int number, final int color)
    {
        this.sendLaunchpadSysEx ("2B 0" + Integer.toString (number) + " 00 " + StringUtils.toHexStr (color) + " 00");
    }


    /**
     * Set the color of a panorama fader (8 vertical pads).
     *
     * @param number The number of the fader (0-7)
     * @param color The color to set
     */
    public void setupPanFader (final int number, final int color)
    {
        this.sendLaunchpadSysEx ("2B 0" + Integer.toString (number) + " 01 " + StringUtils.toHexStr (color) + " 00");
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        // Turn off front LED
        this.sendLaunchpadSysEx ("0A 63 00");

        super.shutdown ();
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final int channel, final int cc, final int state)
    {
        if (!this.isPro () && (cc == LAUNCHPAD_BUTTON_SCENE1 || cc == LAUNCHPAD_BUTTON_SCENE2 || cc == LAUNCHPAD_BUTTON_SCENE3 || cc == LAUNCHPAD_BUTTON_SCENE4 || cc == LAUNCHPAD_BUTTON_SCENE5 || cc == LAUNCHPAD_BUTTON_SCENE6 || cc == LAUNCHPAD_BUTTON_SCENE7 || cc == LAUNCHPAD_BUTTON_SCENE8))
            this.output.sendNote (cc, state);
        else
            this.output.sendCC (cc, state);
    }


    /**
     * Send sysex data to the launchpad.
     *
     * @param data The data without the header and closing byte
     */
    public void sendLaunchpadSysEx (final String data)
    {
        this.output.sendSysex (this.definition.getSysExHeader () + data + " F7");
    }


    /** {@inheritDoc} */
    @Override
    public int getSceneTrigger (final int index)
    {
        return LAUNCHPAD_BUTTON_SCENE1 - 10 * index;
    }


    /**
     * Is this device a Pro model with additional buttons?
     *
     * @return True if it is a pro version
     */
    public boolean isPro ()
    {
        return this.definition.isPro ();
    }


    private void handleSysEx (final String data)
    {
        final int [] resultData = StringUtils.fromHexStr (data);

        if (compareInts (LAUNCHPAD_VERSION_INQUIRY_RESPONSE, resultData, LAUNCHPAD_VERSION_INQUIRY_RESPONSE.length))
        {
            // Returns the current bootloader and firmware versions and size of bootloader in KB
            // f0 00 20 29 00 70 - 00 - 00 01 05 04 - 00 - 00 01 07 03 - 19 01 - f7

            final int bootloaderVersion = resultData[8] * 100 + resultData[9] * 10 + resultData[10];
            final int firmwareVersion = resultData[13] * 100 + resultData[14] * 10 + resultData[15];
            this.host.println ("Bootloader: " + bootloaderVersion);
            this.host.println ("Firmware: " + firmwareVersion);
        }

        // Further received data, which is not used:
        // - Mode status: f000202902102dxxf7
        // - Standalone Layout status: f000202902102fyyf7
    }


    private static boolean compareInts (final int [] array1, final int [] array2, final int length)
    {
        if (array1.length < length || array2.length < length)
            return false;

        for (int i = 0; i < length; i++)
        {
            if (array1[i] != array2[i])
                return false;
        }
        return true;
    }
}