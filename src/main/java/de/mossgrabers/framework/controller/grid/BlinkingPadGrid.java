// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.grid;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.midi.IMidiOutput;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


/**
 * Implementation of a grid of pads with software simulated blinking pads.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class BlinkingPadGrid extends PadGridImpl
{
    protected static final int              BLINK_SPEED    = 600;

    protected final Map<Integer, LightInfo> blinkingLights = new HashMap<> ();
    protected final Map<Integer, LightInfo> padInfos       = new TreeMap<> ();
    protected boolean                       isBlink;
    protected long                          updateTime     = System.currentTimeMillis ();


    /**
     * Constructor.
     *
     * @param colorManager The color manager for accessing specific colors to use
     * @param output The MIDI output which can address the pad states
     */
    protected BlinkingPadGrid (final ColorManager colorManager, final IMidiOutput output)
    {
        super (colorManager, output);
    }


    /**
     * Constructor.
     *
     * @param colorManager The color manager for accessing specific colors to use
     * @param output The MIDI output which can address the pad states
     * @param rows The number of rows of the grid
     * @param cols The number of columns of the grid
     * @param startNote The start note of the grid
     */
    protected BlinkingPadGrid (final ColorManager colorManager, final IMidiOutput output, final int rows, final int cols, final int startNote)
    {
        super (colorManager, output, rows, cols, startNote);
    }


    /**
     * Flush the changed pad LEDs using system exclusive.
     */
    public void flush ()
    {
        synchronized (this.padInfos)
        {
            this.updateController ();
            this.padInfos.clear ();
        }
    }


    /**
     * Send the changes including blinking pad changes to the controller.
     */
    protected void updateController ()
    {
        for (final Entry<Integer, LightInfo> e: this.padInfos.entrySet ())
        {
            final Integer note = e.getKey ();
            final LightInfo info = e.getValue ();
            this.sendPadUpdate (note.intValue (), info.getColor ());

            if (info.getBlinkColor () > 0)
                this.blinkingLights.put (note, info);
            else
                this.blinkingLights.remove (note);
        }

        // Toggle blink colors every 600ms
        if (!this.checkBlinking ())
            return;
        for (final Entry<Integer, LightInfo> value: this.blinkingLights.entrySet ())
        {
            final LightInfo info = value.getValue ();
            final int colorIndex = this.isBlink ? info.getBlinkColor () : info.getColor ();
            final int note = value.getKey ().intValue ();
            this.sendPadUpdate (note, colorIndex);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void sendNoteState (final int channel, final int note, final int color)
    {
        synchronized (this.padInfos)
        {
            this.padInfos.computeIfAbsent (Integer.valueOf (note), key -> new LightInfo ()).setColor (color);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void sendBlinkState (final int channel, final int note, final int blinkColor, final boolean fast)
    {
        synchronized (this.padInfos)
        {
            final LightInfo info = this.padInfos.computeIfAbsent (Integer.valueOf (note), key -> new LightInfo ());
            info.setBlinkColor (blinkColor);
            info.setFast (fast);
        }
    }


    protected boolean checkBlinking ()
    {
        final long now = System.currentTimeMillis ();
        if (now - this.updateTime > BLINK_SPEED)
        {
            this.updateTime = now;
            this.isBlink = !this.isBlink;
            return true;
        }
        return false;
    }


    /**
     * Send the update to the controller.
     *
     * @param note The MIDI note to send
     * @param colorIndex The index of the color to send
     */
    protected void sendPadUpdate (final int note, final int colorIndex)
    {
        this.output.sendNoteEx (0, note, colorIndex);
    }
}
