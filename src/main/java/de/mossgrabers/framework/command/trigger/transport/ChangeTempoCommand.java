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
 * Command to de- or increase the tempo. The assigned button can be keeped pressed.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ChangeTempoCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private final boolean isPlus;
    private boolean       isTempoChange;


    /**
     * Constructor.
     *
     * @param isPlus True if nudge positive
     * @param model The model
     * @param surface The surface
     */
    public ChangeTempoCommand (final boolean isPlus, final IModel model, final S surface)
    {
        super (model, surface);
        this.isPlus = isPlus;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event == ButtonEvent.DOWN)
            this.isTempoChange = true;
        else if (event == ButtonEvent.UP)
            this.isTempoChange = false;
        this.doChangeTempo ();
    }


    private void doChangeTempo ()
    {
        if (!this.isTempoChange)
            return;
        this.model.getTransport ().changeTempo (this.isPlus, this.surface.isKnobSensitivitySlow ());
        this.surface.scheduleTask (this::doChangeTempo, 200);
    }
}
