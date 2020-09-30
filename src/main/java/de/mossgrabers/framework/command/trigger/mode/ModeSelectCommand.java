// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.mode;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Select a mode.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ModeSelectCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private final Modes   modeId;
    private final boolean toggle;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param modeId The ID of the mode to select
     */
    public ModeSelectCommand (final IModel model, final S surface, final Modes modeId)
    {
        this (model, surface, modeId, false);
    }


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param modeId The ID of the mode to select
     * @param toggle Activates the previous mode if the mode is already active and this flag is set
     *            to true
     */
    public ModeSelectCommand (final IModel model, final S surface, final Modes modeId, final boolean toggle)
    {
        super (model, surface);
        this.modeId = modeId;
        this.toggle = toggle;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActiveOrTempMode (this.modeId))
        {
            if (!this.toggle)
                return;
            modeManager.restoreMode ();
        }
        else
            modeManager.setActiveMode (this.modeId);
        this.displayMode (modeManager);
    }


    /**
     * Display the modes' name.
     *
     * @param modeManager The mode manager
     */
    protected void displayMode (final ModeManager modeManager)
    {
        this.model.getHost ().showNotification (modeManager.getActiveOrTempMode ().getName ());
    }
}
