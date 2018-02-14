// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.bitwig.data;

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
    private ValueChanger                       valueChanger;
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
        super (master, valueChanger.getUpperBound (), -1, 0, 0);
        this.valueChanger = valueChanger;
        this.track.addIsSelectedInEditorObserver (this::handleIsSelected);

        final int maxParameterValue = valueChanger.getUpperBound ();
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
    public void setColor (final double red, final double green, final double blue)
    {
        this.track.color ().set ((float) red, (float) green, (float) blue);
    }


    /** {@inheritDoc} */
    @Override
    public void changeVolume (final int control)
    {
        this.track.volume ().inc (Double.valueOf (this.valueChanger.calcKnobSpeed (control)), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void setVolume (final double value)
    {
        this.track.volume ().set (Double.valueOf (value), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void resetVolume ()
    {
        this.track.volume ().reset ();
    }


    /** {@inheritDoc} */
    @Override
    public void touchVolume (final boolean isBeingTouched)
    {
        this.track.volume ().touch (isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void setVolumeIndication (final boolean indicate)
    {
        this.track.volume ().setIndication (indicate);
    }


    /** {@inheritDoc} */
    @Override
    public void changePan (final int control)
    {
        this.track.pan ().inc (Double.valueOf (this.valueChanger.calcKnobSpeed (control)), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void setPan (final double value)
    {
        this.track.pan ().set (Double.valueOf (value), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void resetPan ()
    {
        this.track.pan ().reset ();
    }


    /** {@inheritDoc} */
    @Override
    public void touchPan (final boolean isBeingTouched)
    {
        this.track.pan ().touch (isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void setPanIndication (final boolean indicate)
    {
        this.track.pan ().setIndication (indicate);
    }


    /** {@inheritDoc} */
    @Override
    public void setIsActivated (final boolean value)
    {
        this.track.isActivated ().set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleIsActivated ()
    {
        this.track.isActivated ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void setMute (final boolean value)
    {
        this.track.mute ().set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMute ()
    {
        this.track.mute ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void setSolo (final boolean value)
    {
        this.track.solo ().set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleSolo ()
    {
        this.track.solo ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void setArm (final boolean value)
    {
        this.track.arm ().set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleArm ()
    {
        this.track.arm ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void setMonitor (final boolean value)
    {
        this.track.monitor ().set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMonitor ()
    {
        this.track.monitor ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void setAutoMonitor (final boolean value)
    {
        this.track.autoMonitor ().set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleAutoMonitor ()
    {
        this.track.autoMonitor ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        this.track.selectInEditor ();
        this.track.selectInMixer ();
    }


    /** {@inheritDoc} */
    @Override
    public void makeVisible ()
    {
        this.track.makeVisibleInArranger ();
        this.track.makeVisibleInMixer ();
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