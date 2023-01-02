// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.continuous;

import de.mossgrabers.framework.command.core.AbstractContinuousCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;


/**
 * Command to change the length of the arranger loop.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LoopLengthCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractContinuousCommand<S, C>
{
    protected final ITransport transport;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public LoopLengthCommand (final IModel model, final S surface)
    {
        super (model, surface);

        this.transport = this.model.getTransport ();
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        this.transport.changeLoopLength (this.model.getValueChanger ().isIncrease (value), this.surface.isKnobSensitivitySlow ());
    }
}
