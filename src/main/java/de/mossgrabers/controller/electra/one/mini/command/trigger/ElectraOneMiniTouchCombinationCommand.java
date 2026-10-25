// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.mini.command.trigger;

import de.mossgrabers.controller.electra.one.mini.ElectraOneMiniConfiguration;
import de.mossgrabers.controller.electra.one.mini.controller.ElectraOneMiniControlSurface;
import de.mossgrabers.framework.command.trigger.FootswitchCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for assignable knob touch combination functions.
 *
 * @author Jürgen Moßgraber
 */
public class ElectraOneMiniTouchCombinationCommand extends FootswitchCommand<ElectraOneMiniControlSurface, ElectraOneMiniConfiguration>
{
    /**
     * Constructor.
     *
     * @param index The index of the assignable button
     * @param model The model
     * @param surface The surface
     */
    public ElectraOneMiniTouchCombinationCommand (final int index, final IModel model, final ElectraOneMiniControlSurface surface)
    {
        super (model, surface, index);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        final ElectraOneMiniConfiguration configuration = this.surface.getConfiguration ();

        switch (this.surface.getConfiguration ().getAssignable (this.index))
        {
            case ElectraOneMiniConfiguration.ELECTRA_ONE_FUNC_OFF:
                // Nothing to do
                break;

            case ElectraOneMiniConfiguration.ELECTRA_ONE_FUNC_MODE_MIXER:
                if (event == ButtonEvent.DOWN)
                    this.surface.selectPage (0);
                break;

            case ElectraOneMiniConfiguration.ELECTRA_ONE_FUNC_MODE_DEVICE:
                if (event == ButtonEvent.DOWN)
                    this.surface.selectPage (1);
                break;

            case ElectraOneMiniConfiguration.ELECTRA_ONE_FUNC_MODE_TRANSPORT:
                if (event == ButtonEvent.DOWN)
                    this.surface.selectPage (2);
                break;

            case ElectraOneMiniConfiguration.ELECTRA_ONE_FUNC_MODE_ACTION:
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
