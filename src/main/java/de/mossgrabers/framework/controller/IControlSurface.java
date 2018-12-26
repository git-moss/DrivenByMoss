// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.view.ViewManager;


/**
 * Interface of a hardware control surface.
 *
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IControlSurface<C extends Configuration>
{
    /**
     * Get the view manager.
     *
     * @return The view manager
     */
    ViewManager getViewManager ();


    /**
     * Get the mode manager.
     *
     * @return The mode manager
     */
    ModeManager getModeManager ();


    /**
     * Get the configuration settings.
     *
     * @return The configuration object
     */
    C getConfiguration ();


    /**
     * Get the interface to the display if the controller does have one.
     *
     * @return The display interface
     */
    Display getDisplay ();


    /**
     * Set the interface to the display if the controller does have one.
     *
     * @param display The display interface
     */
    void setDisplay (final Display display);


    /**
     * Get the interface to the grid of some pads if the controller does have pads.
     *
     * @return The interface to pads
     */
    PadGrid getPadGrid ();


    /**
     * Get the midi output.
     *
     * @return The output
     */
    IMidiOutput getOutput ();


    /**
     * Get the midi input.
     *
     * @return The input
     */
    IMidiInput getInput ();


    /**
     * Assigns a command to a midi CC. When the midi CC is received the command is executed.
     *
     * @param midiCC The midi CC
     * @param commandID The command ID
     */
    void assignTriggerCommand (int midiCC, Integer commandID);


    /**
     * Assigns a command to a midi CC. When the midi CC is received the command is executed.
     *
     * @param midiCC The midi CC
     * @param midiChannel The midi channel to assign to
     * @param commandID The command ID
     */
    void assignTriggerCommand (int midiCC, int midiChannel, Integer commandID);


    /**
     * Get the ID of an assigned command.
     *
     * @param midiCC The midi CC
     * @return The command ID or null if none is assigned to the given midi CC
     */
    Integer getTriggerCommand (int midiCC);


    /**
     * Get the ID of an assigned command.
     *
     * @param midiCC The midi CC
     * @param midiChannel The midi channel to assign to
     * @return The command ID or null if none is assigned to the given midi CC
     */
    Integer getTriggerCommand (int midiCC, int midiChannel);


    /**
     * Assigns a continuous command to a midi CC. When the midi CC is received the command is
     * executed.
     *
     * @param midiCC The midi CC
     * @param commandID The command ID
     */
    void assignContinuousCommand (int midiCC, Integer commandID);


    /**
     * Assigns a continuous command to a midi CC. When the midi CC is received the command is
     * executed.
     *
     * @param midiCC The midi CC
     * @param midiChannel The midi channel to assign to
     * @param commandID The command ID
     */
    void assignContinuousCommand (int midiCC, int midiChannel, Integer commandID);


    /**
     * Get the ID of an assigned continuous command.
     *
     * @param midiCC The midi CC
     * @return The command ID or null if none is assigned to the given midi CC
     */
    Integer getContinuousCommand (int midiCC);


    /**
     * Get the ID of an assigned continuous command.
     *
     * @param midiCC The midi CC
     * @param midiChannel The midi channel to assign to
     * @return The command ID or null if none is assigned to the given midi CC
     */
    Integer getContinuousCommand (int midiCC, int midiChannel);


    /**
     * Assigns a note (continuous) command to a midi CC. When the midi note is received the command
     * is executed.
     *
     * @param midiNote The midi note
     * @param commandID The command ID
     */
    void assignNoteCommand (final int midiNote, final Integer commandID);


    /**
     * Get the ID of an assigned note (continuous) command.
     *
     * @param midiNote The midi note
     * @return The command ID or null if none is assigned to the given midi CC
     */
    Integer getNoteCommand (final int midiNote);


    /**
     * Check if a given note belongs to the grid.
     *
     * @param note The note to check
     * @return True if the note belongs to the grid
     */
    boolean isGridNote (int note);


    /**
     * Set the mapping of midi notes to the midi notes sent to the DAW.
     *
     * @param table The table has 128 items. The index is the incoming note, the value at the index
     *            the outgoing note.
     */
    void setKeyTranslationTable (int [] table);


    /**
     * Get the mapping of midi notes to the midi notes sent to the DAW.
     *
     * @return The table has 128 items. The index is the incoming note, the value at the index the
     *         outgoing note.
     */
    int [] getKeyTranslationTable ();


    /**
     * Sets the mapping of midi note velocities to the midi note velocities sent to the DAW
     *
     * @param table The table has 128 items. The index is the incoming velocity, the value at the
     *            index the outgoing velocity. E.g. if you set all values to 127 you set the
     *            velocity always to maximum
     */
    void setVelocityTranslationTable (int [] table);


    /**
     * Is the select button pressed (if the controller has one).
     *
     * @return The state of the select button
     */
    boolean isSelectPressed ();


    /**
     * Is the shift button pressed (if the controller has one).
     *
     * @return The state of the shift button
     */
    boolean isShiftPressed ();


    /**
     * Is the delete button pressed (if the controller has one).
     *
     * @return The state of the delete button
     */
    boolean isDeletePressed ();


    /**
     * Is the solo button pressed (if the controller has one).
     *
     * @return The state of the solo button
     */
    boolean isSoloPressed ();


    /**
     * Is the mute button pressed (if the controller has one).
     *
     * @return The state of the mute button
     */
    boolean isMutePressed ();


    /**
     * Get the midi cc of the Shift button.
     *
     * @return The midi cc
     */
    int getShiftButtonId ();


    /**
     * Get the midi cc of the Select button.
     *
     * @return The midi cc
     */
    int getSelectButtonId ();


    /**
     * Get the midi cc of the Delete button.
     *
     * @return The midi cc
     */
    int getDeleteButtonId ();


    /**
     * Get the midi cc of the Mute button.
     *
     * @return The midi cc
     */
    int getMuteButtonId ();


    /**
     * Get the midi cc of the Solo button.
     *
     * @return The midi cc
     */
    int getSoloButtonId ();


    /**
     * Get the midi cc of the Left button.
     *
     * @return The midi cc
     */
    int getLeftButtonId ();


    /**
     * Get the midi cc of the Right button.
     *
     * @return The midi cc
     */
    int getRightButtonId ();


    /**
     * Get the midi cc of the Up button.
     *
     * @return The midi cc
     */
    int getUpButtonId ();


    /**
     * Get the midi cc of the Down button.
     *
     * @return The midi cc
     */
    int getDownButtonId ();


    /**
     * Get the midi cc of one of the scene buttons.
     *
     *
     * @param index The index of the scene button
     * @return The midi cc
     */
    int getSceneButton (final int index);


    /**
     * Test if the button with the given midi cc is pressed.
     *
     * @param button The button to test
     * @return True if pressed
     */
    boolean isPressed (int button);


    /**
     * Sets a button as consumed which prevents LONG and UP events following a DOWN event for a
     * button.
     *
     * @param buttonID The button to set as consumed
     */
    void setButtonConsumed (int buttonID);


    /**
     * Test if the consumed flag is set for a button.
     *
     * @param buttonID The button to set as consumed
     * @return The consumed flag
     */
    boolean isButtonConsumed (int buttonID);


    /**
     * Update the lighting of a button (if the buttons has light), sending on midi channel 1. This
     * method caches the state of the button and sends only updates to the controller if the state
     * has changed, in contrast to setButton.
     *
     * @param button The button
     * @param value The color / brightness depending on the controller
     */
    void updateButton (int button, int value);


    /**
     * Update the lighting of a button (if the buttons has light). This method caches the state of
     * the button and sends only updates to the controller if the state has changed, in contrast to
     * setButton.
     *
     * @param button The button
     * @param channel The midi channel to use
     * @param value The color / brightness depending on the controller
     */
    void updateButtonEx (int button, int channel, int value);


    /**
     * Update the lighting of a button (if the buttons has light), sending on midi channel 1. This
     * method caches the state of the button and sends only updates to the controller if the state
     * has changed, in contrast to setButton.
     *
     * @param button The button
     * @param colorID A registered color ID of the color / brightness depending on the controller
     */
    void updateButton (int button, String colorID);


    /**
     * Update the lighting of a button (if the buttons has light). This method caches the state of
     * the button and sends only updates to the controller if the state has changed, in contrast to
     * setButton.
     *
     * @param button The button
     * @param channel The midi channel to use
     * @param colorID A registered color ID of the color / brightness depending on the controller
     */
    void updateButtonEx (int button, int channel, String colorID);


    /**
     * Update the lighting of a button (if the buttons has light), sending on midi channel 1.
     *
     * @param button The button
     * @param value The color / brightness depending on the controller
     */
    void setButton (int button, int value);


    /**
     * Update the lighting of a button (if the buttons has light), sending on midi channel 1.
     *
     * @param button The button
     * @param colorID A registered color ID of the color / brightness depending on the controller
     */
    void setButton (int button, String colorID);


    /**
     * Update the lighting of a button (if the buttons has light).
     *
     * @param button The button
     * @param channel The midi channel to use
     * @param value The color / brightness depending on the controller
     */
    void setButtonEx (int button, int channel, int value);


    /**
     * Update the lighting of a button (if the buttons has light).
     *
     * @param button The button
     * @param channel The midi channel to use
     * @param colorID A registered color ID of the color / brightness depending on the controller
     */
    void setButtonEx (int button, int channel, String colorID);


    /**
     * Clear the cached lighting state of a button.
     *
     * @param button The button
     */
    void clearButtonCache (int button);


    /**
     * Clear the cached lighting state of all buttons.
     */
    void clearButtonCache ();


    /**
     * Check if the midi CC belongs to a button
     *
     * @param cc The CC to check
     * @return True if it belongs to a button
     */
    boolean isButton (int cc);


    /**
     * Schedule a task.
     *
     * @param callback The code to delay
     * @param delay The time in ms how long to delay the execution of the task
     */
    void scheduleTask (Runnable callback, long delay);


    /**
     * Send a midi message to the DAW (not to the midi output).
     *
     * @param status The midi status byte
     * @param data1 The midi data byte 1
     * @param data2 The midi data byte 2
     */
    void sendMidiEvent (int status, int data1, int data2);


    /**
     * Flush all displays and grids.
     */
    void flush ();


    /**
     * Overwrite for shutdown cleanups.
     */
    void shutdown ();


    /**
     * Print a message to the console.
     *
     * @param message The message to print
     */
    void println (String message);


    /**
     * Print an error message to the console.
     *
     * @param message The message to print
     */
    void errorln (String message);
}
