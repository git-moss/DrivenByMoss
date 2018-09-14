// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.view;

import de.mossgrabers.controller.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.launchpad.controller.LaunchpadColors;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.controller.launchpad.mode.Modes;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.DAWColors;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractSessionView;
import de.mossgrabers.framework.view.SessionColor;
import de.mossgrabers.framework.view.ViewManager;


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
        final Integer activeModeId = modeManager.getActiveOrTempModeId ();
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
                final ITrackBank tb = this.model.getCurrentTrackBank ();
                final ITrack track = tb.getItem (t);
                if (track.doesExist ())
                {
                    final int s = this.rows - 1 - index / this.columns;
                    track.getSlotBank ().getItem (s).duplicate ();
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
        final Integer controlMode = this.surface.getModeManager ().getActiveOrTempModeId ();
        final boolean isOff = controlMode == null;
        final boolean flip = this.surface.getConfiguration ().isFlipSession ();
        this.rows = isOff || flip ? 8 : 7;
        this.columns = isOff || !flip ? 8 : 7;

        super.drawGrid ();

        if (isOff)
            return;

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final PadGrid pads = this.surface.getPadGrid ();
        final ModeManager modeManager = this.surface.getModeManager ();
        for (int x = 0; x < this.columns; x++)
        {
            final ITrack track = tb.getItem (x);
            final boolean exists = track.doesExist ();
            if (modeManager.isActiveOrTempMode (Modes.MODE_REC_ARM))
                pads.lightEx (x, 7, exists ? track.isRecArm () ? LaunchpadColors.LAUNCHPAD_COLOR_RED_HI : LaunchpadColors.LAUNCHPAD_COLOR_RED_LO : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
            else if (modeManager.isActiveOrTempMode (Modes.MODE_TRACK_SELECT))
                pads.lightEx (x, 7, exists ? track.isSelected () ? LaunchpadColors.LAUNCHPAD_COLOR_GREEN_HI : LaunchpadColors.LAUNCHPAD_COLOR_GREEN_LO : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
            else if (modeManager.isActiveOrTempMode (Modes.MODE_MUTE))
                pads.lightEx (x, 7, exists ? track.isMute () ? LaunchpadColors.LAUNCHPAD_COLOR_YELLOW_HI : LaunchpadColors.LAUNCHPAD_COLOR_YELLOW_LO : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
            else if (modeManager.isActiveOrTempMode (Modes.MODE_SOLO))
                pads.lightEx (x, 7, exists ? track.isSolo () ? LaunchpadColors.LAUNCHPAD_COLOR_BLUE_HI : LaunchpadColors.LAUNCHPAD_COLOR_BLUE_LO : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
            else if (modeManager.isActiveOrTempMode (Modes.MODE_STOP_CLIP))
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
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ISceneBank sceneBank = tb.getSceneBank ();
        for (int i = 0; i < 8; i++)
        {
            final IScene scene = sceneBank.getItem (i);
            if (scene.doesExist ())
                this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE1 - i * 10, DAWColors.getColorIndex (scene.getColor ()));
            else
                this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE1 - i * 10, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        }
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
                final ITrack selectedTrack = this.model.getSelectedTrack ();
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

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ISceneBank sceneBank = tb.getSceneBank ();

        // Calculate page offsets
        final int trackPosition = tb.getItem (0).getPosition () / tb.getPageSize ();
        final int scenePosition = sceneBank.getScrollPosition () / sceneBank.getPageSize ();
        final boolean flip = this.surface.getConfiguration ().isFlipSession ();
        final int selX = flip ? scenePosition : trackPosition;
        final int selY = flip ? trackPosition : scenePosition;
        final int padsX = flip ? this.rows : this.columns;
        final int padsY = flip ? this.columns : isOffset ? this.rows + 1 : this.rows;
        final int offsetX = selX / padsX * padsX;
        final int offsetY = selY / padsY * padsY;
        tb.scrollTo (offsetX * tb.getPageSize () + (flip ? y : x) * padsX);
        sceneBank.scrollTo (offsetY * sceneBank.getPageSize () + (flip ? x : y) * padsY);
    }


    private void handleFirstRowModes (final int note, final ModeManager modeManager)
    {
        // First row mode handling
        final int index = note - 36;
        final ITrack track = this.model.getCurrentTrackBank ().getItem (index);

        if (this.surface.isPressed (LaunchpadControlSurface.LAUNCHPAD_BUTTON_DUPLICATE))
        {
            this.surface.setButtonConsumed (LaunchpadControlSurface.LAUNCHPAD_BUTTON_DUPLICATE);
            track.duplicate ();
            return;
        }

        if (modeManager.isActiveOrTempMode (Modes.MODE_REC_ARM))
            track.toggleRecArm ();
        else if (modeManager.isActiveOrTempMode (Modes.MODE_TRACK_SELECT))
            this.selectTrack (index);
        else if (modeManager.isActiveOrTempMode (Modes.MODE_MUTE))
            track.toggleMute ();
        else if (modeManager.isActiveOrTempMode (Modes.MODE_SOLO))
            track.toggleSolo ();
        else if (modeManager.isActiveOrTempMode (Modes.MODE_STOP_CLIP))
            track.stop ();
    }
}
