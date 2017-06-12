// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.controller.ValueChanger;
import de.mossgrabers.framework.daw.data.TrackData;

import com.bitwig.extension.controller.api.ControllerHost;

import java.util.ArrayList;
import java.util.List;


/**
 * The master track.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MasterTrackProxy extends TrackData
{
    private final List<TrackSelectionObserver> observers = new ArrayList<> ();
    private ValueChanger                       valueChanger;
    private int                                vuLeft;
    private int                                vuRight;


    /**
     * Constructor.
     *
     * @param host The host
     * @param valueChanger The valueChanger
     */
    public MasterTrackProxy (final ControllerHost host, final ValueChanger valueChanger)
    {
        super (host.createMasterTrack (0), valueChanger.getUpperBound (), -1, 0, 0);
        this.valueChanger = valueChanger;
        this.track.addIsSelectedInEditorObserver (this::handleIsSelected);

        final int maxParameterValue = valueChanger.getUpperBound ();
        this.track.addVuMeterObserver (maxParameterValue, 0, true, value -> this.handleVULeftMeter (maxParameterValue, value));
        this.track.addVuMeterObserver (maxParameterValue, 1, true, value -> this.handleVURightMeter (maxParameterValue, value));
    }


    /**
     * Register an observer to get notified when the master track gets de-/selected.
     *
     * @param observer The observer to register
     */
    public void addTrackSelectionObserver (final TrackSelectionObserver observer)
    {
        this.observers.add (observer);
    }


    /**
     * Set the color of the track as a RGB value.
     *
     * @param red The red part of the color
     * @param green The green part of the color
     * @param blue The blue part of the color
     */
    public void setColor (final double red, final double green, final double blue)
    {
        this.track.color ().set ((float) red, (float) green, (float) blue);
    }


    /**
     * Change the volume.
     *
     * @param control The control value
     */
    public void changeVolume (final int control)
    {
        this.track.getVolume ().inc (Double.valueOf (this.valueChanger.calcKnobSpeed (control)), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /**
     * Set the volume.
     *
     * @param value The new value
     */
    public void setVolume (final double value)
    {
        this.track.getVolume ().set (Double.valueOf (value), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /**
     * Reset the volume to its default value.
     */
    public void resetVolume ()
    {
        this.track.getVolume ().reset ();
    }


    /**
     * Signal that the volume fader/knob is touched for automation recording.
     *
     * @param isBeingTouched True if touched
     */
    public void touchVolume (final boolean isBeingTouched)
    {
        this.track.getVolume ().touch (isBeingTouched);
    }


    /**
     * Signal that the volume is edited.
     *
     * @param indicate True if edited
     */
    public void setVolumeIndication (final boolean indicate)
    {
        this.track.getVolume ().setIndication (indicate);
    }


    /**
     * Change the panorama.
     *
     * @param control The control value
     */
    public void changePan (final int control)
    {
        this.track.getPan ().inc (Double.valueOf (this.valueChanger.calcKnobSpeed (control)), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /**
     * Set the panorama.
     *
     * @param value The new value
     */
    public void setPan (final double value)
    {
        this.track.getPan ().set (Double.valueOf (value), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /**
     * Reset the panorama to its default value.
     */
    public void resetPan ()
    {
        this.track.getPan ().reset ();
    }


    /**
     * Signal that the panorama fader/knob is touched for automation recording.
     *
     * @param isBeingTouched True if touched
     */
    public void touchPan (final boolean isBeingTouched)
    {
        this.track.getPan ().touch (isBeingTouched);
    }


    /**
     * Signal that the panorama is edited.
     *
     * @param indicate True if edited
     */
    public void setPanIndication (final boolean indicate)
    {
        this.track.getPan ().setIndication (indicate);
    }


    /**
     * Sets the activated state of the track.
     *
     * @param value True to activate
     */
    public void setIsActivated (final boolean value)
    {
        this.track.isActivated ().set (value);
    }


    /**
     * Toggle the activated state of the track.
     */
    public void toggleIsActivated ()
    {
        this.track.isActivated ().toggle ();
    }


    /**
     * Turn on/off mute.
     *
     * @param value True to turn on mute, otherwise off
     */
    public void setMute (final boolean value)
    {
        this.track.getMute ().set (value);
    }


    /**
     * Toggle mute.
     */
    public void toggleMute ()
    {
        this.track.getMute ().toggle ();
    }


    /**
     * Turn on/off solo.
     *
     * @param value True to turn on solo, otherwise off
     */
    public void setSolo (final boolean value)
    {
        this.track.getSolo ().set (value);
    }


    /**
     * Toggle solo.
     */
    public void toggleSolo ()
    {
        this.track.getSolo ().toggle ();
    }


    /**
     * Turn on/off record arm.
     *
     * @param value True to turn arm the track for recording, otherwise off
     */
    public void setArm (final boolean value)
    {
        this.track.getArm ().set (value);
    }


    /**
     * Toggle record arm.
     */
    public void toggleArm ()
    {
        this.track.getArm ().toggle ();
    }


    /**
     * Turn on/off track monitoring.
     *
     * @param value True to turn on track monitoring, otherwise off
     */
    public void setMonitor (final boolean value)
    {
        this.track.getMonitor ().set (value);
    }


    /**
     * Toggle monitor.
     */
    public void toggleMonitor ()
    {
        this.track.getMonitor ().toggle ();
    }


    /**
     * Turn on/off auto track monitoring.
     *
     * @param value True to turn on auto track monitoring, otherwise off
     */
    public void setAutoMonitor (final boolean value)
    {
        this.track.getAutoMonitor ().set (value);
    }


    /**
     * Toggle auto monitor.
     */
    public void toggleAutoMonitor ()
    {
        this.track.getAutoMonitor ().toggle ();
    }


    /**
     * Select the master track.
     */
    public void select ()
    {
        this.track.selectInEditor ();
    }


    /**
     * Get the left VU value.
     *
     * @return The left VU value
     */
    public int getVuLeft ()
    {
        return this.vuLeft;
    }


    /**
     * Get the right VU value.
     *
     * @return The right VU value
     */
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