// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider;

import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;


/**
 * Get a number of parameters. This implementation provides the selected tracks volume, panorama and
 * send parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackParameterProvider extends AbstractChannelParameterProvider
{
    /**
     * Constructor.
     *
     * @param model Uses the current track bank from this model to get the parameters
     */
    public TrackParameterProvider (final IModel model)
    {
        super (model);
    }


    /** {@inheritDoc} */
    @Override
    public IParameter get (final int index)
    {
        final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (selectedTrack == null)
            return EmptyParameter.INSTANCE;
        switch (index)
        {
            case 0:
                return selectedTrack.getVolumeParameter ();
            case 1:
                return selectedTrack.getPanParameter ();
            default:
                final ISendBank sendBank = selectedTrack.getSendBank ();
                if (this.model.isEffectTrackBankActive ())
                    return EmptyParameter.INSTANCE;
                try
                {
                    return sendBank.getItem (index - 2);
                }
                catch (IndexOutOfBoundsException ex)
                {
                    return EmptyParameter.INSTANCE;
                }
        }
    }
}
