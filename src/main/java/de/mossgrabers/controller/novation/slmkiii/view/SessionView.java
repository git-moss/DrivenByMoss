// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.slmkiii.view;

import de.mossgrabers.controller.novation.slmkiii.SLMkIIIConfiguration;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIColorManager;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.LightInfo;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.view.AbstractSessionView;


/**
 * The Session view.
 *
 * @author Jürgen Moßgraber
 */
public class SessionView extends AbstractSessionView<SLMkIIIControlSurface, SLMkIIIConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SessionView (final SLMkIIIControlSurface surface, final IModel model)
    {
        super ("Session", surface, model, 2, 8, true);

        final LightInfo isRecording = new LightInfo (SLMkIIIColorManager.SLMKIII_RED, SLMkIIIColorManager.SLMKIII_RED, false);
        final LightInfo isRecordingQueued = new LightInfo (SLMkIIIColorManager.SLMKIII_RED_HALF, SLMkIIIColorManager.SLMKIII_RED_HALF, true);
        final LightInfo isPlaying = new LightInfo (SLMkIIIColorManager.SLMKIII_GREEN_GRASS, SLMkIIIColorManager.SLMKIII_GREEN, false);
        final LightInfo isPlayingQueued = new LightInfo (SLMkIIIColorManager.SLMKIII_GREEN_GRASS, SLMkIIIColorManager.SLMKIII_GREEN, true);
        final LightInfo hasContent = new LightInfo (SLMkIIIColorManager.SLMKIII_AMBER, -1, false);
        final LightInfo noContent = new LightInfo (SLMkIIIColorManager.SLMKIII_BLACK, -1, false);
        final LightInfo recArmed = new LightInfo (SLMkIIIColorManager.SLMKIII_RED_HALF, -1, false);
        this.setColors (isRecording, isRecordingQueued, isPlaying, isPlayingQueued, hasContent, noContent, recArmed);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        final int index = note - 36;
        final int t = index % this.columns;
        final int s = this.rows - 1 - index / this.columns;
        final ITrackBank tb = this.model.getCurrentTrackBank ();

        // Birds-eye-view navigation
        if (this.isBirdsEyeActive ())
        {
            final ISceneBank sceneBank = tb.getSceneBank ();

            // Calculate page offsets
            final int numTracks = tb.getPageSize ();
            final int numScenes = sceneBank.getPageSize ();
            final int trackPosition = tb.getItem (0).getPosition () / numTracks;
            final int scenePosition = sceneBank.getScrollPosition () / numScenes;
            final int selX = trackPosition;
            final int selY = scenePosition;
            final int padsX = this.columns;
            final int padsY = this.rows;
            final int offsetX = selX / padsX * padsX;
            final int offsetY = selY / padsY * padsY;
            tb.scrollTo (offsetX * numTracks + t * padsX);
            sceneBank.scrollTo (offsetY * numScenes + s * padsY);
            return;
        }

        // Duplicate a clip
        final ITrack track = tb.getItem (t);
        if (this.isButtonCombination (ButtonID.DUPLICATE))
        {
            if (track.doesExist ())
                track.getSlotBank ().getItem (s).duplicate ();
            return;
        }

        // Stop clip with normal stop button
        if (this.isButtonCombination (ButtonID.STOP))
        {
            track.stop (false);
            return;
        }

        super.onGridNote (note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        if (!ButtonID.isSceneButton (buttonID))
            return super.getButtonColor (buttonID);

        final ColorManager colorManager = this.model.getColorManager ();
        final IScene s = this.model.getSceneBank ().getItem (buttonID.ordinal () - ButtonID.SCENE1.ordinal ());
        if (!s.doesExist ())
            return colorManager.getColorIndex (AbstractSessionView.COLOR_SCENE_OFF);
        return colorManager.getColorIndex (s.isSelected () ? AbstractSessionView.COLOR_SELECTED_SCENE : AbstractSessionView.COLOR_SCENE);
    }
}