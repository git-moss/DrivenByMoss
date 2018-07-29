// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.controller.display.grid;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.graphics.Align;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.utils.Pair;

import java.util.ArrayList;
import java.util.List;


/**
 * An element in the grid which contains several text items, which represent a clip. Each item can
 * be selected.
 *
 * Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ClipListGridElement extends AbstractGridElement
{
    private final List<Pair<ITrack, ISlot>> items;


    /**
     * Constructor.
     *
     * @param slots The list items
     */
    public ClipListGridElement (final List<Pair<ITrack, ISlot>> slots)
    {
        super (null, false, null, null, null, false);
        this.items = new ArrayList<> (slots);
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsContext gc, final double left, final double width, final double height, final PushConfiguration configuration)
    {
        final int size = this.items.size ();
        final double itemLeft = left + SEPARATOR_SIZE;
        final double itemWidth = width - SEPARATOR_SIZE;
        final double itemHeight = DISPLAY_HEIGHT / (double) size;
        final double fontHeight = itemHeight > 30 ? itemHeight / 2 : itemHeight * 2 / 3;
        final double boxLeft = itemLeft + INSET;
        final double boxWidth = fontHeight - 2 * SEPARATOR_SIZE;
        final double radius = boxWidth / 2;

        final ColorEx textColor = configuration.getColorText ();
        final ColorEx borderColor = configuration.getColorBackgroundLighter ();

        for (int i = 0; i < size; i++)
        {
            final Pair<ITrack, ISlot> pair = this.items.get (i);
            final ISlot slot = pair.getValue ();
            final ITrack track = pair.getKey ();

            final double itemTop = i * itemHeight;

            if (!slot.doesExist ())
                continue;

            String name = slot.getName ();

            final double boxTop = itemTop + (itemHeight - fontHeight) / 2;

            // Draw the background
            final ColorEx clipBackgroundColor = new ColorEx (slot.getColor ());
            if (track.isGroup ())
            {
                if (name.isEmpty ())
                    name = "Scene " + (slot.getPosition () + 1);
                gc.fillRectangle (itemLeft, itemTop + SEPARATOR_SIZE, itemWidth, itemHeight - 2 * SEPARATOR_SIZE, ColorEx.darker (ColorEx.DARK_GRAY));
                gc.fillRectangle (itemLeft + itemWidth - 2 * INSET, itemTop + SEPARATOR_SIZE, 2 * INSET, itemHeight - 2 * SEPARATOR_SIZE, clipBackgroundColor);
            }
            else
                gc.fillRectangle (itemLeft, itemTop + SEPARATOR_SIZE, itemWidth, itemHeight - 2 * SEPARATOR_SIZE, clipBackgroundColor);

            // Draw the play/record state indicator box
            final boolean isPlaying = slot.isPlaying ();
            if (isPlaying || slot.isRecording () || slot.isPlayingQueued () || slot.isRecordingQueued ())
                gc.fillRectangle (boxLeft, boxTop, fontHeight, fontHeight, ColorEx.BLACK);

            // Draw the play, record or stop symbol depending on the slots state
            if (slot.hasContent ())
            {
                if (slot.isRecording ())
                    gc.fillCircle (boxLeft + SEPARATOR_SIZE + radius, boxTop + SEPARATOR_SIZE + radius, radius, ColorEx.RED);
                else
                {
                    ColorEx fillColor = ColorEx.darker (clipBackgroundColor);
                    if (isPlaying)
                        fillColor = ColorEx.GREEN;
                    else if (slot.isPlayingQueued () || slot.isRecordingQueued ())
                        fillColor = ColorEx.WHITE;
                    gc.fillTriangle (boxLeft + SEPARATOR_SIZE, boxTop + SEPARATOR_SIZE, boxLeft + SEPARATOR_SIZE, boxTop + fontHeight - SEPARATOR_SIZE, boxLeft + fontHeight - SEPARATOR_SIZE, boxTop + fontHeight / 2, fillColor);
                }
            }
            else
            {
                if (track.isRecArm ())
                    gc.fillCircle (boxLeft + SEPARATOR_SIZE + radius, boxTop + SEPARATOR_SIZE + radius, radius, ColorEx.DARK_GRAY);
                else
                    gc.fillRectangle (boxLeft + SEPARATOR_SIZE, boxTop + SEPARATOR_SIZE, boxWidth, boxWidth, ColorEx.DARK_GRAY);
            }

            // Draw the text
            gc.drawTextInBounds (name, itemLeft + 2 * INSET + fontHeight, itemTop - 1, itemWidth - 2 * INSET, itemHeight, Align.LEFT, ColorEx.BLACK, fontHeight);

            // Draw the border
            ColorEx color = borderColor;
            if (slot.isSelected ())
                color = textColor;
            else if (track.isSelected ())
                color = ColorEx.darker (ColorEx.YELLOW);
            gc.strokeRectangle (itemLeft, itemTop + SEPARATOR_SIZE, itemWidth, itemHeight - 2 * SEPARATOR_SIZE, color, slot.isSelected () ? 2 : 1);
        }
    }
}
