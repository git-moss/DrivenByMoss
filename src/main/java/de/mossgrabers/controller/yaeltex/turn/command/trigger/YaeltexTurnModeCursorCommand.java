// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.yaeltex.turn.command.trigger;

import de.mossgrabers.controller.yaeltex.turn.YaeltexTurnConfiguration;
import de.mossgrabers.controller.yaeltex.turn.controller.YaeltexTurnControlSurface;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.command.trigger.mode.ModeCursorCommand;
import de.mossgrabers.framework.command.trigger.transport.WindCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for navigating mode pages and items. Scrolls the play cursor when used with Shift.
 *
 * @author Jürgen Moßgraber
 */
public class YaeltexTurnModeCursorCommand extends ModeCursorCommand<YaeltexTurnControlSurface, YaeltexTurnConfiguration>
{
    private final WindCommand<YaeltexTurnControlSurface, YaeltexTurnConfiguration> rewindCommand;
    private final WindCommand<YaeltexTurnControlSurface, YaeltexTurnConfiguration> forwardCommand;


    /**
     * Constructor.
     *
     * @param direction The direction of the pushed cursor arrow
     * @param model The model
     * @param surface The surface
     */
    public YaeltexTurnModeCursorCommand (final Direction direction, final IModel model, final YaeltexTurnControlSurface surface)
    {
        super (direction, model, surface, false);

        this.rewindCommand = new WindCommand<> (this.model, surface, false);
        this.forwardCommand = new WindCommand<> (this.model, surface, true);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (this.surface.isSelectPressed ())
        {
            if (this.direction == Direction.LEFT)
            {
                this.rewindCommand.execute (event, velocity);
                return;
            }
            if (this.direction == Direction.RIGHT)
            {
                this.forwardCommand.execute (event, velocity);
                return;
            }
        }

        super.execute (event, velocity);
    }
}
