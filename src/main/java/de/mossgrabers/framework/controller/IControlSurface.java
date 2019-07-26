// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller;

import de.mossgrabers.framework.command.ContinuousCommandID;
import de.mossgrabers.framework.command.TriggerCommandID;
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
     * Assigns a command to a midi CC on the default midi channel. When the midi CC is received the
     * command is executed.
     *
     * @param cc The midi CC
     * @param commandID The command ID
     */
    void assignTriggerCommand (int cc, TriggerCommandID commandID);


    /**
     * Assigns a command to a midi CC. When the midi CC is received the command is executed.
     * 
     * @param channel The midi channel to assign to (0-15)
     * @param cc The midi CC
     * @param commandID The command ID
     */
    void assignTriggerCommand (int channel, int cc, TriggerCommandID commandID);


    /**
     * Get the ID of an assigned command on the default midi channel.
     *
     * @param cc The midi CC
     * @return The command ID or null if none is assigned to the given midi CC
     */
    TriggerCommandID getTriggerCommand (int cc);


    /**
     * Get the ID of an assigned command.
     * 
     * @param channel The midi channel to which it was assign to (0-15)
     * @param cc The midi CC
     * @return The command ID or null if none is assigned to the given midi CC
     */
    TriggerCommandID getTriggerCommand (int channel, int cc);


    /**
     * Assigns a continuous command to a midi CC on the default midi channel. When the midi CC is
     * received the command is executed.
     *
     * @param cc The midi CC
     * @param commandID The command ID
     */
    void assignContinuousCommand (int cc, ContinuousCommandID commandID);


    /**
     * Assigns a continuous command to a midi CC. When the midi CC is received the command is
     * executed.
     * 
     * @param channel The midi channel to assign to (0-15)
     * @param cc The midi CC
     * @param commandID The command ID
     */
    void assignContinuousCommand (int channel, int cc, ContinuousCommandID commandID);


    /**
     * Get the ID of an assigned continuous command on the default midi channel.
     *
     * @param cc The midi CC
     * @return The command ID or null if none is assigned to the given midi CC
     */
    ContinuousCommandID getContinuousCommand (int cc);


    /**
     * Get the ID of an assigned continuous command.
     * 
     * @param channel The midi channel to which it was assign to (0-15)
     * @param cc The midi CC
     * @return The command ID or null if none is assigned to the given midi CC
     */
    ContinuousCommandID getContinuousCommand (int channel, int cc);


    /**
     * Assigns a note (continuous) command to a midi note on all midi channels. When the midi note
     * is received the command is executed.
     *
     * @param note The midi note
     * @param commandID The command ID
     */
    void assignNoteCommand (final int note, final TriggerCommandID commandID);


    /**
     * Get the ID of an assigned note (continuous) command on all midi channels.
     *
     * @param note The midi note
     * @return The command ID or null if none is assigned to the given midi CC
     */
    TriggerCommandID getNoteCommand (final int note);


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
     * Is the select trigger pressed (if the controller has one).
     *
     * @return The state of the select trigger
     */
    boolean isSelectPressed ();


    /**
     * Is the shift trigger pressed (if the controller has one).
     *
     * @return The state of the shift trigger
     */
    boolean isShiftPressed ();


    /**
     * Is the delete trigger pressed (if the controller has one).
     *
     * @return The state of the delete trigger
     */
    boolean isDeletePressed ();


    /**
     * Is the solo trigger pressed (if the controller has one).
     *
     * @return The state of the solo trigger
     */
    boolean isSoloPressed ();


    /**
     * Is the mute trigger pressed (if the controller has one).
     *
     * @return The state of the mute trigger
     */
    boolean isMutePressed ();


    /**
     * Get the midi cc of the Shift trigger.
     *
     * @return The midi cc
     */
    int getShiftTriggerId ();


    /**
     * Get the midi cc of the Select trigger.
     *
     * @return The midi cc
     */
    int getSelectTriggerId ();


    /**
     * Get the midi cc of the Delete trigger.
     *
     * @return The midi cc
     */
    int getDeleteTriggerId ();


    /**
     * Get the midi cc of the Mute trigger.
     *
     * @return The midi cc
     */
    int getMuteTriggerId ();


    /**
     * Get the midi cc of the Solo trigger.
     *
     * @return The midi cc
     */
    int getSoloTriggerId ();


    /**
     * Get the midi cc of the Left trigger.
     *
     * @return The midi cc
     */
    int getLeftTriggerId ();


    /**
     * Get the midi cc of the Right trigger.
     *
     * @return The midi cc
     */
    int getRightTriggerId ();


    /**
     * Get the midi cc of the Up trigger.
     *
     * @return The midi CC
     */
    int getUpTriggerId ();


    /**
     * Get the midi cc of the Down trigger.
     *
     * @return The midi CC
     */
    int getDownTriggerId ();


    /**
     * Get the midi cc of one of the scene triggers.
     *
     *
     * @param index The index of the scene trigger
     * @return The midi cc
     */
    int getSceneTrigger (final int index);


    /**
     * Test if the trigger with the given midi CC on the default midi channel is pressed.
     *
     * @param cc The trigger to test
     * @return True if pressed
     */
    boolean isPressed (int cc);


    /**
     * Test if the trigger with the given midi CC is pressed.
     *
     * @param channel The midi channel to use
     * @param cc The trigger to test
     * @return True if pressed
     */
    boolean isPressed (int channel, int cc);


    /**
     * Test if the trigger with the given midi CC is long pressed.
     *
     * @param cc The trigger to test
     * @return True if long pressed
     */
    boolean isLongPressed (int cc);


    /**
     * Test if the trigger with the given midi CC is long pressed.
     *
     * @param channel The midi channel to use
     * @param cc The trigger to test
     * @return True if long pressed
     */
    boolean isLongPressed (int channel, int cc);


    /**
     * Sets a trigger as consumed which prevents LONG and UP events following a DOWN event for a
     * trigger.
     *
     * @param cc The trigger to set as consumed
     */
    void setTriggerConsumed (int cc);


    /**
     * Sets a trigger as consumed which prevents LONG and UP events following a DOWN event for a
     * trigger.
     *
     * @param channel The midi channel to use
     * @param cc The trigger to test
     */
    void setTriggerConsumed (int channel, int cc);


    /**
     * Test if the consumed flag is set for a trigger.
     *
     * @param cc The trigger to set as consumed
     * @return The consumed flag
     */
    boolean isTriggerConsumed (int cc);


    /**
     * Test if the consumed flag is set for a trigger.
     *
     * @param channel The midi channel to use
     * @param cc The trigger to set as consumed
     * @return The consumed flag
     */
    boolean isTriggerConsumed (int channel, int cc);


    /**
     * Update the lighting of a trigger (if the trigger has light), sending on the default midi
     * channel. This method caches the state of the trigger and sends only updates to the controller
     * if the state has changed, in contrast to setTrigger.
     *
     * @param cc The trigger
     * @param value The color / brightness depending on the controller
     */
    void updateTrigger (int cc, int value);


    /**
     * Update the lighting of a trigger (if the trigger has light). This method caches the state of
     * the trigger and sends only updates to the controller if the state has changed, in contrast to
     * setTrigger.
     * 
     * @param channel The midi channel to use
     * @param cc The trigger
     * @param value The color / brightness depending on the controller
     */
    void updateTrigger (int channel, int cc, int value);


    /**
     * Update the lighting of a trigger (if the trigger has light), sending on the default midi
     * channel. This method caches the state of the trigger and sends only updates to the controller
     * if the state has changed, in contrast to setTrigger.
     *
     * @param cc The trigger
     * @param colorID A registered color ID of the color / brightness depending on the controller
     */
    void updateTrigger (int cc, String colorID);


    /**
     * Update the lighting of a trigger (if the trigger has light). This method caches the state of
     * the trigger and sends only updates to the controller if the state has changed, in contrast to
     * setTrigger.
     * 
     * @param channel The midi channel to use
     * @param cc The trigger
     * @param colorID A registered color ID of the color / brightness depending on the controller
     */
    void updateTrigger (int channel, int cc, String colorID);


    /**
     * Update the lighting of a trigger (if the trigger has light), sending on the default midi
     * channel.
     *
     * @param cc The trigger
     * @param value The color / brightness depending on the controller
     */
    void setTrigger (int cc, int value);


    /**
     * Update the lighting of a trigger (if the trigger has light), sending on the default midi
     * channel.
     *
     * @param cc The trigger
     * @param colorID A registered color ID of the color / brightness depending on the controller
     */
    void setTrigger (int cc, String colorID);


    /**
     * Update the lighting of a trigger (if the trigger has light).
     * 
     * @param channel The midi channel to use
     * @param cc The trigger
     * @param value The color / brightness depending on the controller
     */
    void setTrigger (int channel, int cc, int value);


    /**
     * Update the lighting of a trigger (if the trigger has light).
     * 
     * @param channel The midi channel to use
     * @param cc The trigger
     * @param colorID A registered color ID of the color / brightness depending on the controller
     */
    void setTrigger (int channel, int cc, String colorID);


    /**
     * Clear the cached lighting state of all triggers.
     */
    void clearTriggerCache ();


    /**
     * Clear the cached lighting state of a trigger of the default MIDI channel.
     *
     * @param cc The trigger
     */
    void clearTriggerCache (int cc);


    /**
     * Clear the cached lighting state of a trigger of the given MIDI channel.
     *
     * @param channel The midi channel
     * @param cc The trigger
     */
    void clearTriggerCache (int channel, int cc);


    /**
     * Clear the cached state of all continuous.
     */
    void clearContinuousCache ();


    /**
     * Clear the cached state of a continuous of the default MIDI channel.
     *
     * @param cc The trigger
     */
    void clearContinuousCache (int cc);


    /**
     * Clear the cached state of a continuous of the given MIDI channel.
     *
     * @param channel The midi channel
     * @param cc The trigger
     */
    void clearContinuousCache (int channel, int cc);


    /**
     * Check if the midi CC on the default midi channel belongs to a trigger.
     *
     * @param cc The CC to check
     * @return True if it belongs to a trigger
     */
    boolean isTrigger (int cc);


    /**
     * Check if the midi CC belongs to a trigger.
     *
     * @param channel The midi channel
     * @param cc The CC to check
     * @return True if it belongs to a trigger
     */
    boolean isTrigger (int channel, int cc);


    /**
     * Turn off all triggers.
     */
    void turnOffTriggers ();


    /**
     * Update the position of a continuous (if the knob/fader e.g. has motors), sending on midi
     * channel 1. This method caches the state of the continuous and sends only updates to the
     * controller if the state has changed, in contrast to setContinuous.
     *
     * @param cc The trigger
     * @param value The position depending on the controller
     */
    void updateContinuous (int cc, int value);


    /**
     * Update the position of a continuous (if the knob/fader e.g. has motors). This method caches
     * the state of the continuous and sends only updates to the controller if the state has
     * changed, in contrast to setContinuous.
     * 
     * @param channel The midi channel to use
     * @param cc The trigger
     * @param value The position depending on the controller
     */
    void updateContinuous (int channel, int cc, int value);


    /**
     * Update the position of a continuous (if the knob/fader e.g. has motors), sending on midi
     * channel 1.
     *
     * @param cc The continuous
     * @param value The position depending on the controller
     */
    void setContinuous (int cc, int value);


    /**
     * Update the position of a continuous (if the knob/fader e.g. has motors).
     * 
     * @param channel The midi channel to use
     * @param cc The continuous
     * @param value The position depending on the controller
     */
    void setContinuous (int channel, int cc, int value);


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
