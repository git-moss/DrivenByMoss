// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.clip;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;


/**
 * Command to create a new clip on the current track, start it and activate overdub. The length of
 * the new clip is given as a parameter.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class TemporaryNewCommand<S extends IControlSurface<C>, C extends Configuration> extends NewCommand<S, C>
{
    private final int clipLength;


    /**
     * Constructor.
     *
     * @param clipLength The length of the new clip
     * @param model The model
     * @param surface The surface
     */
    public TemporaryNewCommand (final int clipLength, final IModel model, final S surface)
    {
        super (model, surface);
        this.clipLength = clipLength;
    }


    /** {@inheritDoc} */
    @Override
    protected int getClipLength ()
    {
        final double quartersPerMeasure = this.model.getTransport ().getQuartersPerMeasure ();
        return (int) (this.clipLength < 2 ? Math.pow (2, this.clipLength) : Math.pow (2, this.clipLength - 2.0) * quartersPerMeasure);
    }
}
