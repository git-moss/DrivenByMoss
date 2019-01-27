// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.observer.ItemSelectionObserver;

import com.bitwig.extension.controller.api.MasterTrack;

import java.util.ArrayList;
import java.util.List;


/**
 * The master track.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MasterTrackImpl extends TrackImpl implements IMasterTrack
{
    private final List<ItemSelectionObserver> observers = new ArrayList<> ();


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The valueChanger
     * @param master The master track
     */
    public MasterTrackImpl (final IHost host, final IValueChanger valueChanger, final MasterTrack master)
    {
        super (host, valueChanger, master, -1, 0, 0);

        this.track.addIsSelectedInEditorObserver (this::handleIsSelected);
    }


    /** {@inheritDoc} */
    @Override
    public void addSelectionObserver (final ItemSelectionObserver observer)
    {
        this.observers.add (observer);
    }


    /**
     * Handle selection changes. Notifies all registered observers.
     *
     * @param isSelected True if selected
     */
    private void handleIsSelected (final boolean isSelected)
    {
        this.setSelected (isSelected);
        for (final ItemSelectionObserver observer: this.observers)
            observer.call (-1, isSelected);
    }


    /** {@inheritDoc} */
    @Override
    public ChannelType getType ()
    {
        return ChannelType.MASTER;
    }
}