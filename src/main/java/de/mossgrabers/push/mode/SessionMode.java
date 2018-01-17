// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.mode;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.BitwigColors;
import de.mossgrabers.framework.daw.SceneBankProxy;
import de.mossgrabers.framework.daw.data.SceneData;
import de.mossgrabers.framework.daw.data.SlotData;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.push.controller.DisplayMessage;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.controller.PushDisplay;
import de.mossgrabers.push.view.ScenePlayView;
import de.mossgrabers.push.view.Views;

import java.util.ArrayList;
import java.util.List;


/**
 * Mode for editing the parameters of a clip.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SessionMode extends BaseMode
{
    private static final double [] BLACK = new double []
    {
        0,
        0,
        0
    };

    private static enum RowDisplayMode
    {
        ALL,
        UPPER,
        LOWER
    }

    private RowDisplayMode rowDisplayMode;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public SessionMode (final PushControlSurface surface, final Model model)
    {
        super (surface, model);
        this.isTemporary = false;
        this.rowDisplayMode = this.isPush2 ? RowDisplayMode.ALL : RowDisplayMode.UPPER;
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;

        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
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
        }
        else
        {
            this.rowDisplayMode = this.rowDisplayMode == RowDisplayMode.UPPER ? RowDisplayMode.LOWER : RowDisplayMode.UPPER;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final ColorManager colorManager = this.model.getColorManager ();
        this.surface.updateButton (20, colorManager.getColor (this.rowDisplayMode == RowDisplayMode.UPPER ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON));
        this.surface.updateButton (21, colorManager.getColor (this.rowDisplayMode == RowDisplayMode.LOWER ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON));
        for (int i = 2; i < 8; i++)
            this.surface.updateButton (20 + i, colorManager.getColor (AbstractMode.BUTTON_COLOR_OFF));
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
        final AbstractTrackBankProxy tb = ((ScenePlayView) this.surface.getViewManager ().getView (Views.VIEW_SCENE_PLAY)).getTrackBank ();
        final SceneBankProxy sceneBank = tb.getSceneBank ();

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

                final SceneData scene = sceneBank.getScene (sceneIndex);
                if (scene.doesExist ())
                    d.setCell (row, col, this.optimizeName (scene.getName (8), 8));
            }
        }
        d.allDone ();
    }


    private void updateDisplay1Clips ()
    {
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final boolean flipSession = this.surface.getConfiguration ().isFlipSession ();

        final int numTracks = tb.getNumTracks ();
        final int numScenes = tb.getNumScenes ();

        int maxCols = flipSession ? numScenes : numTracks;
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

                final SlotData [] slots = tb.getTrack (x).getSlots ();
                if (slots[y].doesExist ())
                {
                    String optimizedName = this.optimizeName (slots[y].getName (8), 8);
                    // TODO Bugfix required: Workaround to displaying unnamed clips, since
                    // doesExist does not work reliably
                    if (optimizedName.length () == 0)
                    {
                        double [] color = slots[y].getColor ();
                        if (color[0] != 0 || color[1] != 0 || color[2] != 0)
                            optimizedName = "[------]";
                    }

                    d.setCell (row, col, optimizedName);
                }
            }
        }
        d.allDone ();
    }


    private void updateDisplay2Scenes ()
    {
        final AbstractTrackBankProxy tb = ((ScenePlayView) this.surface.getViewManager ().getView (Views.VIEW_SCENE_PLAY)).getTrackBank ();
        final SceneBankProxy sceneBank = tb.getSceneBank ();

        final int maxCols = 8;
        final int maxRows = this.rowDisplayMode == RowDisplayMode.ALL ? 8 : 4;

        final DisplayMessage message = ((PushDisplay) this.surface.getDisplay ()).createMessage ();
        for (int col = 0; col < maxCols; col++)
        {
            final String [] items = new String [maxRows];
            final List<double []> slotColors = new ArrayList<> (maxRows);

            for (int row = 0; row < maxRows; row++)
            {
                int sceneIndex = (maxRows - 1 - row) * 8 + col;
                if (this.rowDisplayMode == RowDisplayMode.LOWER)
                    sceneIndex += 32;

                final SceneData scene = sceneBank.getScene (sceneIndex);
                items[row] = scene.doesExist () ? scene.getName () : "";
                slotColors.add (scene.doesExist () ? BitwigColors.getColorEntry (tb.getColorOfFirstClipInScene (sceneIndex)) : BLACK);
            }
            message.addBoxListElement (items, slotColors);
        }
        message.send ();
    }


    private void updateDisplay2Clips ()
    {
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final DisplayMessage message = ((PushDisplay) this.surface.getDisplay ()).createMessage ();

        final boolean flipSession = this.surface.getConfiguration ().isFlipSession ();

        final int numTracks = tb.getNumTracks ();
        final int numScenes = tb.getNumScenes ();

        int maxCols = flipSession ? numScenes : numTracks;
        int maxRows = flipSession ? numTracks : numScenes;
        if (this.rowDisplayMode != RowDisplayMode.ALL)
            maxRows = maxRows / 2;

        for (int col = 0; col < maxCols; col++)
        {
            final String [] items = new String [maxRows];
            final List<double []> slotColors = new ArrayList<> (maxRows);

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

                final SlotData [] slots = tb.getTrack (x).getSlots ();
                items[row] = slots[y].doesExist () ? slots[y].getName () : "";
                slotColors.add (slots[y].getColor ());
            }
            message.addBoxListElement (items, slotColors);
        }

        message.send ();
    }
}