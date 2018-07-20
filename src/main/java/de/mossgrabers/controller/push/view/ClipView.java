// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushColors;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.ICursorClip;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractSequencerView;


/**
 * The Clip view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ClipView extends AbstractSequencerView<PushControlSurface, PushConfiguration>
{
    private int    loopPadPressed = -1;
    private int [] padResolutions =
    {
        1,
        4,
        16
    };
    private int    padResolution  = 0;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ClipView (final PushControlSurface surface, final IModel model)
    {
        super ("Clip", surface, model, 0, 0);
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.delayedUpdateNoteMapping (EMPTY_TABLE);
    }


    /** {@inheritDoc} */
    @Override
    public boolean usesButton (final int buttonID)
    {
        switch (buttonID)
        {
            case PushControlSurface.PUSH_BUTTON_REPEAT:
            case PushControlSurface.PUSH_BUTTON_OCTAVE_DOWN:
            case PushControlSurface.PUSH_BUTTON_OCTAVE_UP:
                return false;

            default:
                return !this.surface.getConfiguration ().isPush2 () || buttonID != PushControlSurface.PUSH_BUTTON_USER_MODE;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        final int index = note - 36;
        final int x = index % 8;
        final int y = index / 8;

        // Clip length/loop area
        final int pad = (7 - y) * 8 + x;
        if (velocity > 0) // Button pressed
        {
            if (this.loopPadPressed == -1) // Not yet a button pressed, store it
                this.loopPadPressed = pad;
        }
        else if (this.loopPadPressed != -1)
        {
            final int start = this.loopPadPressed < pad ? this.loopPadPressed : pad;
            final int end = (this.loopPadPressed < pad ? pad : this.loopPadPressed) + 1;
            final double quartersPerPad = this.getQuartersPerPad ();

            // Set a new loop between the 2 selected pads
            final double newStart = start * quartersPerPad;
            final ICursorClip clip = this.model.getCursorClip ();
            clip.setLoopStart (newStart);
            clip.setLoopLength ((int) ((end - start) * quartersPerPad));
            clip.setPlayRange (newStart, end * quartersPerPad);

            this.loopPadPressed = -1;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final ICursorClip clip = this.model.getCursorClip ();
        // Clip length/loop area
        final int step = clip.getCurrentStep ();
        final double quartersPerPad = this.getQuartersPerPad ();
        final int stepsPerMeasure = (int) Math.round (quartersPerPad / RESOLUTIONS[this.selectedIndex]);
        final int currentMeasure = step / stepsPerMeasure;
        final double maxQuarters = quartersPerPad * 64;
        final double start = clip.getLoopStart ();
        final int loopStartPad = (int) Math.floor (Math.max (0, start) / quartersPerPad);
        final int loopEndPad = (int) Math.ceil (Math.min (maxQuarters, start + clip.getLoopLength ()) / quartersPerPad);
        final boolean isPush2 = this.surface.getConfiguration ().isPush2 ();
        final int white = isPush2 ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH1_COLOR2_WHITE;
        final int green = isPush2 ? PushColors.PUSH2_COLOR2_GREEN : PushColors.PUSH1_COLOR2_GREEN;
        final int off = isPush2 ? PushColors.PUSH2_COLOR_BLACK : PushColors.PUSH1_COLOR_BLACK;
        for (int pad = 0; pad < 64; pad++)
            this.surface.getPadGrid ().lightEx (pad % 8, pad / 8, pad >= loopStartPad && pad < loopEndPad ? pad == currentMeasure ? green : white : off, -1, false);
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final int res = 7 - index;
        if (res <= 3)
            this.padResolution = res;
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        final boolean isPush2 = this.surface.getConfiguration ().isPush2 ();
        final int yellow = isPush2 ? PushColors.PUSH2_COLOR_SCENE_YELLOW : PushColors.PUSH1_COLOR_SCENE_YELLOW;
        final int green = isPush2 ? PushColors.PUSH2_COLOR_SCENE_GREEN : PushColors.PUSH1_COLOR_SCENE_GREEN;
        final int off = isPush2 ? PushColors.PUSH2_COLOR_BLACK : PushColors.PUSH1_COLOR_BLACK;
        for (int i = 0; i < 8; i++)
        {
            if (i < 3)
                this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE1 + i, i == this.padResolution ? yellow : green);
            else
                this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE1 + i, off);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateButtons ()
    {
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_OCTAVE_UP, ColorManager.BUTTON_STATE_OFF);
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_OCTAVE_DOWN, ColorManager.BUTTON_STATE_OFF);
    }


    private double getQuartersPerPad ()
    {
        return this.model.getTransport ().getQuartersPerMeasure () / (double) this.padResolutions[this.padResolution];
    }
}