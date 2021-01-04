// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.flexihandler;

import de.mossgrabers.controller.generic.GenericFlexiConfiguration;
import de.mossgrabers.controller.generic.command.trigger.ToggleKnobSpeedCommand;
import de.mossgrabers.controller.generic.controller.FlexiCommand;
import de.mossgrabers.controller.generic.controller.GenericFlexiControlSurface;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * The handler for global commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GlobalHandler extends AbstractHandler
{
    private final ToggleKnobSpeedCommand<GenericFlexiControlSurface, GenericFlexiConfiguration> knobSpeedCommand;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param configuration The configuration
     * @param relative2ValueChanger The relative value changer variant 2
     * @param relative3ValueChanger The relative value changer variant 3
     */
    public GlobalHandler (final IModel model, final GenericFlexiControlSurface surface, final GenericFlexiConfiguration configuration, final IValueChanger relative2ValueChanger, final IValueChanger relative3ValueChanger)
    {
        super (model, surface, configuration, relative2ValueChanger, relative3ValueChanger);

        this.knobSpeedCommand = new ToggleKnobSpeedCommand<> (this.model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public FlexiCommand [] getSupportedCommands ()
    {
        return new FlexiCommand []
        {
            FlexiCommand.GLOBAL_UNDO,
            FlexiCommand.GLOBAL_REDO,
            FlexiCommand.GLOBAL_PREVIOUS_PROJECT,
            FlexiCommand.GLOBAL_NEXT_PROJECT,
            FlexiCommand.GLOBAL_TOGGLE_AUDIO_ENGINE,
            FlexiCommand.GLOBAL_SHIFT_BUTTON
        };
    }


    /** {@inheritDoc} */
    @Override
    public int getCommandValue (final FlexiCommand command)
    {
        switch (command)
        {
            case GLOBAL_TOGGLE_AUDIO_ENGINE:
                return this.model.getApplication ().isEngineActive () ? 127 : 0;

            case GLOBAL_SHIFT_BUTTON:
                return this.surface.isShiftPressed () ? 127 : 0;

            default:
                return -1;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void handle (final FlexiCommand command, final int knobMode, final int value)
    {
        final boolean isButtonPressed = this.isButtonPressed (knobMode, value);

        switch (command)
        {
            // Global: Undo
            case GLOBAL_UNDO:
                if (isButtonPressed)
                    this.model.getApplication ().undo ();
                break;
            // Global: Redo
            case GLOBAL_REDO:
                if (isButtonPressed)
                    this.model.getApplication ().redo ();
                break;
            // Global: Previous Project
            case GLOBAL_PREVIOUS_PROJECT:
                if (isButtonPressed)
                    this.model.getProject ().previous ();
                break;
            // Global: Next Project
            case GLOBAL_NEXT_PROJECT:
                if (isButtonPressed)
                    this.model.getProject ().next ();
                break;
            // Global: Toggle Audio Engine
            case GLOBAL_TOGGLE_AUDIO_ENGINE:
                if (isButtonPressed)
                    this.model.getApplication ().toggleEngineActive ();
                break;
            // Global: Shift Button
            case GLOBAL_SHIFT_BUTTON:
                this.surface.setShiftPressed (isButtonPressed);
                this.knobSpeedCommand.execute (isButtonPressed ? ButtonEvent.DOWN : ButtonEvent.UP, isButtonPressed ? 127 : 0);
                break;

            default:
                throw new FlexiHandlerException (command);
        }
    }
}
