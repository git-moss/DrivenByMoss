// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.acvs.controller;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.display.AbstractTextDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.EnumMap;
import java.util.Map;


/**
 * The ACVS display, which is not really a display but a bunch of strings placed in the display.
 *
 * @author Jürgen Moßgraber
 */
public class ACVSDisplay extends AbstractTextDisplay
{
    /** Track headers (8) - text + color. */
    public static final int                ITEM_ID_TRACK_HEADER_FIRST           = 0x00;
    /** Clips (64) - text + color. */
    public static final int                ITEM_ID_CLIPS_FIRST                  = 0x10;
    /** Scenes (8) - text + color. */
    public static final int                ITEM_ID_SCENES_FIRST                 = 0x50;
    /** Track fader level (8). */
    public static final int                ITEM_ID_TRACK_FADER_LEVEL_FIRST      = 0x100;
    /** Track peak level (8). */
    public static final int                ITEM_ID_TRACK_PEAK_LEVEL_FIRST       = 0x108;
    /** Track panorama (8). */
    public static final int                ITEM_ID_TRACK_PAN_FIRST              = 0x110;
    /** Track send 1 (8). */
    public static final int                ITEM_ID_TRACK_SEND1_FIRST            = 0x118;
    /** Track send 2 (8). */
    public static final int                ITEM_ID_TRACK_SEND2_FIRST            = 0x120;
    /** Track send 3 (8). */
    public static final int                ITEM_ID_TRACK_SEND3_FIRST            = 0x128;
    /** Track send 4 (8). */
    public static final int                ITEM_ID_TRACK_SEND4_FIRST            = 0x130;
    /** Device bank name. */
    public static final int                ITEM_ID_DEVICE_BANK_NAME             = 0x200;
    /** Device name. */
    public static final int                ITEM_ID_DEVICE_NAME                  = 0x201;
    /** Device parameter names (8). */
    public static final int                ITEM_ID_DEVICE_PARAMETER_NAME_FIRST  = 0x210;
    /** Device parameter values (8). */
    public static final int                ITEM_ID_DEVICE_PARAMETER_VALUE_FIRST = 0x220;
    /** Tempo. */
    public static final int                ITEM_ID_TEMPO                        = 0x300;
    /** Arrangement position. */
    public static final int                ITEM_ID_ARRANGEMENT_POSITION         = 0x310;
    /** Loop start. */
    public static final int                ITEM_ID_LOOP_START                   = 0x311;
    /** Loop length. */
    public static final int                ITEM_ID_LOOP_LENGTH                  = 0x312;

    /** Device parameter name for the OLED display. */
    public static final int                ITEM_ID_DEVICE_PARAM_NAME1           = 0x1210;
    /** Device parameter value for the display. */
    public static final int                ITEM_ID_DEVICE_PARAM_VALUE1          = 0x122F;

    private static final int               ITEM_ID_MAX                          = ITEM_ID_DEVICE_PARAM_VALUE1 + 16 + 1;

    private final ColorEx []               currentColor;
    private final ColorEx []               color;
    private final Map<ScreenItem, Integer> screenItemCache                      = new EnumMap<> (ScreenItem.class);


    /**
     * Constructor.
     *
     * @param host The host
     * @param output The MIDI output
     */
    public ACVSDisplay (final IHost host, final ACVSMidiOutput output)
    {
        super (host, output, ITEM_ID_MAX, 1, 16);

        for (final ScreenItem item: ScreenItem.values ())
            this.screenItemCache.put (item, Integer.valueOf (-1));

        this.currentColor = new ColorEx [this.noOfLines];
        this.color = new ColorEx [this.noOfLines];
    }


    /** {@inheritDoc} */
    @Override
    public void writeLine (final int row, final String text)
    {
        if (text != null)
            ((ACVSMidiOutput) this.output).sendText (row, StringUtils.fixASCII (text));
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        // Not used
    }


    /**
     * Set a color for the given row (item ID).
     *
     * @param row The row
     * @param color The color
     */
    public void setColor (final int row, final ColorEx color)
    {
        this.color[row] = color;
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        super.flush ();

        for (int row = 0; row < this.noOfLines; row++)
        {
            // Has anything changed?
            if (this.currentColor[row] != null && this.currentColor[row].equals (this.color[row]))
                continue;
            this.currentColor[row] = this.color[row];
            if (this.currentColor[row] != null)
                this.updateColor (row, this.currentColor[row]);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void forceFlush ()
    {
        for (int row = 0; row < this.noOfLines; row++)
            this.currentMessage[row] = null;
        for (int row = 0; row < this.noOfLines; row++)
            this.currentColor[row] = null;
        for (final ScreenItem item: ScreenItem.values ())
            this.screenItemCache.put (item, Integer.valueOf (-1));
    }


    private void updateColor (final int row, final ColorEx color)
    {
        if (color != null)
            ((ACVSMidiOutput) this.output).sendColor (row, color);
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay done (final int row)
    {
        return this.fullRows[row] == null ? this : super.done (row);
    }


    /** {@inheritDoc} */
    @Override
    protected void notifyOnDisplay (final String message)
    {
        // Can't show notifications
    }


    /**
     * Set an item value on the screen.
     *
     * @param screenItem The ID of the item
     * @param value The value to set [0..127]
     */
    public void setScreenItem (final ScreenItem screenItem, final int value)
    {
        final Integer currentValue = this.screenItemCache.get (screenItem);
        if (currentValue.intValue () == value)
            return;

        this.screenItemCache.put (screenItem, Integer.valueOf (value));

        if (screenItem.isNote ())
            this.output.sendNoteEx (screenItem.getChannel (), screenItem.getNoteCC (), value);
        else
            this.output.sendCCEx (screenItem.getChannel (), screenItem.getNoteCC (), value);
    }
}
