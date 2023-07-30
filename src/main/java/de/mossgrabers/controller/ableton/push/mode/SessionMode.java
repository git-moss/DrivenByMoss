// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode;

import de.mossgrabers.controller.ableton.push.controller.Push1Display;
import de.mossgrabers.controller.ableton.push.controller.PushColorManager;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.controller.ableton.push.mode.track.AbstractTrackMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.Pair;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.framework.view.Views;

import java.util.ArrayList;
import java.util.List;


/**
 * Mode for displaying clips or scenes.
 *
 * @author Jürgen Moßgraber
 */
public class SessionMode extends AbstractTrackMode
{
    private enum RowDisplayMode
    {
        ALL,
        UPPER,
        LOWER
    }


    private RowDisplayMode   rowDisplayMode;
    private final ISceneBank sceneBank;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public SessionMode (final PushControlSurface surface, final IModel model)
    {
        super ("Session", surface, model);

        this.sceneBank = model.getSceneBank (64);

        this.rowDisplayMode = this.isPushModern ? RowDisplayMode.ALL : RowDisplayMode.UPPER;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.setTouchedKnob (index, isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        if (this.isPushModern)
        {
            if (index == 0)
            {
                if (this.rowDisplayMode == RowDisplayMode.ALL || this.rowDisplayMode == RowDisplayMode.LOWER)
                    this.rowDisplayMode = RowDisplayMode.UPPER;
                else
                    this.rowDisplayMode = RowDisplayMode.ALL;
            }
            else if (index == 1)
            {
                if (this.rowDisplayMode == RowDisplayMode.ALL || this.rowDisplayMode == RowDisplayMode.UPPER)
                    this.rowDisplayMode = RowDisplayMode.LOWER;
                else
                    this.rowDisplayMode = RowDisplayMode.ALL;
            }
            else if (index == 7)
                super.onSecondRow (index, event);
        }
        else
        {
            if (index < 2)
                this.rowDisplayMode = this.rowDisplayMode == RowDisplayMode.UPPER ? RowDisplayMode.LOWER : RowDisplayMode.UPPER;
            else if (index == 7)
                this.model.getTrackBank ().selectParent ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final int index = this.isButtonRow (1, buttonID);
        if (index >= 0)
        {
            if (index == 0)
                return this.colorManager.getColorIndex (this.rowDisplayMode == RowDisplayMode.UPPER ? AbstractMode.BUTTON_COLOR_HI : AbstractFeatureGroup.BUTTON_COLOR_ON);
            if (index == 1)
                return this.colorManager.getColorIndex (this.rowDisplayMode == RowDisplayMode.LOWER ? AbstractMode.BUTTON_COLOR_HI : AbstractFeatureGroup.BUTTON_COLOR_ON);
            if (index < 5)
                return this.colorManager.getColorIndex (AbstractFeatureGroup.BUTTON_COLOR_OFF);

            final ITrackBank tb = this.model.getCurrentTrackBank ();
            return tb.hasParent () ? PushColorManager.PUSH2_COLOR2_WHITE : PushColorManager.PUSH2_COLOR_BLACK;
        }

        return super.getButtonColor (buttonID);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        if (this.surface.getViewManager ().isActive (Views.SESSION))
            this.updateDisplay1Clips (display);
        else
            this.updateDisplay1Scenes (display);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        if (this.surface.getViewManager ().isActive (Views.SESSION))
            this.updateDisplay2Clips (display);
        else
            this.updateDisplay2Scenes (display);
    }


    private void updateDisplay1Scenes (final ITextDisplay display)
    {
        final int maxCols = 8;
        final int maxRows = this.rowDisplayMode == RowDisplayMode.ALL ? 8 : 4;

        for (int col = 0; col < maxCols; col++)
        {
            for (int row = 0; row < maxRows; row++)
            {
                int sceneIndex = (maxRows - 1 - row) * 8 + col;
                if (this.rowDisplayMode == RowDisplayMode.LOWER)
                    sceneIndex += 32;

                final IScene scene = this.sceneBank.getItem (sceneIndex);
                if (!scene.doesExist ())
                    continue;
                final boolean isSel = scene.isSelected ();
                final String n = StringUtils.shortenAndFixASCII (scene.getName (8), isSel ? 7 : 8);
                display.setCell (row, col, isSel ? Push1Display.SELECT_ARROW + n : n);
            }
        }
    }


    private void updateDisplay1Clips (final ITextDisplay display)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final int numTracks = tb.getPageSize ();
        final int numScenes = tb.getSceneBank ().getPageSize ();

        final boolean flipSession = this.surface.getConfiguration ().isFlipSession ();
        final int maxCols = flipSession ? numScenes : numTracks;
        int maxRows = flipSession ? numTracks : numScenes;
        if (this.rowDisplayMode != RowDisplayMode.ALL)
            maxRows = maxRows / 2;

        for (int col = 0; col < maxCols; col++)
        {
            for (int row = 0; row < maxRows; row++)
            {
                int x = flipSession ? row : col;
                int y = flipSession ? col : row;

                if (this.rowDisplayMode == RowDisplayMode.LOWER)
                {
                    if (flipSession)
                        x += maxRows;
                    else
                        y += maxRows;
                }

                final ITrack track = tb.getItem (x);
                final ISlot slot = track.getSlotBank ().getItem (y);
                if (!slot.doesExist ())
                    continue;

                String name = slot.getName (8);
                if (track.isGroup ())
                {
                    if (name.isEmpty ())
                        name = "Scene " + (slot.getPosition () + 1);
                }
                else
                {
                    // TODO Bugfix required: Workaround to displaying unnamed clips, since
                    // doesExist does not work reliably -
                    // https://github.com/teotigraphix/Framework4Bitwig/issues/193
                    if (name.isEmpty ())
                    {
                        final ColorEx color = slot.getColor ();
                        if (color.getRed () != 0 || color.getGreen () != 0 || color.getBlue () != 0)
                            name = "--------";
                    }
                }

                if (slot.isSelected ())
                    name = Push1Display.SELECT_ARROW + name;
                else if (slot.isPlaying ())
                    name = ">" + name;
                else if (slot.isPlayingQueued () || slot.isRecordingQueued ())
                    name = ")" + name;
                else if (track.isRecArm () || slot.isRecording ())
                    name = "*" + name;
                else if (slot.hasContent ())
                    name = Push1Display.RIGHT_ARROW + name;
                else
                    name = Push1Display.DEGREE + name;

                display.setCell (row, col, StringUtils.shortenAndFixASCII (name, 8));
            }
        }
    }


    private void updateDisplay2Scenes (final IGraphicDisplay display)
    {
        final int maxCols = 8;
        final int maxRows = this.rowDisplayMode == RowDisplayMode.ALL ? 8 : 4;

        for (int col = 0; col < maxCols; col++)
        {
            final List<IScene> scenes = new ArrayList<> (maxRows);
            for (int row = 0; row < maxRows; row++)
            {
                int sceneIndex = (maxRows - 1 - row) * 8 + col;
                if (this.rowDisplayMode == RowDisplayMode.LOWER)
                    sceneIndex += 32;
                scenes.add (this.sceneBank.getItem (sceneIndex));
            }
            display.addSceneListElement (scenes);
        }
    }


    private void updateDisplay2Clips (final IGraphicDisplay display)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final int numTracks = tb.getPageSize ();
        final int numScenes = tb.getSceneBank ().getPageSize ();

        final boolean flipSession = this.surface.getConfiguration ().isFlipSession ();
        final int maxCols = flipSession ? numScenes : numTracks;
        int maxRows = flipSession ? numTracks : numScenes;
        if (this.rowDisplayMode != RowDisplayMode.ALL)
            maxRows = maxRows / 2;

        for (int col = 0; col < maxCols; col++)
        {
            final List<Pair<ITrack, ISlot>> slots = new ArrayList<> (maxRows);

            for (int row = 0; row < maxRows; row++)
            {
                int x = flipSession ? row : col;
                int y = flipSession ? col : row;

                if (this.rowDisplayMode == RowDisplayMode.LOWER)
                {
                    if (flipSession)
                        x += maxRows;
                    else
                        y += maxRows;
                }
                final ITrack track = tb.getItem (x);
                slots.add (new Pair<> (track, track.getSlotBank ().getItem (y)));
            }
            display.addSlotListElement (slots);
        }
    }
}