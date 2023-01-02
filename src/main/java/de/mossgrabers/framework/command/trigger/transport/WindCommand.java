// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.transport;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command handle the rewind and fast forward buttons.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class WindCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    protected final boolean isFastForwarding;

    private boolean         isRewinding;
    private boolean         isForwarding;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param isFastForward If true the command executes fast forwarding otherwise rewinding
     */
    public WindCommand (final IModel model, final S surface, final boolean isFastForward)
    {
        super (model, surface);
        this.isFastForwarding = isFastForward;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (this.isFastForwarding)
            this.onForward (event, false);
        else
            this.onRewind (event, false);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (this.isFastForwarding)
            this.onForward (event, true);
        else
            this.onRewind (event, true);
    }


    private void onRewind (final ButtonEvent event, final boolean slow)
    {
        if (event == ButtonEvent.DOWN)
            this.isRewinding = true;
        else if (event == ButtonEvent.UP)
            this.isRewinding = false;
        this.doChangePosition (slow);
    }


    private void onForward (final ButtonEvent event, final boolean slow)
    {
        if (event == ButtonEvent.DOWN)
            this.isForwarding = true;
        else if (event == ButtonEvent.UP)
            this.isForwarding = false;
        this.doChangePosition (slow);
    }


    private void doChangePosition (final boolean slow)
    {
        if (!this.isRewinding && !this.isForwarding)
            return;

        this.model.getTransport ().changePosition (this.isForwarding, slow);
        this.surface.scheduleTask ( () -> this.doChangePosition (slow), 100);
    }


    /**
     * Returns true if currently rewinding.
     *
     * @return True if currently rewinding
     */
    public boolean isRewinding ()
    {
        return this.isRewinding;
    }


    /**
     * Returns true if currently forwarding.
     *
     * @return True if currently forwarding
     */
    public boolean isForwarding ()
    {
        return this.isForwarding;
    }
}
