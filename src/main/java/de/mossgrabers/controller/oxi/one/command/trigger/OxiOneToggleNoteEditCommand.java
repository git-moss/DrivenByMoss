// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.command.trigger;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.INoteEditorMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to toggle note edit mode.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneToggleNoteEditCommand extends AbstractTriggerCommand<OxiOneControlSurface, OxiOneConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public OxiOneToggleNoteEditCommand (final IModel model, final OxiOneControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc}} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        // Toggle note mode
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActive (Modes.NOTE))
            modeManager.restore ();
        else
            modeManager.setActive (Modes.NOTE);
        this.surface.getDisplay ().notify ("Edit Notes: " + (modeManager.isActive (Modes.NOTE) ? "On" : "Off"));

        ((INoteEditorMode) modeManager.get (Modes.NOTE)).getNoteEditor ().clearNotes ();
    }


    /** {@inheritDoc}} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        // Toggle Automation mode
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActive (Modes.AUTOMATION))
            modeManager.restore ();
        else
            modeManager.setActive (Modes.AUTOMATION);
    }
}
