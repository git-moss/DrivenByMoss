// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apcmini.view;

import de.mossgrabers.controller.akai.apcmini.APCminiConfiguration;
import de.mossgrabers.controller.akai.apcmini.controller.APCminiControlSurface;
import de.mossgrabers.controller.akai.apcmini.controller.APCminiMk1ColorManager;
import de.mossgrabers.controller.akai.apcmini.controller.APCminiMk2ColorManager;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.LightInfo;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractSessionView;


/**
 * Session view.
 *
 * @author Jürgen Moßgraber
 */
public class SessionView extends AbstractSessionView<APCminiControlSurface, APCminiConfiguration> implements APCminiView
{
    private final TrackButtons extensions;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     * @param trackButtons The track button control
     * @param useTrackColor True to use the color of the current track for coloring the octaves
     */
    public SessionView (final APCminiControlSurface surface, final IModel model, final TrackButtons trackButtons, final boolean useTrackColor)
    {
        super ("Session", surface, model, 8, 8, useTrackColor);

        this.extensions = trackButtons;

        if (useTrackColor)
        {
            this.ignoreClipColorForPlayAndRecord = true;

            final LightInfo isRecording = new LightInfo (APCminiMk2ColorManager.RED, APCminiMk2ColorManager.RED, false);
            final LightInfo isRecordingQueued = new LightInfo (APCminiMk2ColorManager.RED, APCminiMk2ColorManager.RED, true);
            final LightInfo isPlaying = new LightInfo (APCminiMk2ColorManager.GREEN, APCminiMk2ColorManager.GREEN, false);
            final LightInfo isPlayingQueued = new LightInfo (APCminiMk2ColorManager.GREEN, APCminiMk2ColorManager.GREEN, true);
            final LightInfo isStopQueued = new LightInfo (APCminiMk2ColorManager.GREEN, APCminiMk2ColorManager.GREEN, true);
            final LightInfo hasContent = new LightInfo (APCminiMk2ColorManager.ORANGE, APCminiMk2ColorManager.WHITE, false);
            final LightInfo noContent = new LightInfo (APCminiMk2ColorManager.BLACK, -1, false);
            final LightInfo recArmed = new LightInfo (APCminiMk2ColorManager.DARK_RED, -1, false);
            final LightInfo isMuted = new LightInfo (APCminiMk2ColorManager.DARK_GRAY, -1, false);
            this.setColors (isRecording, isRecordingQueued, isPlaying, isPlayingQueued, isStopQueued, hasContent, noContent, recArmed, isMuted);
        }
        else
        {
            final LightInfo isRecording = new LightInfo (APCminiMk1ColorManager.APC_COLOR_RED, -1, false);
            final LightInfo isRecordingQueued = new LightInfo (APCminiMk1ColorManager.APC_COLOR_RED, APCminiMk1ColorManager.APC_COLOR_RED_BLINK, false);
            final LightInfo isPlaying = new LightInfo (APCminiMk1ColorManager.APC_COLOR_GREEN, -1, false);
            final LightInfo isPlayingQueued = new LightInfo (APCminiMk1ColorManager.APC_COLOR_GREEN, APCminiMk1ColorManager.APC_COLOR_GREEN_BLINK, false);
            final LightInfo isStopQueued = new LightInfo (APCminiMk1ColorManager.APC_COLOR_GREEN, APCminiMk1ColorManager.APC_COLOR_GREEN_BLINK, false);
            final LightInfo hasContent = new LightInfo (APCminiMk1ColorManager.APC_COLOR_YELLOW, APCminiMk1ColorManager.APC_COLOR_YELLOW_BLINK, false);
            final LightInfo noContent = new LightInfo (APCminiMk1ColorManager.APC_COLOR_BLACK, -1, false);
            final LightInfo recArmed = new LightInfo (APCminiMk1ColorManager.APC_COLOR_BLACK, -1, false);
            final LightInfo isMuted = new LightInfo (APCminiMk1ColorManager.APC_COLOR_BLACK, -1, false);
            this.setColors (isRecording, isRecordingQueued, isPlaying, isPlayingQueued, isStopQueued, hasContent, noContent, recArmed, isMuted);
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean doSelectClipOnLaunch ()
    {
        return this.surface.getConfiguration ().isSelectClipOnLaunch ();
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        super.onButton (buttonID, event, velocity);

        if (ButtonID.isSceneButton (buttonID) && event == ButtonEvent.UP && this.surface.isShiftPressed ())
            this.setAlternateInteractionUsed (true);
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        final int scene = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        if (scene < 0 || scene >= 8)
            return AbstractFeatureGroup.BUTTON_COLOR_OFF;

        final ISceneBank sceneBank = this.model.getSceneBank ();
        final IScene s = sceneBank.getItem (scene);
        if (s.doesExist ())
            return s.isSelected () ? AbstractSessionView.COLOR_SELECTED_SCENE : AbstractSessionView.COLOR_SCENE;
        return AbstractSessionView.COLOR_SCENE_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void onSelectTrack (final int index, final ButtonEvent event)
    {
        this.extensions.onSelectTrack (index, event);
    }


    /** {@inheritDoc} */
    @Override
    public int getTrackButtonColor (final int index)
    {
        return this.extensions.getTrackButtonColor (index);
    }
}