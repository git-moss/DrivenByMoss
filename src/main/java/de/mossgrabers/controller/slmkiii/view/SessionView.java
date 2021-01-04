// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii.view;

import de.mossgrabers.controller.slmkiii.SLMkIIIConfiguration;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIColorManager;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.ILightGuide;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.KeyManager;
import de.mossgrabers.framework.view.AbstractPlayView;
import de.mossgrabers.framework.view.AbstractSessionView;
import de.mossgrabers.framework.view.SessionColor;


/**
 * The Session view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SessionView extends AbstractSessionView<SLMkIIIControlSurface, SLMkIIIConfiguration>
{
    final Scales     keyboardScales;
    final KeyManager keyboardManager;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SessionView (final SLMkIIIControlSurface surface, final IModel model)
    {
        super ("Session", surface, model, 2, 8, true);

        final SessionColor isRecording = new SessionColor (SLMkIIIColorManager.SLMKIII_RED, SLMkIIIColorManager.SLMKIII_RED, false);
        final SessionColor isRecordingQueued = new SessionColor (SLMkIIIColorManager.SLMKIII_RED_HALF, SLMkIIIColorManager.SLMKIII_RED_HALF, true);
        final SessionColor isPlaying = new SessionColor (SLMkIIIColorManager.SLMKIII_GREEN_GRASS, SLMkIIIColorManager.SLMKIII_GREEN, false);
        final SessionColor isPlayingQueued = new SessionColor (SLMkIIIColorManager.SLMKIII_GREEN_GRASS, SLMkIIIColorManager.SLMKIII_GREEN, true);
        final SessionColor hasContent = new SessionColor (SLMkIIIColorManager.SLMKIII_AMBER, -1, false);
        final SessionColor noContent = new SessionColor (SLMkIIIColorManager.SLMKIII_BLACK, -1, false);
        final SessionColor recArmed = new SessionColor (SLMkIIIColorManager.SLMKIII_RED_HALF, -1, false);
        this.setColors (isRecording, isRecordingQueued, isPlaying, isPlayingQueued, hasContent, noContent, recArmed);

        final IPadGrid lightGuide = (IPadGrid) this.surface.getLightGuide ();
        this.keyboardScales = new Scales (model.getValueChanger (), 36, 36 + 61, 61, 1);
        this.keyboardScales.setChromatic (true);
        this.keyboardManager = new KeyManager (this.model, this.keyboardScales, lightGuide);
        this.keyboardManager.setNoteMatrix (this.keyboardScales.getNoteMatrix ());

        final ITrackBank tb = model.getTrackBank ();
        tb.addSelectionObserver ( (index, isSelected) -> this.keyboardManager.clearPressedKeys ());
        tb.addNoteObserver (this.keyboardManager::call);
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
            track.stop ();
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


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        super.drawGrid ();

        this.drawLightGuide (this.surface.getLightGuide ());
    }


    /**
     * Mark selected notes immediately for better performance.
     *
     * @param key The pressed/released key
     * @param velocity The velocity
     */
    public void updateKeyboardNote (final int key, final int velocity)
    {
        final int note = this.keyboardManager.map (key);
        if (note != -1)
            this.keyboardManager.setAllKeysPressed (note, velocity);
    }


    protected void drawLightGuide (final ILightGuide lightGuide)
    {
        final boolean isKeyboardEnabled = this.model.canSelectedTrackHoldNotes ();
        final boolean isRecording = this.model.hasRecordingState ();

        this.keyboardScales.setScaleOffset (this.scales.getScaleOffset ());
        this.keyboardScales.setScale (this.scales.getScale ());

        final ITrack cursorTrack = this.model.getCursorTrack ();
        for (int i = this.keyboardScales.getStartNote (); i < this.keyboardScales.getEndNote (); i++)
            lightGuide.light (i - 36, this.getGridColor (isKeyboardEnabled, isRecording, cursorTrack, i));
    }


    protected String getGridColor (final boolean isKeyboardEnabled, final boolean isRecording, final ITrack track, final int note)
    {
        if (isKeyboardEnabled && this.surface.getConfiguration ().isLightEnabled ())
        {
            if (this.keyboardManager.isKeyPressed (note))
                return isRecording ? AbstractPlayView.COLOR_RECORD : AbstractPlayView.COLOR_PLAY;
            return replaceOctaveColorWithTrackColor (track, this.keyboardManager.getColor (note));
        }
        return AbstractPlayView.COLOR_OFF;
    }
}