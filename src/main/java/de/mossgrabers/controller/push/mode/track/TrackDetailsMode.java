// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode.track;

import de.mossgrabers.controller.push.controller.PushColors;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.controller.display.DisplayModel;
import de.mossgrabers.controller.push.mode.BaseMode;
import de.mossgrabers.controller.push.view.ColorView;
import de.mossgrabers.controller.push.view.Views;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.ViewManager;


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
                ((ColorView) viewManager.getView (Views.VIEW_COLOR)).setMode (ColorView.SelectMode.MODE_TRACK);
                viewManager.setActiveView (Views.VIEW_COLOR);
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
                ((ColorView) viewManager.getView (Views.VIEW_COLOR)).setMode (ColorView.SelectMode.MODE_TRACK);
                viewManager.setActiveView (Views.VIEW_COLOR);
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

        this.surface.updateButton (20, deviceChain.isActivated () ? this.isPush2 ? PushColors.PUSH2_COLOR_YELLOW_MD : PushColors.PUSH1_COLOR_YELLOW_MD : this.isPush2 ? PushColors.PUSH2_COLOR_YELLOW_LO : PushColors.PUSH1_COLOR_YELLOW_LO);
        this.surface.updateButton (21, deviceChain.isRecArm () ? this.isPush2 ? PushColors.PUSH2_COLOR_RED_HI : PushColors.PUSH1_COLOR_RED_HI : this.isPush2 ? PushColors.PUSH2_COLOR_RED_LO : PushColors.PUSH1_COLOR_RED_LO);
        this.surface.updateButton (22, deviceChain.isMute () ? this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_HI : PushColors.PUSH1_COLOR_ORANGE_HI : this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_LO : PushColors.PUSH1_COLOR_ORANGE_LO);
        this.surface.updateButton (23, deviceChain.isSolo () ? this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_HI : PushColors.PUSH1_COLOR_ORANGE_HI : this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_LO : PushColors.PUSH1_COLOR_ORANGE_LO);
        this.surface.updateButton (24, deviceChain.isMonitor () ? this.isPush2 ? PushColors.PUSH2_COLOR_GREEN_HI : PushColors.PUSH1_COLOR_GREEN_HI : this.isPush2 ? PushColors.PUSH2_COLOR_GREEN_LO : PushColors.PUSH1_COLOR_GREEN_LO);
        this.surface.updateButton (25, deviceChain.isAutoMonitor () ? this.isPush2 ? PushColors.PUSH2_COLOR_GREEN_HI : PushColors.PUSH1_COLOR_GREEN_HI : this.isPush2 ? PushColors.PUSH2_COLOR_GREEN_LO : PushColors.PUSH1_COLOR_GREEN_LO);
        this.surface.updateButton (26, this.model.isCursorTrackPinned () ? this.isPush2 ? PushColors.PUSH2_COLOR_GREEN_HI : PushColors.PUSH1_COLOR_GREEN_HI : this.isPush2 ? PushColors.PUSH2_COLOR_GREEN_LO : PushColors.PUSH1_COLOR_GREEN_LO);
        this.surface.updateButton (27, this.isPush2 ? PushColors.PUSH2_COLOR_GREEN_HI : PushColors.PUSH1_COLOR_GREEN_HI);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ();
        final ITrack deviceChain = this.getSelectedTrack ();
        if (deviceChain == null)
            d.setRow (1, "                     Please selecta track...                        ").clearRow (0).clearRow (2).done (0).done (2);
        else
        {
            d.clearRow (0).clearRow (1).setBlock (0, 0, "Track: " + deviceChain.getName ());
            d.setCell (2, 0, "Active").setCell (3, 0, deviceChain.isActivated () ? "On" : "Off");
            d.setCell (2, 1, "Rec Arm");
            d.setCell (3, 1, deviceChain.isRecArm () ? "On" : "Off");
            d.setCell (2, 2, "Mute").setCell (3, 2, deviceChain.isMute () ? "On" : "Off");
            d.setCell (2, 3, "Solo").setCell (3, 3, deviceChain.isSolo () ? "On" : "Off");
            d.setCell (2, 4, "Monitor");
            d.setCell (3, 4, deviceChain.isMonitor () ? "On" : "Off");
            d.setCell (2, 5, "Auto Monitor");
            d.setCell (3, 5, deviceChain.isAutoMonitor () ? "On" : "Off");
            final boolean hasPinning = this.model.getHost ().hasPinning ();
            d.setCell (2, 6, hasPinning ? "Pin Trck" : "");
            d.setCell (3, 6, hasPinning ? this.model.isCursorTrackPinned () ? "On" : "Off" : "");
            d.setCell (2, 7, "Select").setCell (3, 7, "Color").done (0).done (1).done (2).done (3);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final DisplayModel message = this.surface.getDisplay ().getModel ();
        final ITrack deviceChain = this.getSelectedTrack ();
        if (deviceChain == null)
            message.setMessage (3, "Please select a track...");
        else
        {
            message.addOptionElement ("Track: " + deviceChain.getName (), "", false, "", "Active", deviceChain.isActivated (), false);
            message.addOptionElement ("", "", false, "", "Rec Arm", deviceChain.isRecArm (), false);
            message.addOptionElement ("", "", false, "", "Mute", deviceChain.isMute (), false);
            message.addOptionElement ("", "", false, "", "Solo", deviceChain.isSolo (), false);
            message.addOptionElement ("", "", false, "", "Monitor", deviceChain.isMonitor (), false);
            message.addOptionElement ("", "", false, "", "Auto Monitor", deviceChain.isAutoMonitor (), false);
            final boolean hasPinning = this.model.getHost ().hasPinning ();
            message.addOptionElement ("", "", false, "", hasPinning ? "Pin Track" : "", hasPinning && this.model.isCursorTrackPinned (), false);
            message.addOptionElement ("", "", false, "", "Select Color", false, false);
        }
        message.send ();
    }


    private ITrack getSelectedTrack ()
    {
        final ITrack t = this.model.getSelectedTrack ();
        if (t != null)
            return t;
        final IMasterTrack master = this.model.getMasterTrack ();
        return master.isSelected () ? master : null;
    }
}