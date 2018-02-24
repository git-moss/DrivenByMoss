// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.beatstep.view;

import de.mossgrabers.beatstep.BeatstepConfiguration;
import de.mossgrabers.beatstep.controller.BeatstepColors;
import de.mossgrabers.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractPlayView;


/**
 * The Play view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PlayView extends AbstractPlayView<BeatstepControlSurface, BeatstepConfiguration> implements BeatstepView
{
    private TrackEditing extensions;


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public PlayView (final BeatstepControlSurface surface, final IModel model)
    {
        super ("Play", surface, model, false);
        this.extensions = new TrackEditing (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnob (final int index, final int value)
    {
        if (index < 12)
        {
            this.extensions.onTrackKnob (index, value);
            return;
        }

        final boolean isInc = value >= 65;

        switch (index)
        {
            // Chromatic
            case 12:
                this.scales.setChromatic (!isInc);
                this.surface.getConfiguration ().setScaleInKey (isInc);
                this.surface.getDisplay ().notify (isInc ? "In Key" : "Chromatic");
                break;

            // Base Note
            case 13:
                this.scales.changeScaleOffset (value);
                final String scaleBase = Scales.BASES[this.scales.getScaleOffset ()];
                this.surface.getDisplay ().notify (scaleBase);
                this.surface.getConfiguration ().setScaleBase (scaleBase);
                break;

            // Scale
            case 14:
                if (isInc)
                    this.scales.nextScale ();
                else
                    this.scales.prevScale ();
                final String scale = this.scales.getScale ().getName ();
                this.surface.getConfiguration ().setScale (scale);
                this.surface.getDisplay ().notify (scale);
                break;

            // Octave
            case 15:
                this.clearPressedKeys ();
                if (isInc)
                    this.scales.incOctave ();
                else
                    this.scales.decOctave ();
                this.surface.getDisplay ().notify ("Octave " + (this.scales.getOctave () > 0 ? "+" : "") + this.scales.getOctave () + " (" + this.scales.getRangeText () + ")");
                break;
        }

        this.updateNoteMapping ();
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (!this.model.canSelectedTrackHoldNotes ())
            return;

        // Mark selected notes
        for (int i = 0; i < 128; i++)
        {
            if (this.noteMap[note] == this.noteMap[i])
                this.pressedKeys[i] = velocity;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.noteMap = this.model.canSelectedTrackHoldNotes () ? this.scales.getNoteMatrix () : Scales.getEmptyMatrix ();
        // Workaround: https://github.com/git-moss/Push4Bitwig/issues/7
        this.surface.scheduleTask ( () -> this.surface.setKeyTranslationTable (this.noteMap), 100);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final boolean isKeyboardEnabled = this.model.canSelectedTrackHoldNotes ();
        final PadGrid padGrid = this.surface.getPadGrid ();
        final ColorManager colorManager = this.model.getColorManager ();
        for (int i = 36; i < 52; i++)
            padGrid.light (i, isKeyboardEnabled ? this.pressedKeys[i] > 0 ? BeatstepColors.BEATSTEP_BUTTON_STATE_PINK : colorManager.getColor (this.scales.getColor (this.noteMap, i)) : BeatstepColors.BEATSTEP_BUTTON_STATE_OFF);
    }
}