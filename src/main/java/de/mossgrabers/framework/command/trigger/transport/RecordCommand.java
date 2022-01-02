// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.transport;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to handle the record button.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class RecordCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public RecordCommand (final IModel model, final S surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.handleExecute (false);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.handleExecute (true);
    }


    protected void handleExecute (final boolean isShiftPressed)
    {
        final boolean flipRecord = this.surface.getConfiguration ().isFlipRecord ();
        if (isShiftPressed && !flipRecord || !isShiftPressed && flipRecord)
            this.model.getTransport ().toggleLauncherOverdub ();
        else
            this.model.getTransport ().startRecording ();
    }
}
