// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.view;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushColorManager;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IClip;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractSequencerView;


/**
 * The Clip view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ClipView extends AbstractSequencerView<PushControlSurface, PushConfiguration>
{
    private int          loopPadPressed = -1;
    private final int [] padResolutions =
    {
        1,
        4,
        16
    };
    private int          padResolution  = 0;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ClipView (final PushControlSurface surface, final IModel model)
    {
        super ("Clip", surface, model, 0, 0, true);
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.delayedUpdateNoteMapping (EMPTY_TABLE);
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
        // Button pressed?
        if (velocity > 0)
        {
            // Not yet a button pressed, store it
            if (this.loopPadPressed == -1)
                this.loopPadPressed = pad;
        }
        else if (this.loopPadPressed != -1)
        {
            final int start = this.loopPadPressed < pad ? this.loopPadPressed : pad;
            final int end = (this.loopPadPressed < pad ? pad : this.loopPadPressed) + 1;
            final double quartersPerPad = this.getQuartersPerPad ();

            // Set a new loop between the 2 selected pads
            final double newStart = start * quartersPerPad;
            final IClip clip = this.getClip ();
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
        final INoteClip clip = this.getClip ();
        // Clip length/loop area
        final int step = clip.getCurrentStep ();
        final double quartersPerPad = this.getQuartersPerPad ();
        final int stepsPerMeasure = (int) Math.round (quartersPerPad / Resolution.getValueAt (this.selectedResolutionIndex));
        final int currentMeasure = step / stepsPerMeasure;
        final double maxQuarters = quartersPerPad * 64;
        final double start = clip.getLoopStart ();
        final int loopStartPad = (int) Math.floor (Math.max (0, start) / quartersPerPad);
        final int loopEndPad = (int) Math.ceil (Math.min (maxQuarters, start + clip.getLoopLength ()) / quartersPerPad);
        final boolean isPush2 = this.surface.getConfiguration ().isPush2 ();
        final int white = isPush2 ? PushColorManager.PUSH2_COLOR2_WHITE : PushColorManager.PUSH1_COLOR2_WHITE;
        final int green = isPush2 ? PushColorManager.PUSH2_COLOR2_GREEN : PushColorManager.PUSH1_COLOR2_GREEN;
        final int off = isPush2 ? PushColorManager.PUSH2_COLOR_BLACK : PushColorManager.PUSH1_COLOR_BLACK;
        for (int pad = 0; pad < 64; pad++)
        {
            final int color;
            if (pad >= loopStartPad && pad < loopEndPad)
                color = pad == currentMeasure ? green : white;
            else
                color = off;
            this.surface.getPadGrid ().lightEx (pad % 8, pad / 8, color, -1, false);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN)
            return;

        final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        if (index >= 3)
            return;

        this.padResolution = index;
        this.surface.getDisplay ().notify ("1/" + this.padResolutions[this.padResolution]);
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        final int scene = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        if (scene < 0 || scene >= 8)
            return AbstractFeatureGroup.BUTTON_COLOR_OFF;

        if (scene < 3)
            return scene == this.padResolution ? AbstractSequencerView.COLOR_RESOLUTION_SELECTED : AbstractSequencerView.COLOR_RESOLUTION;
        return AbstractSequencerView.COLOR_RESOLUTION_OFF;
    }


    private double getQuartersPerPad ()
    {
        return this.model.getTransport ().getQuartersPerMeasure () / (double) this.padResolutions[this.padResolution];
    }
}