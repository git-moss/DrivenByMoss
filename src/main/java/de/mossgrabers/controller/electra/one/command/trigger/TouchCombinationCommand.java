// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.command.trigger;

import de.mossgrabers.controller.electra.one.ElectraOneConfiguration;
import de.mossgrabers.controller.electra.one.controller.ElectraOneControlSurface;
import de.mossgrabers.framework.command.trigger.FootswitchCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for assignable knob touch combination functions.
 *
 * @author Jürgen Moßgraber
 */
public class TouchCombinationCommand extends FootswitchCommand<ElectraOneControlSurface, ElectraOneConfiguration>
{
    /**
     * Constructor.
     *
     * @param index The index of the assignable button
     * @param model The model
     * @param surface The surface
     */
    public TouchCombinationCommand (final int index, final IModel model, final ElectraOneControlSurface surface)
    {
        super (model, surface, index);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        final ElectraOneConfiguration configuration = this.surface.getConfiguration ();

        switch (this.surface.getConfiguration ().getAssignable (this.index))
        {
            case ElectraOneConfiguration.ELECTRA_ONE_FUNC_OFF:
                // Nothing to do
                break;

            case ElectraOneConfiguration.ELECTRA_ONE_FUNC_SHIFT_BUTTON:
                this.surface.updateShift (event == ButtonEvent.DOWN);
                break;

            case ElectraOneConfiguration.ELECTRA_ONE_FUNC_MODE_MIXER:
                if (event == ButtonEvent.DOWN)
                    this.surface.selectPage (0);
                break;

            case ElectraOneConfiguration.ELECTRA_ONE_FUNC_MODE_SENDS:
                if (event == ButtonEvent.DOWN)
                    this.surface.selectPage (1);
                break;

            case ElectraOneConfiguration.ELECTRA_ONE_FUNC_MODE_DEVICE:
                if (event == ButtonEvent.DOWN)
                    this.surface.selectPage (2);
                break;

            case ElectraOneConfiguration.ELECTRA_ONE_FUNC_MODE_EQ:
                if (event == ButtonEvent.DOWN)
                    this.surface.selectPage (3);
                break;

            case ElectraOneConfiguration.ELECTRA_ONE_FUNC_MODE_TRANSPORT:
                if (event == ButtonEvent.DOWN)
                    this.surface.selectPage (4);
                break;

            case ElectraOneConfiguration.ELECTRA_ONE_FUNC_MODE_SESSION:
                if (event == ButtonEvent.DOWN)
                    this.surface.selectPage (5);
                break;

            case ElectraOneConfiguration.ELECTRA_ONE_FUNC_MODE_PARAMS:
                if (event == ButtonEvent.DOWN)
                    this.surface.selectPage (6);
                break;

            case ElectraOneConfiguration.ELECTRA_ONE_FUNC_MODE_SYNTH_PRESET:
                if (event == ButtonEvent.DOWN)
                    this.surface.switchToSpecificDevicePreset ();
                break;

            case ElectraOneConfiguration.ELECTRA_ONE_FUNC_MODE_ACTION:
                if (event != ButtonEvent.DOWN)
                    return;
                final String assignableActionID = configuration.getAssignableAction (this.index);
                if (assignableActionID != null)
                    this.model.getApplication ().invokeAction (assignableActionID);
                break;

            default:
                super.execute (event, velocity);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    protected int getSetting ()
    {
        // Only to be used by base class!
        return this.surface.getConfiguration ().getAssignable (this.index) - 10;
    }
}
