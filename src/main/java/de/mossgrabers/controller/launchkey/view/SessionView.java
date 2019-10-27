// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchkey.view;

import de.mossgrabers.controller.launchkey.LaunchkeyMiniMk3Configuration;
import de.mossgrabers.controller.launchkey.controller.LaunchkeyMiniMk3Colors;
import de.mossgrabers.controller.launchkey.controller.LaunchkeyMiniMk3ControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.Pair;
import de.mossgrabers.framework.view.AbstractSessionView;
import de.mossgrabers.framework.view.SessionColor;
import de.mossgrabers.framework.view.Views;


/**
 * The Session view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SessionView extends AbstractSessionView<LaunchkeyMiniMk3ControlSurface, LaunchkeyMiniMk3Configuration>
{
    /** Modes which can take over the first row of pads. */
    public static final Modes [] PAD_MODES =
    {
        Modes.REC_ARM,
        Modes.TRACK_SELECT,
        Modes.MUTE,
        Modes.SOLO,
        Modes.STOP_CLIP
    };

    private Modes                padMode   = null;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SessionView (final LaunchkeyMiniMk3ControlSurface surface, final IModel model)
    {
        super ("Session", surface, model, 2, 8, true);

        final SessionColor isRecording = new SessionColor (LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_RED, LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_RED, false);
        final SessionColor isRecordingQueued = new SessionColor (LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_RED_LO, LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_RED_LO, true);
        final SessionColor isPlaying = new SessionColor (LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_GREEN_SPRING, LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_GREEN, false);
        final SessionColor isPlayingQueued = new SessionColor (LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_GREEN_SPRING, LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_GREEN, true);
        final SessionColor hasContent = new SessionColor (LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_AMBER, -1, false);
        final SessionColor noContent = new SessionColor (LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_BLACK, -1, false);
        final SessionColor recArmed = new SessionColor (LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_RED_LO, -1, false);
        this.setColors (isRecording, isRecordingQueued, isPlaying, isPlayingQueued, hasContent, noContent, recArmed);
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        final ColorManager colorManager = this.model.getColorManager ();
        final int colorScene = colorManager.getColor (AbstractSessionView.COLOR_SCENE);
        final int colorSceneSelected = colorManager.getColor (AbstractSessionView.COLOR_SELECTED_SCENE);
        final int colorSceneOff = colorManager.getColor (AbstractSessionView.COLOR_SCENE_OFF);

        final ISceneBank sceneBank = this.model.getSceneBank ();
        IScene scene = sceneBank.getItem (0);
        this.surface.updateTrigger (LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_SCENE1, scene.doesExist () ? scene.isSelected () ? colorSceneSelected : colorScene : colorSceneOff);

        if (this.padMode == null)
        {
            scene = sceneBank.getItem (1);
            this.surface.updateTrigger (LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_SCENE2, scene.doesExist () ? scene.isSelected () ? colorSceneSelected : colorScene : colorSceneOff);
            return;
        }

        switch (this.padMode)
        {
            case REC_ARM:
                this.surface.updateTrigger (LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_SCENE2, LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_RED_HI);
                break;
            case TRACK_SELECT:
                this.surface.updateTrigger (LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_SCENE2, LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_WHITE);
                break;
            case MUTE:
                this.surface.updateTrigger (LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_SCENE2, LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_AMBER_HI);
                break;
            case SOLO:
                this.surface.updateTrigger (LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_SCENE2, LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_YELLOW_HI);
                break;
            case STOP_CLIP:
                this.surface.updateTrigger (LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_SCENE2, LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_ROSE);
                break;
            default:
                // Unused
                break;
        }

    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final boolean controlModeIsOff = this.padMode == null;
        this.rows = controlModeIsOff ? 2 : 1;

        super.drawGrid ();

        if (controlModeIsOff)
            return;

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final PadGrid pads = this.surface.getPadGrid ();
        for (int x = 0; x < this.columns; x++)
        {
            final ITrack track = tb.getItem (x);
            final boolean exists = track.doesExist ();
            switch (this.padMode)
            {
                case REC_ARM:
                    pads.lightEx (x, 1, exists ? track.isRecArm () ? LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_RED_HI : LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_RED_LO : LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_BLACK);
                    break;
                case TRACK_SELECT:
                    pads.lightEx (x, 1, exists ? track.isSelected () ? LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_WHITE : LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_GREY_LO : LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_BLACK);
                    break;
                case MUTE:
                    pads.lightEx (x, 1, exists ? track.isMute () ? LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_AMBER_HI : LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_AMBER_LO : LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_BLACK);
                    break;
                case SOLO:
                    pads.lightEx (x, 1, exists ? track.isSolo () ? LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_YELLOW_HI : LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_YELLOW_LO : LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_BLACK);
                    break;
                case STOP_CLIP:
                    pads.lightEx (x, 1, exists ? LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_ROSE : LaunchkeyMiniMk3Colors.LAUNCHKEY_COLOR_BLACK);
                    break;
                default:
                    // Unused
                    break;
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (int note, int velocity)
    {
        if (velocity == 0)
            return;

        final Pair<Integer, Integer> padPos = this.getPad (note);
        int row = padPos.getValue ().intValue ();
        if (row == 0 || this.padMode == null)
            super.onGridNote (note, velocity);
        else
            this.handleFirstRowModes (padPos.getKey ().intValue ());
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int sceneIndex, final ButtonEvent event)
    {
        final ISceneBank sceneBank = this.model.getCurrentTrackBank ().getSceneBank ();

        if (this.surface.isShiftPressed ())
        {
            if (event != ButtonEvent.UP)
                return;

            if (sceneIndex == 0)
                sceneBank.selectPreviousPage ();
            else
                sceneBank.selectNextPage ();
            return;
        }

        if (event == ButtonEvent.DOWN)
        {
            if (sceneIndex == 1)
                this.surface.getViewManager ().setActiveView (Views.CONTROL);
        }

        if (event == ButtonEvent.UP)
        {
            final IScene scene = sceneBank.getItem (sceneIndex);
            scene.select ();
            scene.launch ();
        }
    }


    /** {@inheritDoc} */
    @Override
    protected Pair<Integer, Integer> getPad (final int note)
    {
        final Pair<Integer, Integer> pad = super.getPad (note);
        if (this.padMode != null)
            pad.setValue (Integer.valueOf (pad.getValue ().intValue () == 0 ? -1 : 0));
        return pad;
    }


    /**
     * Get the pad mode.
     *
     * @return The pad mode
     */
    public Modes getPadMode ()
    {
        return this.padMode;
    }


    /**
     * Set the pad mode.
     *
     * @param padMode The pad mode
     */
    public void setPadMode (final Modes padMode)
    {
        this.padMode = padMode;
    }


    /**
     * Execute the functions of row 1 if active.
     *
     * @param column The column of the pressed pad
     */
    private void handleFirstRowModes (final int column)
    {
        final ITrack track = this.model.getCurrentTrackBank ().getItem (column);
        switch (this.padMode)
        {
            case REC_ARM:
                track.toggleRecArm ();
                break;
            case TRACK_SELECT:
                this.selectTrack (column);
                break;
            case MUTE:
                track.toggleMute ();
                break;
            case SOLO:
                track.toggleSolo ();
                break;
            case STOP_CLIP:
                track.stop ();
                break;
            default:
                // Unused
                break;
        }
    }
}