// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.continuous;

import de.mossgrabers.framework.command.core.AbstractContinuousCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.GrooveParameterID;
import de.mossgrabers.framework.daw.IGroove;
import de.mossgrabers.framework.daw.IModel;


/**
 * Command to change the swing.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SwingCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractContinuousCommand<S, C>
{
    protected final IGroove groove;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public SwingCommand (final IModel model, final S surface)
    {
        super (model, surface);

        this.groove = this.model.getGroove ();
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        this.groove.getParameter (GrooveParameterID.SHUFFLE_AMOUNT).changeValue (value);
    }
}
