// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.controller;

import de.mossgrabers.controller.launchpad.LaunchpadConfiguration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The Launchpad 1 and Launchpad 2 control surface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class LaunchpadControlSurface extends AbstractControlSurface<LaunchpadConfiguration>
{
    public static final int      LAUNCHPAD_BUTTON_SHIFT             = 80;
    public static final int      LAUNCHPAD_BUTTON_CLICK             = 70;
    public static final int      LAUNCHPAD_BUTTON_UNDO              = 60;
    public static final int      LAUNCHPAD_BUTTON_DELETE            = 50;
    public static final int      LAUNCHPAD_BUTTON_QUANTIZE          = 40;
    public static final int      LAUNCHPAD_BUTTON_DUPLICATE         = 30;
    public static final int      LAUNCHPAD_BUTTON_DOUBLE            = 20;
    public static final int      LAUNCHPAD_BUTTON_RECORD            = 10;

    public static final int      LAUNCHPAD_BUTTON_REC_ARM           = 1;
    public static final int      LAUNCHPAD_BUTTON_TRACK             = 2;
    public static final int      LAUNCHPAD_BUTTON_MUTE              = 3;
    public static final int      LAUNCHPAD_BUTTON_SOLO              = 4;
    public static final int      LAUNCHPAD_BUTTON_VOLUME            = 5;
    public static final int      LAUNCHPAD_BUTTON_PAN               = 6;
    public static final int      LAUNCHPAD_BUTTON_SENDS             = 7;
    public static final int      LAUNCHPAD_BUTTON_STOP_CLIP         = 8;

    public static final int      LAUNCHPAD_BUTTON_SCENE1            = 89;                      // 1/4
    public static final int      LAUNCHPAD_BUTTON_SCENE2            = 79;
    public static final int      LAUNCHPAD_BUTTON_SCENE3            = 69;
    public static final int      LAUNCHPAD_BUTTON_SCENE4            = 59;
    public static final int      LAUNCHPAD_BUTTON_SCENE5            = 49;                      // ...
    public static final int      LAUNCHPAD_BUTTON_SCENE6            = 39;
    public static final int      LAUNCHPAD_BUTTON_SCENE7            = 29;
    public static final int      LAUNCHPAD_BUTTON_SCENE8            = 19;                      // 1/32T

    public static final int      LAUNCHPAD_FADER_1                  = 21;
    public static final int      LAUNCHPAD_FADER_2                  = 22;
    public static final int      LAUNCHPAD_FADER_3                  = 23;
    public static final int      LAUNCHPAD_FADER_4                  = 24;
    public static final int      LAUNCHPAD_FADER_5                  = 25;
    public static final int      LAUNCHPAD_FADER_6                  = 26;
    public static final int      LAUNCHPAD_FADER_7                  = 27;
    public static final int      LAUNCHPAD_FADER_8                  = 28;

    public static final int      LAUNCHPAD_PRO_BUTTON_UP            = 91;
    public static final int      LAUNCHPAD_PRO_BUTTON_DOWN          = 92;
    public static final int      LAUNCHPAD_PRO_BUTTON_LEFT          = 93;
    public static final int      LAUNCHPAD_PRO_BUTTON_RIGHT         = 94;
    public static final int      LAUNCHPAD_PRO_BUTTON_SESSION       = 95;
    // User 1 on MkII
    public static final int      LAUNCHPAD_PRO_BUTTON_NOTE          = 96;
    // User 2 on MkII
    public static final int      LAUNCHPAD_PRO_BUTTON_DEVICE        = 97;
    // Mixer on MkII
    public static final int      LAUNCHPAD_PRO_BUTTON_USER          = 98;

    public static final int      LAUNCHPAD_MKII_BUTTON_UP           = 104;
    public static final int      LAUNCHPAD_MKII_BUTTON_DOWN         = 105;
    public static final int      LAUNCHPAD_MKII_BUTTON_LEFT         = 106;
    public static final int      LAUNCHPAD_MKII_BUTTON_RIGHT        = 107;
    public static final int      LAUNCHPAD_MKII_BUTTON_SESSION      = 108;
    public static final int      LAUNCHPAD_MKII_BUTTON_NOTE         = 109;                     // User
                                                                                               // 1
    public static final int      LAUNCHPAD_MKII_BUTTON_DEVICE       = 110;                     // User
                                                                                               // 2
    public static final int      LAUNCHPAD_MKII_BUTTON_USER         = 111;                     // Mixer

    public static final int      LAUNCHPAD_BUTTON_STATE_OFF         = 0;
    public static final int      LAUNCHPAD_BUTTON_STATE_ON          = 1;
    public static final int      LAUNCHPAD_BUTTON_STATE_HI          = 4;

    private static final int []  LAUNCHPAD_PRO_BUTTONS_ALL          =
    {
        LAUNCHPAD_PRO_BUTTON_LEFT,
        LAUNCHPAD_PRO_BUTTON_RIGHT,
        LAUNCHPAD_PRO_BUTTON_UP,
        LAUNCHPAD_PRO_BUTTON_DOWN,
        LAUNCHPAD_PRO_BUTTON_SESSION,
        LAUNCHPAD_PRO_BUTTON_NOTE,
        LAUNCHPAD_PRO_BUTTON_DEVICE,
        LAUNCHPAD_PRO_BUTTON_USER,
        LAUNCHPAD_BUTTON_SHIFT,
        LAUNCHPAD_BUTTON_CLICK,
        LAUNCHPAD_BUTTON_UNDO,
        LAUNCHPAD_BUTTON_DELETE,
        LAUNCHPAD_BUTTON_QUANTIZE,
        LAUNCHPAD_BUTTON_DUPLICATE,
        LAUNCHPAD_BUTTON_DOUBLE,
        LAUNCHPAD_BUTTON_RECORD,
        LAUNCHPAD_BUTTON_REC_ARM,
        LAUNCHPAD_BUTTON_TRACK,
        LAUNCHPAD_BUTTON_MUTE,
        LAUNCHPAD_BUTTON_SOLO,
        LAUNCHPAD_BUTTON_VOLUME,
        LAUNCHPAD_BUTTON_PAN,
        LAUNCHPAD_BUTTON_SENDS,
        LAUNCHPAD_BUTTON_STOP_CLIP,
        LAUNCHPAD_BUTTON_SCENE1,
        LAUNCHPAD_BUTTON_SCENE2,
        LAUNCHPAD_BUTTON_SCENE3,
        LAUNCHPAD_BUTTON_SCENE4,
        LAUNCHPAD_BUTTON_SCENE5,
        LAUNCHPAD_BUTTON_SCENE6,
        LAUNCHPAD_BUTTON_SCENE7,
        LAUNCHPAD_BUTTON_SCENE8
    };

    private static final int []  LAUNCHPAD_MKII_BUTTONS_ALL         =
    {
        LAUNCHPAD_MKII_BUTTON_LEFT,
        LAUNCHPAD_MKII_BUTTON_RIGHT,
        LAUNCHPAD_MKII_BUTTON_UP,
        LAUNCHPAD_MKII_BUTTON_DOWN,
        LAUNCHPAD_MKII_BUTTON_SESSION,
        LAUNCHPAD_MKII_BUTTON_NOTE,
        LAUNCHPAD_MKII_BUTTON_DEVICE,
        LAUNCHPAD_MKII_BUTTON_USER,
        LAUNCHPAD_BUTTON_SHIFT,
        LAUNCHPAD_BUTTON_CLICK,
        LAUNCHPAD_BUTTON_UNDO,
        LAUNCHPAD_BUTTON_DELETE,
        LAUNCHPAD_BUTTON_QUANTIZE,
        LAUNCHPAD_BUTTON_DUPLICATE,
        LAUNCHPAD_BUTTON_DOUBLE,
        LAUNCHPAD_BUTTON_RECORD,
        LAUNCHPAD_BUTTON_REC_ARM,
        LAUNCHPAD_BUTTON_TRACK,
        LAUNCHPAD_BUTTON_MUTE,
        LAUNCHPAD_BUTTON_SOLO,
        LAUNCHPAD_BUTTON_VOLUME,
        LAUNCHPAD_BUTTON_PAN,
        LAUNCHPAD_BUTTON_SENDS,
        LAUNCHPAD_BUTTON_STOP_CLIP,
        LAUNCHPAD_BUTTON_SCENE1,
        LAUNCHPAD_BUTTON_SCENE2,
        LAUNCHPAD_BUTTON_SCENE3,
        LAUNCHPAD_BUTTON_SCENE4,
        LAUNCHPAD_BUTTON_SCENE5,
        LAUNCHPAD_BUTTON_SCENE6,
        LAUNCHPAD_BUTTON_SCENE7,
        LAUNCHPAD_BUTTON_SCENE8
    };

    public static final int      CONTROL_MODE_OFF                   = 0;
    public static final int      CONTROL_MODE_REC_ARM               = 1;
    public static final int      CONTROL_MODE_TRACK_SELECT          = 2;
    public static final int      CONTROL_MODE_MUTE                  = 3;
    public static final int      CONTROL_MODE_SOLO                  = 4;
    public static final int      CONTROL_MODE_STOP_CLIP             = 5;

    public static final String   LAUNCHPAD_PRO_SYSEX_HEADER         = "F0 00 20 29 02 10 ";
    public static final String   LAUNCHPAD_PRO_PRG_MODE             = "2C 03";
    public static final String   LAUNCHPAD_PRO_FADER_MODE           = "2C 02";
    public static final String   LAUNCHPAD_PRO_PAN_MODE             = LAUNCHPAD_PRO_FADER_MODE;

    public static final String   LAUNCHPAD_MKII_SYSEX_HEADER        = "F0 00 20 29 02 18 ";
    public static final String   LAUNCHPAD_MKII_PRG_MODE            = "22 00";
    public static final String   LAUNCHPAD_MKII_FADER_MODE          = "22 04";
    public static final String   LAUNCHPAD_MKII_PAN_MODE            = "22 05";

    private static final byte [] LAUNCHPAD_VERSION_INQUIRY          = new byte []
    {
        (byte) 0xF0,
        (byte) 0x00,
        (byte) 0x20,
        (byte) 0x29,
        (byte) 0x00,
        (byte) 0x70,
        (byte) 0xF7
    };

    private static final int []  LAUNCHPAD_VERSION_INQUIRY_RESPONSE = new int []
    {
        0xF0,
        0x00,
        0x20,
        0x29,
        0x00,
        0x70
    };

    private final boolean        isPro;
    private final String         sysexHeader;
    private final String         prgMode;
    private final String         faderMode;
    private final String         panMode;

    private final int            noteButtonId;
    private final int            userButtonId;
    private final int            sessionButtonId;
    private final int            deviceButtonId;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The midi output
     * @param input The midi input
     * @param isPro Is Pro or MkII?
     */
    public LaunchpadControlSurface (final IHost host, final ColorManager colorManager, final LaunchpadConfiguration configuration, final IMidiOutput output, final IMidiInput input, final boolean isPro)
    {
        super (host, configuration, colorManager, output, input, new LaunchpadPadGrid (colorManager, output, isPro ? LAUNCHPAD_PRO_SYSEX_HEADER : LAUNCHPAD_MKII_SYSEX_HEADER), isPro ? LAUNCHPAD_PRO_BUTTONS_ALL : LAUNCHPAD_MKII_BUTTONS_ALL);

        this.isPro = isPro;

        this.sysexHeader = this.isPro ? LAUNCHPAD_PRO_SYSEX_HEADER : LAUNCHPAD_MKII_SYSEX_HEADER;

        this.prgMode = this.isPro ? LAUNCHPAD_PRO_PRG_MODE : LAUNCHPAD_MKII_PRG_MODE;
        this.faderMode = this.isPro ? LAUNCHPAD_PRO_FADER_MODE : LAUNCHPAD_MKII_FADER_MODE;
        this.panMode = this.isPro ? LAUNCHPAD_PRO_PAN_MODE : LAUNCHPAD_MKII_PAN_MODE;

        this.shiftButtonId = this.isPro ? LAUNCHPAD_BUTTON_SHIFT : LAUNCHPAD_MKII_BUTTON_USER;
        this.deleteButtonId = LAUNCHPAD_BUTTON_DELETE;
        this.soloButtonId = LAUNCHPAD_BUTTON_SOLO;
        this.muteButtonId = LAUNCHPAD_BUTTON_MUTE;
        this.leftButtonId = this.isPro ? LAUNCHPAD_PRO_BUTTON_LEFT : LAUNCHPAD_MKII_BUTTON_LEFT;
        this.rightButtonId = this.isPro ? LAUNCHPAD_PRO_BUTTON_RIGHT : LAUNCHPAD_MKII_BUTTON_RIGHT;
        this.upButtonId = this.isPro ? LAUNCHPAD_PRO_BUTTON_UP : LAUNCHPAD_MKII_BUTTON_UP;
        this.downButtonId = this.isPro ? LAUNCHPAD_PRO_BUTTON_DOWN : LAUNCHPAD_MKII_BUTTON_DOWN;
        this.noteButtonId = this.isPro ? LAUNCHPAD_PRO_BUTTON_NOTE : LAUNCHPAD_MKII_BUTTON_NOTE;
        this.userButtonId = this.isPro ? LAUNCHPAD_PRO_BUTTON_USER : LAUNCHPAD_MKII_BUTTON_USER;
        this.sessionButtonId = this.isPro ? LAUNCHPAD_PRO_BUTTON_SESSION : LAUNCHPAD_MKII_BUTTON_SESSION;
        this.deviceButtonId = this.isPro ? LAUNCHPAD_PRO_BUTTON_DEVICE : LAUNCHPAD_MKII_BUTTON_DEVICE;

        this.input.setSysexCallback (this::handleSysEx);
        this.output.sendSysex (LAUNCHPAD_VERSION_INQUIRY);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isShiftPressed ()
    {
        return this.isPressed (this.shiftButtonId) || this.isPro () && this.isUserPressed ();
    }


    /**
     * Is the user button pressed (mixer on MkII)?
     *
     * @return True if pressed
     */
    public boolean isUserPressed ()
    {
        return this.isPressed (this.userButtonId);
    }


    /**
     * Get the user button ID.
     *
     * @return The ID
     */
    public int getUserButtonId ()
    {
        return this.userButtonId;
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
    public void setButtonEx (final int button, final int channel, final int state)
    {
        if (!this.isPro && (button == LAUNCHPAD_BUTTON_SCENE1 || button == LAUNCHPAD_BUTTON_SCENE2 || button == LAUNCHPAD_BUTTON_SCENE3 || button == LAUNCHPAD_BUTTON_SCENE4 || button == LAUNCHPAD_BUTTON_SCENE5 || button == LAUNCHPAD_BUTTON_SCENE6 || button == LAUNCHPAD_BUTTON_SCENE7 || button == LAUNCHPAD_BUTTON_SCENE8))
            this.output.sendNote (button, state);

        this.output.sendCC (button, state);
    }


    /**
     * Send sysex data to the launchpad.
     *
     * @param data The data without the header and closing byte
     */
    public void sendLaunchpadSysEx (final String data)
    {
        this.output.sendSysex (this.sysexHeader + data + " F7");
    }


    /**
     * Get the note button.
     *
     * @return The ID of the note button
     */
    public int getNoteButton ()
    {
        return this.noteButtonId;
    }


    /**
     * Get the session button.
     *
     * @return The ID of the session button
     */
    public int getSessionButton ()
    {
        return this.sessionButtonId;
    }


    /**
     * Get the device button.
     *
     * @return The ID of the device button
     */
    public int getDeviceButton ()
    {
        return this.deviceButtonId;
    }


    /** {@inheritDoc} */
    @Override
    public int getSceneButton (final int index)
    {
        return LAUNCHPAD_BUTTON_SCENE1 - 10 * index;
    }


    /** {@inheritDoc} */
    @Override
    protected void handleGridNote (final int note, final int velocity)
    {
        super.handleGridNote (this.pads.translateToGrid (note), velocity);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isGridNote (final int note)
    {
        return super.isGridNote (this.pads.translateToGrid (note));
    }


    /**
     * Returns true if it is the Launchpad Pro other wise MkII.
     *
     * @return True if it is the Launchpad Pro other wise MkII
     */
    public boolean isPro ()
    {
        return this.isPro;
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