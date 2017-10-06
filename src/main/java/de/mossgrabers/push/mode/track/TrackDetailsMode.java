// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.mode.track;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.MasterTrackProxy;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.push.controller.DisplayMessage;
import de.mossgrabers.push.controller.PushColors;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.controller.PushDisplay;
import de.mossgrabers.push.mode.BaseMode;
import de.mossgrabers.push.view.ColorView;
import de.mossgrabers.push.view.Views;


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
    public TrackDetailsMode (final PushControlSurface surface, final Model model)
    {
        super (surface, model);
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
                this.model.getMasterTrack ().toggleArm ();
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
                ((ColorView) viewManager.getView (Views.VIEW_COLOR)).setMode (ColorView.SelectMode.MODE_TRACK);
                viewManager.setActiveView (Views.VIEW_COLOR);
                break;
        }
    }


    private void onFirstRowTrack (final int index)
    {
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final TrackData t = tb.getSelectedTrack ();
        if (t == null)
            return;

        switch (index)
        {
            case 0:
                this.model.getCurrentTrackBank ().toggleIsActivated (t.getIndex ());
                break;
            case 1:
                this.model.getCurrentTrackBank ().toggleArm (t.getIndex ());
                break;
            case 2:
                this.model.getCurrentTrackBank ().toggleMute (t.getIndex ());
                break;
            case 3:
                this.model.getCurrentTrackBank ().toggleSolo (t.getIndex ());
                break;
            case 4:
                this.model.getCurrentTrackBank ().toggleMonitor (t.getIndex ());
                break;
            case 5:
                this.model.getCurrentTrackBank ().toggleAutoMonitor (t.getIndex ());
                break;
            case 6:
                // Not used
                break;
            case 7:
                final ViewManager viewManager = this.surface.getViewManager ();
                ((ColorView) viewManager.getView (Views.VIEW_COLOR)).setMode (ColorView.SelectMode.MODE_TRACK);
                viewManager.setActiveView (Views.VIEW_COLOR);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final TrackData deviceChain = this.getSelectedTrack ();
        if (deviceChain == null)
        {
            this.disableFirstRow ();
            return;
        }

        final int off = this.isPush2 ? PushColors.PUSH2_COLOR_BLACK : PushColors.PUSH1_COLOR_BLACK;
        this.surface.updateButton (20, deviceChain.isActivated () ? this.isPush2 ? PushColors.PUSH2_COLOR_YELLOW_MD : PushColors.PUSH1_COLOR_YELLOW_MD : this.isPush2 ? PushColors.PUSH2_COLOR_YELLOW_LO : PushColors.PUSH1_COLOR_YELLOW_LO);
        this.surface.updateButton (21, deviceChain.isRecArm() ? this.isPush2 ? PushColors.PUSH2_COLOR_RED_HI : PushColors.PUSH1_COLOR_RED_HI : this.isPush2 ? PushColors.PUSH2_COLOR_RED_LO : PushColors.PUSH1_COLOR_RED_LO);
        this.surface.updateButton (22, deviceChain.isMute () ? this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_HI : PushColors.PUSH1_COLOR_ORANGE_HI : this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_LO : PushColors.PUSH1_COLOR_ORANGE_LO);
        this.surface.updateButton (23, deviceChain.isSolo () ? this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_HI : PushColors.PUSH1_COLOR_ORANGE_HI : this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_LO : PushColors.PUSH1_COLOR_ORANGE_LO);
        this.surface.updateButton (24, deviceChain.isMonitor () ? this.isPush2 ? PushColors.PUSH2_COLOR_GREEN_HI : PushColors.PUSH1_COLOR_GREEN_HI : this.isPush2 ? PushColors.PUSH2_COLOR_GREEN_LO : PushColors.PUSH1_COLOR_GREEN_LO);
        this.surface.updateButton (25, deviceChain.isAutoMonitor () ? this.isPush2 ? PushColors.PUSH2_COLOR_GREEN_HI : PushColors.PUSH1_COLOR_GREEN_HI : this.isPush2 ? PushColors.PUSH2_COLOR_GREEN_LO : PushColors.PUSH1_COLOR_GREEN_LO);
        this.surface.updateButton (26, off);
        this.surface.updateButton (27, this.isPush2 ? PushColors.PUSH2_COLOR_GREEN_HI : PushColors.PUSH1_COLOR_GREEN_HI);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ();
        final TrackData deviceChain = this.getSelectedTrack ();
        if (deviceChain == null)
            d.setRow (1, "                     Please selecta track...                        ").clearRow (0).clearRow (2).done (0).done (2);
        else
        {
            d.clearRow (0).clearRow (1).setBlock (0, 0, "Track: " + deviceChain.getName ());
            d.setCell (2, 0, "Active").setCell (3, 0, deviceChain.isActivated () ? "On" : "Off");
            d.setCell (2, 1, "Rec Arm");
            d.setCell (3, 1, deviceChain.isRecArm() ? "On" : "Off");
            d.setCell (2, 2, "Mute").setCell (3, 2, deviceChain.isMute () ? "On" : "Off");
            d.setCell (2, 3, "Solo").setCell (3, 3, deviceChain.isSolo () ? "On" : "Off");
            d.setCell (2, 4, "Monitor");
            d.setCell (3, 4, deviceChain.isMonitor () ? "On" : "Off");
            d.setCell (2, 5, "Auto Monitor");
            d.setCell (3, 5, deviceChain.isAutoMonitor () ? "On" : "Off");
            d.clearCell (2, 6).clearCell (3, 6);
            d.setCell (2, 7, "Select").setCell (3, 7, "Color").done (0).done (1).done (2).done (3);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final DisplayMessage message = ((PushDisplay) this.surface.getDisplay ()).createMessage ();

        final TrackData deviceChain = this.getSelectedTrack ();
        if (deviceChain == null)
            message.setMessage (3, "Please select a track...");
        else
        {
            message.addOptionElement ("Track: " + deviceChain.getName (), "", false, "", "Active", deviceChain.isActivated (), false);
            message.addOptionElement ("", "", false, "", "Rec Arm", deviceChain.isRecArm(), false);
            message.addOptionElement ("", "", false, "", "Mute", deviceChain.isMute (), false);
            message.addOptionElement ("", "", false, "", "Solo", deviceChain.isSolo (), false);
            message.addOptionElement ("", "", false, "", "Monitor", deviceChain.isMonitor (), false);
            message.addOptionElement ("", "", false, "", "Auto Monitor", deviceChain.isAutoMonitor (), false);
            message.addOptionElement ("", "", false, "", "", false, false);
            message.addOptionElement ("", "", false, "", "Select Color", false, false);
        }
        message.send ();
    }


    private TrackData getSelectedTrack ()
    {
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final TrackData t = tb.getSelectedTrack ();
        if (t != null)
            return t;
        final MasterTrackProxy master = this.model.getMasterTrack ();
        return master.isSelected () ? master : null;
    }
}