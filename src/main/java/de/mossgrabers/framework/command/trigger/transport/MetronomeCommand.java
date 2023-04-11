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
 * Command to disable/enable the metronome. Also toggles metronome ticks when Shift is pressed.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class MetronomeCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private final boolean tapWithShift;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param tapWithShift Shift funtionality is changed to tap tempo instead of toggle metronome
     *            ticks if true
     */
    public MetronomeCommand (final IModel model, final S surface, final boolean tapWithShift)
    {
        super (model, surface);

        this.tapWithShift = tapWithShift;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.model.getTransport ().toggleMetronome ();
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        if (this.tapWithShift)
            this.model.getTransport ().tapTempo ();
        else
            this.model.getTransport ().toggleMetronomeTicks ();
    }
}
