// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.intuitiveinstruments.exquis.view;

import de.mossgrabers.controller.intuitiveinstruments.exquis.ExquisConfiguration;
import de.mossgrabers.controller.intuitiveinstruments.exquis.command.trigger.ExquisSessionCommand;
import de.mossgrabers.controller.intuitiveinstruments.exquis.controller.ExquisColorManager;
import de.mossgrabers.controller.intuitiveinstruments.exquis.controller.ExquisControlSurface;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
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
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.Pair;
import de.mossgrabers.framework.view.AbstractSessionView;


/**
 * The Session view.
 *
 * @author Jürgen Moßgraber
 */
public class ExquisSessionView extends AbstractSessionView<ExquisControlSurface, ExquisConfiguration>
{
    // @formatter:off
    private static final int [][] GRID_MAPPING = {
        { 56, 57, 58, 59 },
        { 50, 51, 52, 53 },
        { 45, 46, 47, 48 },
        { 39, 40, 41, 42 },
        { 34, 35, 36, 37 },
        { 28, 29, 30, 31 },
        { 23, 24, 25, 26 }
    };

    private static final int [] GRID_SCENES    = { 27, 32, 38, 43, 49, 54, 60 };
    private static final int [] GRID_STOP      = { 17, 18, 19, 20, 21 };
    private static final int [] GRID_MUTE      = { 12, 13, 14, 15, 16 };
    private static final int [] GRID_SOLO      = { 6, 7, 8, 9, 10 };
    private static final int [] GRID_SELECT    = { 1, 2, 3, 4, 5 };
    // @formatter:on

    private static final int                                            FUNCTION_DELETE      = 55;
    private static final int                                            FUNCTION_DUPLICATE   = 44;
    private static final int                                            FUNCTION_NEW_CLIP    = 33;
    private static final int                                            FUNCTION_ADD_SCENE   = 22;
    private static final int                                            FUNCTION_ALTERNATIVE = 11;
    private static final int                                            FUNCTION_SELECT      = 0;

    private boolean                                                     isRecArmActive       = false;
    private final NewCommand<ExquisControlSurface, ExquisConfiguration> newCommand;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ExquisSessionView (final ExquisControlSurface surface, final IModel model)
    {
        super ("Session", surface, model, 7, 4, true);

        this.newCommand = new NewCommand<> (model, surface);

        final int redLo = ExquisColorManager.DARK_RED;
        final int redHi = ExquisColorManager.RED;
        final int black = ExquisColorManager.BLACK;
        final int white = ExquisColorManager.WHITE;
        final int green = ExquisColorManager.GREEN;
        final int orange = ExquisColorManager.ORANGE;
        final int grey = ExquisColorManager.DARK_GREY;
        final LightInfo isRecording = new LightInfo (redHi, redHi, false);
        final LightInfo isRecordingQueued = new LightInfo (redHi, black, true);
        final LightInfo isPlaying = new LightInfo (green, green, false);
        final LightInfo isPlayingQueued = new LightInfo (green, green, true);
        final LightInfo isStopQueued = new LightInfo (green, green, true);
        final LightInfo hasContent = new LightInfo (orange, white, false);
        final LightInfo noContent = new LightInfo (black, -1, false);
        final LightInfo recArmed = new LightInfo (redLo, -1, false);
        final LightInfo isMuted = new LightInfo (grey, -1, false);
        this.setColors (isRecording, isRecordingQueued, isPlaying, isPlayingQueued, isStopQueued, hasContent, noContent, recArmed, isMuted);

        this.birdColorHasContent = new LightInfo (orange, -1, false);
        this.birdColorSelected = isPlaying;
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.surface.configureDeveloperMode (ExquisControlSurface.DEV_MODE_FULL);

        super.onActivate ();
        this.surface.forceFlush ();
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            ((ExquisSessionCommand) this.surface.getButton (ButtonID.SESSION).getCommand ()).setTemporary ();

        final int noteIndex = note - 36;

        if (noteIndex == FUNCTION_NEW_CLIP)
        {
            this.newCommand.execute (velocity == 0 ? ButtonEvent.UP : ButtonEvent.DOWN, velocity);
            return;
        }
        else if (noteIndex == FUNCTION_ADD_SCENE)
        {
            if (velocity != 0)
                this.model.getProject ().createSceneFromPlayingLauncherClips ();
            return;
        }

        // Handling scenes
        final int sceneIndex = isFeatureButton (noteIndex, GRID_SCENES);
        if (sceneIndex >= 0)
        {
            this.onSceneButton (ButtonID.get (ButtonID.SCENE1, this.rows - 1 - sceneIndex), velocity == 0 ? ButtonEvent.UP : ButtonEvent.DOWN);
            return;
        }

        // Handling stop buttons
        final int stopIndex = isFeatureButton (noteIndex, GRID_STOP);
        if (stopIndex >= 0)
        {
            if (velocity == 0)
                return;
            final ITrackBank trackBank = this.model.getTrackBank ();
            final boolean alternative = this.isAlternateFunction ();
            if (stopIndex < 4)
                trackBank.getItem (stopIndex).stop (alternative);
            else
                trackBank.stop (alternative);
            return;
        }

        // Handling mute buttons
        final int muteIndex = isFeatureButton (noteIndex, GRID_MUTE);
        if (muteIndex >= 0)
        {
            if (velocity == 0)
                return;
            final ITrackBank trackBank = this.model.getTrackBank ();
            if (muteIndex < 4)
                trackBank.getItem (muteIndex).toggleMute ();
            else
                this.model.getProject ().clearMute ();
            return;
        }

        // Handling solo buttons
        final int soloIndex = isFeatureButton (noteIndex, GRID_SOLO);
        if (soloIndex >= 0)
        {
            if (velocity == 0)
                return;
            final ITrackBank trackBank = this.model.getTrackBank ();
            if (soloIndex < 4)
                trackBank.getItem (soloIndex).toggleSolo ();
            else
                this.model.getProject ().clearSolo ();
            return;
        }

        // Handling select/rec-arm buttons
        final int selectIndex = isFeatureButton (noteIndex, GRID_SELECT);
        if (selectIndex >= 0)
        {
            if (velocity == 0)
                return;
            final ITrackBank trackBank = this.model.getTrackBank ();
            if (selectIndex < 4)
            {
                if (this.isRecArmActive)
                    trackBank.getItem (selectIndex).toggleRecArm ();
                else
                    trackBank.getItem (selectIndex).select ();
            }
            else
                this.isRecArmActive = !this.isRecArmActive;
            return;
        }

        super.onGridNote (note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleButtonCombinations (final ITrack track, final ISlot slot)
    {
        if (super.handleButtonCombinations (track, slot))
            return true;

        if (this.surface.isPressed (ButtonID.get (ButtonID.PAD1, FUNCTION_SELECT)))
        {
            if (slot.doesExist ())
                slot.select ();
            return true;
        }

        return super.handleButtonCombinations (track, slot);
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
    protected Pair<Integer, Integer> getPad (final int note)
    {
        final int offsetNote = note - this.surface.getPadGrid ().getStartNote ();

        for (int row = 0; row < GRID_MAPPING.length; row++)
        {
            for (int column = 0; column < GRID_MAPPING[row].length; column++)
            {
                if (GRID_MAPPING[row][column] == offsetNote)
                    return new Pair<> (Integer.valueOf (column), Integer.valueOf (row));
            }
        }

        return null;
    }


    /** {@inheritDoc} */
    @Override
    protected void drawSessionGrid ()
    {
        // Draw the clips
        super.drawSessionGrid ();

        final IPadGrid padGrid = this.surface.getPadGrid ();

        // Draw the scenes
        for (int i = 0; i < GRID_SCENES.length; i++)
        {
            final String buttonColorID = this.getButtonColorID (ButtonID.get (ButtonID.SCENE1, this.rows - i - 1));
            padGrid.light (36 + GRID_SCENES[i], buttonColorID);
        }

        final ITrackBank trackBank = this.model.getTrackBank ();
        for (int i = 0; i < 4; i++)
        {
            final ITrack track = trackBank.getItem (i);

            // Draw the stop buttons
            padGrid.light (36 + GRID_STOP[i], ExquisColorManager.DARK_GREY);

            // Draw the mute buttons
            padGrid.light (36 + GRID_MUTE[i], track.isMute () ? ExquisColorManager.DARKER_ORANGE : ExquisColorManager.BLACK);

            // Draw the solo buttons
            padGrid.light (36 + GRID_SOLO[i], track.isSolo () ? ExquisColorManager.DARKER_BLUE : ExquisColorManager.BLACK);

            // Draw the select/rec-arm buttons
            if (this.isRecArmActive)
                padGrid.light (36 + GRID_SELECT[i], track.doesExist () && track.isRecArm () ? ExquisColorManager.RED : ExquisColorManager.BLACK);
            else
                padGrid.light (36 + GRID_SELECT[i], track.doesExist () ? DAWColor.getColorID (track.getColor ()) : ColorManager.BUTTON_STATE_OFF);
        }

        padGrid.light (36 + GRID_STOP[4], ExquisColorManager.WHITE);
        padGrid.light (36 + GRID_MUTE[4], ExquisColorManager.DARK_ORANGE);
        padGrid.light (36 + GRID_SOLO[4], ExquisColorManager.BLUE);
        padGrid.light (36 + GRID_SELECT[4], this.isRecArmActive ? ExquisColorManager.RED : ExquisColorManager.DARK_RED);

        padGrid.light (36 + FUNCTION_DELETE, ExquisColorManager.DARK_RED);
        padGrid.light (36 + FUNCTION_DUPLICATE, ExquisColorManager.DARKER_GREEN);
        padGrid.light (36 + FUNCTION_NEW_CLIP, ExquisColorManager.DARKER_YELLOW);
        padGrid.light (36 + FUNCTION_ADD_SCENE, ExquisColorManager.DARKER_GREEN);
        padGrid.light (36 + FUNCTION_ALTERNATIVE, ExquisColorManager.DARK_GREY);
        padGrid.light (36 + FUNCTION_SELECT, ExquisColorManager.DARK_GREY);
    }


    /** {@inheritDoc} */
    @Override
    protected void drawPad (final ISlot slot, final int x, final int y, final boolean isArmed)
    {
        final int note = GRID_MAPPING[y][x];
        final LightInfo color = this.getPadColor (slot, isArmed);
        this.surface.getPadGrid ().light (36 + note, color.getColor (), color.getBlinkColor (), color.isFast ());
    }


    private static int isFeatureButton (final int note, final int [] buttonIndices)
    {
        for (int i = 0; i < buttonIndices.length; i++)
            if (buttonIndices[i] == note)
                return i;
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    protected boolean isDeletePressed ()
    {
        return this.surface.isPressed (ButtonID.get (ButtonID.PAD1, FUNCTION_DELETE));
    }


    /** {@inheritDoc} */
    @Override
    protected boolean isDuplicatePressed ()
    {
        return this.surface.isPressed (ButtonID.get (ButtonID.PAD1, FUNCTION_DUPLICATE));
    }


    /** {@inheritDoc} */
    @Override
    protected boolean isAlternateFunction ()
    {
        return this.surface.isPressed (ButtonID.get (ButtonID.PAD1, FUNCTION_ALTERNATIVE));
    }
}