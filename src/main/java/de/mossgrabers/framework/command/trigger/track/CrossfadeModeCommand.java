// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.track;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to toggle the cross-fade mode of the current track.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CrossfadeModeCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private final int index;


    /**
     * Constructor.
     *
     * @param index The channel index
     * @param model The model
     * @param surface The surface
     */
    public CrossfadeModeCommand (final int index, final IModel model, final S surface)
    {
        super (model, surface);
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final IParameter crossfadeParameter = this.model.getCurrentTrackBank ().getItem (this.index).getCrossfadeParameter ();
        final double value = this.model.getValueChanger ().toNormalizedValue (crossfadeParameter.getValue ()) + 0.5;
        crossfadeParameter.setNormalizedValue (value > 1.1 ? 0 : value);
    }
}
