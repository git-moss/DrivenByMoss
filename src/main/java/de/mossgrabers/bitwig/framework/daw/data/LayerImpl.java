// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.resource.ChannelType;

import com.bitwig.extension.controller.api.Channel;


/**
 * The data of a layer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LayerImpl extends ChannelImpl implements ILayer
{
    /**
     * Constructor.
     *
     * @param layer The layer
     * @param valueChanger The valueChanger
     * @param index The index of the channel in the page
     * @param numSends The number of sends of a bank
     */
    public LayerImpl (final Channel layer, final IValueChanger valueChanger, final int index, final int numSends)
    {
        super (layer, valueChanger, index, numSends);
    }


    /** {@inheritDoc} */
    @Override
    public ChannelType getType ()
    {
        return ChannelType.LAYER;
    }
}
