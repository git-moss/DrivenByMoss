// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.mode.Modes;
import de.mossgrabers.push.view.DrumView;
import de.mossgrabers.push.view.Views;


/**
 * Command to quantize the currently selected clip.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class QuantizeCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public QuantizeCommand (final Model model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        if (event == ButtonEvent.LONG || event == ButtonEvent.DOWN && this.surface.isShiftPressed ())
        {
            modeManager.setActiveMode (Modes.MODE_GROOVE);
            this.surface.setButtonConsumed (PushControlSurface.PUSH_BUTTON_QUANTIZE);
            return;
        }

        if (event == ButtonEvent.UP)
        {
            if (modeManager.getActiveModeId () == Modes.MODE_GROOVE)
            {
                modeManager.restoreMode ();
                return;
            }

            // We can use any cursor clip, e.g. the one of the drum view
            final DrumView view = (DrumView) this.surface.getViewManager ().getView (Views.VIEW_DRUM);
            view.getClip ().quantize (this.surface.getConfiguration ().getQuantizeAmount () / 100.0);
        }
    }
}
