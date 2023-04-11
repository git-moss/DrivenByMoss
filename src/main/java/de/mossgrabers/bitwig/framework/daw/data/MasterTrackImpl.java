// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.bitwig.framework.daw.ApplicationImpl;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.observer.IItemSelectionObserver;

import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.MasterTrack;
import com.bitwig.extension.controller.api.Track;

import java.util.ArrayList;
import java.util.List;


/**
 * The master track.
 *
 * @author Jürgen Moßgraber
 */
public class MasterTrackImpl extends TrackImpl implements IMasterTrack
{
    private final List<IItemSelectionObserver> observers = new ArrayList<> ();


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The valueChanger
     * @param application The application
     * @param master The master track
     * @param cursorTrack The cursor track
     * @param rootGroup The root track
     */
    public MasterTrackImpl (final IHost host, final IValueChanger valueChanger, final MasterTrack master, final CursorTrack cursorTrack, final Track rootGroup, final ApplicationImpl application)
    {
        super (host, valueChanger, application, cursorTrack, rootGroup, master, -1, 0, 0);

        this.track.addIsSelectedInEditorObserver (this::handleIsSelected);
    }


    /** {@inheritDoc} */
    @Override
    public void enter ()
    {
        // Master track is not a group
    }


    /** {@inheritDoc} */
    @Override
    public void addSelectionObserver (final IItemSelectionObserver observer)
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
        for (final IItemSelectionObserver observer: this.observers)
            observer.call (-1, isSelected);
    }


    /** {@inheritDoc} */
    @Override
    public ChannelType getType ()
    {
        return ChannelType.MASTER;
    }
}