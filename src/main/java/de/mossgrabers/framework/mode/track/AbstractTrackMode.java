// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode.track;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.mode.AbstractMode;

import java.util.List;


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
        this (name, surface, model, isAbsolute, null);
    }


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happending with a setter otherwise relative
     *            change method is used
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public AbstractTrackMode (final String name, final S surface, final IModel model, final boolean isAbsolute, final List<ContinuousID> controls)
    {
        super (name, surface, model, isAbsolute, model.getCurrentTrackBank (), controls);

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


    /**
     * Get the track for which to change the volume.
     *
     * @param index The index of the track. If set to -1 the selected track is used.
     * @return The selected track
     */
    protected ITrack getTrack (final int index)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        return index < 0 ? tb.getSelectedItem () : tb.getItem (index);
    }
}