// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.view;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.controller.oxi.one.controller.OxiOneColorManager;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.controller.oxi.one.controller.OxiOnePadGrid;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.IDrumDevice;
import de.mossgrabers.framework.daw.data.IDrumPad;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.view.Views;
import de.mossgrabers.framework.view.sequencer.AbstractDrumXoXView;


/**
 * The Drum XoX view.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneDrumXoXView extends AbstractDrumXoXView<OxiOneControlSurface, OxiOneConfiguration>
{
    // @formatter:off
    protected static final int [] DRUM_MATRIX_LARGE =
    {
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
    };
    // @formatter:on

    private final OxiOnePadGrid   padGrid;
    private final int             numRows;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public OxiOneDrumXoXView (final OxiOneControlSurface surface, final IModel model)
    {
        super (Views.NAME_DRUM_XOX, surface, model, 16, 96);

        this.drumMatrix = DRUM_MATRIX_LARGE;

        this.sequencerLines = 6;
        // Only the sequencer rows!
        this.allRows = 6;
        this.sequencerSteps = 96;

        this.padGrid = this.surface.getPadGrid ();
        this.numRows = this.padGrid.getRows ();
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNoteLongPress (final int note)
    {
        if (!this.isActive ())
            return;

        final int index = note - this.padGrid.getStartNote ();
        final int x = index % this.numColumns;
        final int y = index / this.numColumns;

        // Sequencer steps
        if (y < this.numStepRows)
        {
            this.surface.getButton (ButtonID.get (ButtonID.PAD1, index)).setConsumed ();

            final int offsetY = this.scales.getDrumOffset ();
            final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), (this.numStepRows - 1 - y) * this.numColumns + x, offsetY + this.selectedPad);
            this.editNote (this.getClip (), notePosition, false);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final NotePosition notePosition, final int velocity)
    {
        if (this.surface.isPressed (ButtonID.REPEAT) || this.surface.isPressed (ButtonID.ACCENT))
        {
            if (velocity > 0)
                this.handleSequencerAreaRepeatOperator (clip, notePosition, velocity, this.surface.isPressed (ButtonID.REPEAT));
            return true;
        }

        return super.handleSequencerAreaButtonCombinations (clip, notePosition, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        if (this.surface.isMutePressed ())
        {
            if (!this.isActive ())
            {
                this.padGrid.turnOff ();
                return;
            }

            this.drawPages (this.getClip (), true);

            final int black = this.model.getColorManager ().getColorIndex (AbstractFeatureGroup.BUTTON_COLOR_OFF);
            final IDrumDevice primary = this.getDrumDevice ();
            final boolean hasDrumPads = primary.hasDrumPads ();
            final IDrumPadBank drumPadBank = primary.getDrumPadBank ();

            int start;
            for (int sound = 0; sound < this.numColumns; sound++)
            {
                final IDrumPad drumPad = drumPadBank.getItem (sound);

                if (hasDrumPads && drumPad.doesExist ())
                {
                    start = 4;

                    final String padColor = DAWColor.getColorID (drumPad.getColor ());
                    this.padGrid.lightEx (sound, 1, padColor);
                    // Mute
                    this.padGrid.lightEx (sound, 2, drumPad.isMute () ? OxiOneColorManager.OXI_ONE_COLOR_YELLOW : OxiOneColorManager.OXI_ONE_COLOR_DARKER_YELLOW);
                    // Solo
                    this.padGrid.lightEx (sound, 3, drumPad.isSolo () ? OxiOneColorManager.OXI_ONE_COLOR_BLUE : OxiOneColorManager.OXI_ONE_COLOR_DARKER_BLUE);
                }
                else
                    start = 0;
                for (int row = start; row < this.numRows; row++)
                    this.padGrid.lightEx (sound, row, black);
            }
            return;
        }

        super.drawGrid ();
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (this.surface.isMutePressed ())
        {
            final IDrumDevice primary = this.getDrumDevice ();
            if (velocity != 0 || !this.isActive () || !primary.hasDrumPads ())
                return;

            final int index = note - this.padGrid.getStartNote ();
            final int x = index % this.numColumns;
            final int y = this.padGrid.getRows () - 1 - (index / this.numColumns);
            final int sound = x;

            final IDrumPadBank drumPadBank = primary.getDrumPadBank ();
            final IDrumPad drumPad = drumPadBank.getItem (sound);
            if (!drumPad.doesExist ())
                return;

            switch (y)
            {
                case 0:
                    // Select clip
                    this.model.getCursorTrack ().getSlotBank ().getItem (x).select ();
                    break;

                // Select drum pad
                case 1:
                    drumPad.select ();
                    if (!this.surface.getModeManager ().isActive (Modes.DEVICE_LAYER))
                        this.surface.getDisplay ().notify (drumPad.getName (12));
                    break;

                // Mute drum pad
                case 2:
                    drumPad.toggleMute ();
                    break;

                // Solo drum pad
                case 3:
                    drumPad.toggleSolo ();
                    break;

                default:
                    // Not used
                    break;
            }
            return;
        }

        // Set loop start and end
        final boolean init = this.surface.isPressed (ButtonID.PUNCH_IN);
        final boolean end = this.surface.isPressed (ButtonID.PUNCH_OUT);
        if (init || end)
        {
            if (velocity == 0)
            {
                final INoteClip clip = this.getClip ();
                final int lengthOfOnePage = this.getLengthOfOnePage (this.sequencerSteps);
                final int offset = clip.getEditPage () * lengthOfOnePage;
                final int index = note - this.padGrid.getStartNote ();
                final int y = this.padGrid.getRows () - (index / this.numColumns) - 3;
                final int x = y * this.numColumns + (index % this.numColumns);
                final double lengthOfOnePad = Resolution.getValueAt (this.getResolutionIndex ());
                final double pos = offset + x * lengthOfOnePad;
                final double newStart = init ? pos : clip.getLoopStart ();
                final double newLength = end ? Math.max (pos - newStart + lengthOfOnePad, lengthOfOnePad) : clip.getLoopLength ();
                clip.setLoopStart (newStart);
                clip.setLoopLength (newLength);
                clip.setPlayRange (newStart, newStart + newLength);
            }
            return;
        }

        super.onGridNote (note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    protected int [] getDrumMatrix ()
    {
        return DRUM_MATRIX_LARGE;
    }
}