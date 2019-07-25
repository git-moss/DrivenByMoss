// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode;

import de.mossgrabers.controller.push.controller.PushColors;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.controller.PushDisplay;
import de.mossgrabers.controller.push.mode.track.AbstractTrackMode;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.graphics.display.DisplayModel;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.Pair;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.framework.view.Views;

import java.util.ArrayList;
import java.util.List;


/**
 * Mode for displaying clips or scenes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SessionMode extends AbstractTrackMode
{
    private enum RowDisplayMode
    {
        ALL,
        UPPER,
        LOWER
    }

    private RowDisplayMode rowDisplayMode;
    private ISceneBank     sceneBank;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public SessionMode (final PushControlSurface surface, final IModel model)
    {
        super ("Session", surface, model);
        this.isTemporary = false;
        this.rowDisplayMode = this.isPush2 ? RowDisplayMode.ALL : RowDisplayMode.UPPER;
        this.sceneBank = model.createSceneBank (64);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;
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

        if (this.isPush2)
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
    public void updateSecondRow ()
    {
        final ColorManager colorManager = this.model.getColorManager ();
        this.surface.updateTrigger (102, colorManager.getColor (this.rowDisplayMode == RowDisplayMode.UPPER ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON));
        this.surface.updateTrigger (103, colorManager.getColor (this.rowDisplayMode == RowDisplayMode.LOWER ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON));
        for (int i = 0; i < 5; i++)
            this.surface.updateTrigger (104 + i, colorManager.getColor (AbstractMode.BUTTON_COLOR_OFF));

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        this.surface.updateTrigger (109, tb.hasParent () ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH2_COLOR_BLACK);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        if (this.surface.getViewManager ().isActiveView (Views.VIEW_SESSION))
            this.updateDisplay1Clips ();
        else
            this.updateDisplay1Scenes ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        if (this.surface.getViewManager ().isActiveView (Views.VIEW_SESSION))
            this.updateDisplay2Clips ();
        else
            this.updateDisplay2Scenes ();
    }


    private void updateDisplay1Scenes ()
    {
        final int maxCols = 8;
        final int maxRows = this.rowDisplayMode == RowDisplayMode.ALL ? 8 : 4;

        final Display d = this.surface.getDisplay ().clear ();
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
                d.setCell (row, col, isSel ? PushDisplay.SELECT_ARROW + n : n);
            }
        }
        d.allDone ();
    }


    private void updateDisplay1Clips ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final boolean flipSession = this.surface.getConfiguration ().isFlipSession ();

        final int numTracks = tb.getPageSize ();
        final int numScenes = tb.getSceneBank ().getPageSize ();

        final int maxCols = flipSession ? numScenes : numTracks;
        int maxRows = flipSession ? numTracks : numScenes;
        if (this.rowDisplayMode != RowDisplayMode.ALL)
            maxRows = maxRows / 2;

        final Display d = this.surface.getDisplay ().clear ();
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
                        final double [] color = slot.getColor ();
                        if (color[0] != 0 || color[1] != 0 || color[2] != 0)
                            name = "--------";
                    }
                }

                if (slot.isSelected ())
                    name = PushDisplay.SELECT_ARROW + name;
                else if (slot.isPlaying ())
                    name = ">" + name;
                else if (slot.isPlayingQueued () || slot.isRecordingQueued ())
                    name = ")" + name;
                else if (track.isRecArm () || slot.isRecording ())
                    name = "*" + name;
                else if (slot.hasContent ())
                    name = PushDisplay.RIGHT_ARROW + name;
                else
                    name = PushDisplay.DEGREE + name;

                d.setCell (row, col, StringUtils.shortenAndFixASCII (name, 8));
            }
        }
        d.allDone ();
    }


    private void updateDisplay2Scenes ()
    {
        final int maxCols = 8;
        final int maxRows = this.rowDisplayMode == RowDisplayMode.ALL ? 8 : 4;

        final DisplayModel message = this.surface.getDisplay ().getModel ();
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
            message.addSceneListElement (scenes);
        }
        message.send ();
    }


    private void updateDisplay2Clips ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final DisplayModel message = this.surface.getDisplay ().getModel ();

        final boolean flipSession = this.surface.getConfiguration ().isFlipSession ();

        final int numTracks = tb.getPageSize ();
        final int numScenes = tb.getSceneBank ().getPageSize ();

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
            message.addSlotListElement (slots);
        }
        message.send ();
    }
}