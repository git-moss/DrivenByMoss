// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.view;

import de.mossgrabers.controller.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractSessionView;
import de.mossgrabers.framework.view.SessionColor;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;


/**
 * Session view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SessionView extends AbstractSessionView<LaunchpadControlSurface, LaunchpadConfiguration>
{
    protected boolean isTemporary;
    private boolean   isBirdsEyeViewActive = false;


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     */
    public SessionView (final String name, final LaunchpadControlSurface surface, final IModel model)
    {
        super (name, surface, model, 8, 8, true);

        final SessionColor isRecording = new SessionColor (LaunchpadColorManager.LAUNCHPAD_COLOR_RED_HI, LaunchpadColorManager.LAUNCHPAD_COLOR_RED_HI, false);
        final SessionColor isRecordingQueued = new SessionColor (LaunchpadColorManager.LAUNCHPAD_COLOR_RED_HI, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK, true);
        final SessionColor isPlaying = new SessionColor (LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN, LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN, false);
        final SessionColor isPlayingQueued = new SessionColor (LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN, LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN, true);
        final SessionColor hasContent = new SessionColor (LaunchpadColorManager.LAUNCHPAD_COLOR_AMBER, -1, false);
        final SessionColor noContent = new SessionColor (LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK, -1, false);
        final SessionColor recArmed = new SessionColor (LaunchpadColorManager.LAUNCHPAD_COLOR_RED_LO, -1, false);
        this.setColors (isRecording, isRecordingQueued, isPlaying, isPlayingQueued, hasContent, noContent, recArmed);

        this.birdColorHasContent = hasContent;
        this.birdColorSelected = new SessionColor (LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN, -1, false);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        final boolean controlModeIsOff = this.isControlModeOff ();

        // Block 1st row if mode is active
        final boolean isNotRow1 = note >= 44;
        if (controlModeIsOff || isNotRow1)
        {
            if (this.isBirdsEyeActive ())
            {
                this.onGridNoteBankSelection (note, velocity, isNotRow1);
                return;
            }

            final int n = note - (controlModeIsOff ? 0 : 8);

            super.onGridNote (n, velocity);
            return;
        }

        if (velocity != 0)
            this.handleFirstRowModes (note);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final boolean controlModeIsOff = this.isControlModeOff ();

        if (this.surface.getConfiguration ().isFlipSession ())
        {
            this.rows = 8;
            this.columns = controlModeIsOff ? 8 : 7;
        }
        else
        {
            this.rows = controlModeIsOff ? 8 : 7;
            this.columns = 8;
        }

        super.drawGrid ();

        if (controlModeIsOff)
            return;

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final IPadGrid pads = this.surface.getPadGrid ();
        final ModeManager modeManager = this.surface.getModeManager ();
        for (int x = 0; x < 8; x++)
        {
            final ITrack track = tb.getItem (x);
            final boolean exists = track.doesExist ();
            if (modeManager.isActiveOrTempMode (Modes.REC_ARM))
                pads.lightEx (x, 7, exists ? track.isRecArm () ? LaunchpadColorManager.LAUNCHPAD_COLOR_RED_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_RED_LO : LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
            else if (modeManager.isActiveOrTempMode (Modes.TRACK_SELECT))
                pads.lightEx (x, 7, exists ? track.isSelected () ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_LO : LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
            else if (modeManager.isActiveOrTempMode (Modes.MUTE))
                pads.lightEx (x, 7, exists ? track.isMute () ? LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW_LO : LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
            else if (modeManager.isActiveOrTempMode (Modes.SOLO))
                pads.lightEx (x, 7, exists ? track.isSolo () ? LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_LO : LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
            else if (modeManager.isActiveOrTempMode (Modes.STOP_CLIP))
                pads.lightEx (x, 7, exists ? this.surface.isPressed (ButtonID.get (ButtonID.PAD1, x)) ? LaunchpadColorManager.LAUNCHPAD_COLOR_RED : LaunchpadColorManager.LAUNCHPAD_COLOR_ROSE : LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        }
    }


    /**
     * Set the birds eye view in-/active.
     *
     * @param isBirdsEyeActive True to activate
     */
    public void setBirdsEyeActive (final boolean isBirdsEyeActive)
    {
        this.isBirdsEyeViewActive = isBirdsEyeActive;
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        if (index >= 0 || index < 8)
        {
            final ITrackBank tb = this.model.getCurrentTrackBank ();
            final ISceneBank sceneBank = tb.getSceneBank ();
            final IScene s = sceneBank.getItem (index);

            if (s.doesExist ())
                return DAWColor.getColorIndex (s.getColor ());
        }

        return AbstractMode.BUTTON_COLOR_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        if (this.surface.getButton (buttonID).isPressed ())
            return LaunchpadColorManager.LAUNCHPAD_COLOR_WHITE;
        return super.getButtonColor (buttonID);
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
                final Views viewId = viewManager.getPreferredView (selectedTrack.getPosition ());
                viewManager.setActiveView (viewId == null ? Views.PLAY : viewId);
                break;

            default:
                // Intentionally empty
                return;
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean isBirdsEyeActive ()
    {
        return this.isBirdsEyeViewActive;
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleButtonCombinations (final ITrack track, final ISlot slot)
    {
        final boolean result = super.handleButtonCombinations (track, slot);

        final LaunchpadConfiguration configuration = this.surface.getConfiguration ();
        if (this.isButtonCombination (ButtonID.DELETE) && configuration.isDeleteModeActive ())
            configuration.toggleDeleteModeActive ();
        else if (this.isButtonCombination (ButtonID.DUPLICATE) && configuration.isDuplicateModeActive () && (!slot.doesExist () || !slot.hasContent ()))
            configuration.toggleDuplicateModeActive ();

        return result;
    }


    /** {@inheritDoc} */
    @Override
    protected boolean isButtonCombination (final ButtonID buttonID)
    {
        if (super.isButtonCombination (buttonID))
            return true;

        final LaunchpadConfiguration configuration = this.surface.getConfiguration ();
        if (buttonID == ButtonID.DELETE && configuration.isDeleteModeActive ())
            return true;

        return buttonID == ButtonID.DUPLICATE && configuration.isDuplicateModeActive ();
    }


    /**
     * Handle pad presses in the birds eye view.
     *
     * @param note The note of the pad
     * @param velocity The velocity of the press
     * @param isNotOffset Apply row 1 note offset if false
     */
    protected void onGridNoteBankSelection (final int note, final int velocity, final boolean isNotOffset)
    {
        if (velocity == 0)
            return;
        final int n = isNotOffset ? note : note - 8;
        final int index = n - 36;
        this.onGridNoteBirdsEyeView (index % this.columns, this.rows - 1 - index / this.columns, isNotOffset ? 0 : 1);
    }


    /**
     * Execute the functions of row 1 if active.
     *
     * @param note The pressed note on the first row
     */
    private void handleFirstRowModes (final int note)
    {
        // First row mode handling
        final int index = note - 36;
        final ITrack track = this.model.getCurrentTrackBank ().getItem (index);

        if (this.isButtonCombination (ButtonID.DELETE))
        {
            track.remove ();
            return;
        }

        if (this.isButtonCombination (ButtonID.DUPLICATE))
        {
            track.duplicate ();
            return;
        }

        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActiveOrTempMode (Modes.REC_ARM))
            track.toggleRecArm ();
        else if (modeManager.isActiveOrTempMode (Modes.TRACK_SELECT))
        {
            track.select ();
            this.surface.getDisplay ().notify (track.getPosition () + 1 + ": " + track.getName ());
        }
        else if (modeManager.isActiveOrTempMode (Modes.MUTE))
            track.toggleMute ();
        else if (modeManager.isActiveOrTempMode (Modes.SOLO))
            track.toggleSolo ();
        else if (modeManager.isActiveOrTempMode (Modes.STOP_CLIP))
            track.stop ();
    }


    private boolean isControlModeOff ()
    {
        return this.surface.hasTrackSelectionButtons () || this.surface.getModeManager ().getActiveOrTempModeId () == Modes.DUMMY;
    }
}
