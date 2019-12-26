// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode.track;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;


/**
 * Mode for editing Send volumes. The knobs control the volumes of the given send on the selected
 * track.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendMode<S extends IControlSurface<C>, C extends Configuration> extends AbstractTrackMode<S, C>
{
    private int sendIndex;


    /**
     * Constructor.
     *
     * @param sendIndex The send index
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happending with a setter otherwise relative
     *            change method is used
     */
    public SendMode (final int sendIndex, final S surface, final IModel model, final boolean isAbsolute)
    {
        super ("Send " + (sendIndex + 1), surface, model, isAbsolute);
        this.sendIndex = sendIndex;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final ITrack track = this.model.getCurrentTrackBank ().getItem (index);
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
        final ITrack track = this.model.getCurrentTrackBank ().getItem (index);
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
        final ITrack track = this.model.getCurrentTrackBank ().getItem (index);
        return track == null ? -1 : track.getSendBank ().getItem (this.sendIndex).getValue ();
    }
}