// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.AbstractItemImpl;
import de.mossgrabers.framework.daw.data.IMarker;
import de.mossgrabers.framework.observer.IValueObserver;

import com.bitwig.extension.controller.api.ColorValue;
import com.bitwig.extension.controller.api.CueMarker;


/**
 * Encapsulates the data of a marker.
 *
 * @author Jürgen Moßgraber
 */
public class MarkerImpl extends AbstractItemImpl implements IMarker
{
    private final CueMarker  marker;
    private final ITransport transport;


    /**
     * Constructor.
     *
     * @param marker The marker
     * @param index The index of the marker
     * @param transport The transport for marker positioning
     */
    public MarkerImpl (final CueMarker marker, final int index, final ITransport transport)
    {
        super (index);

        this.transport = transport;
        this.marker = marker;

        marker.exists ().markInterested ();
        marker.name ().markInterested ();
        marker.getColor ().markInterested ();
        marker.position ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        Util.setIsSubscribed (this.marker.exists (), enable);
        Util.setIsSubscribed (this.marker.name (), enable);
        Util.setIsSubscribed (this.marker.getColor (), enable);
        Util.setIsSubscribed (this.marker.position (), enable);
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return this.marker.exists ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.marker.name ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName (final int limit)
    {
        return this.marker.name ().getLimited (limit);
    }


    /** {@inheritDoc} */
    @Override
    public void addNameObserver (final IValueObserver<String> observer)
    {
        this.marker.name ().addValueObserver (observer::update);
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColor ()
    {
        final ColorValue color = this.marker.getColor ();
        return new ColorEx (color.red (), color.green (), color.blue ());
    }


    /** {@inheritDoc} */
    @Override
    public void launch (final boolean quantized)
    {
        this.marker.launch (quantized);
    }


    /** {@inheritDoc} */
    @Override
    public void removeMarker ()
    {
        this.marker.deleteObject ();
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        this.transport.setPosition (this.marker.position ().get ());
    }
}
