// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.controller.display.grid;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.daw.resource.ResourceHandler;
import de.mossgrabers.framework.graphics.Align;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.graphics.IImage;

import java.util.EnumMap;


/**
 * An element in the grid which contains a menu and a channels' icon, name and color.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SelectionGridElement extends AbstractGridElement
{
    private static final EnumMap<ChannelType, String> ICONS            = new EnumMap<> (ChannelType.class);

    protected static final int                        TRACK_ROW_HEIGHT = (int) (1.6 * UNIT);

    static
    {
        ICONS.put (ChannelType.AUDIO, "track/audio_track.svg");
        ICONS.put (ChannelType.INSTRUMENT, "track/instrument_track.svg");
        ICONS.put (ChannelType.GROUP, "track/group_track.svg");
        ICONS.put (ChannelType.EFFECT, "track/return_track.svg");
        ICONS.put (ChannelType.HYBRID, "track/hybrid_track.svg");
        ICONS.put (ChannelType.MASTER, "track/master_track.svg");
        ICONS.put (ChannelType.LAYER, "track/multi_layer.svg");
    }

    private final ChannelType type;


    /**
     * Constructor.
     *
     * @param menuName The text for the menu
     * @param isMenuSelected True if the menu is selected
     * @param name The of the grid element (track name, parameter name, etc.)
     * @param color The color to use for the header, may be null
     * @param isSelected True if the grid element is selected
     * @param type The type of the track
     */
    public SelectionGridElement (final String menuName, final boolean isMenuSelected, final String name, final ColorEx color, final boolean isSelected, final ChannelType type)
    {
        super (menuName, isMenuSelected, null, name, color, isSelected);
        this.type = type;
    }


    /**
     * Get the type of the channel.
     *
     * @return The type
     */
    public ChannelType getType ()
    {
        return this.type;
    }


    /** {@inheritDoc} */
    @Override
    public String getIcon ()
    {
        return this.type == null ? null : ICONS.get (this.type);
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsContext gc, final double left, final double width, final double height, final PushConfiguration configuration)
    {
        this.drawMenu (gc, left, width, configuration);

        final String name = this.getName ();
        // Element is off if the name is empty
        if (name == null || name.length () == 0)
            return;

        final double trackRowTop = height - TRACK_ROW_HEIGHT - UNIT - SEPARATOR_SIZE;
        this.drawTrackInfo (gc, left, width, height, trackRowTop, name, configuration);
    }


    /**
     * Draws the tracks info, like icon, color and name.
     *
     * @param gc The graphics context
     * @param left The left bound of the drawing area
     * @param width The width of the drawing area
     * @param height The height of the drawing area
     * @param trackRowTop The top of the drawing area
     * @param name The name of the track
     * @param configuration The layout settings
     */
    protected void drawTrackInfo (final IGraphicsContext gc, final double left, final double width, final double height, final double trackRowTop, final String name, final PushConfiguration configuration)
    {
        // Draw the background
        final ColorEx backgroundColor = configuration.getColorBackground ();
        gc.fillRectangle (left, trackRowTop + 1, width, height - UNIT - 1, this.isSelected () ? configuration.getColorBackgroundLighter () : backgroundColor);

        // The tracks icon and name
        final String iconName = this.getIcon ();
        if (iconName != null)
        {
            final IImage icon = ResourceHandler.getSVGImage (iconName);
            final ColorEx maskColor = this.getMaskColor (configuration);
            if (maskColor == null)
                gc.drawImage (icon, left + (DOUBLE_UNIT - icon.getWidth ()) / 2, height - TRACK_ROW_HEIGHT - UNIT + (TRACK_ROW_HEIGHT - icon.getHeight ()) / 2.0);
            else
                gc.maskImage (icon, left + (DOUBLE_UNIT - icon.getWidth ()) / 2, height - TRACK_ROW_HEIGHT - UNIT + (TRACK_ROW_HEIGHT - icon.getHeight ()) / 2.0, maskColor);
        }

        gc.drawTextInBounds (name, left + DOUBLE_UNIT, height - TRACK_ROW_HEIGHT - UNIT, width - DOUBLE_UNIT, TRACK_ROW_HEIGHT, Align.LEFT, configuration.getColorText (), 1.2 * UNIT);

        // The track color section
        gc.fillRectangle (left, height - UNIT, width, UNIT, this.getColor ());
    }


    protected ColorEx getMaskColor (final PushConfiguration configuration)
    {
        return configuration.getColorText ();
    }
}
