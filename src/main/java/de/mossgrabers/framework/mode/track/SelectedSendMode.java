// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode.track;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.AbstractMode;


/**
 * Mode for editing selected Send volumes. All knobs control the send of the selected track with the
 * given index.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SelectedSendMode<S extends IControlSurface<C>, C extends Configuration> extends AbstractMode<S, C>
{
    private int sendIndex;


    /**
     * Constructor.
     *
     * @param sendIndex The send index
     * @param surface The control surface
     * @param model The model
     */
    public SelectedSendMode (final int sendIndex, final S surface, final IModel model)
    {
        super ("Send", surface, model, false);
        this.sendIndex = sendIndex;
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (selectedTrack != null)
            selectedTrack.getSendBank ().getItem (this.sendIndex).changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (selectedTrack == null)
            return;
        final ISend item = selectedTrack.getSendBank ().getItem (this.sendIndex);
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
    protected ISendBank getBank ()
    {
        final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
        return selectedTrack == null ? null : selectedTrack.getSendBank ();
    }
}