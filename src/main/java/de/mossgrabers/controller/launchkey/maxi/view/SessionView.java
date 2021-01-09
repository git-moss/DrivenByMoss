// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchkey.maxi.view;

import de.mossgrabers.controller.launchkey.maxi.LaunchkeyMk3Configuration;
import de.mossgrabers.controller.launchkey.maxi.controller.LaunchkeyMk3ColorManager;
import de.mossgrabers.controller.launchkey.maxi.controller.LaunchkeyMk3ControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
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
public class SessionView extends AbstractSessionView<LaunchkeyMk3ControlSurface, LaunchkeyMk3Configuration>
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
    public SessionView (final LaunchkeyMk3ControlSurface surface, final IModel model)
    {
        super ("Session", surface, model, 2, 8, true);

        final SessionColor isRecording = new SessionColor (LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_RED, LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_RED, false);
        final SessionColor isRecordingQueued = new SessionColor (LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_RED_LO, LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_RED_LO, true);
        final SessionColor isPlaying = new SessionColor (LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREEN_SPRING, LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREEN, false);
        final SessionColor isPlayingQueued = new SessionColor (LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREEN_SPRING, LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREEN, true);
        final SessionColor hasContent = new SessionColor (LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_AMBER, -1, false);
        final SessionColor noContent = new SessionColor (LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK, -1, false);
        final SessionColor recArmed = new SessionColor (LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_RED_LO, -1, false);
        this.setColors (isRecording, isRecordingQueued, isPlaying, isPlayingQueued, hasContent, noContent, recArmed);
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
        final IPadGrid pads = this.surface.getPadGrid ();
        for (int x = 0; x < this.columns; x++)
        {
            final ITrack track = tb.getItem (x);
            final boolean exists = track.doesExist ();
            switch (this.padMode)
            {
                case REC_ARM:
                    pads.lightEx (x, 1, exists ? track.isRecArm () ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_RED_HI : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_RED_LO : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
                    break;
                case TRACK_SELECT:
                    pads.lightEx (x, 1, exists ? track.isSelected () ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_WHITE : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREY_LO : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
                    break;
                case MUTE:
                    pads.lightEx (x, 1, exists ? track.isMute () ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_AMBER_HI : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_AMBER_LO : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
                    break;
                case SOLO:
                    pads.lightEx (x, 1, exists ? track.isSolo () ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_YELLOW_HI : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_YELLOW_LO : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
                    break;
                case STOP_CLIP:
                    pads.lightEx (x, 1, exists ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_ROSE : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
                    break;
                default:
                    // Unused
                    break;
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity != 0)
            return;

        final Pair<Integer, Integer> padPos = this.getPad (note);
        final int row = padPos.getValue ().intValue ();
        if (row == 0 || this.padMode == null)
            super.onGridNote (note, velocity);
        else
            this.handleFirstRowModes (padPos.getKey ().intValue ());
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNoteLongPress (final int note)
    {
        final Pair<Integer, Integer> padPos = this.getPad (note);
        final int row = padPos.getValue ().intValue ();
        if (row == 0 || this.padMode == null)
            super.onGridNoteLongPress (note);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        final ISceneBank sceneBank = this.model.getCurrentTrackBank ().getSceneBank ();

        if (buttonID == ButtonID.ARROW_UP || buttonID == ButtonID.ARROW_DOWN)
        {
            if (event != ButtonEvent.UP)
                return;

            if (buttonID == ButtonID.ARROW_UP)
                sceneBank.selectPreviousPage ();
            else
                sceneBank.selectNextPage ();
            return;
        }

        if (!ButtonID.isSceneButton (buttonID))
            return;

        final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();

        if (event == ButtonEvent.DOWN)
        {
            if (index == 1)
                this.surface.getViewManager ().setActive (Views.CONTROL);
            return;
        }

        if (event == ButtonEvent.UP)
        {
            final IScene scene = sceneBank.getItem (index);
            scene.select ();
            scene.launch ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final ISceneBank sceneBank = this.model.getSceneBank ();
        final ColorManager colorManager = this.model.getColorManager ();

        if (buttonID == ButtonID.ARROW_UP || buttonID == ButtonID.ARROW_DOWN)
        {
            final boolean isOn = buttonID == ButtonID.ARROW_UP ? sceneBank.canScrollPageBackwards () : sceneBank.canScrollPageForwards ();
            return isOn ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREEN : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREY_LO;
        }

        if (!ButtonID.isSceneButton (buttonID))
            return LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK;

        if (buttonID == ButtonID.SCENE1)
            return colorManager.getColorIndex (getSceneColor (sceneBank.getItem (0)));

        // SCENE 2

        if (this.padMode == null)
            return colorManager.getColorIndex (getSceneColor (sceneBank.getItem (1)));

        switch (this.padMode)
        {
            case REC_ARM:
                return LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_RED_HI;
            case TRACK_SELECT:
                return LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_WHITE;
            case MUTE:
                return LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_AMBER_HI;
            case SOLO:
                return LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_YELLOW_HI;
            case STOP_CLIP:
                return LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_ROSE;
            default:
                return LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK;
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


    private static String getSceneColor (final IScene scene)
    {
        if (scene.doesExist ())
            return scene.isSelected () ? AbstractSessionView.COLOR_SELECTED_SCENE : AbstractSessionView.COLOR_SCENE;
        return AbstractSessionView.COLOR_SCENE_OFF;
    }
}