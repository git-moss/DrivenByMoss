// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.DAWColors;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.ISlotBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Abstract implementation for a view which provides a session with clips.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractSessionView<S extends IControlSurface<C>, C extends Configuration> extends AbstractView<S, C> implements SceneView
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

    protected int              rows;
    protected int              columns;
    protected boolean          useClipColor;


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
    public void onScene (final int sceneIndex, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final IScene scene = this.model.getCurrentTrackBank ().getSceneBank ().getItem (sceneIndex);
        scene.select ();
        scene.launch ();
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final int index = note - 36;
        int t = index % this.columns;
        int s = this.rows - 1 - index / this.columns;

        final C configuration = this.surface.getConfiguration ();
        if (configuration.isFlipSession ())
        {
            final int dummy = t;
            t = s;
            s = dummy;
        }

        final ITrack track = this.model.getCurrentTrackBank ().getItem (t);
        final ISlot slot = track.getSlotBank ().getItem (s);

        // Delete selected clip
        if (this.surface.isDeletePressed ())
        {
            this.surface.setButtonConsumed (this.surface.getDeleteButtonId ());
            slot.remove ();
            return;
        }

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

        switch (configuration.getActionForRecArmedPad ())
        {
            case 0:
                // Record clip
                if (!slot.isRecording ())
                    slot.record ();
                slot.launch ();
                break;

            case 1:
                // Execute new clip
                this.model.createClip (slot, this.surface.getConfiguration ().getNewClipLength ());
                slot.select ();
                slot.launch ();
                this.model.getTransport ().setLauncherOverdub (true);
                break;

            case 2:
            default:
                // Do nothing
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        if (this.surface.isShiftPressed ())
            this.drawBirdsEyeGrid ();
        else
            this.drawSessionGrid ();
    }


    /**
     * Draw a session grid, where each pad stands for a clip.
     */
    protected void drawSessionGrid ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final boolean flipSession = this.surface.getConfiguration ().isFlipSession ();
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

        for (int x = 0; x < this.columns; x++)
        {
            final SessionColor rowColor = x < maxX ? this.clipColorHasContent : this.clipColorHasNoContent;
            for (int y = 0; y < this.rows; y++)
            {
                SessionColor color = y < maxY ? rowColor : this.clipColorHasNoContent;
                if (selX == x && selY == y)
                    color = this.clipColorIsPlaying;
                this.surface.getPadGrid ().lightEx (x, y, color.getColor (), color.getBlink (), color.isFast ());
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
        final double [] slotColor = slot.getColor ();
        final String colorIndex = DAWColors.getColorIndex (slotColor[0], slotColor[1], slotColor[2]);
        final ColorManager cm = this.model.getColorManager ();

        if (slot.isRecordingQueued ())
            return this.clipColorIsRecordingQueued;

        if (slot.isRecording ())
        {
            if (this.useClipColor && colorIndex != null)
                return new SessionColor (cm.getColor (colorIndex), this.clipColorIsRecording.getBlink (), this.clipColorIsRecording.isFast ());
            return this.clipColorIsRecording;
        }

        if (slot.isPlayingQueued ())
            return this.clipColorIsPlayingQueued;

        if (slot.isPlaying ())
        {
            if (this.useClipColor && colorIndex != null)
                return new SessionColor (cm.getColor (colorIndex), this.clipColorIsPlaying.getBlink (), this.clipColorIsPlaying.isFast ());
            return this.clipColorIsPlaying;
        }

        if (slot.hasContent ())
        {
            if (this.useClipColor && colorIndex != null)
                return new SessionColor (cm.getColor (colorIndex), this.clipColorHasContent.getBlink (), this.clipColorHasContent.isFast ());
            return this.clipColorHasContent;
        }

        return isArmed && this.surface.getConfiguration ().isDrawRecordStripe () ? this.clipColorIsRecArmed : this.clipColorHasNoContent;
    }
}