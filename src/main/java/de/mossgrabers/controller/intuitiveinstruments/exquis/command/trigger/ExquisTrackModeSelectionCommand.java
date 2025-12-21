package de.mossgrabers.controller.intuitiveinstruments.exquis.command.trigger;

import de.mossgrabers.controller.intuitiveinstruments.exquis.ExquisConfiguration;
import de.mossgrabers.controller.intuitiveinstruments.exquis.controller.ExquisControlSurface;
import de.mossgrabers.framework.command.trigger.mode.ModeMultiSelectCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for selecting either the track or volume mode.
 *
 * @author Jürgen Moßgraber
 */
public class ExquisTrackModeSelectionCommand extends ModeMultiSelectCommand<ExquisControlSurface, ExquisConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public ExquisTrackModeSelectionCommand (final IModel model, final ExquisControlSurface surface)
    {
        super (model, surface, Modes.TRACK, Modes.VOLUME);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActive (Modes.REPEAT_NOTE))
        {
            if (event == ButtonEvent.DOWN)
                modeManager.get (Modes.REPEAT_NOTE).onKnobTouch (3, true);
            return;
        }

        super.executeNormal (event);
    }
}
