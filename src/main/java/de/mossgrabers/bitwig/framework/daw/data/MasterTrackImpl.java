// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.TrackSelectionObserver;
import de.mossgrabers.framework.daw.data.IMasterTrack;

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
    private final List<TrackSelectionObserver> observers = new ArrayList<> ();


    /**
     * Constructor.
     *
     * @param master The master track
     * @param valueChanger The valueChanger
     */
    public MasterTrackImpl (final MasterTrack master, final IValueChanger valueChanger)
    {
        super (master, valueChanger, -1, 0, 0);

        this.track.addIsSelectedInEditorObserver (this::handleIsSelected);
    }


    /** {@inheritDoc} */
    @Override
    public void addTrackSelectionObserver (final TrackSelectionObserver observer)
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
        for (final TrackSelectionObserver observer: this.observers)
            observer.call (-1, isSelected);
    }
}