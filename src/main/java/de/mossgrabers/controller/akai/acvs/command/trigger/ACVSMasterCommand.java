// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.acvs.command.trigger;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Optional;


/**
 * Selects the master track. Selects the previous track if executed again.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ACVSMasterCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private int position = 0;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public ACVSMasterCommand (final IModel model, final S surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final IMasterTrack masterTrack = this.model.getMasterTrack ();
        final ITrackBank trackBank = this.model.getTrackBank ();

        if (masterTrack.isSelected ())
            trackBank.selectItemAtPosition (this.position);
        else
        {
            final Optional<ITrack> selectedTrack = trackBank.getSelectedItem ();
            this.position = selectedTrack.isPresent () ? selectedTrack.get ().getPosition () : 0;
            masterTrack.select ();
        }
    }
}
