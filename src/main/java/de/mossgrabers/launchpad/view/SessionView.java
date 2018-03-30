// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad.view;

import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractSessionView;
import de.mossgrabers.framework.view.SessionColor;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.launchpad.LaunchpadConfiguration;
import de.mossgrabers.launchpad.controller.LaunchpadColors;
import de.mossgrabers.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.launchpad.mode.Modes;


/**
 * Session view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SessionView extends AbstractSessionView<LaunchpadControlSurface, LaunchpadConfiguration>
{
    protected boolean isTemporary;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SessionView (final LaunchpadControlSurface surface, final IModel model)
    {
        super ("Session", surface, model, 8, 8, true);

        final SessionColor isRecording = new SessionColor (LaunchpadColors.LAUNCHPAD_COLOR_RED_HI, LaunchpadColors.LAUNCHPAD_COLOR_RED_HI, false);
        final SessionColor isRecordingQueued = new SessionColor (LaunchpadColors.LAUNCHPAD_COLOR_RED_HI, LaunchpadColors.LAUNCHPAD_COLOR_BLACK, true);
        final SessionColor isPlaying = new SessionColor (LaunchpadColors.LAUNCHPAD_COLOR_GREEN, LaunchpadColors.LAUNCHPAD_COLOR_GREEN, false);
        final SessionColor isPlayingQueued = new SessionColor (LaunchpadColors.LAUNCHPAD_COLOR_GREEN, LaunchpadColors.LAUNCHPAD_COLOR_BLACK, true);
        final SessionColor hasContent = new SessionColor (LaunchpadColors.LAUNCHPAD_COLOR_AMBER, -1, false);
        final SessionColor noContent = new SessionColor (LaunchpadColors.LAUNCHPAD_COLOR_BLACK, -1, false);
        final SessionColor recArmed = new SessionColor (LaunchpadColors.LAUNCHPAD_COLOR_RED_LO, -1, false);
        this.setColors (isRecording, isRecordingQueued, isPlaying, isPlayingQueued, hasContent, noContent, recArmed);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.switchLaunchpadMode ();

        super.onActivate ();

        this.surface.scheduleTask (this::delayedUpdateArrowButtons, 150);
    }


    protected void delayedUpdateArrowButtons ()
    {
        this.surface.setButton (this.surface.getSessionButton (), LaunchpadColors.LAUNCHPAD_COLOR_LIME);
        this.surface.setButton (this.surface.getNoteButton (), LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        this.surface.setButton (this.surface.getDeviceButton (), LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        this.surface.setButton (this.surface.getUserButton (), LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        final Integer activeModeId = modeManager.getActiveModeId ();
        // Block 1st row if mode is active
        final boolean isNotRow1 = note >= 44;
        if (activeModeId == null || isNotRow1)
        {
            if (this.surface.isShiftPressed ())
            {
                this.onGridNoteBankSelection (note, velocity, isNotRow1);
                return;
            }

            final int n = note - (activeModeId != null ? 8 : 0);
            final int index = n - 36;
            final int t = index % this.columns;

            // Duplicate a clip
            if (this.surface.isPressed (LaunchpadControlSurface.LAUNCHPAD_BUTTON_DUPLICATE))
            {
                this.surface.setButtonConsumed (LaunchpadControlSurface.LAUNCHPAD_BUTTON_DUPLICATE);
                final IChannelBank tb = this.model.getCurrentTrackBank ();
                final ITrack track = tb.getTrack (t);
                if (track.doesExist ())
                {
                    final int s = this.rows - 1 - index / this.columns;
                    track.getSlot (s).duplicate ();
                }
                return;
            }

            super.onGridNote (n, velocity);
            return;
        }

        if (velocity != 0)
            this.handleFirstRowModes (note, modeManager);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final Integer controlMode = this.surface.getModeManager ().getActiveModeId ();
        final boolean isOff = controlMode == null;
        final boolean flip = this.surface.getConfiguration ().isFlipSession ();
        this.rows = isOff || flip ? 8 : 7;
        this.columns = isOff || !flip ? 8 : 7;

        super.drawGrid ();

        if (isOff)
            return;

        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final PadGrid pads = this.surface.getPadGrid ();
        final ModeManager modeManager = this.surface.getModeManager ();
        for (int x = 0; x < this.columns; x++)
        {
            final ITrack track = tb.getTrack (x);
            final boolean exists = track.doesExist ();
            if (modeManager.isActiveMode (Modes.MODE_REC_ARM))
                pads.lightEx (x, 7, exists ? track.isRecArm () ? LaunchpadColors.LAUNCHPAD_COLOR_RED_HI : LaunchpadColors.LAUNCHPAD_COLOR_RED_LO : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
            else if (modeManager.isActiveMode (Modes.MODE_TRACK_SELECT))
                pads.lightEx (x, 7, exists ? track.isSelected () ? LaunchpadColors.LAUNCHPAD_COLOR_GREEN_HI : LaunchpadColors.LAUNCHPAD_COLOR_GREEN_LO : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
            else if (modeManager.isActiveMode (Modes.MODE_MUTE))
                pads.lightEx (x, 7, exists ? track.isMute () ? LaunchpadColors.LAUNCHPAD_COLOR_YELLOW_HI : LaunchpadColors.LAUNCHPAD_COLOR_YELLOW_LO : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
            else if (modeManager.isActiveMode (Modes.MODE_SOLO))
                pads.lightEx (x, 7, exists ? track.isSolo () ? LaunchpadColors.LAUNCHPAD_COLOR_BLUE_HI : LaunchpadColors.LAUNCHPAD_COLOR_BLUE_LO : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
            else if (modeManager.isActiveMode (Modes.MODE_STOP_CLIP))
                pads.lightEx (x, 7, exists ? LaunchpadColors.LAUNCHPAD_COLOR_ROSE : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        }
    }


    /**
     * Switch to the appropriate launchpad mode, which supports different features with the pad
     * grid, e.g. the simulation of faders.
     */
    public void switchLaunchpadMode ()
    {
        this.surface.setLaunchpadToPrgMode ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE1, LaunchpadColors.LAUNCHPAD_COLOR_GREEN);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE2, LaunchpadColors.LAUNCHPAD_COLOR_GREEN);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE3, LaunchpadColors.LAUNCHPAD_COLOR_GREEN);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE4, LaunchpadColors.LAUNCHPAD_COLOR_GREEN);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE5, LaunchpadColors.LAUNCHPAD_COLOR_GREEN);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE6, LaunchpadColors.LAUNCHPAD_COLOR_GREEN);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE7, LaunchpadColors.LAUNCHPAD_COLOR_GREEN);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE8, LaunchpadColors.LAUNCHPAD_COLOR_GREEN);
    }


    /**
     * The session button was pressed.
     *
     * @param event The button event
     */
    public void onSession (final ButtonEvent event)
    {
        switch (event)
        {
            case LONG:
                this.isTemporary = true;
                break;

            case UP:
                if (!this.isTemporary)
                    return;
                this.isTemporary = false;

                final ViewManager viewManager = this.surface.getViewManager ();
                final ITrackBank tb = this.model.getTrackBank ();
                final ITrack selectedTrack = tb.getSelectedTrack ();
                if (selectedTrack == null)
                    return;
                final Integer viewId = viewManager.getPreferredView (selectedTrack.getPosition ());
                viewManager.setActiveView (viewId == null ? Views.VIEW_PLAY : viewId);
                break;

            default:
                // Intentionally empty
                return;
        }
    }


    protected void onGridNoteBankSelection (final int note, final int velocity, final boolean isOffset)
    {
        if (velocity == 0)
            return;

        final int n = isOffset ? note : note - 8;
        final int index = n - 36;
        final int x = index % this.columns;
        final int y = this.rows - 1 - index / this.columns;

        final IChannelBank tb = this.model.getCurrentTrackBank ();

        // Calculate page offsets
        final int trackPosition = tb.getTrack (0).getPosition () / tb.getNumTracks ();
        final int scenePosition = tb.getScenePosition () / tb.getNumScenes ();
        final boolean flip = this.surface.getConfiguration ().isFlipSession ();
        final int selX = flip ? scenePosition : trackPosition;
        final int selY = flip ? trackPosition : scenePosition;
        final int padsX = flip ? this.rows : this.columns;
        final int padsY = flip ? this.columns : isOffset ? this.rows + 1 : this.rows;
        final int offsetX = selX / padsX * padsX;
        final int offsetY = selY / padsY * padsY;
        tb.scrollToChannel (offsetX * tb.getNumTracks () + (flip ? y : x) * padsX);
        tb.scrollToScene (offsetY * tb.getNumScenes () + (flip ? x : y) * padsY);
    }


    private void handleFirstRowModes (final int note, final ModeManager modeManager)
    {
        // First row mode handling
        final int index = note - 36;
        final ITrack track = this.model.getCurrentTrackBank ().getTrack (index);

        if (this.surface.isPressed (LaunchpadControlSurface.LAUNCHPAD_BUTTON_DUPLICATE))
        {
            this.surface.setButtonConsumed (LaunchpadControlSurface.LAUNCHPAD_BUTTON_DUPLICATE);
            track.duplicate ();
            return;
        }

        if (modeManager.isActiveMode (Modes.MODE_REC_ARM))
            track.toggleRecArm ();
        else if (modeManager.isActiveMode (Modes.MODE_TRACK_SELECT))
            this.selectTrack (index);
        else if (modeManager.isActiveMode (Modes.MODE_MUTE))
            track.toggleMute ();
        else if (modeManager.isActiveMode (Modes.MODE_SOLO))
            track.toggleSolo ();
        else if (modeManager.isActiveMode (Modes.MODE_STOP_CLIP))
            track.stop ();
    }
}
