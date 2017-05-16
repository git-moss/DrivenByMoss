// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import com.bitwig.extension.controller.api.Arranger;
import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Encapsulates the Arranger instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ArrangerProxy
{
    private Arranger arranger;


    /**
     * Constructor
     *
     * @param host The host
     */
    public ArrangerProxy (final ControllerHost host)
    {
        this.arranger = host.createArranger ();

        this.arranger.areCueMarkersVisible ().markInterested ();
        this.arranger.isPlaybackFollowEnabled ().markInterested ();
        this.arranger.hasDoubleRowTrackHeight ().markInterested ();
        this.arranger.isClipLauncherVisible ().markInterested ();
        this.arranger.isTimelineVisible ().markInterested ();
        this.arranger.isIoSectionVisible ().markInterested ();
        this.arranger.areEffectTracksVisible ().markInterested ();
    }


    /**
     * Dis-/Enable all attributes. They are enabled by default. Use this function if values are
     * currently not needed to improve performance.
     *
     * @param enable True to enable
     */
    public void enableObservers (final boolean enable)
    {
        this.arranger.areCueMarkersVisible ().setIsSubscribed (enable);
        this.arranger.isPlaybackFollowEnabled ().setIsSubscribed (enable);
        this.arranger.hasDoubleRowTrackHeight ().setIsSubscribed (enable);
        this.arranger.isClipLauncherVisible ().setIsSubscribed (enable);
        this.arranger.isTimelineVisible ().setIsSubscribed (enable);
        this.arranger.isIoSectionVisible ().setIsSubscribed (enable);
        this.arranger.areEffectTracksVisible ().setIsSubscribed (enable);
    }


    /**
     * Are the cue markers visible?
     *
     * @return True if cue markers are visible
     */
    public boolean areCueMarkersVisible ()
    {
        return this.arranger.areCueMarkersVisible ().get ();
    }


    /**
     * Show/hide the cue markers in the arranger panel.
     */
    public void toggleCueMarkerVisibility ()
    {
        this.arranger.areCueMarkersVisible ().toggle ();
    }


    /**
     * Is playback follow enabled?.
     *
     * @return True if playback follows
     */
    public boolean isPlaybackFollowEnabled ()
    {
        return this.arranger.isPlaybackFollowEnabled ().get ();
    }


    /**
     * Enable/disable arranger playback follow.
     */
    public void togglePlaybackFollow ()
    {
        this.arranger.isPlaybackFollowEnabled ().toggle ();
    }


    /**
     * Are double track heights enabled?.
     *
     * @return True if double row track height is enabled
     */
    public boolean hasDoubleRowTrackHeight ()
    {
        return this.arranger.hasDoubleRowTrackHeight ().get ();
    }


    /**
     * Toggles the double/single row height of the Arranger tracks.
     */
    public void toggleTrackRowHeight ()
    {
        this.arranger.hasDoubleRowTrackHeight ().toggle ();
    }


    /**
     * Is the clip launcher visible?
     *
     * @return True if the clip launcher is visible
     */
    public boolean isClipLauncherVisible ()
    {
        return this.arranger.isClipLauncherVisible ().get ();
    }


    /**
     * Toggle the clip launcher visibility.
     */
    public void toggleClipLauncher ()
    {
        this.arranger.isClipLauncherVisible ().toggle ();
    }


    /**
     * Is the timeline visible?
     *
     * @return True if the timeline is visible?
     */
    public boolean isTimelineVisible ()
    {
        return this.arranger.isTimelineVisible ().get ();
    }


    /**
     * Toggles the timeline visibility.
     */
    public void toggleTimeLine ()
    {
        this.arranger.isTimelineVisible ().toggle ();
    }


    /**
     * Is the IO section visible?
     *
     * @return True if the IO section is visible
     */
    public boolean isIoSectionVisible ()
    {
        return this.arranger.isIoSectionVisible ().get ();
    }


    /**
     * Toggles the visibility of the IO section
     */
    public void toggleIoSection ()
    {
        this.arranger.isIoSectionVisible ().toggle ();
    }


    /**
     * Are effect tracks visible?
     *
     * @return True if effect tracks are visible
     */
    public boolean areEffectTracksVisible ()
    {
        return this.arranger.areEffectTracksVisible ().get ();
    }


    /**
     * Toggles the effect tracks visibility.
     */
    public void toggleEffectTracks ()
    {
        this.arranger.areEffectTracksVisible ().toggle ();
    }
}