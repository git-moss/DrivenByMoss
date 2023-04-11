// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchkey.mini.view;

import de.mossgrabers.controller.novation.launchkey.mini.LaunchkeyMiniMk3Configuration;
import de.mossgrabers.controller.novation.launchkey.mini.controller.LaunchkeyMiniMk3ColorManager;
import de.mossgrabers.controller.novation.launchkey.mini.controller.LaunchkeyMiniMk3ControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.controller.grid.LightInfo;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.Pair;
import de.mossgrabers.framework.view.AbstractSessionView;
import de.mossgrabers.framework.view.Views;

import java.util.List;


/**
 * The Session view.
 *
 * @author Jürgen Moßgraber
 */
public class SessionView extends AbstractSessionView<LaunchkeyMiniMk3ControlSurface, LaunchkeyMiniMk3Configuration>
{
    /** Modes which can take over the first row of pads. */
    public static final List<Modes> PAD_MODES = List.of (Modes.REC_ARM, Modes.TRACK_SELECT, Modes.MUTE, Modes.SOLO, Modes.STOP_CLIP);

    private Modes                   padMode   = null;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SessionView (final LaunchkeyMiniMk3ControlSurface surface, final IModel model)
    {
        super ("Session", surface, model, 2, 8, true);

        final LightInfo isRecording = new LightInfo (LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_RED, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_RED, false);
        final LightInfo isRecordingQueued = new LightInfo (LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_RED_LO, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_RED_LO, true);
        final LightInfo isPlaying = new LightInfo (LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_GREEN_SPRING, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_GREEN, false);
        final LightInfo isPlayingQueued = new LightInfo (LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_GREEN_SPRING, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_GREEN, true);
        final LightInfo hasContent = new LightInfo (LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_AMBER, -1, false);
        final LightInfo noContent = new LightInfo (LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK, -1, false);
        final LightInfo recArmed = new LightInfo (LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_RED_LO, -1, false);
        this.setColors (isRecording, isRecordingQueued, isPlaying, isPlayingQueued, hasContent, noContent, recArmed);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final ColorManager colorManager = this.model.getColorManager ();
        final int colorScene = colorManager.getColorIndex (AbstractSessionView.COLOR_SCENE);
        final int colorSceneSelected = colorManager.getColorIndex (AbstractSessionView.COLOR_SELECTED_SCENE);
        final int colorSceneOff = colorManager.getColorIndex (AbstractSessionView.COLOR_SCENE_OFF);

        final ISceneBank sceneBank = this.model.getSceneBank ();
        IScene s = sceneBank.getItem (0);

        if (buttonID == ButtonID.SCENE1)
        {
            if (!s.doesExist ())
                return colorSceneOff;
            return s.isSelected () ? colorSceneSelected : colorScene;
        }

        // SCENE 2

        if (this.padMode == null)
        {
            s = sceneBank.getItem (1);
            if (!s.doesExist ())
                return colorSceneOff;
            return s.isSelected () ? colorSceneSelected : colorScene;
        }

        switch (this.padMode)
        {
            case REC_ARM:
                return LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_RED_HI;
            case TRACK_SELECT:
                return LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_WHITE;
            case MUTE:
                return LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_AMBER_HI;
            case SOLO:
                return LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_YELLOW_HI;
            case STOP_CLIP:
                return LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_ROSE;
            default:
                return LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK;
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
        final IPadGrid pads = this.surface.getPadGrid ();
        for (int x = 0; x < this.columns; x++)
        {
            final ITrack track = tb.getItem (x);
            final boolean exists = track.doesExist ();
            switch (this.padMode)
            {
                case REC_ARM:
                    int recColor = LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK;
                    if (exists)
                        recColor = track.isRecArm () ? LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_RED_HI : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_RED_LO;
                    pads.lightEx (x, 1, recColor);
                    break;
                case TRACK_SELECT:
                    int selectColor = LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK;
                    if (exists)
                        selectColor = track.isSelected () ? LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_WHITE : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_GREY_LO;
                    pads.lightEx (x, 1, selectColor);
                    break;
                case MUTE:
                    int muteColor = LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK;
                    if (exists)
                        muteColor = track.isMute () ? LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_AMBER_HI : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_AMBER_LO;
                    pads.lightEx (x, 1, muteColor);
                    break;
                case SOLO:
                    int soloColor = LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK;
                    if (exists)
                        soloColor = track.isSolo () ? LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_YELLOW_HI : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_YELLOW_LO;
                    pads.lightEx (x, 1, soloColor);
                    break;
                case STOP_CLIP:
                    pads.lightEx (x, 1, exists ? LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_ROSE : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
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
        final Pair<Integer, Integer> padPos = this.getPad (note);
        final int row = padPos.getValue ().intValue ();
        if (row == 0 || this.padMode == null)
            super.onGridNote (note, velocity);
        else if (velocity != 0)
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
        if (!ButtonID.isSceneButton (buttonID))
            return;

        final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        final ISceneBank sceneBank = this.model.getCurrentTrackBank ().getSceneBank ();

        if (this.surface.isShiftPressed ())
        {
            if (event != ButtonEvent.UP)
                return;

            if (index == 0)
            {
                if (this.padMode == null)
                    sceneBank.selectPreviousPage ();
                else
                    sceneBank.scrollBackwards ();
            }
            else
            {
                if (this.padMode == null)
                    sceneBank.selectNextPage ();
                else
                    sceneBank.scrollForwards ();
            }
            return;
        }

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
            scene.launch (true, false);
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