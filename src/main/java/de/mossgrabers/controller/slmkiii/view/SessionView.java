// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii.view;

import de.mossgrabers.controller.slmkiii.SLMkIIIConfiguration;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIColors;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.view.AbstractSessionView;
import de.mossgrabers.framework.view.SessionColor;


/**
 * The Session view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
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

        final SessionColor isRecording = new SessionColor (SLMkIIIColors.SLMKIII_RED, SLMkIIIColors.SLMKIII_RED, false);
        final SessionColor isRecordingQueued = new SessionColor (SLMkIIIColors.SLMKIII_RED_HALF, SLMkIIIColors.SLMKIII_RED_HALF, true);
        final SessionColor isPlaying = new SessionColor (SLMkIIIColors.SLMKIII_GREEN_GRASS, SLMkIIIColors.SLMKIII_GREEN, false);
        final SessionColor isPlayingQueued = new SessionColor (SLMkIIIColors.SLMKIII_GREEN_GRASS, SLMkIIIColors.SLMKIII_GREEN, true);
        final SessionColor hasContent = new SessionColor (SLMkIIIColors.SLMKIII_AMBER, -1, false);
        final SessionColor noContent = new SessionColor (SLMkIIIColors.SLMKIII_BLACK, -1, false);
        final SessionColor recArmed = new SessionColor (SLMkIIIColors.SLMKIII_RED_HALF, -1, false);
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
        if (this.surface.isShiftPressed ())
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
        if (this.surface.isPressed (SLMkIIIControlSurface.MKIII_DUPLICATE))
        {
            this.surface.setTriggerConsumed (SLMkIIIControlSurface.MKIII_DUPLICATE);
            if (track.doesExist ())
                track.getSlotBank ().getItem (s).duplicate ();
            return;
        }

        super.onGridNote (note, velocity);
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
        for (int i = 0; i < sceneBank.getPageSize (); i++)
        {
            final IScene scene = sceneBank.getItem (i);
            final int color = scene.doesExist () ? scene.isSelected () ? colorSceneSelected : colorScene : colorSceneOff;
            this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_SCENE_1 + i, color);
        }
    }
}