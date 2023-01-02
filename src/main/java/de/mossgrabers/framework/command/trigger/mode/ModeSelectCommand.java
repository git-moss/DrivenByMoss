// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.mode;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ModeManager;
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
    protected final ModeManager modeManager;
    protected final Modes       modeId;
    protected final boolean     toggle;


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
     * @param modeManager The mode manager to use
     * @param model The model
     * @param surface The surface
     * @param modeId The ID of the mode to select
     */
    public ModeSelectCommand (final ModeManager modeManager, final IModel model, final S surface, final Modes modeId)
    {
        this (modeManager, model, surface, modeId, false);
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
        this (null, model, surface, modeId, toggle);
    }


    /**
     * Constructor.
     *
     * @param modeManager The mode manager to use, uses the default mode manager if null
     * @param model The model
     * @param surface The surface
     * @param modeId The ID of the mode to select
     * @param toggle Activates the previous mode if the mode is already active and this flag is set
     *            to true
     */
    public ModeSelectCommand (final ModeManager modeManager, final IModel model, final S surface, final Modes modeId, final boolean toggle)
    {
        super (model, surface);

        this.modeManager = modeManager == null ? surface.getModeManager () : modeManager;
        this.modeId = modeId;
        this.toggle = toggle;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        if (this.modeManager.isActive (this.modeId))
        {
            if (!this.toggle)
                return;
            this.modeManager.restore ();
        }
        else
            this.modeManager.setActive (this.modeId);
        this.displayMode ();
    }


    /**
     * Display the modes' name.
     */
    protected void displayMode ()
    {
        this.model.getHost ().showNotification (this.modeManager.getActive ().getName ());
    }
}
