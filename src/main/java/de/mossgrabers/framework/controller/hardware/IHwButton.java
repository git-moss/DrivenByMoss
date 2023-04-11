// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.hardware;

import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Interface for a proxy to a button on a hardware controller.
 *
 * @author Jürgen Moßgraber
 */
public interface IHwButton extends IHwInputControl
{
    /**
     * Assign a command to a button, which is triggered by the button.
     *
     * @param command The command to assign
     */
    void bind (TriggerCommand command);


    /**
     * Bind a MIDI command coming from a MIDI input to the button.
     *
     * @param input The MIDI input
     * @param type How to bind
     * @param channel The MIDI channel
     * @param control The MIDI CC or note to bind
     * @param value The specific value of the control to bind to
     */
    void bind (IMidiInput input, BindType type, int channel, int control, int value);


    /**
     * Get the trigger command,
     *
     * @return The command or null if not bound
     */
    TriggerCommand getCommand ();


    /**
     * Add a light / LED to the button.
     *
     * @param light The light to assign
     */
    void addLight (IHwLight light);


    /**
     * Get the light, if any.
     *
     * @return The light or null
     */
    IHwLight getLight ();


    /**
     * Test if the button is in pressed state.
     *
     * @return True if pressed (or long pressed)
     */
    boolean isPressed ();


    /**
     * Test if the button is in long pressed state.
     *
     * @return True if long pressed
     */
    boolean isLongPressed ();


    /**
     * Manually trigger a button press and release.
     */
    void trigger ();


    /**
     * Manually trigger a button event.
     *
     * @param event The button event
     */
    void trigger (ButtonEvent event);


    /**
     * Manually triggers a button press and release.
     *
     * @param event The button event
     * @param velocity The press velocity
     */
    void trigger (final ButtonEvent event, final double velocity);


    /**
     * Set the consumed state, which means the UP event is not fired on button release.
     */
    void setConsumed ();


    /**
     * Test if the consumed state is set.
     *
     * @return True if set
     */
    boolean isConsumed ();


    /**
     * Resets the buttons state.
     */
    void clearState ();


    /**
     * Get the velocity of the last (dynamic) button press.
     *
     * @return The velocity
     */
    int getPressedVelocity ();


    /**
     * Register a button event handler.
     *
     * @param event The event type for which to register the handler
     * @param eventHandler The event handler
     */
    void addEventHandler (ButtonEvent event, ButtonEventHandler eventHandler);


    /**
     * Unregister a button event handler.
     *
     * @param event The event type from which to unregister the handler
     * @param eventHandler The event handler
     */
    void removeEventHandler (ButtonEvent event, ButtonEventHandler eventHandler);
}
