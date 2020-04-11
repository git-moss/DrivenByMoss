// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode.track;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.AbstractMode;


/**
 * Base mode for track related modes.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class AbstractTrackMode<S extends IControlSurface<C>, C extends Configuration> extends AbstractMode<S, C>
{
    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happending with a setter otherwise relative
     *            change method is used
     */
    public AbstractTrackMode (final String name, final S surface, final IModel model, final boolean isAbsolute)
    {
        super (name, surface, model, isAbsolute, model.getCurrentTrackBank (), null, 0);

        model.addTrackBankObserver (this::switchBanks);

        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public String getSelectedItemName ()
    {
        final ITrack selectedItem = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (selectedItem == null || !selectedItem.doesExist ())
            return null;
        return selectedItem.getPosition () + 1 + ": " + selectedItem.getName ();
    }
}