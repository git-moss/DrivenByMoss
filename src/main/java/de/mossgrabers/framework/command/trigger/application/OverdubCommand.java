// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.application;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for toggling the arranger and launcher Overdub.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OverdubCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private final ITransport transport;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public OverdubCommand (final IModel model, final S surface)
    {
        super (model, surface);

        this.transport = this.model.getTransport ();
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        if (this.surface.isSelectPressed ())
            this.transport.toggleWriteClipLauncherAutomation ();
        else
            this.transport.toggleOverdub ();
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.shiftedFunction ();
    }


    /**
     * Hook to overwrite the shifted function.
     */
    protected void shiftedFunction ()
    {
        this.transport.toggleLauncherOverdub ();
    }


    /**
     * Returns true if overdub is on (depending on shift state).
     *
     * @return True if enabled
     */
    public boolean isActive ()
    {
        if (this.surface.isSelectPressed ())
            return this.transport.isWritingClipLauncherAutomation ();
        return this.surface.isShiftPressed () ? this.transport.isLauncherOverdub () : this.transport.isArrangerOverdub ();
    }
}
