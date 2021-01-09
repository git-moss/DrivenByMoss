// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode.track;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.parameterprovider.SendParameterProvider;

import java.util.List;
import java.util.function.BooleanSupplier;


/**
 * Mode for editing Send volumes. The knobs control the volumes of the given send of the tracks on
 * the selected track page or the sends of the selected track if the sendIndex is negative.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendMode<S extends IControlSurface<C>, C extends Configuration> extends AbstractTrackMode<S, C>
{
    protected int sendIndex;


    /**
     * Constructor.
     *
     * @param sendIndex The send index, if negative the sends of the selected track are edited
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happending with a setter otherwise relative
     *            change method is used
     */
    public SendMode (final int sendIndex, final S surface, final IModel model, final boolean isAbsolute)
    {
        this (sendIndex, surface, model, isAbsolute, null);
    }


    /**
     * Constructor.
     *
     * @param sendIndex The send index
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happending with a setter otherwise relative
     *            change method is used
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public SendMode (final int sendIndex, final S surface, final IModel model, final boolean isAbsolute, final List<ContinuousID> controls)
    {
        this (sendIndex, surface, model, isAbsolute, controls, surface::isShiftPressed);
    }


    /**
     * Constructor.
     *
     * @param sendIndex The send index
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happending with a setter otherwise relative
     *            change method is used
     * @param controls The IDs of the knobs or faders to control this mode
     * @param isAlternativeFunction Callback function to execute the secondary function, e.g. a
     *            shift button
     */
    public SendMode (final int sendIndex, final S surface, final IModel model, final boolean isAbsolute, final List<ContinuousID> controls, final BooleanSupplier isAlternativeFunction)
    {
        super ("Send", surface, model, isAbsolute, controls, isAlternativeFunction);

        this.sendIndex = sendIndex;

        if (controls != null)
            this.setParameters (new SendParameterProvider (model, sendIndex));
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final ITrack track = this.getTrack (index);
        if (track == null)
            return;
        final ISend item = track.getSendBank ().getItem (this.sendIndex);
        if (this.isAbsolute)
            item.setValue (value);
        else
            item.changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        final ITrack track = this.getTrack (index);
        if (track == null)
            return;
        final ISend item = track.getSendBank ().getItem (this.sendIndex);
        if (!item.doesExist ())
            return;

        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);
            item.resetValue ();
        }
        item.touchValue (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        final ITrack track = this.getTrack (index);
        return track == null ? -1 : track.getSendBank ().getItem (this.sendIndex).getValue ();
    }
}