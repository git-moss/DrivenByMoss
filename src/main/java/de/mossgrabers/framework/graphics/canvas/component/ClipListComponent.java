// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.canvas.component;

import java.util.ArrayList;
import java.util.List;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.graphics.Align;
import de.mossgrabers.framework.graphics.IBounds;
import de.mossgrabers.framework.graphics.IGraphicsConfiguration;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.graphics.IGraphicsDimensions;
import de.mossgrabers.framework.graphics.IGraphicsInfo;
import de.mossgrabers.framework.utils.Pair;


/**
 * A component which contains several text items, which represent a clip. Each item can be selected.
 *
 * @author Jürgen Moßgraber
 */
public class ClipListComponent extends ChannelSelectComponent
{
    private final List<Pair<ITrack, ISlot>> items;


    /**
     * Constructor.
     *
     * @param slots The list items
     * @param type The type of the track
     * @param name The of the grid element (track name, parameter name, etc.)
     * @param color The color to use for the header, may be null
     * @param isSelected True if the grid element is selected
     * @param isActive True if channel is activated
     * @param isPinned True if the channel is pinned
     */
    public ClipListComponent (final List<Pair<ITrack, ISlot>> slots, final ChannelType type, final String name, final ColorEx color, final boolean isSelected, final boolean isActive, final boolean isPinned)
    {
        super (type, null, false, name, color, isSelected, isActive, isPinned);

        this.items = new ArrayList<> (slots);
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsInfo info)
    {
        super.draw (info);

        final IGraphicsContext gc = info.getContext ();
        final IGraphicsDimensions dimensions = info.getDimensions ();
        final IGraphicsConfiguration configuration = info.getConfiguration ();
        final IBounds bounds = info.getBounds ();
        final double left = bounds.left ();
        final double width = bounds.width ();
        final double height = bounds.height ();

        final double separatorSize = dimensions.getSeparatorSize () / 2;
        final double inset = dimensions.getInset ();

        final int size = this.items.size ();
        final double itemLeft = left;
        final double itemWidth = width;
        final double itemHeight = height / (size + 1);
        final double fontHeight = itemHeight > 30 ? itemHeight / 2 : itemHeight * 2 / 3;
        final double boxLeft = itemLeft + inset;
        final double boxWidth = fontHeight - 2 * separatorSize;
        final double radius = boxWidth / 2;

        final ColorEx textColor = configuration.getColorText ();

        for (int i = 0; i < size; i++)
        {
            final Pair<ITrack, ISlot> pair = this.items.get (i);
            final ITrack track = pair.getKey ();
            if (!track.doesExist ())
                continue;

            final ISlot slot = pair.getValue ();

            final double itemTop = i * itemHeight;

            String name = slot.getName ();

            final double boxTop = itemTop + (itemHeight - fontHeight) / 2;

            // Draw the background
            final ColorEx clipBackgroundColor = slot.getColor ();
            if (track.isGroup ())
            {
                if (name.isEmpty ())
                    name = "Scene " + (slot.getPosition () + 1);
                gc.fillRectangle (itemLeft, itemTop + separatorSize, itemWidth, itemHeight - 2 * separatorSize, ColorEx.darker (ColorEx.DARK_GRAY));
                gc.fillRectangle (itemLeft + itemWidth - 2 * inset, itemTop + separatorSize, 2 * inset, itemHeight - 2 * separatorSize, clipBackgroundColor);
            }
            else
                gc.fillRectangle (itemLeft, itemTop + separatorSize, itemWidth, itemHeight - 2 * separatorSize, clipBackgroundColor);

            if (slot.doesExist ())
            {
                // Draw the play/record state indicator box
                final boolean isPlaying = slot.isPlaying ();
                final boolean isPlayingQueued = slot.isPlayingQueued ();
                final boolean isRecording = slot.isRecording ();
                final boolean isRecordingQueued = slot.isRecordingQueued ();
                final boolean isStopQueued = slot.isStopQueued ();
                if (isPlaying || isPlayingQueued || isRecording || isRecordingQueued || isStopQueued)
                    gc.fillRectangle (boxLeft, boxTop, fontHeight, fontHeight, ColorEx.BLACK);

                // Draw the play, record or stop symbol depending on the slots state
                ColorEx fillColor = ColorEx.darker (clipBackgroundColor);
                if (isRecording || isRecordingQueued)
                {
                    fillColor = isStopQueued ? ColorEx.WHITE : ColorEx.RED;
                    gc.fillCircle (boxLeft + separatorSize + radius, boxTop + separatorSize + radius, radius, fillColor);
                }
                else if (slot.hasContent ())
                {
                    if (isStopQueued)
                        fillColor = ColorEx.WHITE;
                    else if (isPlaying || isPlayingQueued)
                        fillColor = isPlayingQueued ? ColorEx.DARKER_GREEN : ColorEx.GREEN;
                    gc.fillTriangle (boxLeft + separatorSize, boxTop + separatorSize, boxLeft + separatorSize, boxTop + fontHeight - separatorSize, boxLeft + fontHeight - separatorSize, boxTop + fontHeight / 2, fillColor);
                }
                else
                {
                    if (track.isRecArm ())
                        gc.fillCircle (boxLeft + separatorSize + radius, boxTop + separatorSize + radius, radius, ColorEx.DARK_GRAY);
                    else
                        gc.fillRectangle (boxLeft + separatorSize, boxTop + separatorSize, boxWidth, boxWidth, ColorEx.DARK_GRAY);
                }

                // Draw the text
                final double padLeft = 2 * inset + fontHeight;
                gc.drawTextInBounds (name, itemLeft + padLeft, itemTop - 1, itemWidth - padLeft - separatorSize - inset, itemHeight, Align.LEFT, slot.isSelected () ? ColorEx.calcContrastColor (textColor) : textColor, fontHeight);
            }

            // Draw the border if selected
            if (slot.isSelected ())
                gc.strokeRectangle (itemLeft, itemTop + separatorSize, itemWidth, itemHeight - 2 * separatorSize, ColorEx.calcContrastColor (textColor), slot.isSelected () ? 2 : 1);
        }
    }


    /** {@inheritDoc} */
    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.items == null ? 0 : this.items.hashCode ());
        return result;
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals (final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null || this.getClass () != obj.getClass ())
            return false;
        final ClipListComponent other = (ClipListComponent) obj;
        if (this.items == null)
        {
            if (other.items != null)
                return false;
        }
        else if (!this.items.equals (other.items))
            return false;
        return true;
    }
}
