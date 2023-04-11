// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mki.controller;

import de.mossgrabers.controller.ni.kontrol.mki.Kontrol1Configuration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.OutputID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwContinuousControl;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * The Kontrol 1 surface.
 *
 * @author Jürgen Moßgraber
 */
public class Kontrol1ControlSurface extends AbstractControlSurface<Kontrol1Configuration> implements UIChangeCallback
{
    /** Clicking the main encoder. */
    public static final int                     BUTTON_MAIN_ENCODER   = 1;
    /** The preset up button. */
    public static final int                     BUTTON_PRESET_UP      = 2;
    /** The enter button. */
    public static final int                     BUTTON_ENTER          = 3;
    /** The preset down button. */
    public static final int                     BUTTON_PRESET_DOWN    = 4;
    /** The browse button. */
    public static final int                     BUTTON_BROWSE         = 5;
    /** The instance button. */
    public static final int                     BUTTON_INSTANCE       = 6;
    /** The octave down button. */
    public static final int                     BUTTON_OCTAVE_DOWN    = 7;
    /** The octave up button. */
    public static final int                     BUTTON_OCTAVE_UP      = 8;

    /** The stop button. */
    public static final int                     BUTTON_STOP           = 9;
    /** The record button. */
    public static final int                     BUTTON_REC            = 10;
    /** The play button. */
    public static final int                     BUTTON_PLAY           = 11;
    /** The navigate right button. */
    public static final int                     BUTTON_NAVIGATE_RIGHT = 12;
    /** The navigate down button. */
    public static final int                     BUTTON_NAVIGATE_DOWN  = 13;
    /** The navigate left button. */
    public static final int                     BUTTON_NAVIGATE_LEFT  = 14;
    /** The back button. */
    public static final int                     BUTTON_BACK           = 15;
    /** The navigate up button. */
    public static final int                     BUTTON_NAVIGATE_UP    = 16;

    /** The shift button. */
    public static final int                     BUTTON_SHIFT          = 17;
    /** The scale button. */
    public static final int                     BUTTON_SCALE          = 18;
    /** The arp button. */
    public static final int                     BUTTON_ARP            = 19;
    /** The loop button. */
    public static final int                     BUTTON_LOOP           = 20;
    /** The page right button. */
    public static final int                     BUTTON_PAGE_RIGHT     = 21;
    /** The page left button. */
    public static final int                     BUTTON_PAGE_LEFT      = 22;
    /** The rewind button. */
    public static final int                     BUTTON_RWD            = 23;
    /** The forward button. */
    public static final int                     BUTTON_FWD            = 24;

    /** Touching encoder 1. */
    public static final int                     TOUCH_ENCODER_1       = 25;
    /** Touching encoder 2. */
    public static final int                     TOUCH_ENCODER_2       = 26;
    /** Touching encoder 3. */
    public static final int                     TOUCH_ENCODER_3       = 27;
    /** Touching encoder 4. */
    public static final int                     TOUCH_ENCODER_4       = 28;
    /** Touching encoder 5. */
    public static final int                     TOUCH_ENCODER_5       = 29;
    /** Touching encoder 6. */
    public static final int                     TOUCH_ENCODER_6       = 30;
    /** Touching encoder 7. */
    public static final int                     TOUCH_ENCODER_7       = 31;
    /** Touching encoder 8. */
    public static final int                     TOUCH_ENCODER_8       = 32;

    /** Touching the main encoder. */
    public static final int                     TOUCH_ENCODER_MAIN    = 33;

    // Continuous

    /** Moving encoder 1. */
    public static final int                     ENCODER_1             = 40;
    /** Moving encoder 2. */
    public static final int                     ENCODER_2             = 41;
    /** Moving encoder 3. */
    public static final int                     ENCODER_3             = 42;
    /** Moving encoder 4. */
    public static final int                     ENCODER_4             = 43;
    /** Moving encoder 5. */
    public static final int                     ENCODER_5             = 44;
    /** Moving encoder 6. */
    public static final int                     ENCODER_6             = 45;
    /** Moving encoder 7. */
    public static final int                     ENCODER_7             = 46;
    /** Moving encoder 8. */
    public static final int                     ENCODER_8             = 47;
    /** Moving the main encoder. */
    public static final int                     MAIN_ENCODER          = 48;

    private static final Map<Integer, ButtonID> BUTTON_MAP            = new HashMap<> ();
    static
    {
        BUTTON_MAP.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_SHIFT), ButtonID.SHIFT);
        BUTTON_MAP.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_SCALE), ButtonID.SCALES);
        BUTTON_MAP.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_ARP), ButtonID.METRONOME);
        BUTTON_MAP.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_PLAY), ButtonID.PLAY);
        BUTTON_MAP.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_REC), ButtonID.RECORD);
        BUTTON_MAP.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_STOP), ButtonID.STOP);
        BUTTON_MAP.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_RWD), ButtonID.REWIND);
        BUTTON_MAP.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_FWD), ButtonID.FORWARD);
        BUTTON_MAP.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_LOOP), ButtonID.LOOP);
        BUTTON_MAP.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_PAGE_LEFT), ButtonID.PAGE_LEFT);
        BUTTON_MAP.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_PAGE_RIGHT), ButtonID.PAGE_RIGHT);
        BUTTON_MAP.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_MAIN_ENCODER), ButtonID.MASTERTRACK);
        BUTTON_MAP.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_NAVIGATE_DOWN), ButtonID.ARROW_DOWN);
        BUTTON_MAP.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_NAVIGATE_UP), ButtonID.ARROW_UP);
        BUTTON_MAP.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_NAVIGATE_LEFT), ButtonID.ARROW_LEFT);
        BUTTON_MAP.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_NAVIGATE_RIGHT), ButtonID.ARROW_RIGHT);
        BUTTON_MAP.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_BACK), ButtonID.MUTE);
        BUTTON_MAP.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_ENTER), ButtonID.SOLO);
        BUTTON_MAP.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_BROWSE), ButtonID.BROWSE);
    }

    private static final Map<Integer, ContinuousID> CONTINUOUS_MAP = new HashMap<> ();
    static
    {
        for (int i = 0; i < 8; i++)
            CONTINUOUS_MAP.put (Integer.valueOf (Kontrol1ControlSurface.TOUCH_ENCODER_1 + i), ContinuousID.get (ContinuousID.KNOB1, i));
        CONTINUOUS_MAP.put (Integer.valueOf (Kontrol1ControlSurface.TOUCH_ENCODER_MAIN), ContinuousID.MASTER_KNOB);
    }

    private final Kontrol1UsbDevice usbDevice;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param input The MIDI input
     * @param usbDevice The USB device
     */
    public Kontrol1ControlSurface (final IHost host, final ColorManager colorManager, final Kontrol1Configuration configuration, final IMidiInput input, final Kontrol1UsbDevice usbDevice)
    {
        super (0, host, configuration, colorManager, null, input, null, null, 800, 300);

        this.usbDevice = usbDevice;

        this.lightGuide = new Kontrol1LightGuide (colorManager, usbDevice);
        this.createLightGuide ();
    }


    /** {@inheritDoc} */
    @Override
    protected void createLightGuide ()
    {
        if (this.lightGuide == null)
            return;

        for (int i = 0; i < this.usbDevice.getNumKeys (); i++)
        {
            final int index = i;

            this.createLight (OutputID.get (OutputID.LIGHT_GUIDE1, i), () -> {

                // The lights on the device are always addressed from 0..N, therefore the currently
                // selected first note of the keyboard (depending on octave transpose) needs to be
                // added
                final int firstNote = this.usbDevice.getFirstNote ();
                final int note = firstNote + index;
                if (note >= 128)
                    return -1;
                return this.lightGuide.getLightInfo (note).getEncoded ();

            }, state -> {

                final int firstNote = this.usbDevice.getFirstNote ();
                final int note = firstNote + index;
                this.lightGuide.sendState (note);

            }, colorIndex -> this.colorManager.getColor (colorIndex, null), null);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void internalShutdown ()
    {
        this.usbDevice.turnOffButtonLEDs ();

        for (int i = 0; i < 88; i++)
            this.usbDevice.setKeyLED (i, 0, 0, 0);
        this.updateKeyLEDs ();

        this.getTextDisplay ().clear ().notify (" START  " + this.host.getName ().toUpperCase (Locale.US) + " TO PLAY");

        super.internalShutdown ();
    }


    /** {@inheritDoc} */
    @Override
    protected void updateGrid ()
    {
        super.updateGrid ();

        this.updateKeyLEDs ();
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final BindType bindType, final int channel, final int cc, final int value)
    {
        this.usbDevice.setButtonLED (cc, value);
    }


    /** {@inheritDoc} */
    @Override
    public void buttonChange (final int usbControlNumber, final boolean isPressed)
    {
        final ButtonID buttonID = BUTTON_MAP.get (Integer.valueOf (usbControlNumber));
        if (buttonID == null)
        {
            // Simulate knob touch via CC
            final ContinuousID continuousID = CONTINUOUS_MAP.get (Integer.valueOf (usbControlNumber));
            if (continuousID == null)
                return;
            final IHwContinuousControl continuous = this.getContinuous (continuousID);
            if (isPressed)
            {
                if (!continuous.isTouched ())
                    continuous.triggerTouch (true);
            }
            else if (continuous.isTouched ())
                continuous.triggerTouch (false);
            return;
        }

        // Simulate button press via CC
        if (isPressed)
        {
            if (!this.isPressed (buttonID))
                this.getButton (buttonID).trigger (ButtonEvent.DOWN);
        }
        else if (this.isPressed (buttonID))
            this.getButton (buttonID).trigger (ButtonEvent.UP);
    }


    /** {@inheritDoc} */
    @Override
    public void mainEncoderChanged (final boolean valueIncreased)
    {
        this.getContinuous (ContinuousID.MASTER_KNOB).getCommand ().execute (valueIncreased ? 3 : 125);
    }


    /** {@inheritDoc} */
    @Override
    public void encoderChanged (final int encIndex, final int change)
    {
        final int v;
        if (this.isShiftPressed ())
            v = change < 0 ? 127 : 1;
        else
            v = change < 0 ? 127 + change : change;
        this.getContinuous (ContinuousID.get (ContinuousID.KNOB1, encIndex)).getCommand ().execute (v);
    }


    /** {@inheritDoc} */
    @Override
    public void octaveChanged (final int firstNote)
    {
        final int endNote = firstNote + this.usbDevice.getNumKeys () - 1;
        this.getDisplay ().notify (Scales.formatDrumNote (firstNote) + " to " + Scales.formatDrumNote (endNote));
    }


    /**
     * Send the LED status to the device.
     */
    public void updateButtonLEDs ()
    {
        this.usbDevice.updateButtonLEDs ();
    }


    /**
     * Send the key LED status updates to the device.
     */
    public void updateKeyLEDs ()
    {
        this.usbDevice.updateKeyLEDs ();
    }


    /** {@inheritDoc} */
    @Override
    protected void updateViewControls ()
    {
        super.updateViewControls ();
        this.updateButtonLEDs ();
    }


    /** {@inheritDoc} */
    @Override
    protected void handleCC (final int data1, final int data2)
    {
        if (data1 != 1)
            super.handleCC (data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    protected void handlePitchbend (final int data1, final int data2)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    protected void handleNoteOff (final int data1, final int data2)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    protected void handleNoteOn (final int data1, final int data2)
    {
        // Intentionally empty
    }
}