// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.bitwig.daw.data;

import de.mossgrabers.framework.controller.ValueChanger;
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
    private int                                vuLeft;
    private int                                vuRight;


    /**
     * Constructor.
     *
     * @param master The master track
     * @param valueChanger The valueChanger
     */
    public MasterTrackImpl (final MasterTrack master, final ValueChanger valueChanger)
    {
        super (master, valueChanger, -1, 0, 0);

        this.track.addIsSelectedInEditorObserver (this::handleIsSelected);

        final int maxParameterValue = this.valueChanger.getUpperBound ();
        this.track.addVuMeterObserver (maxParameterValue, 0, true, value -> this.handleVULeftMeter (maxParameterValue, value));
        this.track.addVuMeterObserver (maxParameterValue, 1, true, value -> this.handleVURightMeter (maxParameterValue, value));
    }


    /** {@inheritDoc} */
    @Override
    public void addTrackSelectionObserver (final TrackSelectionObserver observer)
    {
        this.observers.add (observer);
    }


    /** {@inheritDoc} */
    @Override
    public int getVuLeft ()
    {
        return this.vuLeft;
    }


    /** {@inheritDoc} */
    @Override
    public int getVuRight ()
    {
        return this.vuRight;
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


    private void handleVULeftMeter (final int maxParameterValue, final int value)
    {
        // Limit value to this.configuration.getMaxParameterValue () due to
        // https://github.com/teotigraphix/Framework4Bitwig/issues/98
        this.vuLeft = value >= maxParameterValue ? maxParameterValue - 1 : value;
    }


    private void handleVURightMeter (final int maxParameterValue, final int value)
    {
        // Limit value to this.configuration.getMaxParameterValue () due to
        // https://github.com/teotigraphix/Framework4Bitwig/issues/98
        this.vuRight = value >= maxParameterValue ? maxParameterValue - 1 : value;
    }
}