// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.daw.data.MarkerImpl;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IMarkerBank;
import de.mossgrabers.framework.daw.data.IMarker;

import com.bitwig.extension.controller.api.CueMarkerBank;


/**
 * A marker bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MarkerBankImpl extends AbstractBankImpl<CueMarkerBank, IMarker> implements IMarkerBank
{
    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param bank The Bitwig bank to encapsulate
     * @param numMarkers The number of tracks of a bank page
     */
    public MarkerBankImpl (final IHost host, final IValueChanger valueChanger, final CueMarkerBank bank, final int numMarkers)
    {
        super (host, valueChanger, bank, numMarkers);
        this.initItems ();
    }


    /** {@inheritDoc} */
    @Override
    public void addMarker ()
    {
        // TODO API extension required - https://github.com/teotigraphix/Framework4Bitwig/issues/215
    }


    /** {@inheritDoc} */
    @Override
    protected void initItems ()
    {
        for (int i = 0; i < this.pageSize; i++)
            this.items.add (new MarkerImpl (this.bank.getItemAt (i), i));
    }
}
