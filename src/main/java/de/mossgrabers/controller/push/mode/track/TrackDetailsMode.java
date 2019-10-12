// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode.track;

import de.mossgrabers.controller.push.controller.PushColors;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.BaseMode;
import de.mossgrabers.controller.push.view.ColorView;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;


/**
 * Mode for editing details of a track.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackDetailsMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public TrackDetailsMode (final PushControlSurface surface, final IModel model)
    {
        super ("Track details", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        if (this.model.getMasterTrack ().isSelected ())
            this.onFirstRowMasterTrack (index);
        else
            this.onFirstRowTrack (index);
    }


    private void onFirstRowMasterTrack (final int index)
    {
        switch (index)
        {
            case 0:
                this.model.getMasterTrack ().toggleIsActivated ();
                break;
            case 1:
                this.model.getMasterTrack ().toggleRecArm ();
                break;
            case 2:
                this.model.getMasterTrack ().toggleMute ();
                break;
            case 3:
                this.model.getMasterTrack ().toggleSolo ();
                break;
            case 4:
                this.model.getMasterTrack ().toggleMonitor ();
                break;
            case 5:
                this.model.getMasterTrack ().toggleAutoMonitor ();
                break;
            case 6:
                // Not used
                break;
            case 7:
                final ViewManager viewManager = this.surface.getViewManager ();
                ((ColorView) viewManager.getView (Views.COLOR)).setMode (ColorView.SelectMode.MODE_TRACK);
                viewManager.setActiveView (Views.COLOR);
                break;
            default:
                // Not used
                break;
        }
    }


    private void onFirstRowTrack (final int index)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack t = tb.getSelectedItem ();
        if (t == null)
            return;

        switch (index)
        {
            case 0:
                t.toggleIsActivated ();
                break;
            case 1:
                t.toggleRecArm ();
                break;
            case 2:
                t.toggleMute ();
                break;
            case 3:
                t.toggleSolo ();
                break;
            case 4:
                t.toggleMonitor ();
                break;
            case 5:
                t.toggleAutoMonitor ();
                break;
            case 6:
                this.model.toggleCursorTrackPinned ();
                break;
            case 7:
                final ViewManager viewManager = this.surface.getViewManager ();
                ((ColorView) viewManager.getView (Views.COLOR)).setMode (ColorView.SelectMode.MODE_TRACK);
                viewManager.setActiveView (Views.COLOR);
                break;
            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final ITrack deviceChain = this.getSelectedTrack ();
        if (deviceChain == null)
        {
            this.disableFirstRow ();
            return;
        }

        this.surface.updateTrigger (20, deviceChain.isActivated () ? this.isPush2 ? PushColors.PUSH2_COLOR_YELLOW_MD : PushColors.PUSH1_COLOR_YELLOW_MD : this.isPush2 ? PushColors.PUSH2_COLOR_YELLOW_LO : PushColors.PUSH1_COLOR_YELLOW_LO);
        this.surface.updateTrigger (21, deviceChain.isRecArm () ? this.isPush2 ? PushColors.PUSH2_COLOR_RED_HI : PushColors.PUSH1_COLOR_RED_HI : this.isPush2 ? PushColors.PUSH2_COLOR_RED_LO : PushColors.PUSH1_COLOR_RED_LO);
        this.surface.updateTrigger (22, deviceChain.isMute () ? this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_HI : PushColors.PUSH1_COLOR_ORANGE_HI : this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_LO : PushColors.PUSH1_COLOR_ORANGE_LO);
        this.surface.updateTrigger (23, deviceChain.isSolo () ? this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_HI : PushColors.PUSH1_COLOR_ORANGE_HI : this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_LO : PushColors.PUSH1_COLOR_ORANGE_LO);
        this.surface.updateTrigger (24, deviceChain.isMonitor () ? this.isPush2 ? PushColors.PUSH2_COLOR_GREEN_HI : PushColors.PUSH1_COLOR_GREEN_HI : this.isPush2 ? PushColors.PUSH2_COLOR_GREEN_LO : PushColors.PUSH1_COLOR_GREEN_LO);
        this.surface.updateTrigger (25, deviceChain.isAutoMonitor () ? this.isPush2 ? PushColors.PUSH2_COLOR_GREEN_HI : PushColors.PUSH1_COLOR_GREEN_HI : this.isPush2 ? PushColors.PUSH2_COLOR_GREEN_LO : PushColors.PUSH1_COLOR_GREEN_LO);
        this.surface.updateTrigger (26, this.model.isCursorTrackPinned () ? this.isPush2 ? PushColors.PUSH2_COLOR_GREEN_HI : PushColors.PUSH1_COLOR_GREEN_HI : this.isPush2 ? PushColors.PUSH2_COLOR_GREEN_LO : PushColors.PUSH1_COLOR_GREEN_LO);
        this.surface.updateTrigger (27, this.isPush2 ? PushColors.PUSH2_COLOR_GREEN_HI : PushColors.PUSH1_COLOR_GREEN_HI);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final ITrack track = this.getSelectedTrack ();
        if (track == null)
        {
            display.setRow (1, "                     Please selecta track...                        ");
            return;
        }

        final String trackName = track.getName ();
        display.setBlock (0, 0, getTrackTitle (track) + trackName);
        if (trackName.length () > 10)
            display.setBlock (0, 1, trackName.substring (10));
        display.setCell (2, 0, "Active").setCell (3, 0, track.isActivated () ? "On" : "Off");
        display.setCell (2, 1, "Rec Arm");
        display.setCell (3, 1, track.isRecArm () ? "On" : "Off");
        display.setCell (2, 2, "Mute").setCell (3, 2, track.isMute () ? "On" : "Off");
        display.setCell (2, 3, "Solo").setCell (3, 3, track.isSolo () ? "On" : "Off");
        display.setCell (2, 4, "Monitor");
        display.setCell (3, 4, track.isMonitor () ? "On" : "Off");
        display.setCell (2, 5, "Auto Monitor");
        display.setCell (3, 5, track.isAutoMonitor () ? "On" : "Off");
        final boolean hasPinning = this.model.getHost ().hasPinning ();
        display.setCell (2, 6, hasPinning ? "Pin Trck" : "");
        display.setCell (3, 6, hasPinning ? this.model.isCursorTrackPinned () ? "On" : "Off" : "");
        display.setCell (2, 7, "Select").setCell (3, 7, "Color");
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final ITrack track = this.getSelectedTrack ();
        if (track == null)
        {
            display.setMessage (3, "Please select a track...");
            return;
        }

        display.addOptionElement (getTrackTitle (track) + track.getName (), "", false, "", "Active", track.isActivated (), false);
        display.addOptionElement ("", "", false, "", "Rec Arm", track.isRecArm (), false);
        display.addOptionElement ("", "", false, "", "Mute", track.isMute (), false);
        display.addOptionElement ("", "", false, "", "Solo", track.isSolo (), false);
        display.addOptionElement ("", "", false, "", "Monitor", track.isMonitor (), false);
        display.addOptionElement ("", "", false, "", "Auto Monitor", track.isAutoMonitor (), false);
        final boolean hasPinning = this.model.getHost ().hasPinning ();
        display.addOptionElement ("", "", false, "", hasPinning ? "Pin Track" : "", hasPinning && this.model.isCursorTrackPinned (), false);
        display.addOptionElement ("", "", false, "", "Select Color", false, false);
    }


    /**
     * Get a label for the track.
     *
     * @param track The track
     * @return The label
     */
    private static String getTrackTitle (final ITrack track)
    {
        if (track.hasParent ())
            return "Child Track: ";
        return "Track: ";
    }


    /**
     * Get the currently selected track. If none is selected in the bank the master track is
     * returned.
     *
     * @return The selected track
     */
    private ITrack getSelectedTrack ()
    {
        final ITrack t = this.model.getSelectedTrack ();
        if (t != null)
            return t;
        final IMasterTrack master = this.model.getMasterTrack ();
        return master.isSelected () ? master : null;
    }


    /** {@inheritDoc} */
    @Override
    protected ITrackBank getBank ()
    {
        return this.model.getCurrentTrackBank ();
    }
}