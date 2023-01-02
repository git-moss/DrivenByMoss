// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.featuregroup;

import de.mossgrabers.framework.command.core.AftertouchCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.KeyManager;


/**
 * Interface to a view. A view contains a grid of pads and a number of buttons to which commands can
 * be assigned.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IView extends IFeatureGroup
{
    /**
     * Registers the aftertouch command.
     *
     * @param command The command
     */
    void registerAftertouchCommand (AftertouchCommand command);


    /**
     * Execute the aftertouch command which has been registered before.
     *
     * @param note The note on which aftertouch is applied. Set to -1 for channel aftertouch
     * @param value The updated value
     */
    void executeAftertouchCommand (int note, int value);


    /**
     * Draw the pad grid.
     */
    void drawGrid ();


    /**
     * A pad has been pressed or released.
     *
     * @param note The note of the pad
     * @param velocity The velocity of the press
     */
    void onGridNote (int note, int velocity);


    /**
     * Long press actions on grid pads
     *
     * @param note The long pressed note
     */
    void onGridNoteLongPress (int note);


    /**
     * A button event occurred.
     *
     * @param buttonID The ID of the button
     * @param event The button event
     * @param velocity The velocity with which the button was pressed (0-127)
     */
    void onButton (ButtonID buttonID, ButtonEvent event, int velocity);


    /**
     * Hook to update all button LEDs, displays, etc.
     */
    void updateControlSurface ();


    /**
     * Update the note mapping of the grid pads.
     */
    void updateNoteMapping ();


    /**
     * Selects a track in the current page of the current track bank and makes the track visible in
     * the DAW.
     *
     * @param index The index of the track in the page
     */
    void selectTrack (int index);


    /**
     * Get the key manager.
     *
     * @return The key manager
     */
    KeyManager getKeyManager ();
}
