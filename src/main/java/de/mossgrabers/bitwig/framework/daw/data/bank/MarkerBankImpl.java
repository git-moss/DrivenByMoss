// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data.bank;

import de.mossgrabers.bitwig.framework.daw.TransportImpl;
import de.mossgrabers.bitwig.framework.daw.data.MarkerImpl;
import de.mossgrabers.bitwig.framework.daw.data.Util;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.IMarker;
import de.mossgrabers.framework.daw.data.bank.IMarkerBank;

import com.bitwig.extension.controller.api.Arranger;
import com.bitwig.extension.controller.api.CueMarker;
import com.bitwig.extension.controller.api.CueMarkerBank;


/**
 * A marker bank.
 *
 * @author Jürgen Moßgraber
 */
public class MarkerBankImpl extends AbstractItemBankImpl<CueMarkerBank, IMarker> implements IMarkerBank
{
    private static final int    NUM_LARGE_MARKER_BANK = 100;

    private final ITransport    transport;
    private final CueMarkerBank largeCueMarkerBank;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param bwArranger The Bitwig arranger object
     * @param numMarkers The number of tracks of a bank page
     * @param transport The transport for marker positioning
     */
    public MarkerBankImpl (final IHost host, final IValueChanger valueChanger, final Arranger bwArranger, final int numMarkers, final ITransport transport)
    {
        super (host, valueChanger, bwArranger.createCueMarkerBank (numMarkers), numMarkers);

        this.transport = transport;

        final CueMarkerBank cueMarkerBank = this.bank.get ();
        for (int i = 0; i < this.getPageSize (); i++)
            this.items.add (new MarkerImpl (cueMarkerBank.getItemAt (i), i, this.transport));

        // We need a large bank to emulate previous/next marker selection
        this.largeCueMarkerBank = bwArranger.createCueMarkerBank (NUM_LARGE_MARKER_BANK);
        for (int i = 0; i < NUM_LARGE_MARKER_BANK; i++)
        {
            final CueMarker marker = this.largeCueMarkerBank.getItemAt (i);
            marker.exists ().markInterested ();
            marker.position ().markInterested ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        super.enableObservers (enable);

        for (int i = 0; i < NUM_LARGE_MARKER_BANK; i++)
        {
            final CueMarker marker = this.largeCueMarkerBank.getItemAt (i);
            Util.setIsSubscribed (marker.exists (), enable);
            Util.setIsSubscribed (marker.position (), enable);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void addMarker ()
    {
        ((TransportImpl) this.transport).getTransport ().addCueMarkerAtPlaybackPosition ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        this.findAndSelect (false);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        this.findAndSelect (true);
    }


    private void findAndSelect (final boolean isForward)
    {
        final double playPosition = ((TransportImpl) this.transport).getTransport ().getPosition ().get ();

        double closest = -1;
        double distance = 0;
        for (int i = 0; i < NUM_LARGE_MARKER_BANK; i++)
        {
            final CueMarker marker = this.largeCueMarkerBank.getItemAt (i);
            if (!marker.exists ().get ())
                continue;
            final double pos = marker.position ().get ();
            final double dist = isForward ? pos - playPosition : playPosition - pos;
            if (dist > 0 && (distance == 0 || dist < distance))
            {
                distance = dist;
                closest = pos;
            }
        }

        if (closest >= 0)
            this.transport.setPosition (closest);
    }
}
