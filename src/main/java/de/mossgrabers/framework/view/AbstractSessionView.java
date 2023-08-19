// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.controller.grid.LightInfo;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.ISessionAlternative;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.FrameworkException;
import de.mossgrabers.framework.utils.Pair;


/**
 * Abstract implementation for a view which provides a session with clips.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractSessionView<S extends IControlSurface<C>, C extends Configuration> extends AbstractView<S, C> implements ISessionAlternative
{
    /** The color for a scene. */
    public static final String COLOR_SCENE                     = "COLOR_SCENE";
    /** The color for a selected scene. */
    public static final String COLOR_SELECTED_SCENE            = "COLOR_SELECTED_SCENE";
    /** The color for no scene. */
    public static final String COLOR_SCENE_OFF                 = "COLOR_SELECTED_OFF";

    // Needs to be overwritten with device specific colors
    protected LightInfo        clipColorIsRecording            = new LightInfo (0, -1, false);
    protected LightInfo        clipColorIsRecordingQueued      = new LightInfo (1, -1, false);
    protected LightInfo        clipColorIsPlaying              = new LightInfo (2, -1, false);
    protected LightInfo        clipColorIsPlayingQueued        = new LightInfo (3, -1, false);
    protected LightInfo        clipColorHasContent             = new LightInfo (4, -1, false);
    protected LightInfo        clipColorHasNoContent           = new LightInfo (5, -1, false);
    protected LightInfo        clipColorIsRecArmed             = new LightInfo (6, -1, false);

    protected LightInfo        birdColorHasContent             = new LightInfo (4, -1, false);
    protected LightInfo        birdColorSelected               = new LightInfo (2, -1, false);

    protected int              rows;
    protected int              columns;
    protected boolean          useClipColor;
    protected boolean          ignoreClipColorForPlayAndRecord = false;
    protected ISlot            sourceSlot;
    protected boolean          isBirdsEyeActive                = false;

    private boolean            wasAlternateInteractionUsed     = false;


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
    protected AbstractSessionView (final String name, final S surface, final IModel model, final int rows, final int columns, final boolean useClipColor)
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
        if (ButtonID.isSceneButton (buttonID))
            this.onSceneButton (buttonID, event);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (this.surface.isShiftPressed ())
            this.wasAlternateInteractionUsed = true;

        final Pair<Integer, Integer> padPos = this.getPad (note);
        final ITrack track = this.model.getCurrentTrackBank ().getItem (padPos.getKey ().intValue ());
        final ISlot slot = track.getSlotBank ().getItem (padPos.getValue ().intValue ());

        final boolean isPressed = velocity != 0;
        if (isPressed)
        {
            if (this.handleButtonCombinations (track, slot))
            {
                this.surface.consumePads ();
                return;
            }
            if (this.doSelectClipOnLaunch ())
                slot.select ();
        }

        if (!track.isRecArm () || slot.hasContent ())
        {
            slot.launch (isPressed, this.isAlternateFunction ());
            return;
        }

        this.handleRecording (track, slot);
    }


    /**
     * Check if the alternate launch/stop function should be executed, e.g. when a SHIFT button is
     * pressed.
     *
     * @return True if alternate function should be executed
     */
    protected boolean isAlternateFunction ()
    {
        return this.surface.isShiftPressed ();
    }


    /**
     * Handles the recording of a slot depending on the configuration settings.
     *
     * @param track The track on which to record
     * @param slot The slot on the track into which to record
     */
    protected void handleRecording (final ITrack track, final ISlot slot)
    {
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
            track.stop (this.isAlternateFunction ());
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
     * Toggles the birdseye view.
     */
    public void toggleBirdsEyeView ()
    {
        this.isBirdsEyeActive = !this.isBirdsEyeActive;
    }


    /**
     * Set the birds eye view in-/active.
     *
     * @param isBirdsEyeActive True to activate
     */
    public void setBirdsEyeActive (final boolean isBirdsEyeActive)
    {
        this.isBirdsEyeActive = isBirdsEyeActive;
    }


    /**
     * Is the birds eye view active?
     *
     * @return True if birds eye view is active
     */
    public boolean isBirdsEyeActive ()
    {
        return this.isBirdsEyeActive;
    }


    /** {@inheritDoc} */
    @Override
    public boolean wasAlternateInteractionUsed ()
    {
        return this.wasAlternateInteractionUsed;
    }


    /** {@inheritDoc} */
    @Override
    public void setAlternateInteractionUsed (final boolean wasUsed)
    {
        this.wasAlternateInteractionUsed = wasUsed;
    }


    /**
     * Draw a session grid, where each pad stands for a clip.
     */
    protected void drawSessionGrid ()
    {
        this.drawSessionGrid (false);
    }


    /**
     * Draw a session grid, where each pad stands for a clip.
     *
     * @param ignoreFlipCheck True to ignore the check for same columns and rows
     */
    protected void drawSessionGrid (final boolean ignoreFlipCheck)
    {
        final boolean flipSession = this.surface.getConfiguration ().isFlipSession ();

        if (flipSession && this.columns != this.rows && !ignoreFlipCheck)
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
            final LightInfo rowColor = x < maxX ? this.birdColorHasContent : this.clipColorHasNoContent;
            for (int y = 0; y < this.rows; y++)
            {
                LightInfo color = y < maxY ? rowColor : this.clipColorHasNoContent;
                if (selX == x && selY == y)
                    color = this.birdColorSelected;
                padGrid.lightEx (x, y, color.getColor (), color.getBlinkColor (), color.isFast ());
            }
        }
    }


    protected void setColors (final LightInfo isRecording, final LightInfo isRecordingQueued, final LightInfo isPlaying, final LightInfo isPlayingQueued, final LightInfo hasContent, final LightInfo noContent, final LightInfo recArmed)
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
        final LightInfo color = this.getPadColor (slot, isArmed);
        this.surface.getPadGrid ().lightEx (x, y, color.getColor (), color.getBlinkColor (), color.isFast ());
    }


    /**
     * Get the pad color for a slot.
     *
     * @param slot The slot
     * @param isArmed True if armed
     * @return The light info
     */
    public LightInfo getPadColor (final ISlot slot, final boolean isArmed)
    {
        final String colorID = DAWColor.getColorID (slot.getColor ());
        final ColorManager cm = this.model.getColorManager ();

        if (slot.isRecordingQueued ())
            return this.clipColorIsRecordingQueued;

        if (slot.isRecording ())
            return this.insertClipColor (cm, colorID, this.clipColorIsRecording);

        if (slot.isPlayingQueued ())
            return this.insertClipColor (cm, colorID, this.clipColorIsPlayingQueued);

        if (slot.isPlaying ())
            return this.insertClipColor (cm, colorID, this.clipColorIsPlaying);

        if (slot.hasContent ())
        {
            final int blinkColor = this.clipColorHasContent.getBlinkColor ();
            final int color = this.useClipColor && colorID != null ? cm.getColorIndex (colorID) : this.clipColorHasContent.getColor ();
            return new LightInfo (color, slot.isSelected () ? blinkColor : -1, this.clipColorHasContent.isFast ());
        }

        return slot.doesExist () && isArmed && this.surface.getConfiguration ().isDrawRecordStripe () ? this.clipColorIsRecArmed : this.clipColorHasNoContent;
    }


    protected Pair<Integer, Integer> getPad (final int note)
    {
        final int index = note - 36;
        final int t = index % this.columns;
        final int s = this.rows - 1 - index / this.columns;
        final C configuration = this.surface.getConfiguration ();
        return configuration.isFlipSession () ? new Pair<> (Integer.valueOf (s), Integer.valueOf (t)) : new Pair<> (Integer.valueOf (t), Integer.valueOf (s));
    }


    /**
     * If blinking is supported and clip colors should be used the given light info is updated with
     * the clips' color.
     *
     * @param colorManager The color manager
     * @param colorID The ID of the clip color
     * @param lightInfo The light info
     * @return THe updated light info
     */
    private LightInfo insertClipColor (final ColorManager colorManager, final String colorID, final LightInfo lightInfo)
    {
        if (this.useClipColor && !this.ignoreClipColorForPlayAndRecord && colorID != null)
        {
            final int blinkColor = lightInfo.getBlinkColor ();
            if (blinkColor > 0)
                return new LightInfo (colorManager.getColorIndex (colorID), blinkColor, lightInfo.isFast ());
        }
        return lightInfo;
    }
}