// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.clip;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.IClip;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to double the currently selected clip.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class DoubleCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private final boolean onShift;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public DoubleCommand (final IModel model, final S surface)
    {
        this (model, surface, false);
    }


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param onShift If true execute the command in combination with Shift button
     */
    public DoubleCommand (final IModel model, final S surface, final boolean onShift)
    {
        super (model, surface);

        this.onShift = onShift;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN && !this.onShift)
            this.doubleClip ();
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN && this.onShift)
            this.doubleClip ();
    }


    protected void doubleClip ()
    {
        final IClip clip = this.model.getCursorClip ();
        if (clip.doesExist ())
            clip.duplicateContent ();
    }
}
