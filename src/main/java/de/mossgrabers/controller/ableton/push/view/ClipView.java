// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.view;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushColorManager;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.clip.IClip;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.sequencer.AbstractSequencerView;


/**
 * The Clip view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ClipView extends AbstractSequencerView<PushControlSurface, PushConfiguration>
{
    private static final int [] PAD_RESOLUTIONS  =
    {
        1,
        4,
        16
    };

    private final ITransport    transport;
    private int                 padResolution    = 0;
    private int                 firstPressedPad  = -1;
    private boolean             hasSecondPressed = false;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ClipView (final PushControlSurface surface, final IModel model)
    {
        super ("Clip", surface, model, 0, 0, true);

        this.transport = model.getTransport ();
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

        // On button press...
        if (velocity > 0)
        {
            // Not yet a button pressed, store it
            if (this.firstPressedPad == -1)
                this.firstPressedPad = pad;
            return;
        }

        // On button release...
        final boolean isSecondPad = this.firstPressedPad != pad;
        if (isSecondPad)
            this.hasSecondPressed = true;
        if (isSecondPad || !this.hasSecondPressed)
        {
            final int start = this.firstPressedPad < pad ? this.firstPressedPad : pad;
            final int end = (this.firstPressedPad < pad ? pad : this.firstPressedPad) + 1;
            final double quartersPerPad = this.getQuartersPerPad ();

            // Set a new loop between the 2 selected pads
            final double newStart = start * quartersPerPad;
            final IClip clip = this.getClip ();
            clip.setLoopStart (newStart);
            clip.setLoopLength ((end - start) * quartersPerPad);
            clip.setPlayRange (newStart, end * quartersPerPad);
        }

        if (this.firstPressedPad == pad)
        {
            this.firstPressedPad = -1;
            this.hasSecondPressed = false;
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
        final int stepsPerMeasure = (int) Math.round (quartersPerPad / Resolution.getValueAt (this.getResolutionIndex ()));
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
        this.surface.getDisplay ().notify ("1/" + PAD_RESOLUTIONS[this.padResolution]);
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
        return this.transport.getQuartersPerMeasure () / (double) PAD_RESOLUTIONS[this.padResolution];
    }
}