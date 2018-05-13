// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.data.IDrumPad;

import com.bitwig.extension.controller.api.DrumPad;


/**
 * The data of a channel.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumPadImpl extends ChannelImpl implements IDrumPad
{
    private DrumPad drumPad;


    /**
     * Constructor.
     *
     * @param drumPad The drum pad
     * @param valueChanger The valueChanger
     * @param index The index of the channel in the page
     * @param numSends The number of sends of a bank
     */
    public DrumPadImpl (final DrumPad drumPad, final IValueChanger valueChanger, final int index, final int numSends)
    {
        super (drumPad, valueChanger, index, numSends);
        this.drumPad = drumPad;
    }


    /** {@inheritDoc} */
    @Override
    public void browseToInsert ()
    {
        this.drumPad.browseToInsertAtEndOfChain ();
    }
}
