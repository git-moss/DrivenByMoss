// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.controller;

import de.mossgrabers.framework.Pair;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.push.controller.display.model.DisplayModel;
import de.mossgrabers.push.controller.display.model.grid.ChannelGridElement;
import de.mossgrabers.push.controller.display.model.grid.ChannelSelectionGridElement;
import de.mossgrabers.push.controller.display.model.grid.GridElement;
import de.mossgrabers.push.controller.display.model.grid.ListGridElement;
import de.mossgrabers.push.controller.display.model.grid.OptionsGridElement;
import de.mossgrabers.push.controller.display.model.grid.ParamGridElement;
import de.mossgrabers.push.controller.display.model.grid.SendsGridElement;

import com.bitwig.extension.api.Color;

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
    public static final int         GRID_ELEMENT_CHANNEL_SELECTION  = 0;
    /** Display a channel, edit volume. */
    public static final int         GRID_ELEMENT_CHANNEL_VOLUME     = 1;
    /** Display a channel, edit panorama. */
    public static final int         GRID_ELEMENT_CHANNEL_PAN        = 2;
    /** Display a channel, edit crossfader. */
    public static final int         GRID_ELEMENT_CHANNEL_CROSSFADER = 3;
    /** Display a channel sends. */
    public static final int         GRID_ELEMENT_CHANNEL_SENDS      = 4;
    /** Display a channel, edit all parameters. */
    public static final int         GRID_ELEMENT_CHANNEL_ALL        = 5;
    /** Display a parameter with name and value. */
    public static final int         GRID_ELEMENT_PARAMETERS         = 6;
    /** Display options on top and bottom. */
    public static final int         GRID_ELEMENT_OPTIONS            = 7;
    /** Display a list. */
    public static final int         GRID_ELEMENT_LIST               = 8;

    private DisplayModel            model;
    private final List<GridElement> elements                        = new ArrayList<> (8);


    /**
     * Constructor. Uses the grid command.
     *
     * @param model The display model
     */
    public DisplayMessage (final DisplayModel model)
    {
        this.model = model;
    }


    /**
     * Send the message to the display process.
     */
    public void send ()
    {
        this.model.setGridElements (this.elements);
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
     * @param type The type of the channel
     * @param bottomMenuColor A background color for the menu
     * @param isBottomMenuOn True if the bottom menu is selected
     */
    public void addChannelSelectorElement (final String topMenu, final boolean isTopMenuOn, final String bottomMenu, final ChannelType type, final double [] bottomMenuColor, final boolean isBottomMenuOn)
    {
        this.elements.add (new ChannelSelectionGridElement (topMenu, isTopMenuOn, bottomMenu, Color.fromRGB (bottomMenuColor[0], bottomMenuColor[1], bottomMenuColor[2]), isBottomMenuOn, type));
    }


    /**
     * Adds a channel element.
     *
     * @param topMenu The text of the top menu
     * @param isTopMenuOn True if the top menu is selected
     * @param bottomMenu The text of the bottom menu
     * @param type The type of the channel
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
    public void addChannelElement (final String topMenu, final boolean isTopMenuOn, final String bottomMenu, final ChannelType type, final double [] bottomMenuColor, final boolean isBottomMenuOn, final int volume, final int modulatedVolume, final String volumeStr, final int pan, final int modulatedPan, final String panStr, final int vu, final boolean mute, final boolean solo, final boolean recarm, final int crossfadeMode)
    {
        this.addChannelElement (DisplayMessage.GRID_ELEMENT_CHANNEL_ALL, topMenu, isTopMenuOn, bottomMenu, type, bottomMenuColor, isBottomMenuOn, volume, modulatedVolume, volumeStr, pan, modulatedPan, panStr, vu, mute, solo, recarm, crossfadeMode);
    }


    /**
     * Adds a channel element.
     *
     * @param channelType The type of the channel
     * @param topMenu The text of the top menu
     * @param isTopMenuOn True if the top menu is selected
     * @param bottomMenu The text of the bottom menu
     * @param type The type of the channel
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
    public void addChannelElement (final int channelType, final String topMenu, final boolean isTopMenuOn, final String bottomMenu, final ChannelType type, final double [] bottomMenuColor, final boolean isBottomMenuOn, final int volume, final int modulatedVolume, final String volumeStr, final int pan, final int modulatedPan, final String panStr, final int vu, final boolean mute, final boolean solo, final boolean recarm, final int crossfadeMode)
    {
        int editType;
        switch (channelType)
        {
            case GRID_ELEMENT_CHANNEL_VOLUME:
                editType = ChannelGridElement.EDIT_TYPE_VOLUME;
                break;
            case GRID_ELEMENT_CHANNEL_PAN:
                editType = ChannelGridElement.EDIT_TYPE_PAN;
                break;
            case GRID_ELEMENT_CHANNEL_CROSSFADER:
                editType = ChannelGridElement.EDIT_TYPE_CROSSFADER;
                break;
            default:
                editType = ChannelGridElement.EDIT_TYPE_ALL;
                break;
        }
        this.elements.add (new ChannelGridElement (editType, topMenu, isTopMenuOn, bottomMenu, Color.fromRGB (bottomMenuColor[0], bottomMenuColor[1], bottomMenuColor[2]), isBottomMenuOn, type, volume, modulatedVolume, volumeStr, pan, modulatedPan, panStr, vu, mute, solo, recarm, crossfadeMode));
    }


    /**
     * Adds a channel with 4 sends element.
     *
     * @param topMenu The text of the top menu
     * @param isTopMenuOn True if the top menu is selected
     * @param bottomMenu The text of the bottom menu
     * @param type The type of the channel
     * @param bottomMenuColor A background color for the menu
     * @param isBottomMenuOn True if the bottom menu is selected
     * @param sendName The names of the sends
     * @param valueStr The volumes as string
     * @param value The volumes as values
     * @param modulatedValue The modulated volumes as values
     * @param selected The selected state of sends
     * @param isTrackMode True if track mode otherwise send mode
     */
    public void addSendsElement (final String topMenu, final boolean isTopMenuOn, final String bottomMenu, final ChannelType type, final double [] bottomMenuColor, final boolean isBottomMenuOn, final String [] sendName, final String [] valueStr, final int [] value, final int [] modulatedValue, final boolean [] selected, final boolean isTrackMode)
    {
        this.elements.add (new SendsGridElement (sendName, valueStr, value, modulatedValue, selected, topMenu, isTopMenuOn, bottomMenu, Color.fromRGB (bottomMenuColor[0], bottomMenuColor[1], bottomMenuColor[2]), isBottomMenuOn, type, isTrackMode));

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
        this.addParameterElement ("", false, "", ChannelType.EFFECT, new double []
        {
            0,
            0,
            0
        }, false, parameterName, parameterValue, parameterValueStr, parameterIsActive, parameterModulatedValue);
    }


    /**
     * Adds a parameter element.
     *
     * @param topMenu The text of the top menu
     * @param isTopMenuOn True if the top menu is selected
     * @param bottomMenu The text of the bottom menu
     * @param type The type of the channel
     * @param bottomMenuColor A background color for the menu
     * @param isBottomMenuOn True if the bottom menu is selected
     * @param parameterName The name to display for the parameter
     * @param parameterValue The numeric value of the parameter
     * @param parameterValueStr The textual form of the parameter
     * @param parameterIsActive The parameter is currently edited
     * @param parameterModulatedValue The modulated numeric value
     */
    public void addParameterElement (final String topMenu, final boolean isTopMenuOn, final String bottomMenu, final ChannelType type, final double [] bottomMenuColor, final boolean isBottomMenuOn, final String parameterName, final int parameterValue, final String parameterValueStr, final boolean parameterIsActive, final int parameterModulatedValue)
    {
        this.elements.add (new ParamGridElement (topMenu, isTopMenuOn, bottomMenu, type, Color.fromRGB (bottomMenuColor[0], bottomMenuColor[1], bottomMenuColor[2]), isBottomMenuOn, parameterName, parameterValue, parameterModulatedValue, parameterValueStr, parameterIsActive));
    }


    /**
     * Adds a parameter element.
     *
     * @param topMenu The text of the top menu
     * @param isTopMenuOn True if the top menu is selected
     * @param bottomMenu The text of the bottom menu
     * @param deviceName The name of the device
     * @param bottomMenuColor A background color for the menu
     * @param isBottomMenuOn True if the bottom menu is selected
     * @param parameterName The name to display for the parameter
     * @param parameterValue The numeric value of the parameter
     * @param parameterValueStr The textual form of the parameter
     * @param parameterIsActive The parameter is currently edited
     * @param parameterModulatedValue The modulated numeric value
     */
    public void addParameterElement (final String topMenu, final boolean isTopMenuOn, final String bottomMenu, final String deviceName, final double [] bottomMenuColor, final boolean isBottomMenuOn, final String parameterName, final int parameterValue, final String parameterValueStr, final boolean parameterIsActive, final int parameterModulatedValue)
    {
        this.elements.add (new ParamGridElement (topMenu, isTopMenuOn, bottomMenu, deviceName, Color.fromRGB (bottomMenuColor[0], bottomMenuColor[1], bottomMenuColor[2]), isBottomMenuOn, parameterName, parameterValue, parameterModulatedValue, parameterValueStr, parameterIsActive));
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
        this.elements.add (new OptionsGridElement (headerTopName, menuTopName, isMenuTopSelected, headerBottomName, menuBottomName, isMenuBottomSelected, useSmallTopMenu));
    }


    /**
     * Add a list element to the message.
     *
     * @param items Must contain 6 texts
     * @param selected Must contain 6 states
     */
    public void addListElement (final String [] items, final boolean [] selected)
    {
        final List<Pair<String, Boolean>> menu = new ArrayList<> ();
        for (int i = 0; i < 6; i++)
            menu.add (new Pair<> (items[i], Boolean.valueOf (selected[i])));
        this.elements.add (new ListGridElement (menu));
    }
}