// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode.track;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.AbstractMode;


/**
 * The track mode. The knobs control the volume, the panorama and the sends of the selected track.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackMode<S extends IControlSurface<C>, C extends Configuration> extends AbstractMode<S, C>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happending with a setter otherwise relative
     *            change method is used
     */
    public TrackMode (final S surface, final IModel model, final boolean isAbsolute)
    {
        super ("Track", surface, model, isAbsolute);
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final ITrack track = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (track == null)
            return;
        switch (index)
        {
            case 0:
                if (this.isAbsolute)
                    track.setVolume (value);
                else
                    track.changeVolume (value);
                break;

            case 1:
                if (this.isAbsolute)
                    track.setPan (value);
                else
                    track.changePan (value);
                break;

            default:
                final ISend send = track.getSendBank ().getItem (index - 2);
                if (this.isAbsolute)
                    send.setValue (value);
                else
                    send.changeValue (value);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        if (!isTouched)
            return;
        final ITrack track = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (track == null)
            return;
        switch (index)
        {
            case 0:
                track.resetVolume ();
                break;

            case 1:
                track.resetPan ();
                break;

            default:
                track.getSendBank ().getItem (index - 2).resetValue ();
                break;
        }
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