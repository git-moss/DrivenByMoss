// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.view;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.command.trigger.SelectSessionViewCommand;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.controller.grid.LightInfo;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.Pair;
import de.mossgrabers.framework.view.AbstractSessionView;
import de.mossgrabers.framework.view.Views;


/**
 * Session view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SessionView extends AbstractSessionView<LaunchpadControlSurface, LaunchpadConfiguration>
{
    protected boolean                    isShowTemporarily;
    private final LaunchpadConfiguration configuration;


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

        this.configuration = this.surface.getConfiguration ();

        final LightInfo isRecording = new LightInfo (LaunchpadColorManager.LAUNCHPAD_COLOR_RED_HI, LaunchpadColorManager.LAUNCHPAD_COLOR_RED_HI, false);
        final LightInfo isRecordingQueued = new LightInfo (LaunchpadColorManager.LAUNCHPAD_COLOR_RED_HI, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK, true);
        final LightInfo isPlaying = new LightInfo (LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN, LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN, false);
        final LightInfo isPlayingQueued = new LightInfo (LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN, LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN, true);
        final LightInfo hasContent = new LightInfo (LaunchpadColorManager.LAUNCHPAD_COLOR_AMBER, -1, false);
        final LightInfo noContent = new LightInfo (LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK, -1, false);
        final LightInfo recArmed = new LightInfo (LaunchpadColorManager.LAUNCHPAD_COLOR_RED_LO, -1, false);
        this.setColors (isRecording, isRecordingQueued, isPlaying, isPlayingQueued, hasContent, noContent, recArmed);

        this.birdColorHasContent = hasContent;
        this.birdColorSelected = new LightInfo (LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN, -1, false);

        this.configuration.addSettingObserver (AbstractConfiguration.FLIP_SESSION, this::updateRowsCols);
        this.surface.getModeManager ().addChangeListener ( (oldMode, newMode) -> this.updateRowsCols ());
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();

        this.updateRowsCols ();
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            ((SelectSessionViewCommand) this.surface.getButton (ButtonID.SESSION).getCommand ()).setTemporary ();

        final int modeIndex = this.getControlModeIndex (note);
        if (modeIndex >= 0)
        {
            if (velocity != 0)
                this.handleFirstRowModes (modeIndex);
            return;
        }

        if (this.isBirdsEyeActive ())
        {
            this.onGridNoteBankSelection (note, velocity);
            return;
        }

        super.onGridNote (note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNoteLongPress (final int note)
    {
        final int modeIndex = this.getControlModeIndex (note);
        if (modeIndex >= 0)
            return;

        // Cannot call the super method here since the setConsumed would store the wrong button
        final Pair<Integer, Integer> padPos = this.getPad (note);
        final ITrack track = this.model.getCurrentTrackBank ().getItem (padPos.getKey ().intValue ());
        final ISlot slot = track.getSlotBank ().getItem (padPos.getValue ().intValue ());
        slot.select ();

        final int index = note - 36;
        this.surface.getButton (ButtonID.get (ButtonID.PAD1, index)).setConsumed ();
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        super.onButton (buttonID, event, velocity);

        if (ButtonID.isSceneButton (buttonID) && event == ButtonEvent.UP)
            ((SelectSessionViewCommand) this.surface.getButton (ButtonID.SESSION).getCommand ()).setTemporary ();
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        super.drawGrid ();

        if (this.isControlModeOff ())
            return;

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final IPadGrid pads = this.surface.getPadGrid ();
        final boolean flipSession = this.configuration.isFlipSession ();
        for (int x = 0; x < 8; x++)
        {
            final int padX = flipSession ? 7 : x;
            final int padY = flipSession ? x : 7;
            pads.lightEx (padX, padY, this.getModeColor (tb.getItem (x), x));
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setBirdsEyeActive (final boolean isBirdsEyeActive)
    {
        super.setBirdsEyeActive (isBirdsEyeActive);

        this.updateRowsCols ();
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
                return DAWColor.getColorID (s.getColor ());
        }

        return AbstractFeatureGroup.BUTTON_COLOR_OFF;
    }


    /** {@inheritDoc} */
    @Override
    protected void drawSessionGrid ()
    {
        this.drawSessionGrid (true);
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
                this.isShowTemporarily = true;
                break;

            case UP:
                if (!this.isShowTemporarily)
                    return;
                this.isShowTemporarily = false;

                final ViewManager viewManager = this.surface.getViewManager ();
                final ITrack cursorTrack = this.model.getCursorTrack ();
                if (!cursorTrack.doesExist ())
                    return;
                final Views viewId = viewManager.getPreferredView (cursorTrack.getPosition ());
                viewManager.setActive (viewId == null ? Views.PLAY : viewId);
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
        return this.isBirdsEyeActive;
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleButtonCombinations (final ITrack track, final ISlot slot)
    {
        final boolean result = super.handleButtonCombinations (track, slot);

        if (this.isButtonCombination (ButtonID.DELETE) && this.configuration.isDeleteModeActive ())
            this.configuration.toggleDeleteModeActive ();
        else if (this.isButtonCombination (ButtonID.DUPLICATE) && this.configuration.isDuplicateModeActive () && (!slot.doesExist () || !slot.hasContent ()))
            this.configuration.toggleDuplicateModeActive ();

        return result;
    }


    /** {@inheritDoc} */
    @Override
    protected boolean isButtonCombination (final ButtonID buttonID)
    {
        if (super.isButtonCombination (buttonID) || buttonID == ButtonID.DELETE && this.configuration.isDeleteModeActive ())
            return true;

        return buttonID == ButtonID.DUPLICATE && this.configuration.isDuplicateModeActive ();
    }


    /**
     * Handle pad presses in the birds eye view.
     *
     * @param note The note of the pad
     * @param velocity The velocity of the press
     */
    protected void onGridNoteBankSelection (final int note, final int velocity)
    {
        if (velocity == 0)
            return;
        final int index = note - 36;
        this.onGridNoteBirdsEyeView (index % this.columns, this.rows - 1 - index / this.columns, 0);
    }


    /**
     * Execute the functions of row 1 if active.
     *
     * @param index The index on the first row
     */
    private void handleFirstRowModes (final int index)
    {
        // First row mode handling
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
        if (modeManager.isActive (Modes.REC_ARM))
            track.toggleRecArm ();
        else if (modeManager.isActive (Modes.TRACK_SELECT))
        {
            track.selectOrExpandGroup ();
            this.mvHelper.notifySelectedTrack ();
        }
        else if (modeManager.isActive (Modes.MUTE))
            track.toggleMute ();
        else if (modeManager.isActive (Modes.SOLO))
            track.toggleSolo ();
        else if (modeManager.isActive (Modes.STOP_CLIP))
            track.stop ();
    }


    private boolean isControlModeOff ()
    {
        return this.surface.hasTrackSelectionButtons () || this.isBirdsEyeActive () || this.surface.getModeManager ().getActiveID () == Modes.DUMMY;
    }


    private void updateRowsCols ()
    {
        this.rows = 8;
        this.columns = 8;

        if (!this.isControlModeOff ())
            this.rows = 7;
    }


    /**
     * Get the color to use for a mode pad.
     *
     * @param track The for which to reflect states on the mode, e.g. solo, mute
     * @param index The index of the pad 0-7
     * @return The color
     */
    private int getModeColor (final ITrack track, final int index)
    {
        if (track.doesExist ())
        {
            final ModeManager modeManager = this.surface.getModeManager ();

            if (modeManager.isActive (Modes.REC_ARM))
                return track.isRecArm () ? LaunchpadColorManager.LAUNCHPAD_COLOR_RED_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_RED_LO;

            if (modeManager.isActive (Modes.TRACK_SELECT))
                return track.isSelected () ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_LO;

            if (modeManager.isActive (Modes.MUTE))
                return track.isMute () ? LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW_LO;

            if (modeManager.isActive (Modes.SOLO))
                return track.isSolo () ? LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_LO;

            if (modeManager.isActive (Modes.STOP_CLIP))
                return this.surface.isPressed (ButtonID.get (ButtonID.PAD1, index)) ? LaunchpadColorManager.LAUNCHPAD_COLOR_RED : LaunchpadColorManager.LAUNCHPAD_COLOR_ROSE;
        }

        return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
    }


    /**
     * Get the index of the pressed mode button if a mode is active.
     *
     * @param note The note to test
     * @return The index 0-7 or -1 if no mode button was pressed
     */
    private int getControlModeIndex (final int note)
    {
        if (this.isControlModeOff ())
            return -1;

        if (this.configuration.isFlipSession ())
        {
            final int n = note + 1 - 36;
            if (n % 8 == 0)
                return 8 - n / 8;
            return -1;
        }

        if (note < 44)
            return note - 36;

        return -1;
    }


    /** {@inheritDoc} */
    @Override
    protected Pair<Integer, Integer> getPad (final int note)
    {
        final int index = note - 36;
        final int x = index % 8;
        final int y = 7 - index / 8;
        return this.configuration.isFlipSession () ? new Pair<> (Integer.valueOf (y), Integer.valueOf (x)) : new Pair<> (Integer.valueOf (x), Integer.valueOf (y));
    }
}
