// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.controller;

import de.mossgrabers.controller.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.launchpad.definition.ILaunchpadControllerDefinition;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.DeviceInquiry;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Map.Entry;


/**
 * The Launchpad control surface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class LaunchpadControlSurface extends AbstractControlSurface<LaunchpadConfiguration>
{
    public static final int                      LAUNCHPAD_BUTTON_SCENE1    = 89;             // 1/4
    public static final int                      LAUNCHPAD_BUTTON_SCENE2    = 79;
    public static final int                      LAUNCHPAD_BUTTON_SCENE3    = 69;
    public static final int                      LAUNCHPAD_BUTTON_SCENE4    = 59;
    public static final int                      LAUNCHPAD_BUTTON_SCENE5    = 49;             // ...
    public static final int                      LAUNCHPAD_BUTTON_SCENE6    = 39;
    public static final int                      LAUNCHPAD_BUTTON_SCENE7    = 29;
    public static final int                      LAUNCHPAD_BUTTON_SCENE8    = 19;             // 1/32T

    public static final int                      LAUNCHPAD_FADER_1          = 21;
    public static final int                      LAUNCHPAD_FADER_2          = 22;
    public static final int                      LAUNCHPAD_FADER_3          = 23;
    public static final int                      LAUNCHPAD_FADER_4          = 24;
    public static final int                      LAUNCHPAD_FADER_5          = 25;
    public static final int                      LAUNCHPAD_FADER_6          = 26;
    public static final int                      LAUNCHPAD_FADER_7          = 27;
    public static final int                      LAUNCHPAD_FADER_8          = 28;

    public static final int                      LAUNCHPAD_LOGO             = 99;

    public static final int                      LAUNCHPAD_BUTTON_STATE_OFF = 0;
    public static final int                      LAUNCHPAD_BUTTON_STATE_ON  = 1;
    public static final int                      LAUNCHPAD_BUTTON_STATE_HI  = 4;

    public static final int                      CONTROL_MODE_OFF           = 0;
    public static final int                      CONTROL_MODE_REC_ARM       = 1;
    public static final int                      CONTROL_MODE_TRACK_SELECT  = 2;
    public static final int                      CONTROL_MODE_MUTE          = 3;
    public static final int                      CONTROL_MODE_SOLO          = 4;
    public static final int                      CONTROL_MODE_STOP_CLIP     = 5;

    private final ILaunchpadControllerDefinition definition;
    private final int []                         faderColorCache            = new int [8];
    private final boolean []                     faderPanCache              = new boolean [8];


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
        super (host, configuration, colorManager, output, input, new LaunchpadPadGrid (colorManager, output, definition), 800, 800);

        this.definition = definition;

        this.input.setSysexCallback (this::handleSysEx);
        this.output.sendSysex (DeviceInquiry.createQuery ());
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
        this.sendLaunchpadSysEx (this.definition.getStandaloneModeCommand ());
    }


    /**
     * Set the launchpad to program mode. All pads can freely controlled.
     */
    public void setLaunchpadToPrgMode ()
    {
        this.setLaunchpadMode (this.definition.getProgramModeCommand ());
    }


    /**
     * Set the launchpad to panorama mode. 8 groups of 8 vertical pads are used as a fader.
     */
    public void setLaunchpadToFaderMode ()
    {
        this.setLaunchpadMode (this.definition.getFaderModeCommand ());
    }


    /**
     * Set the launchpad to panorama mode. 8 groups of 8 vertical pads are used to control panorama.
     */
    public void setLaunchpadToPanMode ()
    {
        this.setLaunchpadMode (this.definition.getPanModeCommand ());
    }


    private void setLaunchpadMode (final String data)
    {
        this.sendLaunchpadSysEx (data);

        for (final Entry<ButtonID, IHwButton> entry: this.getButtons ().entrySet ())
        {
            final ButtonID key = entry.getKey ();
            final int keyValue = key.ordinal ();
            if (ButtonID.PAD1.ordinal () < keyValue || ButtonID.PAD64.ordinal () > keyValue)
                entry.getValue ().getLight ().clearCache ();
        }
    }


    /**
     * Set the color of a fader (8 vertical pads).
     *
     * @param index The number of the fader (0-7)
     * @param color The color to set
     * @param isPan True for panorama layout
     */
    public void setupFader (final int index, final int color, final boolean isPan)
    {
        if (this.faderColorCache[index] == color && this.faderPanCache[index] == isPan)
            return;

        this.faderColorCache[index] = color;
        this.faderPanCache[index] = isPan;

        // Configure the emulated fader if there is native hardware support
        if (this.definition.hasFaderSupport ())
            this.sendLaunchpadSysEx ("2B 0" + Integer.toString (index) + (isPan ? " 01 " : " 00 ") + StringUtils.toHexStr (color) + " 00");
    }


    /**
     * Set the faders value.
     *
     * @param index The index of the fader (0-7)
     * @param value The value to set
     */
    public void setFaderValue (final int index, final int value)
    {
        if (this.definition.hasFaderSupport ())
            this.output.sendCC (LAUNCHPAD_FADER_1 + index, value);
        else
        {
            final IPadGrid padGrid = this.getPadGrid ();

            if (this.faderPanCache[index])
            {
                // Simulate pan fader
                if (value == 64)
                {
                    for (int i = 0; i < 8; i++)
                        padGrid.lightEx (index, i, i == 3 || i == 4 ? this.faderColorCache[index] : 0);
                }
                else if (value < 64)
                {
                    for (int i = 4; i < 8; i++)
                        padGrid.lightEx (index, 7 - i, 0);

                    final double numPads = 4.0 * value / 64.0 - 1;
                    for (int i = 0; i < 4; i++)
                        padGrid.lightEx (index, 7 - i, i > numPads ? this.faderColorCache[index] : 0);
                }
                else
                {
                    for (int i = 0; i < 4; i++)
                        padGrid.lightEx (index, 7 - i, 0);

                    final double numPads = 4.0 * (value - 64) / 64.0;
                    for (int i = 4; i < 8; i++)
                        padGrid.lightEx (index, 7 - i, i - 4 < numPads ? this.faderColorCache[index] : 0);
                }
            }
            else
            {
                // Simulate normal fader
                final double numPads = 8.0 * value / 127.0;
                for (int i = 0; i < 8; i++)
                    padGrid.lightEx (index, 7 - i, i < numPads ? this.faderColorCache[index] : 0);
            }
        }
    }


    /**
     * Clear the faders cache.
     */
    public void clearFaders ()
    {
        for (int i = 0; i < 8; i++)
        {
            this.faderColorCache[i] = -1;
            this.faderPanCache[i] = false;
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void internalShutdown ()
    {
        if (this.definition.isPro ())
        {
            // Turn off front LED
            this.sendLaunchpadSysEx ("0A 63 00");
        }
        else
            this.setTrigger (LaunchpadControlSurface.LAUNCHPAD_LOGO, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        this.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE1, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        this.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE2, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        this.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE3, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        this.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE4, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        this.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE5, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        this.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE6, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        this.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE7, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        this.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE8, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        super.internalShutdown ();
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


    /** {@inheritDoc} */
    @Override
    protected void flushHardware ()
    {
        super.flushHardware ();

        ((LaunchpadPadGrid) this.pads).flush ();
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


    /**
     * Is this device a Pro model with additional buttons?
     *
     * @return True if it is a pro version
     */
    public boolean isPro ()
    {
        return this.definition.isPro ();
    }


    /**
     * Does it provide support for fader simulation?
     *
     * @return True if supported
     */
    public boolean hasFaderSupport ()
    {
        return this.definition.hasFaderSupport ();
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
        if (revisionLevel.length == 4)
        {
            final String firmwareVersion = String.format ("%d%d%d%d", Integer.valueOf (revisionLevel[0]), Integer.valueOf (revisionLevel[1]), Integer.valueOf (revisionLevel[2]), Integer.valueOf (revisionLevel[3]));
            this.host.println ("Firmware version: " + (firmwareVersion.charAt (0) == '0' ? firmwareVersion.substring (1) : firmwareVersion));
        }
    }
}