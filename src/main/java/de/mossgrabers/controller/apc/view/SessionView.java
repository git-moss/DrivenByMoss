// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apc.view;

import de.mossgrabers.controller.apc.APCConfiguration;
import de.mossgrabers.controller.apc.controller.APCColorManager;
import de.mossgrabers.controller.apc.controller.APCControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.view.AbstractSessionView;
import de.mossgrabers.framework.view.SessionColor;


/**
 * Session view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SessionView extends AbstractSessionView<APCControlSurface, APCConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SessionView (final APCControlSurface surface, final IModel model)
    {
        super ("Session", surface, model, 5, 8, surface.isMkII ());

        if (surface.isMkII ())
        {
            final SessionColor isRecording = new SessionColor (APCColorManager.APC_MKII_COLOR_RED_HI, APCColorManager.APC_MKII_COLOR_RED_HI, false);
            final SessionColor isRecordingQueued = new SessionColor (APCColorManager.APC_MKII_COLOR_RED_HI, APCColorManager.APC_MKII_COLOR_RED_HI, true);
            final SessionColor isPlaying = new SessionColor (APCColorManager.APC_MKII_COLOR_GREEN_HI, APCColorManager.APC_MKII_COLOR_GREEN_HI, false);
            final SessionColor isPlayingQueued = new SessionColor (APCColorManager.APC_MKII_COLOR_GREEN_HI, APCColorManager.APC_MKII_COLOR_GREEN_HI, true);
            final SessionColor hasContent = new SessionColor (APCColorManager.APC_MKII_COLOR_AMBER, -1, false);
            final SessionColor noContent = new SessionColor (APCColorManager.APC_MKII_COLOR_BLACK, -1, false);
            final SessionColor recArmed = new SessionColor (APCColorManager.APC_MKII_COLOR_RED_LO, -1, false);
            this.setColors (isRecording, isRecordingQueued, isPlaying, isPlayingQueued, hasContent, noContent, recArmed);
        }
        else
        {
            final SessionColor isRecording = new SessionColor (APCColorManager.APC_COLOR_RED, -1, false);
            final SessionColor isRecordingQueued = new SessionColor (APCColorManager.APC_COLOR_RED, APCColorManager.APC_COLOR_RED_BLINK, false);
            final SessionColor isPlaying = new SessionColor (APCColorManager.APC_COLOR_GREEN, -1, false);
            final SessionColor isPlayingQueued = new SessionColor (APCColorManager.APC_COLOR_GREEN, APCColorManager.APC_COLOR_GREEN_BLINK, false);
            final SessionColor hasContent = new SessionColor (APCColorManager.APC_COLOR_YELLOW, -1, false);
            final SessionColor noContent = new SessionColor (APCColorManager.APC_COLOR_BLACK, -1, false);
            final SessionColor recArmed = new SessionColor (APCColorManager.APC_COLOR_BLACK, -1, false);
            this.setColors (isRecording, isRecordingQueued, isPlaying, isPlayingQueued, hasContent, noContent, recArmed);
        }
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
        if (!s.doesExist ())
            return AbstractSessionView.COLOR_SCENE_OFF;

        if (s.isSelected ())
            return AbstractSessionView.COLOR_SELECTED_SCENE;

        return this.useClipColor ? DAWColor.getColorIndex (s.getColor ()) : AbstractSessionView.COLOR_SCENE;
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        // No birds eye view, since Shift is used for view changes...
        this.drawSessionGrid ();
    }
}