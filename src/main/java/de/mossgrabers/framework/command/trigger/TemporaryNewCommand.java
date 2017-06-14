// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger;

import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ControlSurface;


/**
 * Command to create a new clip on the current track, start it and activate overdub. The length of
 * the new clip is given as a parameter.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TemporaryNewCommand<S extends ControlSurface<C>, C extends Configuration> extends NewCommand<S, C>
{
    private int clipLength;


    /**
     * Constructor.
     * 
     * @param clipLength The length of the new clip
     * @param model The model
     * @param surface The surface
     */
    public TemporaryNewCommand (final int clipLength, final Model model, final S surface)
    {
        super (model, surface);
        this.clipLength = clipLength;
    }


    @Override
    protected int getClipLength ()
    {
        return this.clipLength;
    }
}
