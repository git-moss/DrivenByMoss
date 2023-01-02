// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.command.trigger;

import de.mossgrabers.controller.ni.maschine.mk3.MaschineConfiguration;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeSelectCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for the Select button.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MaschineSelectButtonCommand extends AbstractTriggerCommand<MaschineControlSurface, MaschineConfiguration>
{
    final ModeSelectCommand<MaschineControlSurface, MaschineConfiguration> modeSelectCommand;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public MaschineSelectButtonCommand (final IModel model, final MaschineControlSurface surface)
    {
        super (model, surface);

        this.modeSelectCommand = new ModeSelectCommand<> (this.model, this.surface, Modes.NOTE, true);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        this.surface.getViewManager ().getActive ().updateNoteMapping ();
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        this.modeSelectCommand.executeNormal (event);
    }
}
