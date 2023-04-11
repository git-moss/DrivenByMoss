// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.hardware;

import de.mossgrabers.framework.command.core.ContinuousCommand;
import de.mossgrabers.framework.command.core.PitchbendCommand;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.parameter.IParameter;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;


/**
 * A control on a controller surface which sends continuous values.
 *
 * @author Jürgen Moßgraber
 */
public interface IHwContinuousControl extends IHwInputControl
{
    /**
     * Assign a command to the control.
     *
     * @param command The command to assign
     */
    void bind (ContinuousCommand command);


    /**
     * Assign a pitchbend command to the control.
     *
     * @param command The command to assign
     */
    void bind (PitchbendCommand command);


    /**
     * Directly bind a parameter to a continuous control.
     *
     * @param parameter The parameter to bind
     */
    void bind (IParameter parameter);


    /**
     * Bind a command which is executed when the control (knob, fader) is touched.
     *
     * @param command The command to bind to touch
     * @param input The MIDI input
     * @param type How to bind
     * @param channel The MIDI channel
     * @param control The MIDI CC or note to bind
     */
    void bindTouch (TriggerCommand command, IMidiInput input, BindType type, int channel, int control);


    /**
     * Trigger touching the knob.
     *
     * @param isDown True if down otherwise up
     */
    void triggerTouch (final boolean isDown);


    /**
     * Test if the control is touched.
     *
     * @return True if touched
     */
    boolean isTouched ();


    /**
     * Test if the control is long touched.
     *
     * @return True if long touched
     */
    boolean isLongTouched ();


    /**
     * Get the touch trigger command, if any.
     *
     * @return The command or null if not bound
     */
    TriggerCommand getTouchCommand ();


    /**
     * Get the continuous command.
     *
     * @return The command or null if not bound
     */
    ContinuousCommand getCommand ();


    /**
     * Get the pitchbend command.
     *
     * @return The command or null if not bound
     */
    PitchbendCommand getPitchbendCommand ();


    /**
     * Handle a value update. Only for internal updates.
     *
     * @param value The new value
     */
    void handleValue (double value);


    /**
     * Add an output which represents the value of the knob, e.g. an LED ring.
     *
     * @param supplier The supplier of the value
     * @param consumer Send the value to the hardware
     */
    void addOutput (IntSupplier supplier, IntConsumer consumer);


    /**
     * Clear the output cache state.
     */
    void forceFlush ();


    /**
     * Switch off the output.
     */
    void turnOff ();


    /**
     * If this control is part of group of related controls then this specifies the index in that
     * group.
     *
     * @param index The index
     */
    void setIndexInGroup (int index);
}
