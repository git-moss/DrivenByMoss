// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.display;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.ICursorClip;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.graphics.grid.ChannelGridElement;
import de.mossgrabers.framework.graphics.grid.ClipListGridElement;
import de.mossgrabers.framework.graphics.grid.GridChangeListener;
import de.mossgrabers.framework.graphics.grid.IGridElement;
import de.mossgrabers.framework.graphics.grid.ListGridElement;
import de.mossgrabers.framework.graphics.grid.MidiClipElement;
import de.mossgrabers.framework.graphics.grid.OptionsGridElement;
import de.mossgrabers.framework.graphics.grid.ParamGridElement;
import de.mossgrabers.framework.graphics.grid.SceneListGridElement;
import de.mossgrabers.framework.graphics.grid.SelectionGridElement;
import de.mossgrabers.framework.graphics.grid.SendsGridElement;
import de.mossgrabers.framework.utils.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Communication message to talk to the display process.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DisplayModel
{
    /** Display only a channel name for selection. */
    public static final int                GRID_ELEMENT_CHANNEL_SELECTION  = 0;
    /** Display a channel, edit volume. */
    public static final int                GRID_ELEMENT_CHANNEL_VOLUME     = 1;
    /** Display a channel, edit panorama. */
    public static final int                GRID_ELEMENT_CHANNEL_PAN        = 2;
    /** Display a channel, edit crossfader. */
    public static final int                GRID_ELEMENT_CHANNEL_CROSSFADER = 3;
    /** Display a channel sends. */
    public static final int                GRID_ELEMENT_CHANNEL_SENDS      = 4;
    /** Display a channel, edit all parameters. */
    public static final int                GRID_ELEMENT_CHANNEL_ALL        = 5;
    /** Display a parameter with name and value. */
    public static final int                GRID_ELEMENT_PARAMETERS         = 6;
    /** Display options on top and bottom. */
    public static final int                GRID_ELEMENT_OPTIONS            = 7;
    /** Display a list. */
    public static final int                GRID_ELEMENT_LIST               = 8;

    /** Timeout for displaying the notification message. */
    private static final int               TIMEOUT                         = 2;

    private final AtomicInteger            counter                         = new AtomicInteger ();
    private final ScheduledExecutorService executor                        = Executors.newSingleThreadScheduledExecutor ();

    private final List<GridChangeListener> listeners                       = new ArrayList<> ();
    private final List<IGridElement>        elements                        = new ArrayList<> (8);
    private final AtomicReference<String>  notificationMessage             = new AtomicReference<> ();
    private ModelInfo                      info                            = new ModelInfo (null, Collections.emptyList ());


    /**
     * Constructor.
     */
    public DisplayModel ()
    {
        this.executor.scheduleAtFixedRate ( () -> {
            final int c = this.counter.get ();
            if (c <= 0)
                return;
            if (this.counter.decrementAndGet () == 0)
                this.notificationMessage.set (null);
        }, 1, 1, TimeUnit.SECONDS);
    }


    /**
     * Shutdown the count down process.
     */
    public void shutdown ()
    {
        this.executor.shutdown ();
    }


    /**
     * Adds a listener for grid element changes
     *
     * @param listener A listener
     */
    public void addGridElementChangeListener (final GridChangeListener listener)
    {
        this.listeners.add (listener);
    }


    /**
     * Send the message to the display process.
     */
    public void send ()
    {
        if (this.executor.isShutdown ())
            return;

        this.info = new ModelInfo (this.notificationMessage.get (), this.elements);
        this.elements.clear ();
        for (final GridChangeListener listener: this.listeners)
            listener.gridHasChanged ();
    }


    /**
     * Set a midi clip to display in a piano roll.
     *
     * @param clip The clip to display
     * @param quartersPerMeasure The quarters of a measure
     */
    public void setMidiClipElement (final ICursorClip clip, final int quartersPerMeasure)
    {
        this.elements.add (new MidiClipElement (clip, quartersPerMeasure));
    }


    /**
     * Set a notification message on the display, which overlays the current content.
     *
     * @param message The text to display
     */
    public void setNotificationMessage (final String message)
    {
        this.counter.set (TIMEOUT);
        this.notificationMessage.set (message);
    }


    /**
     * Set a message on the display.
     *
     * @param column The column in which to display the message
     * @param text The text to display
     * @return The message
     */
    public DisplayModel setMessage (final int column, final String text)
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
        this.elements.add (new SelectionGridElement (topMenu, isTopMenuOn, bottomMenu, new ColorEx (bottomMenuColor[0], bottomMenuColor[1], bottomMenuColor[2]), isBottomMenuOn, type));
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
     * @param vuLeft The VU meter value of the left channel
     * @param vuRight The VU meter value of the right channel
     * @param mute The mute state
     * @param solo The solo state
     * @param recarm The recording armed state
     * @param crossfadeMode Crossfade mode (0-2)
     */
    public void addChannelElement (final String topMenu, final boolean isTopMenuOn, final String bottomMenu, final ChannelType type, final double [] bottomMenuColor, final boolean isBottomMenuOn, final int volume, final int modulatedVolume, final String volumeStr, final int pan, final int modulatedPan, final String panStr, final int vuLeft, final int vuRight, final boolean mute, final boolean solo, final boolean recarm, final int crossfadeMode)
    {
        this.addChannelElement (DisplayModel.GRID_ELEMENT_CHANNEL_ALL, topMenu, isTopMenuOn, bottomMenu, type, bottomMenuColor, isBottomMenuOn, volume, modulatedVolume, volumeStr, pan, modulatedPan, panStr, vuLeft, vuRight, mute, solo, recarm, crossfadeMode);
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
     * @param vuLeft The VU meter value of the left channel
     * @param vuRight The VU meter value of the right channel
     * @param mute The mute state
     * @param solo The solo state
     * @param recarm The recording armed state
     * @param crossfadeMode Crossfade mode (0-2)
     */
    public void addChannelElement (final int channelType, final String topMenu, final boolean isTopMenuOn, final String bottomMenu, final ChannelType type, final double [] bottomMenuColor, final boolean isBottomMenuOn, final int volume, final int modulatedVolume, final String volumeStr, final int pan, final int modulatedPan, final String panStr, final int vuLeft, final int vuRight, final boolean mute, final boolean solo, final boolean recarm, final int crossfadeMode)
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
        this.elements.add (new ChannelGridElement (editType, topMenu, isTopMenuOn, bottomMenu, new ColorEx (bottomMenuColor[0], bottomMenuColor[1], bottomMenuColor[2]), isBottomMenuOn, type, volume, modulatedVolume, volumeStr, pan, modulatedPan, panStr, vuLeft, vuRight, mute, solo, recarm, crossfadeMode));
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
        this.elements.add (new SendsGridElement (sendName, valueStr, value, modulatedValue, selected, topMenu, isTopMenuOn, bottomMenu, new ColorEx (bottomMenuColor[0], bottomMenuColor[1], bottomMenuColor[2]), isBottomMenuOn, type, isTrackMode));

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
        this.elements.add (new ParamGridElement (topMenu, isTopMenuOn, bottomMenu, type, new ColorEx (bottomMenuColor[0], bottomMenuColor[1], bottomMenuColor[2]), isBottomMenuOn, parameterName, parameterValue, parameterModulatedValue, parameterValueStr, parameterIsActive));
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
        this.elements.add (new ParamGridElement (topMenu, isTopMenuOn, bottomMenu, deviceName, new ColorEx (bottomMenuColor[0], bottomMenuColor[1], bottomMenuColor[2]), isBottomMenuOn, parameterName, parameterValue, parameterModulatedValue, parameterValueStr, parameterIsActive));
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
        this.addOptionElement (headerTopName, menuTopName, isMenuTopSelected, null, headerBottomName, menuBottomName, isMenuBottomSelected, null, useSmallTopMenu);
    }


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
    public void addOptionElement (final String headerTopName, final String menuTopName, final boolean isMenuTopSelected, final double [] menuTopColor, final String headerBottomName, final String menuBottomName, final boolean isMenuBottomSelected, final double [] menuBottomColor, final boolean useSmallTopMenu)
    {
        this.elements.add (new OptionsGridElement (headerTopName, menuTopName, isMenuTopSelected, menuTopColor, headerBottomName, menuBottomName, isMenuBottomSelected, menuBottomColor, useSmallTopMenu));
    }


    /**
     * Add a list element to the message.
     *
     * @param items Must contain X number of texts
     * @param selected Must contain X number of states
     */
    public void addListElement (final String [] items, final boolean [] selected)
    {
        final List<Pair<String, Boolean>> menu = new ArrayList<> ();
        for (int i = 0; i < items.length; i++)
            menu.add (new Pair<> (items[i], Boolean.valueOf (selected[i])));
        this.elements.add (new ListGridElement (menu));
    }


    /**
     * Add a list of scene elements to the message.
     *
     * @param scenes The scenes
     */
    public void addSceneListElement (final List<IScene> scenes)
    {
        this.elements.add (new SceneListGridElement (scenes));
    }


    /**
     * Add a list of box elements to the message.
     *
     * @param slots Must contain X number of slot items
     */
    public void addSlotListElement (final List<Pair<ITrack, ISlot>> slots)
    {
        this.elements.add (new ClipListGridElement (slots));
    }


    /**
     * Get the drawing info object.
     *
     * @return The info.
     */
    public ModelInfo getInfo ()
    {
        return this.info;
    }
}
