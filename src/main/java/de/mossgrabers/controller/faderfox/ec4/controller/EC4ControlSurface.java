// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.faderfox.ec4.controller;

import java.util.Arrays;
import java.util.List;

import de.mossgrabers.controller.faderfox.ec4.EC4Configuration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.AbstractTextDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * A control surface which supports the Faderfox EC4 controller.
 *
 * @author Jürgen Moßgraber
 */
public class EC4ControlSurface extends AbstractControlSurface<EC4Configuration>
{
    /** The MIDI CC of the first knob. */
    public static final int                EC4_KNOB_1          = 10;
    /** The MIDI CC of the first button. */
    public static final int                EC4_BUTTON_1        = 30;

    /** The IDs for the continuous elements. */
    public static final List<ContinuousID> KNOB_IDS            = ContinuousID.createSequentialList (ContinuousID.KNOB1, 16);

    private static final byte []           SYSEX_HEADER        =
    {
        (byte) 0xF0,
        0x00,
        0x00,
        0x00,
        0x4E,
        0x2C,
        0x1B
    };

    private static final byte              CMD_APP_FUNC        = 0x4E;

    private static final byte              APP_CMD_SETUP       = 0x28;
    private static final byte              APP_CMD_GROUP       = 0x24;
    private static final byte              APP_CMD_EXT_KEY     = 0x26;
    private static final byte              APP_CMD_SHIFTED_KEY = 0x2A;
    private static final byte              APP_CMD_KEY_STATE   = 0x2E;

    private final Object                   notificationLock    = new Object ();
    private int                            notificationTimeout = 0;

    private int                            setupSlot           = -1;
    private int                            selectedSetup       = -1;
    private int                            selectedGroup       = -1;
    private boolean                        isOnline            = false;
    private final Object                   onlineLock          = new Object ();
    private Modes                          activeMode;

    private boolean                        isShiftPressed      = false;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The MIDI output
     * @param input The MIDI input
     */
    public EC4ControlSurface (final IHost host, final ColorManager colorManager, final EC4Configuration configuration, final IMidiOutput output, final IMidiInput input)
    {
        super (host, configuration, colorManager, output, input, null, 430, 930);

        this.input.setSysexCallback (this::handleSysEx);
    }


    /**
     * Is the DrivenByMoss preset selected?
     *
     * @return True if selected
     */
    public boolean isOnline ()
    {
        synchronized (this.onlineLock)
        {
            return this.isOnline;
        }
    }


    /**
     * Set the index of the setup slot which contains the DrivenByMoss template.
     *
     * @param setupSlot The setup slot
     */
    public void setSetupSlot (final int setupSlot)
    {
        this.setupSlot = setupSlot;
    }


    /**
     * Request device-ID, setup and group.
     */
    public void requestDeviceInfo ()
    {
        this.output.sendSysex (new byte []
        {
            (byte) 0xF0,
            0x00,
            0x00,
            0x00,
            0x4E,
            0x20,
            0x10,
            (byte) 0xF7
        });
    }


    /**
     * Handle incoming system exclusive data. Messages are split up in chunks of 1024 bytes! This
     * method concatenates and stores the parts until the full message is received and then hands it
     * to the processing.
     *
     * @param dataStr The data
     */
    private void handleSysEx (final String dataStr)
    {
        // Check if it is an EC-4 message
        final byte [] data = StringUtils.asBytes (StringUtils.fromHexStr (dataStr));
        if (Arrays.compareUnsigned (SYSEX_HEADER, 0, SYSEX_HEADER.length, data, 0, SYSEX_HEADER.length) != 0 || data[data.length - 1] != (byte) 0xF7)
            return;

        // Extract the content of the message
        final int contentLength = data.length - SYSEX_HEADER.length - 1;
        final byte [] content = new byte [contentLength];
        System.arraycopy (data, SYSEX_HEADER.length, content, 0, contentLength);

        this.handleSysexCommandsController (content);
    }


    /**
     * Handle all system exclusive messages for controller commands.
     *
     * @param data The information data
     */
    private void handleSysexCommandsController (final byte [] data)
    {
        int specialKey = -1;
        int shiftedKey = -1;

        for (int offset = 0; offset < data.length; offset += 3)
        {
            if (data[offset] != CMD_APP_FUNC)
                return;

            final byte commandID = data[offset + 1];
            final byte value = data[offset + 2];

            switch (commandID)
            {
                case APP_CMD_SETUP:
                    this.selectedSetup = value - 0x10;
                    this.handleOnlineStatus ();
                    break;

                case APP_CMD_GROUP:
                    this.selectedGroup = value - 0x10;
                    this.handleOnlineStatus ();
                    break;

                case APP_CMD_EXT_KEY:
                    specialKey = value;
                    break;

                case APP_CMD_SHIFTED_KEY:
                    shiftedKey = value;
                    break;

                case APP_CMD_KEY_STATE:
                    synchronized (this.onlineLock)
                    {
                        if (this.isOnline)
                            this.handleSpecialKeys (specialKey, shiftedKey, value);
                    }
                    break;

                default:
                    // Ignore
                    break;
            }
        }
    }


    private void handleSpecialKeys (final int specialKey, final int shiftedKey, final byte value)
    {
        final boolean isPressed = value == 0x11;

        if (shiftedKey >= 0x10 && shiftedKey <= 0x1F)
        {
            final ButtonID buttonID = ButtonID.get (ButtonID.PAD1, shiftedKey - 0x10);
            this.getButton (buttonID).getCommand ().execute (isPressed ? ButtonEvent.DOWN : ButtonEvent.UP, 127);
            return;
        }

        if (specialKey == 0x11)
        {
            this.isShiftPressed = isPressed;
            this.setKnobSensitivityIsSlow (this.isShiftPressed);
            return;
        }

        if (specialKey >= 0x12 && specialKey <= 0x15)
            this.getButton (ButtonID.get (ButtonID.FOOTSWITCH1, specialKey - 0x12)).trigger (isPressed ? ButtonEvent.DOWN : ButtonEvent.UP);
    }


    /**
     * Put the extension in online/offline mode depending on the selected setup and group index.
     */
    private void handleOnlineStatus ()
    {
        if (this.selectedSetup == this.setupSlot && this.selectedGroup == 0)
            this.setOnline ();
        else
            this.setOffline ();
    }


    /**
     * Set the extension online.
     */
    private void setOffline ()
    {
        synchronized (this.onlineLock)
        {
            if (!this.isOnline)
                return;

            this.host.println ("Going offline...");
            this.isOnline = false;
            this.activeMode = this.modeManager.getActiveID ();
            this.modeManager.setActive (Modes.DUMMY);
        }
    }


    /**
     * Set the extension online.
     */
    private void setOnline ()
    {
        synchronized (this.onlineLock)
        {
            if (this.isOnline)
                return;

            this.host.println ("Going online...");
            this.isOnline = true;
            this.modeManager.setActive (this.activeMode == null ? Modes.TRACK : this.activeMode);
            this.getTextDisplay ().forceFlush ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final BindType bindType, final int channel, final int cc, final int value)
    {
        // Only send MIDI to the device if the DrivenByMoss template is selected
        synchronized (this.onlineLock)
        {
            if (this.isOnline)
                super.setTrigger (bindType, channel, cc, value);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void handleMidi (final int status, final int data1, final int data2)
    {
        // Not used
    }


    /** {@inheritDoc} */
    @Override
    public boolean isShiftPressed ()
    {
        return this.isShiftPressed;
    }


    /**
     * Show the total display page.
     */
    public void showTotalDisplay ()
    {
        synchronized (this.notificationLock)
        {
            final boolean isRunning = this.notificationTimeout > 0;
            this.notificationTimeout = AbstractTextDisplay.NOTIFICATION_TIME;
            if (!isRunning)
            {
                ((EC4Display) this.getTextDisplay ()).setTotalDisplayVisible (true);
                this.host.scheduleTask (this::watch, 100);
            }
        }
    }


    protected void watch ()
    {
        synchronized (this.notificationLock)
        {
            this.notificationTimeout -= 100;

            if (this.notificationTimeout <= 0)
                ((EC4Display) this.getTextDisplay ()).setTotalDisplayVisible (false);
            else
                this.host.scheduleTask (this::watch, 100);
        }
    }


    /**
     * Write the first string array (must contain 4 lines) on the total display and shows it.
     *
     * @param totalDisplayInfo The content to display
     */
    public void fillTotalDisplay (final List<String []> totalDisplayInfo)
    {
        if (!totalDisplayInfo.isEmpty () && !this.isShuttingDown)
            this.fillTotalDisplay (totalDisplayInfo.get (0));
    }


    private void fillTotalDisplay (final String [] lines)
    {
        final ITextDisplay totalDisplay = this.getTextDisplay (1).clear ();
        for (int i = 0; i < lines.length; i++)
            totalDisplay.setRow (i, StringUtils.pad (StringUtils.fixASCII (lines[i]), 20));
        totalDisplay.allDone ();
        this.showTotalDisplay ();
    }


    /** {@inheritDoc} */
    @Override
    protected void internalShutdown ()
    {
        this.fillTotalDisplay (new String []
        {
            "Please start " + this.host.getName (),
            "     to play...",
            "",
            ""
        });

        super.internalShutdown ();
    }
}