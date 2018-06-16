// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mkii.controller;

import de.mossgrabers.controller.kontrol.usb.mkii.Kontrol2Configuration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.view.View;


/**
 * The Kontrol 1 surface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol2ControlSurface extends AbstractControlSurface<Kontrol2Configuration> implements UIChangeCallback
{
    /** The auto button. */
    public static final int     BUTTON_AUTO           = 1;
    /** The quantize button. */
    public static final int     BUTTON_QUANTIZE       = 2;
    /** The arp button. */
    public static final int     BUTTON_ARP            = 3;
    /** The scale button. */
    public static final int     BUTTON_SCALE          = 4;
    /** The play button. */
    public static final int     BUTTON_PLAY           = 5;
    /** The undo button. */
    public static final int     BUTTON_UNDO           = 6;
    /** The loop button. */
    public static final int     BUTTON_LOOP           = 7;
    /** The shift button. */
    public static final int     BUTTON_SHIFT          = 8;

    /** The stop button. */
    public static final int     BUTTON_STOP           = 9;
    /** The record button. */
    public static final int     BUTTON_REC            = 10;
    /** The tempo button. */
    public static final int     BUTTON_TEMPO          = 11;
    /** The metro button. */
    public static final int     BUTTON_METRO          = 12;
    /** The preset up button. */
    public static final int     BUTTON_PRESET_UP      = 13;
    /** The page right button. */
    public static final int     BUTTON_PAGE_RIGHT     = 14;
    /** The preset down button. */
    public static final int     BUTTON_PRESET_DOWN    = 15;
    /** The page left button. */
    public static final int     BUTTON_PAGE_LEFT      = 16;

    /** The mute button. */
    public static final int     BUTTON_MUTE           = 17;
    /** The solo button. */
    public static final int     BUTTON_SOLO           = 18;
    /** The scene button. */
    public static final int     BUTTON_SCENE          = 19;
    /** The pattern button. */
    public static final int     BUTTON_PATTERN        = 20;
    /** The track button. */
    public static final int     BUTTON_TRACK          = 21;
    /** The clear button. */
    public static final int     BUTTON_CLEAR          = 22;
    /** The key mode button. */
    public static final int     BUTTON_KEY_MODE       = 23;

    /** The mixer button. */
    public static final int     BUTTON_MIXER          = 24;
    /** The plugin button. */
    public static final int     BUTTON_PLUGIN         = 25;
    /** The browser button. */
    public static final int     BUTTON_BROWSER        = 26;
    /** The setup button. */
    public static final int     BUTTON_SETUP          = 27;
    /** The instance button. */
    public static final int     BUTTON_INSTANCE       = 28;
    /** The midi button. */
    public static final int     BUTTON_MIDI           = 29;

    /** Clicking the main encoder. */
    public static final int     BUTTON_MAIN_ENCODER   = 30;
    /** The navigate right button. */
    public static final int     BUTTON_NAVIGATE_RIGHT = 31;
    /** The navigate down button. */
    public static final int     BUTTON_NAVIGATE_DOWN  = 32;
    /** The navigate left button. */
    public static final int     BUTTON_NAVIGATE_LEFT  = 33;
    /** The navigate up button. */
    public static final int     BUTTON_NAVIGATE_UP    = 34;

    /** The button 1. */
    public static final int     BUTTON_GENERIC_1      = 35;
    /** The button 2. */
    public static final int     BUTTON_GENERIC_2      = 36;
    /** The button 3. */
    public static final int     BUTTON_GENERIC_3      = 37;
    /** The button 4. */
    public static final int     BUTTON_GENERIC_4      = 38;
    /** The button 5. */
    public static final int     BUTTON_GENERIC_5      = 39;
    /** The button 6. */
    public static final int     BUTTON_GENERIC_6      = 40;
    /** The button 7. */
    public static final int     BUTTON_GENERIC_7      = 41;
    /** The button 8. */
    public static final int     BUTTON_GENERIC_8      = 42;

    /** Touching encoder 1. */
    public static final int     TOUCH_ENCODER_1       = 43;
    /** Touching encoder 2. */
    public static final int     TOUCH_ENCODER_2       = 44;
    /** Touching encoder 3. */
    public static final int     TOUCH_ENCODER_3       = 45;
    /** Touching encoder 4. */
    public static final int     TOUCH_ENCODER_4       = 46;
    /** Touching encoder 5. */
    public static final int     TOUCH_ENCODER_5       = 47;
    /** Touching encoder 6. */
    public static final int     TOUCH_ENCODER_6       = 48;
    /** Touching encoder 7. */
    public static final int     TOUCH_ENCODER_7       = 49;
    /** Touching encoder 8. */
    public static final int     TOUCH_ENCODER_8       = 50;

    /** Touching the main encoder. */
    public static final int     TOUCH_ENCODER_MAIN    = 51;

    /** The fixed velocity button. */
    public static final int     BUTTON_FIXED_VEL      = 52;

    // Continuous

    /** Moving encoder 1. */
    public static final int     ENCODER_1             = 60;
    /** Moving encoder 2. */
    public static final int     ENCODER_2             = 61;
    /** Moving encoder 3. */
    public static final int     ENCODER_3             = 62;
    /** Moving encoder 4. */
    public static final int     ENCODER_4             = 63;
    /** Moving encoder 5. */
    public static final int     ENCODER_5             = 64;
    /** Moving encoder 6. */
    public static final int     ENCODER_6             = 65;
    /** Moving encoder 7. */
    public static final int     ENCODER_7             = 66;
    /** Moving encoder 8. */
    public static final int     ENCODER_8             = 67;
    /** Moving the main encoder. */
    public static final int     MAIN_ENCODER          = 68;

    private static final int [] KONTROL2_BUTTONS_ALL  =
    {
        BUTTON_AUTO,
        BUTTON_QUANTIZE,
        BUTTON_ARP,
        BUTTON_SCALE,
        BUTTON_PLAY,
        BUTTON_UNDO,
        BUTTON_LOOP,
        BUTTON_SHIFT,

        BUTTON_STOP,
        BUTTON_REC,
        BUTTON_TEMPO,
        BUTTON_METRO,
        BUTTON_PRESET_UP,
        BUTTON_PAGE_RIGHT,
        BUTTON_PRESET_DOWN,
        BUTTON_PAGE_LEFT,

        BUTTON_MUTE,
        BUTTON_SOLO,
        BUTTON_SCENE,
        BUTTON_PATTERN,
        BUTTON_TRACK,
        BUTTON_CLEAR,
        BUTTON_KEY_MODE,

        BUTTON_MIXER,
        BUTTON_PLUGIN,
        BUTTON_BROWSER,
        BUTTON_SETUP,
        BUTTON_INSTANCE,
        BUTTON_MIDI,

        BUTTON_MAIN_ENCODER,
        BUTTON_NAVIGATE_RIGHT,
        BUTTON_NAVIGATE_DOWN,
        BUTTON_NAVIGATE_LEFT,
        BUTTON_NAVIGATE_UP,

        TOUCH_ENCODER_1,
        TOUCH_ENCODER_2,
        TOUCH_ENCODER_3,
        TOUCH_ENCODER_4,
        TOUCH_ENCODER_5,
        TOUCH_ENCODER_6,
        TOUCH_ENCODER_7,
        TOUCH_ENCODER_8,

        TOUCH_ENCODER_MAIN
    };

    /** Color intensity for an active button. */
    public static final int     BUTTON_STATE_HI       = 11;
    /** Color intensity for an enabled button. */
    public static final int     BUTTON_STATE_ON       = 4;
    /** Color intensity for an disabled button. */
    public static final int     BUTTON_STATE_OFF      = 0;

    private Kontrol2UsbDevice   usbDevice;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param input
     * @param usbDevice
     */
    public Kontrol2ControlSurface (final IHost host, final ColorManager colorManager, final Kontrol2Configuration configuration, final IMidiInput input, final Kontrol2UsbDevice usbDevice)
    {
        super (host, configuration, colorManager, null, input, KONTROL2_BUTTONS_ALL);

        this.usbDevice = usbDevice;

        this.colorManager.registerColor (ColorManager.BUTTON_STATE_OFF, BUTTON_STATE_OFF);
        this.colorManager.registerColor (ColorManager.BUTTON_STATE_ON, BUTTON_STATE_ON);
        this.colorManager.registerColor (ColorManager.BUTTON_STATE_HI, BUTTON_STATE_HI);

        this.shiftButtonId = BUTTON_SHIFT;
        this.leftButtonId = BUTTON_NAVIGATE_LEFT;
        this.rightButtonId = BUTTON_NAVIGATE_RIGHT;
        this.upButtonId = BUTTON_NAVIGATE_UP;
        this.downButtonId = BUTTON_NAVIGATE_DOWN;
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
        this.display.notify ("START " + this.host.getName ().toUpperCase () + " TO PLAY", true, false);
        this.display.shutdown ();
    }


    /** {@inheritDoc} */
    @Override
    public void setButton (final int button, final int state)
    {
        this.usbDevice.setButtonLED (button, state);
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
                // Not used
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
     * Set a key LED. Note that key 0 is always the first key of the Sxx, which means they are
     * different for the different models. Furthermore, this is independent from the octave
     * transposition.
     *
     * @param key The index of the key 0-87
     * @param red The red value 0-255
     * @param green The green value 0-255
     * @param blue The blue value 0-255
     */
    public void setKeyLED (final int key, final int red, final int green, final int blue)
    {
        this.usbDevice.setKeyLED (key, red, green, blue);
    }


    /**
     * Send the key LED stati updates to the device.
     */
    public void updateKeyLEDs ()
    {
        this.usbDevice.updateKeyLEDs ();
    }
}