// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.controller.display.model;

import de.mossgrabers.framework.Pair;
import de.mossgrabers.push.controller.display.model.grid.ChannelGridElement;
import de.mossgrabers.push.controller.display.model.grid.ChannelSelectionGridElement;
import de.mossgrabers.push.controller.display.model.grid.ColorEx;
import de.mossgrabers.push.controller.display.model.grid.GridElement;
import de.mossgrabers.push.controller.display.model.grid.ListGridElement;
import de.mossgrabers.push.controller.display.model.grid.OptionsGridElement;
import de.mossgrabers.push.controller.display.model.grid.ParamGridElement;
import de.mossgrabers.push.controller.display.model.grid.SendsGridElement;

import com.bitwig.extension.api.Color;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Parses the communication protocol coming from the Push4Bitwig script.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ProtocolParser
{
    private static final byte GRID_ELEMENT_CHANNEL_SELECTION  = 0;
    private static final byte GRID_ELEMENT_CHANNEL_VOLUME     = 1;
    private static final byte GRID_ELEMENT_CHANNEL_PAN        = 2;
    private static final byte GRID_ELEMENT_CHANNEL_CROSSFADER = 3;
    private static final byte GRID_ELEMENT_CHANNEL_SENDS      = 4;
    private static final byte GRID_ELEMENT_CHANNEL_ALL        = 5;
    private static final byte GRID_ELEMENT_PARAMETER          = 6;
    private static final byte GRID_ELEMENT_OPTIONS            = 7;
    private static final byte GRID_ELEMENT_LIST               = 8;


    /**
     * Parses the given data.
     *
     * @param in The byte array to parse from
     * @return The parsed grid elements
     */
    public List<GridElement> parse (final ByteArrayInputStream in)
    {
        final List<GridElement> elements = new ArrayList<> (8);
        for (int i = 0; i < 8; i++)
        {
            final GridElement el;
            final int gridType = in.read ();
            switch ((byte) gridType)
            {
                case GRID_ELEMENT_CHANNEL_SELECTION:
                    el = this.parseChannelSelection (in);
                    break;
                case GRID_ELEMENT_CHANNEL_VOLUME:
                    el = this.parseChannel (ChannelGridElement.EDIT_TYPE_VOLUME, in);
                    break;
                case GRID_ELEMENT_CHANNEL_PAN:
                    el = this.parseChannel (ChannelGridElement.EDIT_TYPE_PAN, in);
                    break;
                case GRID_ELEMENT_CHANNEL_CROSSFADER:
                    el = this.parseChannel (ChannelGridElement.EDIT_TYPE_CROSSFADER, in);
                    break;
                case GRID_ELEMENT_CHANNEL_SENDS:
                    el = this.parseSends (in);
                    break;
                case GRID_ELEMENT_CHANNEL_ALL:
                    el = this.parseChannel (ChannelGridElement.EDIT_TYPE_ALL, in);
                    break;
                case GRID_ELEMENT_PARAMETER:
                    el = this.parseParameter (in);
                    break;
                case GRID_ELEMENT_OPTIONS:
                    el = this.parseOptions (in);
                    break;
                case GRID_ELEMENT_LIST:
                    el = this.parseList (in);
                    break;
                default:
                    throw new RuntimeException ("Unsupported grid element type: " + gridType);
            }
            elements.add (el);
        }
        return elements;
    }


    /**
     * Parses an empty channel element.
     *
     * @param in The byte array to parse from
     * @return The parsed element
     */
    public ChannelSelectionGridElement parseChannelSelection (final ByteArrayInputStream in)
    {
        final String menuName = parseString (in);
        final boolean isMenuSelected = parseBoolean (in);
        final String name = parseString (in);
        final String typeText = parseString (in);
        final ChannelType type = typeText.length () == 0 ? null : ChannelType.valueOf (typeText.toUpperCase ());
        final Color color = parseColor (in);
        final boolean isSelected = parseBoolean (in);
        return new ChannelSelectionGridElement (menuName, isMenuSelected, name, color, isSelected, type);
    }


    /**
     * Parses a channel element.
     *
     * @param editType What is edited: Volume, Pan, Crossfader
     * @param in The byte array to parse from
     * @return The parsed element
     */
    public ChannelGridElement parseChannel (final int editType, final ByteArrayInputStream in)
    {
        final String menuName = parseString (in);
        final boolean isMenuSelected = parseBoolean (in);
        final String name = parseString (in);
        final String typeText = parseString (in);
        final ChannelType type = typeText.length () == 0 ? null : ChannelType.valueOf (typeText.toUpperCase ());
        final Color color = parseColor (in);
        final boolean isSelected = parseBoolean (in);

        final int volumeValue = parseInteger (in);
        final int modulatedVolumeValue = parseInteger (in);
        final String volumeText = parseString (in);
        final int panValue = parseInteger (in);
        final int modulatedPanValue = parseInteger (in);
        final String panText = parseString (in);
        final int vuValue = parseInteger (in);
        final boolean isMute = parseBoolean (in);
        final boolean isSolo = parseBoolean (in);
        final boolean isArm = parseBoolean (in);
        final int crossfadeMode = parseByte (in);
        return new ChannelGridElement (editType, menuName, isMenuSelected, name, color, isSelected, type, volumeValue, modulatedVolumeValue, volumeText, panValue, modulatedPanValue, panText, vuValue, isMute, isSolo, isArm, crossfadeMode);
    }


    /**
     * Parses a channel element.
     *
     * @param in The byte array to parse from
     * @return The parsed element
     */
    public SendsGridElement parseSends (final ByteArrayInputStream in)
    {
        final String menuName = parseString (in);
        final boolean isMenuSelected = parseBoolean (in);
        final String name = parseString (in);
        final String typeText = parseString (in);
        final ChannelType type = typeText.length () == 0 ? null : ChannelType.valueOf (typeText.toUpperCase ());
        final Color color = parseColor (in);
        final boolean isSelected = parseBoolean (in);

        final String [] sendNames = new String [4];
        final String [] sendTexts = new String [4];
        final int [] sendValues = new int [4];
        final int [] modulatedSendValues = new int [4];
        final boolean [] sendEdited = new boolean [4];
        for (int i = 0; i < 4; i++)
        {
            sendNames[i] = parseString (in);
            sendTexts[i] = parseString (in);
            sendValues[i] = parseInteger (in);
            modulatedSendValues[i] = parseInteger (in);
            sendEdited[i] = parseBoolean (in);
        }

        final boolean isExMode = parseBoolean (in);
        return new SendsGridElement (sendNames, sendTexts, sendValues, modulatedSendValues, sendEdited, menuName, isMenuSelected, name, color, isSelected, type, isExMode);
    }


    /**
     * Parses an options element.
     *
     * @param in The byte array to parse from
     * @return The parsed element
     */
    public OptionsGridElement parseOptions (final ByteArrayInputStream in)
    {
        final String headerTopName = parseString (in);
        final String menuTopName = parseString (in);
        final boolean isMenuTopSelected = parseBoolean (in);
        final String headerBottomName = parseString (in);
        final String menuBottomName = parseString (in);
        final boolean isMenuBottomSelected = parseBoolean (in);
        final boolean useSmallTopMenu = parseBoolean (in);
        return new OptionsGridElement (headerTopName, menuTopName, isMenuTopSelected, headerBottomName, menuBottomName, isMenuBottomSelected, useSmallTopMenu);
    }


    /**
     * Parses a fader element.
     *
     * @param in The byte array to parse from
     * @return The parsed element
     */
    public ParamGridElement parseParameter (final ByteArrayInputStream in)
    {
        final String menuName = parseString (in);
        final boolean isMenuSelected = parseBoolean (in);

        final String name = parseString (in);
        final String typeText = parseString (in);
        ChannelType type = typeText.length () == 0 ? null : ChannelType.valueOf (typeText.toUpperCase ());
        if (type == null)
            type = ChannelType.EFFECT;
        final Color color = parseColor (in);
        final boolean isSelected = parseBoolean (in);

        final String paramName = parseString (in);
        final int paramValue = parseInteger (in);
        final String paramValueText = parseString (in);
        final boolean isTouched = parseBoolean (in);
        final int modulatedParamValue = parseInteger (in);

        return new ParamGridElement (menuName, isMenuSelected, name, type, color, isSelected, paramName, paramValue, modulatedParamValue, paramValueText, isTouched);
    }


    /**
     * Parses a fader element.
     *
     * @param in The byte array to parse from
     * @return The parsed element
     */
    public ListGridElement parseList (final ByteArrayInputStream in)
    {
        final List<Pair<String, Boolean>> menu = new ArrayList<> ();
        for (int i = 0; i < 6; i++)
        {
            final String menuText = parseString (in);
            final Boolean isSelected = Boolean.valueOf (parseBoolean (in));
            menu.add (new Pair<> (menuText, isSelected));
        }
        return new ListGridElement (menu);
    }


    /**
     * Parses a 2 byte with 14 bit integer.
     *
     * @param in The byte array to parse from
     * @return The parsed integer
     */
    private static int parseInteger (final ByteArrayInputStream in)
    {
        return in.read () + (in.read () << 7);
    }


    /**
     * Parses 1 byte with 7 bit (0-127).
     *
     * @param in The byte array to parse from
     * @return The parsed integer
     */
    private static byte parseByte (final ByteArrayInputStream in)
    {
        return (byte) in.read ();
    }


    /**
     * Parses 1 byte which represents a boolean with value 0/1.
     *
     * @param in The byte array to parse from
     * @return The parsed boolean
     */
    private static boolean parseBoolean (final ByteArrayInputStream in)
    {
        return in.read () != 0;
    }


    /**
     * Parses a 0 terminated string.
     *
     * @param in The byte array to parse from
     * @return The parsed string
     */
    public static String parseString (final ByteArrayInputStream in)
    {
        final StringBuilder sb = new StringBuilder ();
        int c;
        while ((c = in.read ()) != 0)
        {
            if (c > 0 && c < 128)
                sb.append ((char) c);
            else
                sb.append ((char) parseInteger (in));
        }
        return sb.toString ();
    }


    /**
     * Parses a color.
     *
     * @param in The byte array to parse from
     * @return The parsed color
     */
    private static Color parseColor (final ByteArrayInputStream in)
    {
        final int r = parseInteger (in);
        final int g = parseInteger (in);
        final int b = parseInteger (in);
        return r == 0 && g == 0 && b == 0 ? ColorEx.GRAY : Color.fromRGB255 (r, g, b);
    }
}
