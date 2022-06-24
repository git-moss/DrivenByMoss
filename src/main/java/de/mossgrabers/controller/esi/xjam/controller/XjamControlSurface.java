// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.esi.xjam.controller;

import de.mossgrabers.controller.esi.xjam.XjamConfiguration;
import de.mossgrabers.controller.ni.kontrol.mki.controller.UIChangeCallback;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.hardware.IHwContinuousControl;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.HashMap;
import java.util.Map;


/**
 * The Kontrol 1 surface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class XjamControlSurface extends AbstractControlSurface<XjamConfiguration> implements UIChangeCallback
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
        BUTTON_MAP.put (Integer.valueOf (XjamControlSurface.BUTTON_SHIFT), ButtonID.SHIFT);
        BUTTON_MAP.put (Integer.valueOf (XjamControlSurface.BUTTON_SCALE), ButtonID.SCALES);
        BUTTON_MAP.put (Integer.valueOf (XjamControlSurface.BUTTON_ARP), ButtonID.METRONOME);
        BUTTON_MAP.put (Integer.valueOf (XjamControlSurface.BUTTON_PLAY), ButtonID.PLAY);
        BUTTON_MAP.put (Integer.valueOf (XjamControlSurface.BUTTON_REC), ButtonID.RECORD);
        BUTTON_MAP.put (Integer.valueOf (XjamControlSurface.BUTTON_STOP), ButtonID.STOP);
        BUTTON_MAP.put (Integer.valueOf (XjamControlSurface.BUTTON_RWD), ButtonID.REWIND);
        BUTTON_MAP.put (Integer.valueOf (XjamControlSurface.BUTTON_FWD), ButtonID.FORWARD);
        BUTTON_MAP.put (Integer.valueOf (XjamControlSurface.BUTTON_LOOP), ButtonID.LOOP);
        BUTTON_MAP.put (Integer.valueOf (XjamControlSurface.BUTTON_PAGE_LEFT), ButtonID.PAGE_LEFT);
        BUTTON_MAP.put (Integer.valueOf (XjamControlSurface.BUTTON_PAGE_RIGHT), ButtonID.PAGE_RIGHT);
        BUTTON_MAP.put (Integer.valueOf (XjamControlSurface.BUTTON_MAIN_ENCODER), ButtonID.MASTERTRACK);
        BUTTON_MAP.put (Integer.valueOf (XjamControlSurface.BUTTON_NAVIGATE_DOWN), ButtonID.ARROW_DOWN);
        BUTTON_MAP.put (Integer.valueOf (XjamControlSurface.BUTTON_NAVIGATE_UP), ButtonID.ARROW_UP);
        BUTTON_MAP.put (Integer.valueOf (XjamControlSurface.BUTTON_NAVIGATE_LEFT), ButtonID.ARROW_LEFT);
        BUTTON_MAP.put (Integer.valueOf (XjamControlSurface.BUTTON_NAVIGATE_RIGHT), ButtonID.ARROW_RIGHT);
        BUTTON_MAP.put (Integer.valueOf (XjamControlSurface.BUTTON_BACK), ButtonID.MUTE);
        BUTTON_MAP.put (Integer.valueOf (XjamControlSurface.BUTTON_ENTER), ButtonID.SOLO);
        BUTTON_MAP.put (Integer.valueOf (XjamControlSurface.BUTTON_BROWSE), ButtonID.BROWSE);
    }

    private static final Map<Integer, ContinuousID> CONTINUOUS_MAP = new HashMap<> ();
    static
    {
        for (int i = 0; i < 8; i++)
            CONTINUOUS_MAP.put (Integer.valueOf (XjamControlSurface.TOUCH_ENCODER_1 + i), ContinuousID.get (ContinuousID.KNOB1, i));
        CONTINUOUS_MAP.put (Integer.valueOf (XjamControlSurface.TOUCH_ENCODER_MAIN), ContinuousID.MASTER_KNOB);
    }


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param input The MIDI input
     */
    public XjamControlSurface (final IHost host, final ColorManager colorManager, final XjamConfiguration configuration, final IMidiInput input)
    {
        super (0, host, configuration, colorManager, null, input, null, null, 800, 300);
    }


    /** {@inheritDoc} */
    @Override
    protected void internalShutdown ()
    {
        // this.usbDevice.turnOffButtonLEDs ();

        super.internalShutdown ();
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final int channel, final int cc, final int value)
    {
        // TODO
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


    /**
     * Send the LED status to the device.
     */
    public void updateButtonLEDs ()
    {
        // TODO
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


    @Override
    public void octaveChanged (int firstNote)
    {
        // TODO Auto-generated method stub

    }
}