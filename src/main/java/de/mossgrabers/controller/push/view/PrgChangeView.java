// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushColors;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractView;
import de.mossgrabers.framework.view.SceneView;


/**
 * The Program Change view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PrgChangeView extends AbstractView<PushControlSurface, PushConfiguration> implements SceneView
{
    private int []  greens;
    private int []  yellows;
    private int     bankNumber    = 0;
    private int     programNumber = -1;
    private boolean isToggled     = false;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public PrgChangeView (final PushControlSurface surface, final IModel model)
    {
        super ("PrgChnge", surface, model);

        final boolean isPush2 = surface.getConfiguration ().isPush2 ();

        final int greenHi = isPush2 ? PushColors.PUSH2_COLOR2_GREEN_HI : PushColors.PUSH1_COLOR2_GREEN_HI;
        final int green = isPush2 ? PushColors.PUSH2_COLOR2_GREEN : PushColors.PUSH1_COLOR2_GREEN;
        final int greenLo = isPush2 ? PushColors.PUSH2_COLOR2_GREEN_LO : PushColors.PUSH1_COLOR2_GREEN_LO;
        final int greenSpring = isPush2 ? PushColors.PUSH2_COLOR2_GREEN_SPRING : PushColors.PUSH1_COLOR2_GREEN_SPRING;
        this.greens = new int []
        {
            greenHi,
            green,
            greenLo,
            greenSpring,
            greenHi,
            green,
            greenLo,
            greenSpring
        };

        final int yellowHi = isPush2 ? PushColors.PUSH2_COLOR2_YELLOW_HI : PushColors.PUSH1_COLOR2_YELLOW_HI;
        final int yellow = isPush2 ? PushColors.PUSH2_COLOR2_YELLOW : PushColors.PUSH1_COLOR2_YELLOW;
        final int yellowLo = isPush2 ? PushColors.PUSH2_COLOR2_YELLOW_LO : PushColors.PUSH1_COLOR2_YELLOW_LO;
        final int yellowLime = isPush2 ? PushColors.PUSH2_COLOR2_YELLOW_LIME : PushColors.PUSH1_COLOR2_YELLOW_LIME;
        this.yellows = new int []
        {
            yellowHi,
            yellow,
            yellowLo,
            yellowLime,
            yellowHi,
            yellow,
            yellowLo,
            yellowLime
        };
    }


    /** {@inheritDoc} */
    @Override
    public boolean usesButton (final int buttonID)
    {
        switch (buttonID)
        {
            case PushControlSurface.PUSH_BUTTON_OCTAVE_DOWN:
            case PushControlSurface.PUSH_BUTTON_OCTAVE_UP:
                return false;

            default:
                return !this.surface.getConfiguration ().isPush2 () || buttonID != PushControlSurface.PUSH_BUTTON_USER_MODE;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final int newBank = index;
        if (newBank == this.bankNumber)
            this.isToggled = !this.isToggled;
        else
        {
            this.bankNumber = newBank;
            this.isToggled = false;
            this.surface.sendMidiEvent (0xB0, 32, this.bankNumber);
            // Forces the bank change
            if (this.programNumber != -1)
                this.surface.sendMidiEvent (0xC0, this.programNumber, 0);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        final boolean isPush2 = this.surface.getConfiguration ().isPush2 ();
        final int green = isPush2 ? PushColors.PUSH2_COLOR_SCENE_GREEN : PushColors.PUSH1_COLOR_SCENE_GREEN;
        final int yellow = isPush2 ? PushColors.PUSH2_COLOR_SCENE_YELLOW : PushColors.PUSH1_COLOR_SCENE_YELLOW;
        final int black = isPush2 ? PushColors.PUSH2_COLOR_BLACK : PushColors.PUSH1_COLOR_BLACK;
        for (int i = 0; i < 8; i++)
            this.surface.updateTrigger (PushControlSurface.PUSH_BUTTON_SCENE1 + i, this.bankNumber == 7 - i ? this.isToggled ? yellow : green : black);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final int [] colors = this.isToggled ? this.yellows : this.greens;
        final int selPad = this.isToggled ? this.programNumber >= 64 ? this.programNumber - 64 : -1 : this.programNumber < 64 ? this.programNumber : -1;
        final PadGrid gridPad = this.surface.getPadGrid ();
        final boolean isPush2 = this.surface.getConfiguration ().isPush2 ();
        final int red = isPush2 ? PushColors.PUSH2_COLOR2_RED : PushColors.PUSH1_COLOR2_RED;
        for (int i = 36; i < 100; i++)
        {
            final int pad = i - 36;
            final int row = pad / 8;
            gridPad.light (i, selPad == pad ? red : colors[row], -1, false);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;
        this.programNumber = note - 36 + (this.isToggled ? 64 : 0);
        this.surface.sendMidiEvent (0xC0, this.programNumber, 0);
    }


    /** {@inheritDoc} */
    @Override
    public void updateArrows ()
    {
        this.surface.updateTrigger (PushControlSurface.PUSH_BUTTON_OCTAVE_UP, ColorManager.BUTTON_STATE_OFF);
        this.surface.updateTrigger (PushControlSurface.PUSH_BUTTON_OCTAVE_DOWN, ColorManager.BUTTON_STATE_OFF);
    }
}