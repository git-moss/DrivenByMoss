// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode.track;

import de.mossgrabers.framework.command.trigger.clip.TemporaryNewCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;


/**
 * Base mode for track related modes.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class DefaultTrackMode<S extends IControlSurface<C>, C extends Configuration> extends AbstractParameterMode<S, C, ITrack>
{
    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happening with a setter otherwise relative
     *            change method is used
     */
    protected DefaultTrackMode (final String name, final S surface, final IModel model, final boolean isAbsolute)
    {
        this (name, surface, model, isAbsolute, null);
    }


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happening with a setter otherwise relative
     *            change method is used
     * @param controls The IDs of the knobs or faders to control this mode
     */
    protected DefaultTrackMode (final String name, final S surface, final IModel model, final boolean isAbsolute, final List<ContinuousID> controls)
    {
        this (name, surface, model, isAbsolute, controls, surface::isShiftPressed);
    }


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happening with a setter otherwise relative
     *            change method is used
     * @param controls The IDs of the knobs or faders to control this mode
     * @param isAlternativeFunction Callback function to execute the secondary function, e.g. a
     *            shift button
     */
    protected DefaultTrackMode (final String name, final S surface, final IModel model, final boolean isAbsolute, final List<ContinuousID> controls, final BooleanSupplier isAlternativeFunction)
    {
        super (name, surface, model, isAbsolute, model.getCurrentTrackBank (), controls, isAlternativeFunction);

        model.addTrackBankObserver (this::switchBanks);
    }


    /** {@inheritDoc} */
    @Override
    public Optional<String> getSelectedItemName ()
    {
        final Optional<ITrack> selectedItem = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (selectedItem.isEmpty ())
            return Optional.empty ();
        final ITrack track = selectedItem.get ();
        return track.doesExist () ? Optional.of (track.getPosition () + 1 + ": " + track.getName ()) : Optional.empty ();
    }


    /**
     * Get the track for which to change the volume.
     *
     * @param index The index of the track. If set to -1 the selected track is used.
     * @return The selected track
     */
    protected Optional<ITrack> getTrack (final int index)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        return index < 0 ? tb.getSelectedItem () : Optional.of (tb.getItem (index));
    }


    /**
     * Test for button combinations like Delete, Duplicate and New.
     *
     * @param track The track to apply the button combinations
     * @return True if a button combination was detected and the applied method was executed
     */
    protected boolean isButtonCombination (final ITrack track)
    {
        if (this.isButtonCombination (ButtonID.DELETE))
        {
            track.remove ();
            return true;
        }

        if (this.isButtonCombination (ButtonID.DUPLICATE))
        {
            track.duplicate ();
            return true;
        }

        if (this.isButtonCombination (ButtonID.NEW))
        {
            new TemporaryNewCommand<> (track.getIndex (), this.model, this.surface).execute ();
            return true;
        }

        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN || row != 0)
            return;
        final Optional<ITrack> trackOpt = this.getTrack (index);
        if (trackOpt.isEmpty ())
            return;

        final ITrack track = trackOpt.get ();
        if (!this.isButtonCombination (track))
            this.executeMethod (track);
    }


    /**
     * Execute the button row 1 method.
     *
     * @param track The track for which to execute the method
     */
    protected void executeMethod (final ITrack track)
    {
        track.selectOrExpandGroup ();
    }
}