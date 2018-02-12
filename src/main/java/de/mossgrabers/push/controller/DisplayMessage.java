// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.controller;

import java.util.ArrayList;
import java.util.List;


/**
 * Communication message to talk to the display process.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DisplayMessage
{
    /** Display only a channel name for selection. */
    public static final int GRID_ELEMENT_CHANNEL_SELECTION  = 0;
    /** Display a channel, edit volume. */
    public static final int GRID_ELEMENT_CHANNEL_VOLUME     = 1;
    /** Display a channel, edit panorama. */
    public static final int GRID_ELEMENT_CHANNEL_PAN        = 2;
    /** Display a channel, edit crossfader. */
    public static final int GRID_ELEMENT_CHANNEL_CROSSFADER = 3;
    /** Display a channel sends. */
    public static final int GRID_ELEMENT_CHANNEL_SENDS      = 4;
    /** Display a channel, edit all parameters. */
    public static final int GRID_ELEMENT_CHANNEL_ALL        = 5;
    /** Display a parameter with name and value. */
    public static final int GRID_ELEMENT_PARAMETERS         = 6;
    /** Display options on top and bottom. */
    public static final int GRID_ELEMENT_OPTIONS            = 7;
    /** Display a list. */
    public static final int GRID_ELEMENT_LIST               = 8;

    /** The grid command. */
    public static final int DISPLAY_COMMAND_GRID            = 10;

    private int             command;
    private List<Integer>   array;


    /**
     * Constructor. Uses the grid command.
     */
    public DisplayMessage ()
    {
        this (DISPLAY_COMMAND_GRID);
    }


    /**
     * Constructor.
     *
     * @param command The command to send
     */
    public DisplayMessage (final int command)
    {
        this.command = command;
        this.array = new ArrayList<> ();
    }


    /**
     * Get the message as a byte array.
     *
     * @return The data of the message
     */
    public byte [] getData ()
    {
        final int size = this.array.size ();
        final byte [] data = new byte [3 + size];
        data[0] = -16; // -16 = 0xF0
        data[1] = (byte) this.command;
        for (int i = 0; i < size; i++)
            data[2 + i] = this.array.get (i).byteValue ();
        data[size + 2] = -9; // -9 = 0xF7
        return data;
    }


    /**
     * Set a message on the display.
     *
     * @param column The column in which to display the message
     * @param text The text to display
     * @return The message
     */
    public DisplayMessage setMessage (final int column, final String text)
    {
        for (int i = 0; i < 8; i++)
            this.addOptionElement (column == i ? text : "", "", false, "", "", false, false);
        return this;
    }


    /**
     * Adds an empty element.
     */
    public void addEmptyElement ()
    {
        this.addOptionElement ("", "", false, "", "", false, false);
    }


    /**
     * Adds a channel selector element.
     *
     * @param topMenu The text of the top menu
     * @param isTopMenuOn True if the top menu is selected
     * @param bottomMenu The text of the bottom menu
     * @param bottomMenuIcon An icon identifier for the menu
     * @param bottomMenuColor A background color for the menu
     * @param isBottomMenuOn True if the bottom menu is selected
     */
    public void addChannelSelectorElement (final String topMenu, final boolean isTopMenuOn, final String bottomMenu, final String bottomMenuIcon, final double [] bottomMenuColor, final boolean isBottomMenuOn)
    {
        this.addByte (DisplayMessage.GRID_ELEMENT_CHANNEL_SELECTION);

        // Top Menu
        this.addString (topMenu);
        this.addBoolean (isTopMenuOn);

        // Bottom Menu
        this.addString (bottomMenu);
        this.addString (bottomMenuIcon);
        this.addColor (bottomMenuColor);
        this.addBoolean (isBottomMenuOn);
    }


    /**
     * Adds a channel element.
     *
     * @param topMenu The text of the top menu
     * @param isTopMenuOn True if the top menu is selected
     * @param bottomMenu The text of the bottom menu
     * @param bottomMenuIcon An icon identifier for the menu
     * @param bottomMenuColor A background color for the menu
     * @param isBottomMenuOn True if the bottom menu is selected
     * @param volume The volume value
     * @param modulatedVolume The modulated volume value
     * @param volumeStr The volume as string
     * @param pan The panorama
     * @param modulatedPan The modulated panorama
     * @param panStr The panorama as string
     * @param vu The VU meter value
     * @param mute The mute state
     * @param solo The solo state
     * @param recarm The recording armed state
     * @param crossfadeMode Crossfade mode (0-2)
     */
    public void addChannelElement (final String topMenu, final boolean isTopMenuOn, final String bottomMenu, final String bottomMenuIcon, final double [] bottomMenuColor, final boolean isBottomMenuOn, final int volume, final int modulatedVolume, final String volumeStr, final int pan, final int modulatedPan, final String panStr, final int vu, final boolean mute, final boolean solo, final boolean recarm, final int crossfadeMode)
    {
        this.addChannelElement (DisplayMessage.GRID_ELEMENT_CHANNEL_ALL, topMenu, isTopMenuOn, bottomMenu, bottomMenuIcon, bottomMenuColor, isBottomMenuOn, volume, modulatedVolume, volumeStr, pan, modulatedPan, panStr, vu, mute, solo, recarm, crossfadeMode);
    }


    /**
     * Adds a channel element.
     *
     * @param channelType The type of the channel
     * @param topMenu The text of the top menu
     * @param isTopMenuOn True if the top menu is selected
     * @param bottomMenu The text of the bottom menu
     * @param bottomMenuIcon An icon identifier for the menu
     * @param bottomMenuColor A background color for the menu
     * @param isBottomMenuOn True if the bottom menu is selected
     * @param volume The volume value
     * @param modulatedVolume The modulated volume value
     * @param volumeStr The volume as string
     * @param pan The panorama
     * @param modulatedPan The modulated panorama
     * @param panStr The panorama as string
     * @param vu The VU meter value
     * @param mute The mute state
     * @param solo The solo state
     * @param recarm The recording armed state
     * @param crossfadeMode Crossfade mode (0-2)
     */
    public void addChannelElement (final int channelType, final String topMenu, final boolean isTopMenuOn, final String bottomMenu, final String bottomMenuIcon, final double [] bottomMenuColor, final boolean isBottomMenuOn, final int volume, final int modulatedVolume, final String volumeStr, final int pan, final int modulatedPan, final String panStr, final int vu, final boolean mute, final boolean solo, final boolean recarm, final int crossfadeMode)
    {
        this.addByte (channelType);

        // Top Menu
        this.addString (topMenu);
        this.addBoolean (isTopMenuOn);

        // Bottom Menu
        this.addString (bottomMenu);
        this.addString (bottomMenuIcon);
        this.addColor (bottomMenuColor);
        this.addBoolean (isBottomMenuOn);

        // Channel
        this.addInteger (volume);
        this.addInteger (modulatedVolume);
        this.addString (volumeStr);
        this.addInteger (pan);
        this.addInteger (modulatedPan);
        this.addString (panStr);
        this.addInteger (vu);
        this.addBoolean (mute);
        this.addBoolean (solo);
        this.addBoolean (recarm);
        this.addByte (crossfadeMode);
    }


    /**
     * Adds a channel with 4 sends element.
     *
     * @param topMenu The text of the top menu
     * @param isTopMenuOn True if the top menu is selected
     * @param bottomMenu The text of the bottom menu
     * @param bottomMenuIcon An icon identifier for the menu
     * @param bottomMenuColor A background color for the menu
     * @param isBottomMenuOn True if the bottom menu is selected
     * @param sendName The names of the sends
     * @param valueStr The volumes as string
     * @param value The volumes as values
     * @param modulatedValue The modulated volumes as values
     * @param selected The selected state of sends
     * @param isTrackMode True if track mode otherwise send mode
     */
    public void addSendsElement (final String topMenu, final boolean isTopMenuOn, final String bottomMenu, final String bottomMenuIcon, final double [] bottomMenuColor, final boolean isBottomMenuOn, final String [] sendName, final String [] valueStr, final int [] value, final int [] modulatedValue, final boolean [] selected, final boolean isTrackMode)
    {
        this.addByte (DisplayMessage.GRID_ELEMENT_CHANNEL_SENDS);

        // Top Menu
        this.addString (topMenu);
        this.addBoolean (isTopMenuOn);

        // Bottom Menu
        this.addString (bottomMenu);
        this.addString (bottomMenuIcon);
        this.addColor (bottomMenuColor);
        this.addBoolean (isBottomMenuOn);

        for (int i = 0; i < 4; i++)
        {
            this.addString (sendName[i]);
            this.addString (valueStr[i]);
            this.addInteger (value[i]);
            this.addInteger (modulatedValue[i]);
            this.addByte (selected[i] ? 1 : 0);
        }

        this.addBoolean (isTrackMode);
    }


    /**
     * Adds a parameter element without top and bottom menu.
     *
     * @param parameterName The name to display for the parameter
     * @param parameterValue The numeric value of the parameter
     * @param parameterValueStr The textual form of the parameter
     * @param parameterIsActive The parameter is currently edited
     * @param parameterModulatedValue The modulated numeric value
     */
    public void addParameterElement (final String parameterName, final int parameterValue, final String parameterValueStr, final boolean parameterIsActive, final int parameterModulatedValue)
    {
        this.addParameterElement ("", false, "", "", null, false, parameterName, parameterValue, parameterValueStr, parameterIsActive, parameterModulatedValue);
    }


    /**
     * Adds a parameter element.
     *
     * @param topMenu The text of the top menu
     * @param isTopMenuOn True if the top menu is selected
     * @param bottomMenu The text of the bottom menu
     * @param bottomMenuIcon An icon identifier for the menu
     * @param bottomMenuColor A background color for the menu
     * @param isBottomMenuOn True if the bottom menu is selected
     * @param parameterName The name to display for the parameter
     * @param parameterValue The numeric value of the parameter
     * @param parameterValueStr The textual form of the parameter
     * @param parameterIsActive The parameter is currently edited
     * @param parameterModulatedValue The modulated numeric value
     */
    public void addParameterElement (final String topMenu, final boolean isTopMenuOn, final String bottomMenu, final String bottomMenuIcon, final double [] bottomMenuColor, final boolean isBottomMenuOn, final String parameterName, final int parameterValue, final String parameterValueStr, final boolean parameterIsActive, final int parameterModulatedValue)
    {
        this.addByte (DisplayMessage.GRID_ELEMENT_PARAMETERS);

        // Top Menu
        this.addString (topMenu);
        this.addBoolean (isTopMenuOn);

        // Bottom Menu
        this.addString (bottomMenu);
        this.addString (bottomMenuIcon);
        this.addColor (bottomMenuColor);
        this.addBoolean (isBottomMenuOn);

        // Parameter
        this.addString (parameterName);
        this.addInteger (parameterValue);
        this.addString (parameterValueStr);
        this.addBoolean (parameterIsActive);
        this.addInteger (parameterModulatedValue);
    }


    /**
     * Add an options element to the message.
     *
     * @param headerTopName A text on the top
     * @param menuTopName The text for the top menu
     * @param isMenuTopSelected True if the top menu is selected (on)
     * @param headerBottomName A text on the bottom
     * @param menuBottomName The text for the bottom menu
     * @param isMenuBottomSelected True if the bottom menu is selected (on)
     * @param useSmallTopMenu If true use small menus
     */
    public void addOptionElement (final String headerTopName, final String menuTopName, final boolean isMenuTopSelected, final String headerBottomName, final String menuBottomName, final boolean isMenuBottomSelected, final boolean useSmallTopMenu)
    {
        this.addByte (DisplayMessage.GRID_ELEMENT_OPTIONS);
        this.addString (headerTopName);
        this.addString (menuTopName);
        this.addBoolean (isMenuTopSelected);
        this.addString (headerBottomName);
        this.addString (menuBottomName);
        this.addBoolean (isMenuBottomSelected);
        this.addBoolean (useSmallTopMenu);
    }


    /**
     * Add a list element to the message.
     *
     * @param items Must contain 6 texts
     * @param selected Must contain 6 states
     */
    public void addListElement (final String [] items, final boolean [] selected)
    {
        if (items.length != selected.length || items.length != 6)
            throw new IllegalArgumentException ("List array must contain 6 elements but contain " + items.length);

        this.addByte (DisplayMessage.GRID_ELEMENT_LIST);
        for (int i = 0; i < 6; i++)
        {
            this.addString (items[i]);
            this.addBoolean (selected[i]);
        }
    }


    /**
     * Adds a string to the message.
     *
     * @param text The text to add
     */
    private void addString (final String text)
    {
        if (text != null)
        {
            for (int i = 0; i < text.length (); i++)
            {
                final char character = text.charAt (i);
                if (character < 128)
                    this.array.add (Integer.valueOf (character));
                else
                {
                    // Split up non-ASII characters into 3 bytes
                    this.array.add (Integer.valueOf (-1));
                    this.addInteger (character);
                }
            }
        }
        this.array.add (Integer.valueOf (0));
    }


    /**
     * Adds an integer to the message.
     *
     * @param value The text to add
     */
    private void addInteger (final int value)
    {
        this.array.add (Integer.valueOf (value & 0x7F));
        this.array.add (Integer.valueOf (value >> 7 & 0x7F));
    }


    /**
     * Adds an boolean to the message.
     *
     * @param value The boolean to add
     */
    private void addBoolean (final boolean value)
    {
        this.array.add (Integer.valueOf (value ? 1 : 0));
    }


    /**
     * Adds an color to the message.
     *
     * @param color The color in RGB to add
     */
    private void addColor (final double [] color)
    {
        if (color != null)
        {
            this.addInteger ((int) Math.round (color[0] * 255));
            this.addInteger ((int) Math.round (color[1] * 255));
            this.addInteger ((int) Math.round (color[2] * 255));
        }
        else
        {
            this.array.add (Integer.valueOf (0));
            this.array.add (Integer.valueOf (0));
            this.array.add (Integer.valueOf (0));
            this.array.add (Integer.valueOf (0));
            this.array.add (Integer.valueOf (0));
            this.array.add (Integer.valueOf (0));
        }
    }


    /**
     * Adds a byte to the message.
     *
     * @param value The byte to add
     */
    private void addByte (final int value)
    {
        this.array.add (Integer.valueOf (value));
    }
}