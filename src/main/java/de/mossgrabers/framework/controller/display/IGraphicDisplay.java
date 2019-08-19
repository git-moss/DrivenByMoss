// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.display;

import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.graphics.canvas.utils.SendData;
import de.mossgrabers.framework.utils.Pair;

import java.util.List;


/**
 * Interface to a graphics display.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IGraphicDisplay extends IDisplay
{
    /**
     * Show the debug window for the graphics display.
     */
    void showDebugWindow ();


    /**
     * Send the message to the display process.
     */
    void send ();


    /**
     * Set a notification message on the display, which overlays the current content.
     *
     * @param message The text to display
     */
    void setNotificationMessage (final String message);


    /**
     * Set a midi clip to display in a piano roll.
     *
     * @param clip The clip to display
     * @param quartersPerMeasure The quarters of a measure
     */
    void setMidiClipElement (final INoteClip clip, final int quartersPerMeasure);


    /**
     * Set a message on the display.
     *
     * @param column The column in which to display the message
     * @param text The text to display
     * @return The display
     */
    public IGraphicDisplay setMessage (final int column, final String text);


    /**
     * Adds an empty element.
     */
    void addEmptyElement ();


    /**
     * Adds a channel selector element.
     *
     * @param topMenu The text of the top menu
     * @param isTopMenuOn True if the top menu is selected
     * @param bottomMenu The text of the bottom menu
     * @param type The type of the channel
     * @param bottomMenuColor A background color for the menu
     * @param isBottomMenuOn True if the bottom menu is selected
     * @param isActive True if channel is activated
     */
    void addChannelSelectorElement (final String topMenu, final boolean isTopMenuOn, final String bottomMenu, final ChannelType type, final double [] bottomMenuColor, final boolean isBottomMenuOn, final boolean isActive);


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
     * @param vuLeft The VU meter value of the left channel
     * @param vuRight The VU meter value of the right channel
     * @param mute The mute state
     * @param solo The solo state
     * @param recarm The recording armed state
     * @param isActive True if channel is activated
     * @param crossfadeMode Crossfade mode (0-2)
     */
    void addChannelElement (final String topMenu, final boolean isTopMenuOn, final String bottomMenu, final ChannelType type, final double [] bottomMenuColor, final boolean isBottomMenuOn, final int volume, final int modulatedVolume, final String volumeStr, final int pan, final int modulatedPan, final String panStr, final int vuLeft, final int vuRight, final boolean mute, final boolean solo, final boolean recarm, final boolean isActive, final int crossfadeMode);


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
     * @param vuLeft The VU meter value of the left channel
     * @param vuRight The VU meter value of the right channel
     * @param mute The mute state
     * @param solo The solo state
     * @param recarm The recording armed state
     * @param isActive True if channel is activated
     * @param crossfadeMode Crossfade mode (0-2)
     */
    void addChannelElement (final int channelType, final String topMenu, final boolean isTopMenuOn, final String bottomMenu, final ChannelType type, final double [] bottomMenuColor, final boolean isBottomMenuOn, final int volume, final int modulatedVolume, final String volumeStr, final int pan, final int modulatedPan, final String panStr, final int vuLeft, final int vuRight, final boolean mute, final boolean solo, final boolean recarm, final boolean isActive, final int crossfadeMode);


    /**
     * Adds a channel with 4 sends element.
     *
     * @param topMenu The text of the top menu
     * @param isTopMenuOn True if the top menu is selected
     * @param bottomMenu The text of the bottom menu
     * @param type The type of the channel
     * @param bottomMenuColor A background color for the menu
     * @param isBottomMenuOn True if the bottom menu is selected
     * @param sendData The send information
     * @param isTrackMode True if track mode otherwise send mode
     * @param isSendActive True if the upper send part is activated
     * @param isChannelLabelActive True if channel is activated
     */
    void addSendsElement (final String topMenu, final boolean isTopMenuOn, final String bottomMenu, final ChannelType type, final double [] bottomMenuColor, final boolean isBottomMenuOn, final SendData [] sendData, final boolean isTrackMode, final boolean isSendActive, final boolean isChannelLabelActive);


    /**
     * Adds a parameter element without top and bottom menu.
     *
     * @param parameterName The name to display for the parameter
     * @param parameterValue The numeric value of the parameter
     * @param parameterValueStr The textual form of the parameter
     * @param parameterIsActive The parameter is currently edited
     * @param parameterModulatedValue The modulated numeric value
     */
    void addParameterElement (final String parameterName, final int parameterValue, final String parameterValueStr, final boolean parameterIsActive, final int parameterModulatedValue);


    /**
     * Adds a parameter element.
     *
     * @param topMenu The text of the top menu
     * @param isTopMenuOn True if the top menu is selected
     * @param bottomMenu The text of the bottom menu
     * @param type The channel type
     * @param bottomMenuColor A background color for the menu
     * @param isBottomMenuOn True if the bottom menu is selected
     * @param parameterName The name to display for the parameter
     * @param parameterValue The numeric value of the parameter
     * @param parameterValueStr The textual form of the parameter
     * @param parameterIsActive The parameter is currently edited
     * @param parameterModulatedValue The modulated numeric value
     */
    void addParameterElement (final String topMenu, final boolean isTopMenuOn, final String bottomMenu, final ChannelType type, final double [] bottomMenuColor, final boolean isBottomMenuOn, final String parameterName, final int parameterValue, final String parameterValueStr, final boolean parameterIsActive, final int parameterModulatedValue);


    /**
     * Adds a parameter element.
     *
     * @param topMenu The text of the top menu
     * @param isTopMenuOn True if the top menu is selected
     * @param bottomMenu The text of the bottom menu
     * @param bottomMenuColor A background color for the menu
     * @param isBottomMenuOn True if the bottom menu is selected
     * @param parameterName The name to display for the parameter
     * @param parameterValue The numeric value of the parameter
     * @param parameterValueStr The textual form of the parameter
     * @param parameterIsActive The parameter is currently edited
     * @param parameterModulatedValue The modulated numeric value
     */
    void addParameterElementWithPlainMenu (final String topMenu, final boolean isTopMenuOn, final String bottomMenu, final double [] bottomMenuColor, final boolean isBottomMenuOn, final String parameterName, final int parameterValue, final String parameterValueStr, final boolean parameterIsActive, final int parameterModulatedValue);


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
    void addParameterElement (final String topMenu, final boolean isTopMenuOn, final String bottomMenu, final String deviceName, final double [] bottomMenuColor, final boolean isBottomMenuOn, final String parameterName, final int parameterValue, final String parameterValueStr, final boolean parameterIsActive, final int parameterModulatedValue);


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
    void addOptionElement (final String headerTopName, final String menuTopName, final boolean isMenuTopSelected, final String headerBottomName, final String menuBottomName, final boolean isMenuBottomSelected, final boolean useSmallTopMenu);


    /**
     * Add an options element to the message.
     *
     * @param headerTopName A text on the top
     * @param menuTopName The text for the top menu
     * @param isMenuTopSelected True if the top menu is selected (on)
     * @param menuTopColor The color to use for the background top menu, may be null
     * @param headerBottomName A text on the bottom
     * @param menuBottomName The text for the bottom menu
     * @param isMenuBottomSelected True if the bottom menu is selected (on)
     * @param menuBottomColor The color to use for the background bottom menu, may be null
     * @param useSmallTopMenu If true use small menus
     */
    void addOptionElement (final String headerTopName, final String menuTopName, final boolean isMenuTopSelected, final double [] menuTopColor, final String headerBottomName, final String menuBottomName, final boolean isMenuBottomSelected, final double [] menuBottomColor, final boolean useSmallTopMenu);


    /**
     * Add an options element to the message.
     *
     * @param headerTopName A text on the top
     * @param menuTopName The text for the top menu
     * @param isMenuTopSelected True if the top menu is selected (on)
     * @param menuTopColor The color to use for the background top menu, may be null
     * @param headerBottomName A text on the bottom
     * @param menuBottomName The text for the bottom menu
     * @param isMenuBottomSelected True if the bottom menu is selected (on)
     * @param menuBottomColor The color to use for the background bottom menu, may be null
     * @param useSmallTopMenu If true use small menus
     * @param isBottomHeaderSelected True to draw the lower header selected
     */
    void addOptionElement (final String headerTopName, final String menuTopName, final boolean isMenuTopSelected, final double [] menuTopColor, final String headerBottomName, final String menuBottomName, final boolean isMenuBottomSelected, final double [] menuBottomColor, final boolean useSmallTopMenu, final boolean isBottomHeaderSelected);


    /**
     * Add a list element to the message with one selected item.
     *
     * @param displaySize The number of items to display in the list
     * @param elements The list with all items
     * @param selectedIndex The selected index in the list
     */
    void addListElement (final int displaySize, final String [] elements, final int selectedIndex);


    /**
     * Add a list element to the message.
     *
     * @param items Must contain X number of texts
     * @param selected Must contain X number of states
     */
    void addListElement (final String [] items, final boolean [] selected);


    /**
     * Add a list of scene elements to the message.
     *
     * @param scenes The scenes
     */
    void addSceneListElement (final List<IScene> scenes);


    /**
     * Add a list of box elements to the message.
     *
     * @param slots Must contain X number of slot items
     */
    void addSlotListElement (final List<Pair<ITrack, ISlot>> slots);
}
