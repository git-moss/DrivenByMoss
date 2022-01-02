// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data.bank;

import de.mossgrabers.bitwig.framework.daw.TransportImpl;
import de.mossgrabers.bitwig.framework.daw.data.MarkerImpl;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.IMarker;
import de.mossgrabers.framework.daw.data.bank.IMarkerBank;

import com.bitwig.extension.controller.api.CueMarkerBank;


/**
 * A marker bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MarkerBankImpl extends AbstractItemBankImpl<CueMarkerBank, IMarker> implements IMarkerBank
{
    private final ITransport transport;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param bank The Bitwig bank to encapsulate
     * @param numMarkers The number of tracks of a bank page
     * @param transport The transport for marker positioning
     */
    public MarkerBankImpl (final IHost host, final IValueChanger valueChanger, final CueMarkerBank bank, final int numMarkers, final ITransport transport)
    {
        super (host, valueChanger, bank, numMarkers);

        this.transport = transport;

        if (this.bank.isEmpty ())
            return;

        final CueMarkerBank cueMarkerBank = this.bank.get ();
        for (int i = 0; i < this.getPageSize (); i++)
            this.items.add (new MarkerImpl (cueMarkerBank.getItemAt (i), i, this.transport));
    }


    /** {@inheritDoc} */
    @Override
    public void addMarker ()
    {
        ((TransportImpl) this.transport).getTransport ().addCueMarkerAtPlaybackPosition ();
    }
}
