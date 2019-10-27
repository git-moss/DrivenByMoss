// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.view;

import de.mossgrabers.controller.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.launchpad.controller.LaunchpadColors;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.command.TriggerCommandID;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
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

        padGrid.light (97, LaunchpadColors.LAUNCHPAD_COLOR_GREEN);
        padGrid.light (98, LaunchpadColors.LAUNCHPAD_COLOR_GREEN_SPRING);
        padGrid.light (99, LaunchpadColors.LAUNCHPAD_COLOR_TURQUOISE_CYAN);

        final int clipLengthIndex = this.surface.getConfiguration ().getNewClipLength ();
        for (int i = 0; i < 8; i++)
            padGrid.light (36 + i, i == clipLengthIndex ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);

        if (this.surface.isPro ())
        {
            for (int i = 44; i < 97; i++)
                padGrid.light (i, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
            return;
        }

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


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        switch (note)
        {
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
                final int newClipLength = note - 36;
                this.surface.getConfiguration ().setNewClipLength (newClipLength);
                this.surface.getDisplay ().notify ("New clip length: " + AbstractConfiguration.getNewClipLengthValue (newClipLength));
                break;

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
                this.executeNormal (TriggerCommandID.METRONOME, ButtonEvent.DOWN);
                break;
            case 93:
                this.executeShifted (TriggerCommandID.METRONOME, ButtonEvent.DOWN);
                break;
            case 84:
                this.executeNormal (TriggerCommandID.UNDO, ButtonEvent.DOWN);
                break;
            case 85:
                this.executeShifted (TriggerCommandID.UNDO, ButtonEvent.DOWN);
                break;
            case 76:
                this.executeNormal (TriggerCommandID.DELETE, ButtonEvent.UP);
                break;
            case 68:
                this.executeNormal (TriggerCommandID.QUANTIZE, ButtonEvent.DOWN);
                break;
            case 60:
                this.executeNormal (TriggerCommandID.DUPLICATE, ButtonEvent.DOWN);
                break;
            case 61:
                this.executeShifted (TriggerCommandID.DUPLICATE, ButtonEvent.DOWN);
                break;
            case 52:
                this.executeNormal (TriggerCommandID.PLAY, ButtonEvent.DOWN);
                break;
            case 53:
                this.executeNormal (TriggerCommandID.NEW, ButtonEvent.DOWN);
                break;
            case 44:
                this.executeNormal (TriggerCommandID.RECORD, ButtonEvent.UP);
                break;
            case 45:
                this.executeShifted (TriggerCommandID.RECORD, ButtonEvent.UP);
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
    public void updateSceneButtons ()
    {
        final boolean isPro = this.surface.isPro ();
        this.surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE1, isPro ? LaunchpadColors.LAUNCHPAD_COLOR_BLACK : LaunchpadColors.LAUNCHPAD_COLOR_CYAN);
        this.surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE2, isPro ? LaunchpadColors.LAUNCHPAD_COLOR_BLACK : LaunchpadColors.LAUNCHPAD_COLOR_SKY);
        this.surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE3, isPro ? LaunchpadColors.LAUNCHPAD_COLOR_BLACK : LaunchpadColors.LAUNCHPAD_COLOR_ORCHID);
        this.surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE4, isPro ? LaunchpadColors.LAUNCHPAD_COLOR_BLACK : LaunchpadColors.LAUNCHPAD_COLOR_GREEN);
        this.surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE5, isPro ? LaunchpadColors.LAUNCHPAD_COLOR_BLACK : LaunchpadColors.LAUNCHPAD_COLOR_ROSE);
        this.surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE6, isPro ? LaunchpadColors.LAUNCHPAD_COLOR_BLACK : LaunchpadColors.LAUNCHPAD_COLOR_YELLOW);
        this.surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE7, isPro ? LaunchpadColors.LAUNCHPAD_COLOR_BLACK : LaunchpadColors.LAUNCHPAD_COLOR_BLUE);
        this.surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE8, isPro ? LaunchpadColors.LAUNCHPAD_COLOR_BLACK : LaunchpadColors.LAUNCHPAD_COLOR_RED);
    }


    private boolean handleControlModes (final TriggerCommandID commandID)
    {
        final View view = this.surface.getViewManager ().getActiveView ();
        view.getTriggerCommand (commandID).execute (ButtonEvent.DOWN);
        final ModeManager modeManager = this.surface.getModeManager ();
        final Modes activeOrTempModeId = modeManager.getActiveOrTempModeId ();
        if (activeOrTempModeId != null && activeOrTempModeId.equals (modeManager.getPreviousModeId ()))
            modeManager.setActiveMode (null);
        return true;
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
                this.handleControlModes (TriggerCommandID.VOLUME);
                break;
            case 1:
                this.handleControlModes (TriggerCommandID.PAN_SEND);
                break;
            case 2:
                this.handleControlModes (TriggerCommandID.SENDS);
                break;
            case 3:
                this.handleControlModes (TriggerCommandID.TRACK);
                break;
            case 4:
                this.handleControlModes (TriggerCommandID.STOP_CLIP);
                break;
            case 5:
                this.handleControlModes (TriggerCommandID.MUTE);
                break;
            case 6:
                this.handleControlModes (TriggerCommandID.SOLO);
                break;
            case 7:
                this.handleControlModes (TriggerCommandID.REC_ARM);
                break;
            default:
                // Not used
                break;
        }
    }


    @SuppressWarnings("rawtypes")
    private void executeNormal (final TriggerCommandID commandID, final ButtonEvent event)
    {
        ((AbstractTriggerCommand) this.surface.getViewManager ().getActiveView ().getTriggerCommand (commandID)).executeNormal (event);
    }


    @SuppressWarnings("rawtypes")
    private void executeShifted (final TriggerCommandID commandID, final ButtonEvent event)
    {
        ((AbstractTriggerCommand) this.surface.getViewManager ().getActiveView ().getTriggerCommand (commandID)).executeShifted (event);
    }
}