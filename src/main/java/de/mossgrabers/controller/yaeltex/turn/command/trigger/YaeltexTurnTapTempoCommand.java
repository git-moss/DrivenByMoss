// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.yaeltex.turn.command.trigger;

import de.mossgrabers.controller.yaeltex.turn.YaeltexTurnConfiguration;
import de.mossgrabers.controller.yaeltex.turn.controller.YaeltexTurnControlSurface;
import de.mossgrabers.framework.command.trigger.transport.TapTempoCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to tap the tempo. Additionally, execute clip quantize with Select button.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class YaeltexTurnTapTempoCommand extends TapTempoCommand<YaeltexTurnControlSurface, YaeltexTurnConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public YaeltexTurnTapTempoCommand (final IModel model, final YaeltexTurnControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (this.surface.isSelectPressed ())
            this.model.getCursorClip ().quantize (1.0);
        else
            super.execute (event, velocity);
    }
}
