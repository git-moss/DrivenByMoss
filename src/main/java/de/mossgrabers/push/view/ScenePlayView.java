// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.view;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.BitwigColors;
import de.mossgrabers.framework.daw.SceneBankProxy;
import de.mossgrabers.framework.daw.TrackBankProxy;
import de.mossgrabers.framework.daw.data.SceneData;
import de.mossgrabers.framework.daw.data.SlotData;
import de.mossgrabers.framework.view.AbstractView;
import de.mossgrabers.framework.view.SceneView;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.PushColors;
import de.mossgrabers.push.controller.PushControlSurface;


/**
 * The Color view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ScenePlayView extends AbstractView<PushControlSurface, PushConfiguration> implements SceneView
{
    private TrackBankProxy trackBank;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ScenePlayView (final PushControlSurface surface, final Model model)
    {
        super ("Scene Play", surface, model);

        this.trackBank = new TrackBankProxy (model.getHost (), model.getValueChanger (), model.getTrackBank ().getCursorTrack (), 8, 64, 0, true);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();
        this.trackBank.enableObservers (true);
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        super.onActivate ();
        this.trackBank.enableObservers (false);
    }


    /** {@inheritDoc} */
    @Override
    public boolean usesButton (final int buttonID)
    {
        switch (buttonID)
        {
            case PushControlSurface.PUSH_BUTTON_REPEAT:
            case PushControlSurface.PUSH_BUTTON_OCTAVE_UP:
            case PushControlSurface.PUSH_BUTTON_OCTAVE_DOWN:
                return false;

            default:
                return !this.surface.getConfiguration ().isPush2 () || buttonID != PushControlSurface.PUSH_BUTTON_USER_MODE;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final SceneBankProxy sceneBank = this.trackBank.getSceneBank ();
        for (int i = 0; i < 64; i++)
        {
            final SceneData scene = sceneBank.getScene (i);
            String color = PadGrid.GRID_OFF;

            if (scene.doesExist ())
            {
                color = BitwigColors.BITWIG_COLOR_GREEN;

                // Find the color of the first clip of the scene
                for (int t = 0; t < this.trackBank.getNumTracks (); t++)
                {
                    final SlotData slotData = this.trackBank.getTrack (t).getSlots ()[i];
                    if (slotData.doesExist () && slotData.hasContent ())
                    {
                        color = BitwigColors.getColorIndex (slotData.getColor ());
                        break;
                    }
                }
            }

            this.surface.getPadGrid ().light (36 + i, color);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity != 0)
            this.trackBank.launchScene (note - 36);
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int scene, final ButtonEvent event)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        final int black = this.surface.getConfiguration ().isPush2 () ? PushColors.PUSH2_COLOR_BLACK : PushColors.PUSH1_COLOR_BLACK;
        for (int i = 0; i < 8; i++)
            this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE1 + i, black);
    }
}