// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.controller;

import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.OutputID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.graphics.canvas.component.LabelComponent;
import de.mossgrabers.framework.graphics.canvas.component.LabelComponent.LabelLayout;


/**
 * The Akai Fire control surface.
 *
 * @author Jürgen Moßgraber
 */
public class FireControlSurface extends AbstractControlSurface<FireConfiguration>
{
    /** The volume knob. */
    public static final int CC_VOLUME       = 0x10;
    /** The panorama knob. */
    public static final int CC_PAN          = 0x11;
    /** The filter knob touch. */
    public static final int CC_FILTER       = 0x12;
    /** The resonance knob. */
    public static final int CC_RESONANCE    = 0x13;
    /** The select knob. */
    public static final int CC_SELECT       = 0x76;

    /** The volume knob touch. */
    public static final int TOUCH_VOLUME    = 0x10;
    /** The panorama knob touch. */
    public static final int TOUCH_PAN       = 0x11;
    /** The filter knob touch. */
    public static final int TOUCH_FILTER    = 0x12;
    /** The resonance knob touch. */
    public static final int TOUCH_RESONANCE = 0x13;

    /** The select knob press. */
    public static final int SELECT          = 0x19;
    /** The bank button. */
    public static final int FIRE_BANK       = 0x1A;
    /** The pattern up button. */
    public static final int FIRE_PAT_UP     = 0x1F;
    /** The pattern down button. */
    public static final int FIRE_PAT_DOWN   = 0x20;
    /** The browser button. */
    public static final int FIRE_BROWSER    = 0x21;
    /** The grid left button. */
    public static final int FIRE_GRID_LEFT  = 0x22;
    /** The grid right button. */
    public static final int FIRE_GRID_RIGHT = 0x23;
    /** The solo 1 button. */
    public static final int FIRE_SOLO_1     = 0x24;
    /** The solo 2 button. */
    public static final int FIRE_SOLO_2     = 0x25;
    /** The solo 3 button. */
    public static final int FIRE_SOLO_3     = 0x26;
    /** The solo 4 button. */
    public static final int FIRE_SOLO_4     = 0x27;
    /** The step button. */
    public static final int FIRE_STEP       = 0x2C;
    /** The note button. */
    public static final int FIRE_NOTE       = 0x2D;
    /** The drum button. */
    public static final int FIRE_DRUM       = 0x2E;
    /** The perform button. */
    public static final int FIRE_PERFORM    = 0x2F;
    /** The shift button. */
    public static final int FIRE_SHIFT      = 0x30;
    /** The alt button. */
    public static final int FIRE_ALT        = 0x31;
    /** The pattern button. */
    public static final int FIRE_PATTERN    = 0x32;
    /** The play button. */
    public static final int FIRE_PLAY       = 0x33;
    /** The stop button. */
    public static final int FIRE_STOP       = 0x34;
    /** The record button. */
    public static final int FIRE_REC        = 0x35;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The MIDI output
     * @param input The MIDI input
     */
    public FireControlSurface (final IHost host, final ColorManager colorManager, final FireConfiguration configuration, final IMidiOutput output, final IMidiInput input)
    {
        super (host, configuration, colorManager, output, input, new FirePadGrid (colorManager, output), 306, 154);
    }


    /** {@inheritDoc} */
    @Override
    protected void flushHardware ()
    {
        super.flushHardware ();

        ((FirePadGrid) this.padGrid).flush ();
    }


    /** {@inheritDoc} */
    @Override
    protected void internalShutdown ()
    {
        final IGraphicDisplay display = this.getGraphicsDisplay ();
        display.addElement (new LabelComponent ("Goodbye", null, ColorEx.BLACK, false, false, LabelLayout.PLAIN));
        display.send ();

        for (int i = 4; i < 8; i++)
            this.getLight (OutputID.get (OutputID.LED1, i)).turnOff ();

        // Switch off knob mode LEDs
        this.setTrigger (0, 0x1B, 16);

        super.internalShutdown ();
    }


    /** {@inheritDoc} */
    @Override
    public FirePadGrid getPadGrid ()
    {
        return (FirePadGrid) this.padGrid;
    }


    /**
     * Update the LEDs brightness and saturation settings on the device.
     */
    public void configureLEDs ()
    {
        // Scale to [0..1]. Brightness is in the range of [0.1..1]
        final double padBrightness = Math.max (0.1, Math.min (1, 0.1 + 0.9 * this.configuration.getPadBrightness () / 100.0));
        final double padSaturation = this.configuration.getPadSaturation () / 100.0;
        final FirePadGrid padGrid = this.getPadGrid ();
        padGrid.configureLEDs (padBrightness, padSaturation);

        for (int i = 0; i < this.padGrid.getRows () * this.padGrid.getCols (); i++)
            this.getButton (ButtonID.get (ButtonID.PAD1, i)).getLight ().forceFlush ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isDeletePressed ()
    {
        // Use mode button as delete button for resetting knobs
        final boolean pressed = this.isPressed (ButtonID.BANK_RIGHT);
        if (pressed)
            this.setTriggerConsumed (ButtonID.BANK_RIGHT);
        return pressed;
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final BindType bindType, final int channel, final int cc, final int value)
    {
        this.output.sendCCEx (channel, cc, value);
    }
}