// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.FrameworkException;
import de.mossgrabers.framework.utils.Pair;


/**
 * Abstract implementation for a view which provides a session with clips.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractSessionView<S extends IControlSurface<C>, C extends Configuration> extends AbstractView<S, C>
{
    /** The color for a scene. */
    public static final String COLOR_SCENE                = "COLOR_SCENE";
    /** The color for a selected scene. */
    public static final String COLOR_SELECTED_SCENE       = "COLOR_SELECTED_SCENE";
    /** The color for no scene. */
    public static final String COLOR_SCENE_OFF            = "COLOR_SELECTED_OFF";

    // Needs to be overwritten with device specific colors
    protected SessionColor     clipColorIsRecording       = new SessionColor (0, -1, false);
    protected SessionColor     clipColorIsRecordingQueued = new SessionColor (1, -1, false);
    protected SessionColor     clipColorIsPlaying         = new SessionColor (2, -1, false);
    protected SessionColor     clipColorIsPlayingQueued   = new SessionColor (3, -1, false);
    protected SessionColor     clipColorHasContent        = new SessionColor (4, -1, false);
    protected SessionColor     clipColorHasNoContent      = new SessionColor (5, -1, false);
    protected SessionColor     clipColorIsRecArmed        = new SessionColor (6, -1, false);

    protected SessionColor     birdColorHasContent        = new SessionColor (4, -1, false);
    protected SessionColor     birdColorSelected          = new SessionColor (2, -1, false);

    protected int              rows;
    protected int              columns;
    protected boolean          useClipColor;
    protected ISlot            sourceSlot;


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param rows The number of rows of the clip grid
     * @param columns The number of columns of the clip grid
     * @param useClipColor Use the clip colors? Only set to true for controllers which support RGB
     *            pads.
     */
    public AbstractSessionView (final String name, final S surface, final IModel model, final int rows, final int columns, final boolean useClipColor)
    {
        super (name, surface, model);

        this.rows = rows;
        this.columns = columns;
        this.useClipColor = useClipColor;
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN)
            return;

        final int sceneIndex = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        this.handleSceneButtonCombinations (this.model.getCurrentTrackBank ().getSceneBank ().getItem (sceneIndex));
    }


    /**
     * Handle a scene command depending on button combinations.
     *
     * @param scene The scene
     */
    protected void handleSceneButtonCombinations (final IScene scene)
    {
        if (this.isButtonCombination (ButtonID.DELETE))
        {
            scene.remove ();
            return;
        }

        if (this.isButtonCombination (ButtonID.DUPLICATE))
        {
            scene.duplicate ();
            return;
        }

        this.launchScene (scene);
    }


    /**
     * Select and launch a scene.
     *
     * @param scene The scene to launch
     */
    protected void launchScene (final IScene scene)
    {
        scene.select ();
        scene.launch ();
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        // Trigger on pad release to intercept long presses
        if (velocity != 0)
            return;

        final Pair<Integer, Integer> padPos = this.getPad (note);
        final ITrack track = this.model.getCurrentTrackBank ().getItem (padPos.getKey ().intValue ());
        final ISlot slot = track.getSlotBank ().getItem (padPos.getValue ().intValue ());

        if (this.handleButtonCombinations (track, slot))
            return;

        if (this.surface.isSelectPressed ())
        {
            slot.select ();
            return;
        }

        if (this.doSelectClipOnLaunch ())
            slot.select ();

        if (!track.isRecArm ())
        {
            slot.launch ();
            return;
        }

        if (slot.hasContent ())
        {
            slot.launch ();
            return;
        }

        final C configuration = this.surface.getConfiguration ();
        switch (configuration.getActionForRecArmedPad ())
        {
            case 0:
                this.model.recordNoteClip (track, slot);
                break;

            case 1:
                final int lengthInBeats = configuration.getNewClipLenghthInBeats (this.model.getTransport ().getQuartersPerMeasure ());
                this.model.createNoteClip (track, slot, lengthInBeats, true);
                break;

            case 2:
            default:
                // Do nothing
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNoteLongPress (final int note)
    {
        final Pair<Integer, Integer> padPos = this.getPad (note);
        final ITrack track = this.model.getCurrentTrackBank ().getItem (padPos.getKey ().intValue ());
        final ISlot slot = track.getSlotBank ().getItem (padPos.getValue ().intValue ());
        slot.select ();

        final int index = note - 36;
        this.surface.getButton (ButtonID.get (ButtonID.PAD1, index)).setConsumed ();
    }


    /**
     * Handle buttons combinations on the grid, e.g. delete, duplicate.
     *
     * @param track The track which contains the slot
     * @param slot The slot
     * @return True if handled
     */
    protected boolean handleButtonCombinations (final ITrack track, final ISlot slot)
    {
        if (!track.doesExist ())
            return true;

        // Delete selected clip
        if (this.isButtonCombination (ButtonID.DELETE))
        {
            if (slot.doesExist ())
                slot.remove ();
            return true;
        }

        // Duplicate a clip
        if (this.isButtonCombination (ButtonID.DUPLICATE))
        {
            if (slot.doesExist () && slot.hasContent ())
                this.sourceSlot = slot;
            else if (this.sourceSlot != null)
                slot.paste (this.sourceSlot);
            return true;
        }

        // Stop clip
        if (this.isButtonCombination (ButtonID.STOP_CLIP))
        {
            track.stop ();
            return true;
        }

        // Browse for clips
        if (this.isButtonCombination (ButtonID.BROWSE))
        {
            this.model.getBrowser ().replace (slot);
            return true;
        }

        return false;
    }


    /**
     * Handle pad presses in the birds eye view (session page selection).
     *
     * @param x The x position of the pad
     * @param y The y position of the pad
     * @param yOffset Optional offset in y-direction
     */
    protected void onGridNoteBirdsEyeView (final int x, final int y, final int yOffset)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ISceneBank sceneBank = tb.getSceneBank ();
        final boolean flip = this.surface.getConfiguration ().isFlipSession ();

        // Calculate page offsets
        final int numTracks = tb.getPageSize ();
        final int numScenes = sceneBank.getPageSize ();
        final int trackPosition = tb.getItem (0).getPosition () / numTracks;
        final int scenePosition = sceneBank.getScrollPosition () / numScenes;
        final int selX = flip ? scenePosition : trackPosition;
        final int selY = flip ? trackPosition : scenePosition;
        final int padsX = flip ? this.rows : this.columns;
        final int padsY = flip ? this.columns : this.rows + yOffset;
        final int offsetX = selX / padsX * padsX;
        final int offsetY = selY / padsY * padsY;
        tb.scrollTo (offsetX * numTracks + (flip ? y : x) * padsX);
        sceneBank.scrollTo (offsetY * numScenes + (flip ? x : y) * padsY);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        if (this.isBirdsEyeActive ())
            this.drawBirdsEyeGrid ();
        else
            this.drawSessionGrid ();
    }


    /**
     * Is the birds eye view active? Default implementation checks for Shift button. Override for
     * different behaviour.
     *
     * @return True if birds eye view should be active
     */
    public boolean isBirdsEyeActive ()
    {
        return this.surface.isShiftPressed ();
    }


    /**
     * Draw a session grid, where each pad stands for a clip.
     */
    protected void drawSessionGrid ()
    {
        final boolean flipSession = this.surface.getConfiguration ().isFlipSession ();

        if (flipSession && this.columns != this.rows)
            throw new FrameworkException ("Session flip is only supported for same size of rows and columns!");

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        for (int x = 0; x < this.columns; x++)
        {
            final ITrack t = tb.getItem (x);
            final ISlotBank slotBank = t.getSlotBank ();
            for (int y = 0; y < this.rows; y++)
                this.drawPad (slotBank.getItem (y), flipSession ? y : x, flipSession ? x : y, t.isRecArm ());
        }
    }


    /**
     * Aggregate the content of 8 pads to 1 pads for quick navigation through the clip matrix.
     */
    protected void drawBirdsEyeGrid ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ISceneBank sceneBank = this.model.getSceneBank ();
        final int numTracks = tb.getPageSize ();
        final int numScenes = sceneBank.getPageSize ();
        final int sceneCount = sceneBank.getItemCount ();
        final int trackCount = tb.getItemCount ();
        final int maxScenePads = sceneCount / numScenes + (sceneCount % numScenes > 0 ? 1 : 0);
        final int maxTrackPads = trackCount / numTracks + (trackCount % numTracks > 0 ? 1 : 0);
        final int scenePosition = sceneBank.getScrollPosition ();
        final int trackPosition = tb.getItem (0).getPosition ();
        final int sceneSelection = scenePosition / numScenes + (scenePosition % numScenes > 0 ? 1 : 0);
        final int trackSelection = trackPosition / numTracks + (trackPosition % numTracks > 0 ? 1 : 0);
        final boolean flipSession = this.surface.getConfiguration ().isFlipSession ();
        int selX = flipSession ? sceneSelection : trackSelection;
        int selY = flipSession ? trackSelection : sceneSelection;
        final int padsX = flipSession ? this.rows : this.columns;
        final int padsY = flipSession ? this.columns : this.rows;
        final int offsetX = selX / padsX * padsX;
        final int offsetY = selY / padsY * padsY;
        final int maxX = (flipSession ? maxScenePads : maxTrackPads) - offsetX;
        final int maxY = (flipSession ? maxTrackPads : maxScenePads) - offsetY;
        selX -= offsetX;
        selY -= offsetY;

        final IPadGrid padGrid = this.surface.getPadGrid ();
        for (int x = 0; x < this.columns; x++)
        {
            final SessionColor rowColor = x < maxX ? this.birdColorHasContent : this.clipColorHasNoContent;
            for (int y = 0; y < this.rows; y++)
            {
                SessionColor color = y < maxY ? rowColor : this.clipColorHasNoContent;
                if (selX == x && selY == y)
                    color = this.birdColorSelected;
                padGrid.lightEx (x, y, color.getColor (), color.getBlink (), color.isFast ());
            }
        }
    }


    protected void setColors (final SessionColor isRecording, final SessionColor isRecordingQueued, final SessionColor isPlaying, final SessionColor isPlayingQueued, final SessionColor hasContent, final SessionColor noContent, final SessionColor recArmed)
    {
        this.clipColorIsRecording = isRecording;
        this.clipColorIsRecordingQueued = isRecordingQueued;
        this.clipColorIsPlaying = isPlaying;
        this.clipColorIsPlayingQueued = isPlayingQueued;
        this.clipColorHasContent = hasContent;
        this.clipColorHasNoContent = noContent;
        this.clipColorIsRecArmed = recArmed;
    }


    /**
     * Can be overwritten to provide an option to not select a clip when it is started.
     *
     * @return Always true
     */
    protected boolean doSelectClipOnLaunch ()
    {
        return this.surface.getConfiguration ().isSelectClipOnLaunch ();
    }


    /**
     * Draws one pad.
     *
     * @param slot The slot data which is represented on that pad
     * @param x The x index on the grid
     * @param y The y index on the grid
     * @param isArmed True if armed for recording
     */
    protected void drawPad (final ISlot slot, final int x, final int y, final boolean isArmed)
    {
        final SessionColor color = this.getPadColor (slot, isArmed);
        this.surface.getPadGrid ().lightEx (x, y, color.getColor (), color.getBlink (), color.isFast ());
    }


    protected SessionColor getPadColor (final ISlot slot, final boolean isArmed)
    {
        final String colorIndex = DAWColor.getColorIndex (slot.getColor ());
        final ColorManager cm = this.model.getColorManager ();

        if (slot.isRecordingQueued ())
            return this.clipColorIsRecordingQueued;

        if (slot.isRecording ())
        {
            if (this.useClipColor && colorIndex != null)
                return new SessionColor (cm.getColorIndex (colorIndex), this.clipColorIsRecording.getBlink (), this.clipColorIsRecording.isFast ());
            return this.clipColorIsRecording;
        }

        if (slot.isPlayingQueued ())
        {
            if (this.useClipColor && colorIndex != null)
                return new SessionColor (cm.getColorIndex (colorIndex), this.clipColorIsPlayingQueued.getBlink (), this.clipColorIsPlayingQueued.isFast ());
            return this.clipColorIsPlayingQueued;
        }

        if (slot.isPlaying ())
        {
            if (this.useClipColor && colorIndex != null)
                return new SessionColor (cm.getColorIndex (colorIndex), this.clipColorIsPlaying.getBlink (), this.clipColorIsPlaying.isFast ());
            return this.clipColorIsPlaying;
        }

        if (slot.hasContent ())
        {
            if (this.useClipColor && colorIndex != null)
                return new SessionColor (cm.getColorIndex (colorIndex), this.clipColorHasContent.getBlink (), this.clipColorHasContent.isFast ());
            return this.clipColorHasContent;
        }

        return isArmed && this.surface.getConfiguration ().isDrawRecordStripe () ? this.clipColorIsRecArmed : this.clipColorHasNoContent;
    }


    protected Pair<Integer, Integer> getPad (final int note)
    {
        final int index = note - 36;
        final int t = index % this.columns;
        final int s = this.rows - 1 - index / this.columns;
        final C configuration = this.surface.getConfiguration ();
        return configuration.isFlipSession () ? new Pair<> (Integer.valueOf (s), Integer.valueOf (t)) : new Pair<> (Integer.valueOf (t), Integer.valueOf (s));
    }
}