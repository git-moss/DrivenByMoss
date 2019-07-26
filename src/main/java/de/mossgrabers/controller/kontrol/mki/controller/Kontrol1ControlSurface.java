// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.mki.controller;

import de.mossgrabers.controller.kontrol.mki.Kontrol1Configuration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.View;


/**
 * The Kontrol 1 surface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol1ControlSurface extends AbstractControlSurface<Kontrol1Configuration> implements UIChangeCallback
{
    /** Clicking the main encoder. */
    public static final int   BUTTON_MAIN_ENCODER   = 1;
    /** The preset up button. */
    public static final int   BUTTON_PRESET_UP      = 2;
    /** The enter button. */
    public static final int   BUTTON_ENTER          = 3;
    /** The preset down button. */
    public static final int   BUTTON_PRESET_DOWN    = 4;
    /** The browse button. */
    public static final int   BUTTON_BROWSE         = 5;
    /** The instance button. */
    public static final int   BUTTON_INSTANCE       = 6;
    /** The octave down button. */
    public static final int   BUTTON_OCTAVE_DOWN    = 7;
    /** The octave up button. */
    public static final int   BUTTON_OCTAVE_UP      = 8;

    /** The stop button. */
    public static final int   BUTTON_STOP           = 9;
    /** The record button. */
    public static final int   BUTTON_REC            = 10;
    /** The play button. */
    public static final int   BUTTON_PLAY           = 11;
    /** The navigate right button. */
    public static final int   BUTTON_NAVIGATE_RIGHT = 12;
    /** The navigate down button. */
    public static final int   BUTTON_NAVIGATE_DOWN  = 13;
    /** The navigate left button. */
    public static final int   BUTTON_NAVIGATE_LEFT  = 14;
    /** The back button. */
    public static final int   BUTTON_BACK           = 15;
    /** The navigate up button. */
    public static final int   BUTTON_NAVIGATE_UP    = 16;

    /** The shift button. */
    public static final int   BUTTON_SHIFT          = 17;
    /** The scale button. */
    public static final int   BUTTON_SCALE          = 18;
    /** The arp button. */
    public static final int   BUTTON_ARP            = 19;
    /** The loop button. */
    public static final int   BUTTON_LOOP           = 20;
    /** The page right button. */
    public static final int   BUTTON_PAGE_RIGHT     = 21;
    /** The page left button. */
    public static final int   BUTTON_PAGE_LEFT      = 22;
    /** The rewind button. */
    public static final int   BUTTON_RWD            = 23;
    /** The forward button. */
    public static final int   BUTTON_FWD            = 24;

    /** Touching encoder 1. */
    public static final int   TOUCH_ENCODER_1       = 25;
    /** Touching encoder 2. */
    public static final int   TOUCH_ENCODER_2       = 26;
    /** Touching encoder 3. */
    public static final int   TOUCH_ENCODER_3       = 27;
    /** Touching encoder 4. */
    public static final int   TOUCH_ENCODER_4       = 28;
    /** Touching encoder 5. */
    public static final int   TOUCH_ENCODER_5       = 29;
    /** Touching encoder 6. */
    public static final int   TOUCH_ENCODER_6       = 30;
    /** Touching encoder 7. */
    public static final int   TOUCH_ENCODER_7       = 31;
    /** Touching encoder 8. */
    public static final int   TOUCH_ENCODER_8       = 32;

    /** Touching the main encoder. */
    public static final int   TOUCH_ENCODER_MAIN    = 33;

    // Continuous

    /** Moving encoder 1. */
    public static final int   ENCODER_1             = 40;
    /** Moving encoder 2. */
    public static final int   ENCODER_2             = 41;
    /** Moving encoder 3. */
    public static final int   ENCODER_3             = 42;
    /** Moving encoder 4. */
    public static final int   ENCODER_4             = 43;
    /** Moving encoder 5. */
    public static final int   ENCODER_5             = 44;
    /** Moving encoder 6. */
    public static final int   ENCODER_6             = 45;
    /** Moving encoder 7. */
    public static final int   ENCODER_7             = 46;
    /** Moving encoder 8. */
    public static final int   ENCODER_8             = 47;
    /** Moving the main encoder. */
    public static final int   MAIN_ENCODER          = 48;

    private Kontrol1UsbDevice usbDevice;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param input The midi input
     * @param usbDevice The usb device
     */
    public Kontrol1ControlSurface (final IHost host, final ColorManager colorManager, final Kontrol1Configuration configuration, final IMidiInput input, final Kontrol1UsbDevice usbDevice)
    {
        super (host, configuration, colorManager, null, input, new Kontrol1PadGrid (colorManager, usbDevice));

        this.usbDevice = usbDevice;
        this.shiftButtonId = BUTTON_SHIFT;
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        this.usbDevice.turnOffButtonLEDs ();

        for (int i = 0; i < 88; i++)
            this.usbDevice.setKeyLED (i, 0, 0, 0);
        this.updateKeyLEDs ();

        this.display.clear ();
        this.display.notify ("START " + this.host.getName ().toUpperCase () + " TO PLAY");

        super.shutdown ();
    }


    /** {@inheritDoc} */
    @Override
    protected void redrawGrid ()
    {
        super.redrawGrid ();
        this.updateKeyLEDs ();
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final int channel, final int cc, final int value)
    {
        this.usbDevice.setButtonLED (cc, value);
    }


    /** {@inheritDoc} */
    @Override
    public void setContinuous (final int channel, final int cc, final int value)
    {
        this.usbDevice.setButtonLED (cc, value);
    }


    /** {@inheritDoc} */
    @Override
    public void buttonChange (final int buttonID, final boolean isPressed)
    {
        if (isPressed)
        {
            if (!this.isPressed (buttonID))
                this.handleCC (0, buttonID, 127);
        }
        else if (this.isPressed (buttonID))
            this.handleCC (0, buttonID, 0);
    }


    /** {@inheritDoc} */
    @Override
    public void mainEncoderChanged (final boolean valueIncreased)
    {
        this.handleCC (0, MAIN_ENCODER, valueIncreased ? 1 : 127);
    }


    /** {@inheritDoc} */
    @Override
    public void encoderChanged (final int encIndex, final boolean valueIncreased)
    {
        this.handleCC (0, ENCODER_1 + encIndex, valueIncreased ? 1 : 127);
    }


    /** {@inheritDoc} */
    @Override
    public void keyboardChanged (final int firstNote)
    {
        final int endNote = firstNote + this.usbDevice.getNumKeys () - 1;
        this.display.notify (Scales.formatDrumNote (firstNote) + " to " + Scales.formatDrumNote (endNote));

        this.host.scheduleTask ( () -> this.getPadGrid ().forceFlush (), 100);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleMidi (final int status, final int data1, final int data2)
    {
        final int code = status & 0xF0;
        final int channel = status & 0xF;

        View view;
        switch (code)
        {
            // Note on/off
            case 0x80:
            case 0x90:
                if (this.isGridNote (data1))
                    this.handleGridNote (data1, data2);
                break;

            // Polyphonic Aftertouch
            case 0xA0:
                view = this.viewManager.getActiveView ();
                if (view != null)
                    view.executeAftertouchCommand (data1, data2);
                break;

            // CC
            case 0xB0:
                // Not used
                break;

            // Channel Aftertouch
            case 0xD0:
                view = this.viewManager.getActiveView ();
                if (view != null)
                    view.executeAftertouchCommand (-1, data1);
                break;

            // Pitch Bend
            case 0xE0:
                view = this.viewManager.getActiveView ();
                if (view != null)
                    view.executePitchbendCommand (channel, data1, data2);
                break;

            default:
                this.host.println ("Unhandled midi status: " + status);
                break;
        }
    }


    /**
     * Send the LED stati to the device.
     */
    public void updateButtonLEDs ()
    {
        this.usbDevice.updateButtonLEDs ();
    }


    /**
     * Send the key LED stati updates to the device.
     */
    public void updateKeyLEDs ()
    {
        this.usbDevice.updateKeyLEDs ();
    }
}