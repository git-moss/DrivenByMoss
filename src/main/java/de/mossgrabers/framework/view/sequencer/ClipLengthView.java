// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view.sequencer;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.clip.IClip;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * The Clip view. Allows to change the length of a clip.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class ClipLengthView<S extends IControlSurface<C>, C extends Configuration> extends AbstractSequencerView<S, C>
{
    /** The color for a part after the clip length. */
    public static final String  COLOR_OUTSIDE    = "COLOR_OUTSIDE";
    /** The color for a part of the clip. */
    public static final String  COLOR_PART       = "COLOR_PART";

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
     * @param useDawColors True to use the DAW color of the clip to color it (for full RGB devices)
     */
    public ClipLengthView (final S surface, final IModel model, final boolean useDawColors)
    {
        super (Views.NAME_CLIP_LENGTH, surface, model, 0, 0, useDawColors);

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
        final IPadGrid padGrid = this.surface.getPadGrid ();
        final int cols = padGrid.getCols ();

        final int index = note - 36;
        final int x = index % cols;
        final int y = index / cols;

        // Invert to top/left to bottom/right
        final int pad = (cols - 1 - y) * cols + x;

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
        final double start = clip.getLoopStart ();
        final double quartersPerPad = this.getQuartersPerPad ();
        final double maxQuarters = quartersPerPad * 64;
        final int loopStartPad = (int) Math.floor (Math.max (0, start) / quartersPerPad);
        final int loopEndPad = (int) Math.ceil (Math.min (maxQuarters, start + clip.getLoopLength ()) / quartersPerPad);

        final String clipColor = this.useDawColors ? DAWColor.getColorID (clip.getColor ()) : COLOR_PART;
        final IPadGrid padGrid = this.surface.getPadGrid ();
        final int cols = padGrid.getCols ();

        for (int pad = 0; pad < 64; pad++)
            padGrid.lightEx (pad % cols, pad / cols, pad >= loopStartPad && pad < loopEndPad ? clipColor : COLOR_OUTSIDE);
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