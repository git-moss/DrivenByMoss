// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.view;

import de.mossgrabers.controller.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.launchpad.controller.LaunchpadColors;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractView;
import de.mossgrabers.framework.view.SceneView;
import de.mossgrabers.framework.view.View;


/**
 * Simulates the missing buttons (in contrast to Launchpad Pro) on the grid.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ShiftView extends AbstractView<LaunchpadControlSurface, LaunchpadConfiguration> implements SceneView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ShiftView (final LaunchpadControlSurface surface, final IModel model)
    {
        super ("Shift", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.surface.setLaunchpadToPrgMode ();
        super.onActivate ();
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final PadGrid padGrid = this.surface.getPadGrid ();

        if (this.surface.isPro ())
        {
            for (int i = 36; i < 97; i++)
                padGrid.light (i, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        }
        else
        {
            padGrid.light (36, LaunchpadColors.LAUNCHPAD_COLOR_RED);
            padGrid.light (37, LaunchpadColors.LAUNCHPAD_COLOR_GREEN);
            padGrid.light (38, LaunchpadColors.LAUNCHPAD_COLOR_YELLOW);
            padGrid.light (39, LaunchpadColors.LAUNCHPAD_COLOR_BLUE);
            padGrid.light (40, LaunchpadColors.LAUNCHPAD_COLOR_CYAN);
            padGrid.light (41, LaunchpadColors.LAUNCHPAD_COLOR_SKY);
            padGrid.light (42, LaunchpadColors.LAUNCHPAD_COLOR_ORCHID);
            padGrid.light (43, LaunchpadColors.LAUNCHPAD_COLOR_ROSE);

            padGrid.light (44, LaunchpadColors.LAUNCHPAD_COLOR_RED);
            padGrid.light (45, LaunchpadColors.LAUNCHPAD_COLOR_ROSE);

            for (int i = 46; i < 51; i++)
                padGrid.light (i, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);

            padGrid.light (51, LaunchpadColors.LAUNCHPAD_COLOR_RED);
            padGrid.light (52, LaunchpadColors.LAUNCHPAD_COLOR_GREEN);
            padGrid.light (53, LaunchpadColors.LAUNCHPAD_COLOR_GREEN_SPRING);

            for (int i = 54; i < 60; i++)
                padGrid.light (i, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);

            padGrid.light (60, LaunchpadColors.LAUNCHPAD_COLOR_GREEN);
            padGrid.light (61, LaunchpadColors.LAUNCHPAD_COLOR_GREEN_SPRING);

            for (int i = 62; i < 68; i++)
                padGrid.light (i, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);

            padGrid.light (68, LaunchpadColors.LAUNCHPAD_COLOR_GREEN);
            padGrid.light (69, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);

            for (int i = 70; i < 76; i++)
                padGrid.light (i, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);

            padGrid.light (76, LaunchpadColors.LAUNCHPAD_COLOR_GREEN);
            padGrid.light (77, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);

            for (int i = 78; i < 84; i++)
                padGrid.light (i, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);

            padGrid.light (84, LaunchpadColors.LAUNCHPAD_COLOR_GREEN);
            padGrid.light (85, LaunchpadColors.LAUNCHPAD_COLOR_GREEN_SPRING);

            for (int i = 86; i < 92; i++)
                padGrid.light (i, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);

            padGrid.light (92, LaunchpadColors.LAUNCHPAD_COLOR_GREEN);
            padGrid.light (93, LaunchpadColors.LAUNCHPAD_COLOR_GREEN_SPRING);

            for (int i = 94; i < 97; i++)
                padGrid.light (i, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        }

        padGrid.light (97, LaunchpadColors.LAUNCHPAD_COLOR_GREEN);
        padGrid.light (98, LaunchpadColors.LAUNCHPAD_COLOR_GREEN_SPRING);
        padGrid.light (99, LaunchpadColors.LAUNCHPAD_COLOR_TURQUOISE_CYAN);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (!this.surface.isPro () && this.handleControlModes (note, velocity))
            return;
        if (velocity > 0)
            this.handleFunctions (note);
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        final boolean isPro = this.surface.isPro ();
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE1, isPro ? LaunchpadColors.LAUNCHPAD_COLOR_BLACK : LaunchpadColors.LAUNCHPAD_COLOR_CYAN);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE2, isPro ? LaunchpadColors.LAUNCHPAD_COLOR_BLACK : LaunchpadColors.LAUNCHPAD_COLOR_SKY);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE3, isPro ? LaunchpadColors.LAUNCHPAD_COLOR_BLACK : LaunchpadColors.LAUNCHPAD_COLOR_ORCHID);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE4, isPro ? LaunchpadColors.LAUNCHPAD_COLOR_BLACK : LaunchpadColors.LAUNCHPAD_COLOR_GREEN);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE5, isPro ? LaunchpadColors.LAUNCHPAD_COLOR_BLACK : LaunchpadColors.LAUNCHPAD_COLOR_ROSE);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE6, isPro ? LaunchpadColors.LAUNCHPAD_COLOR_BLACK : LaunchpadColors.LAUNCHPAD_COLOR_YELLOW);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE7, isPro ? LaunchpadColors.LAUNCHPAD_COLOR_BLACK : LaunchpadColors.LAUNCHPAD_COLOR_BLUE);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE8, isPro ? LaunchpadColors.LAUNCHPAD_COLOR_BLACK : LaunchpadColors.LAUNCHPAD_COLOR_RED);
    }


    private boolean handleControlModes (final int note, final int velocity)
    {
        final ButtonEvent event = velocity == 0 ? ButtonEvent.UP : ButtonEvent.DOWN;
        final View view = this.surface.getViewManager ().getActiveView ();
        switch (note)
        {
            case 36:
                view.getTriggerCommand (Commands.COMMAND_REC_ARM).execute (event);
                break;
            case 37:
                view.getTriggerCommand (Commands.COMMAND_TRACK).execute (event);
                break;
            case 38:
                view.getTriggerCommand (Commands.COMMAND_MUTE).execute (event);
                break;
            case 39:
                view.getTriggerCommand (Commands.COMMAND_SOLO).execute (event);
                break;
            case 40:
                view.getTriggerCommand (Commands.COMMAND_VOLUME).execute (event);
                break;
            case 41:
                view.getTriggerCommand (Commands.COMMAND_PAN_SEND).execute (event);
                break;
            case 42:
                view.getTriggerCommand (Commands.COMMAND_SENDS).execute (event);
                break;
            case 43:
                view.getTriggerCommand (Commands.COMMAND_STOP_CLIP).execute (event);
                break;
            default:
                return false;
        }
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.getPreviousModeId () == modeManager.getActiveOrTempModeId ())
            modeManager.setActiveMode (null);
        return true;
    }


    private void handleFunctions (final int note)
    {
        final View view = this.surface.getViewManager ().getActiveView ();

        switch (note)
        {
            case 97:
                this.model.getApplication ().addInstrumentTrack ();
                return;
            case 98:
                this.model.getApplication ().addAudioTrack ();
                return;
            case 99:
                this.model.getApplication ().addEffectTrack ();
                return;
            default:
                // Not used
                break;
        }

        if (this.surface.isPro ())
            return;

        switch (note)
        {
            case 92:
                view.getTriggerCommand (Commands.COMMAND_METRONOME).executeNormal (ButtonEvent.DOWN);
                break;
            case 93:
                view.getTriggerCommand (Commands.COMMAND_METRONOME).executeShifted (ButtonEvent.DOWN);
                break;
            case 84:
                view.getTriggerCommand (Commands.COMMAND_UNDO).executeNormal (ButtonEvent.DOWN);
                break;
            case 85:
                view.getTriggerCommand (Commands.COMMAND_UNDO).executeShifted (ButtonEvent.DOWN);
                break;
            case 76:
                view.getTriggerCommand (Commands.COMMAND_DELETE).executeNormal (ButtonEvent.UP);
                break;
            case 68:
                view.getTriggerCommand (Commands.COMMAND_QUANTIZE).executeNormal (ButtonEvent.DOWN);
                break;
            case 60:
                view.getTriggerCommand (Commands.COMMAND_DUPLICATE).executeNormal (ButtonEvent.DOWN);
                break;
            case 61:
                view.getTriggerCommand (Commands.COMMAND_DUPLICATE).executeShifted (ButtonEvent.DOWN);
                break;
            case 52:
                view.getTriggerCommand (Commands.COMMAND_NEW).execute (ButtonEvent.DOWN);
                break;
            case 53:
                view.getTriggerCommand (Commands.COMMAND_PLAY).execute (ButtonEvent.DOWN);
                break;
            case 44:
                view.getTriggerCommand (Commands.COMMAND_RECORD).executeNormal (ButtonEvent.UP);
                break;
            case 45:
                view.getTriggerCommand (Commands.COMMAND_RECORD).executeShifted (ButtonEvent.UP);
                break;
            case 51:
                this.model.getCurrentTrackBank ().stop ();
                break;
            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int scene, final ButtonEvent event)
    {
        if (this.surface.isPro () || event != ButtonEvent.DOWN)
            return;

        switch (scene)
        {
            case 0:
                this.handleControlModes (40, 127);
                break;
            case 1:
                this.handleControlModes (41, 127);
                break;
            case 2:
                this.handleControlModes (42, 127);
                break;
            case 3:
                this.handleControlModes (37, 127);
                break;
            case 4:
                this.handleControlModes (43, 127);
                break;
            case 5:
                this.handleControlModes (38, 127);
                break;
            case 6:
                this.handleControlModes (39, 127);
                break;
            case 7:
                this.handleControlModes (36, 127);
                break;
            default:
                // Not used
                break;
        }
    }
}