// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.arturia.beatstep.view;

import de.mossgrabers.controller.arturia.beatstep.BeatstepConfiguration;
import de.mossgrabers.controller.arturia.beatstep.controller.BeatstepColorManager;
import de.mossgrabers.controller.arturia.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractPlayView;
import de.mossgrabers.framework.view.Views;


/**
 * The Play view.
 *
 * @author Jürgen Moßgraber
 */
public class PlayView extends AbstractPlayView<BeatstepControlSurface, BeatstepConfiguration> implements BeatstepView
{
    private final TrackEditing extensions;


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public PlayView (final BeatstepControlSurface surface, final IModel model)
    {
        super (Views.NAME_PLAY, surface, model, false);
        this.extensions = new TrackEditing (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnob (final int index, final int value, final boolean isTurnedRight)
    {
        switch (index)
        {
            // Chromatic
            case 12:
                this.scales.setChromatic (!isTurnedRight);
                this.surface.getConfiguration ().setScaleInKey (isTurnedRight);
                this.surface.getDisplay ().notify (isTurnedRight ? "In Key" : "Chromatic");
                break;

            // Base Note
            case 13:
                if (isTurnedRight)
                    this.scales.nextScaleOffset ();
                else
                    this.scales.prevScaleOffset ();
                final String scaleBase = Scales.BASES.get (this.scales.getScaleOffsetIndex ());
                this.surface.getDisplay ().notify (scaleBase);
                this.surface.getConfiguration ().setScaleBase (scaleBase);
                break;

            // Scale
            case 14:
                if (isTurnedRight)
                    this.scales.nextScale ();
                else
                    this.scales.prevScale ();
                final String scale = this.scales.getScale ().getName ();
                this.surface.getConfiguration ().setScale (scale);
                this.surface.getDisplay ().notify (scale);
                break;

            // Octave
            case 15:
                this.keyManager.clearPressedKeys ();
                if (isTurnedRight)
                    this.scales.incOctave ();
                else
                    this.scales.decOctave ();
                this.surface.getDisplay ().notify ("Octave " + (this.scales.getOctave () > 0 ? "+" : "") + this.scales.getOctave () + " (" + this.scales.getRangeText () + ")");
                break;

            // 0-11
            default:
                this.extensions.onTrackKnob (index, value, isTurnedRight);
                break;
        }

        this.updateNoteMapping ();
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final boolean isKeyboardEnabled = this.model.canSelectedTrackHoldNotes ();
        final IPadGrid padGrid = this.surface.getPadGrid ();
        for (int i = 36; i < 52; i++)
            padGrid.light (i, this.getPadColor (isKeyboardEnabled, i));
    }


    protected int getPadColor (final boolean isKeyboardEnabled, final int pad)
    {
        if (!isKeyboardEnabled)
            return BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF;
        if (this.keyManager.isKeyPressed (pad))
            return BeatstepColorManager.BEATSTEP_BUTTON_STATE_PINK;
        final ColorManager colorManager = this.model.getColorManager ();
        return colorManager.getColorIndex (this.keyManager.getColor (pad));
    }
}