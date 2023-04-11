// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.controller;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.definition.ILaunchpadControllerDefinition;
import de.mossgrabers.controller.novation.launchpad.view.VirtualFaderViewCallback;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IVirtualFader;
import de.mossgrabers.framework.controller.grid.VirtualFaderImpl;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.DeviceInquiry;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.daw.midi.MidiConstants;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.framework.view.Views;

import java.util.Map.Entry;
import java.util.Optional;


/**
 * The Launchpad control surface.
 *
 * @author Jürgen Moßgraber
 */
@SuppressWarnings("javadoc")
public class LaunchpadControlSurface extends AbstractControlSurface<LaunchpadConfiguration>
{
    public static final int                      LAUNCHPAD_BUTTON_SCENE1     = 89;                   // 1/4
    public static final int                      LAUNCHPAD_BUTTON_SCENE2     = 79;
    public static final int                      LAUNCHPAD_BUTTON_SCENE3     = 69;
    public static final int                      LAUNCHPAD_BUTTON_SCENE4     = 59;
    public static final int                      LAUNCHPAD_BUTTON_SCENE5     = 49;                   // ...
    public static final int                      LAUNCHPAD_BUTTON_SCENE6     = 39;
    public static final int                      LAUNCHPAD_BUTTON_SCENE7     = 29;
    public static final int                      LAUNCHPAD_BUTTON_SCENE8     = 19;                   // 1/32T

    public static final int                      LAUNCHPAD_FADER_1           = 21;
    public static final int                      LAUNCHPAD_FADER_2           = 22;
    public static final int                      LAUNCHPAD_FADER_3           = 23;
    public static final int                      LAUNCHPAD_FADER_4           = 24;
    public static final int                      LAUNCHPAD_FADER_5           = 25;
    public static final int                      LAUNCHPAD_FADER_6           = 26;
    public static final int                      LAUNCHPAD_FADER_7           = 27;
    public static final int                      LAUNCHPAD_FADER_8           = 28;

    public static final int                      PRO3_LAUNCHPAD_FIXED_LENGTH = 30;

    public static final int                      LAUNCHPAD_LOGO              = 99;

    public static final int                      LAUNCHPAD_TRACK1            = 101;
    public static final int                      LAUNCHPAD_TRACK2            = 102;
    public static final int                      LAUNCHPAD_TRACK3            = 103;
    public static final int                      LAUNCHPAD_TRACK4            = 104;
    public static final int                      LAUNCHPAD_TRACK5            = 105;
    public static final int                      LAUNCHPAD_TRACK6            = 106;
    public static final int                      LAUNCHPAD_TRACK7            = 107;
    public static final int                      LAUNCHPAD_TRACK8            = 108;

    public static final int                      LAUNCHPAD_BUTTON_STATE_OFF  = 0;
    public static final int                      LAUNCHPAD_BUTTON_STATE_ON   = 1;
    public static final int                      LAUNCHPAD_BUTTON_STATE_HI   = 4;

    public static final int                      CONTROL_MODE_OFF            = 0;
    public static final int                      CONTROL_MODE_REC_ARM        = 1;
    public static final int                      CONTROL_MODE_TRACK_SELECT   = 2;
    public static final int                      CONTROL_MODE_MUTE           = 3;
    public static final int                      CONTROL_MODE_SOLO           = 4;
    public static final int                      CONTROL_MODE_STOP_CLIP      = 5;

    private final ILaunchpadControllerDefinition definition;

    private final IVirtualFader []               virtualFaders               = new IVirtualFader [8];


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The MIDI output
     * @param input The MIDI input
     * @param definition The Launchpad definition
     */
    public LaunchpadControlSurface (final IHost host, final ColorManager colorManager, final LaunchpadConfiguration configuration, final IMidiOutput output, final IMidiInput input, final ILaunchpadControllerDefinition definition)
    {
        super (host, configuration, colorManager, output, input, new LaunchpadPadGrid (colorManager, output, definition), definition.isPro () ? 800 : 680, definition.isPro () ? 740 : 670);

        this.definition = definition;

        for (int i = 0; i < this.virtualFaders.length; i++)
            this.virtualFaders[i] = new VirtualFaderImpl (host, new VirtualFaderViewCallback (i, this.viewManager), this.padGrid, i);

        this.input.setSysexCallback (this::handleSysEx);
        this.output.sendSysex (DeviceInquiry.createQuery ());
    }


    /** {@inheritDoc} */
    @Override
    protected void handleMidi (final int status, final int data1, final int data2)
    {
        // Workaround for user parameter mappings
        if (this.getViewManager ().isActive (Views.USER))
        {
            final int code = status & 0xF0;
            // Note off
            if (code == MidiConstants.CMD_NOTE_OFF || code == MidiConstants.CMD_NOTE_ON)
            {
                final int translated = this.padGrid.translateToGrid (data1);
                this.handleGridNote (code == MidiConstants.CMD_NOTE_OFF || data2 == 0 ? ButtonEvent.UP : ButtonEvent.DOWN, translated, data2);
                return;
            }
        }

        super.handleMidi (status, data1, data2);
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


    private void setLaunchpadMode (final String data)
    {
        this.sendLaunchpadSysEx (data);

        for (final Entry<ButtonID, IHwButton> entry: this.getButtons ().entrySet ())
        {
            final ButtonID key = entry.getKey ();
            final int keyValue = key.ordinal ();
            if (ButtonID.PAD1.ordinal () < keyValue || ButtonID.PAD64.ordinal () > keyValue)
                entry.getValue ().getLight ().forceFlush ();
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
        this.virtualFaders[index].setup (color, isPan);
    }


    /**
     * Set the faders value.
     *
     * @param index The index of the fader (0-7)
     * @param value The value to set
     */
    public void setFaderValue (final int index, final int value)
    {
        this.virtualFaders[index].setValue (value);
    }


    /**
     * Clear the faders cache.
     */
    public void clearFaders ()
    {
        for (int i = 0; i < 8; i++)
            this.setupFader (i, -1, false);
    }


    /**
     * Move the fader to a new position
     *
     * @param index The index of the fader (0-7)
     * @param row The row to move to (0-7)
     * @param velocity The velocity (for speed)
     */
    public void moveFader (final int index, final int row, final int velocity)
    {
        this.virtualFaders[index].moveTo (row, velocity);
    }


    /**
     * Send the current brightness setting to the controller device.
     */
    public void updateBrightness ()
    {
        final Optional<String> brightnessSysex = this.definition.getBrightnessSysex ();
        if (brightnessSysex.isEmpty ())
            return;

        final int padBrightness = this.configuration.getPadBrightness ();
        final String sysexMessage = String.format (brightnessSysex.get (), Integer.valueOf (padBrightness));
        this.output.sendSysex (sysexMessage);
    }


    /** {@inheritDoc} */
    @Override
    protected void internalShutdown ()
    {
        this.definition.setLogoColor (this, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        this.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE1, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        this.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE2, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        this.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE3, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        this.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE4, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        this.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE5, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        this.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE6, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        this.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE7, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        this.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE8, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        super.internalShutdown ();

        this.definition.resetMode (this);
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final BindType bindType, final int channel, final int cc, final int state)
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

        ((LaunchpadPadGrid) this.padGrid).flush ();
    }


    /**
     * Send system exclusive data to the launchpad.
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
     * Does the device have dedicated buttons to select the tracks?
     *
     * @return True if supported
     */
    public boolean hasTrackSelectionButtons ()
    {
        return this.definition.hasTrackSelectionButtons ();
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