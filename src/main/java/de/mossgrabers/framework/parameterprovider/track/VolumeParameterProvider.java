// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider.track;

import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.parameter.IParameter;


/**
 * Get a number of parameters. This implementation provides all volume parameters of the channels of
 * the track bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class VolumeParameterProvider extends AbstractTrackParameterProvider
{
    /**
     * Constructor.
     *
     * @param bank The bank from which to get the parameters
     */
    public VolumeParameterProvider (final ITrackBank bank)
    {
        super (bank);
    }


    /**
     * Constructor.
     *
     * @param model Uses the current track bank from this model to get the parameters
     */
    public VolumeParameterProvider (final IModel model)
    {
        super (model);
    }


    /** {@inheritDoc} */
    @Override
    public IParameter get (final int index)
    {
        return this.bank.getItem (index).getVolumeParameter ();
    }
}
