// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.mode;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to delegate the button pushes of a button row to the active mode.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ButtonRowModeCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private final int         index;
    private final int         row;
    private final ModeManager modeManager;


    /**
     * Constructor.
     *
     * @param row The number of the button row
     * @param index The index of the button
     * @param model The model
     * @param surface The surface
     */
    public ButtonRowModeCommand (final int row, final int index, final IModel model, final S surface)
    {
        this (surface.getModeManager (), row, index, model, surface);
    }


    /**
     * Constructor.
     *
     * @param modeManager The mode manager
     * @param row The number of the button row
     * @param index The index of the button
     * @param model The model
     * @param surface The surface
     */
    public ButtonRowModeCommand (final ModeManager modeManager, final int row, final int index, final IModel model, final S surface)
    {
        super (model, surface);

        this.row = row;
        this.index = index;
        this.modeManager = modeManager;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        final IMode m = this.modeManager.getActive ();
        if (m != null)
            m.onButton (this.row, this.index, event);
    }
}
