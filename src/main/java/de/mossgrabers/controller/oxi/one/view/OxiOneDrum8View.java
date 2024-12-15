// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.view;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.controller.oxi.one.controller.OxiOneColorManager;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.IDrumPad;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.sequencer.AbstractDrum8View;


/**
 * The Drum 8 view.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneDrum8View extends AbstractDrum8View<OxiOneControlSurface, OxiOneConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public OxiOneDrum8View (final OxiOneControlSurface surface, final IModel model)
    {
        super (surface, model, 8, 16, 16, true);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        if (this.surface.isMutePressed ())
        {
            final IPadGrid padGrid = this.surface.getPadGrid ();
            if (!this.isActive ())
            {
                padGrid.turnOff ();
                return;
            }

            final int black = this.model.getColorManager ().getColorIndex (AbstractFeatureGroup.BUTTON_COLOR_OFF);
            final boolean hasDrumPads = this.primary.hasDrumPads ();
            final IDrumPadBank drumPadBank = this.primary.getDrumPadBank ();

            int start;
            for (int sound = 0; sound < this.lanes; sound++)
            {
                final IDrumPad drumPad = drumPadBank.getItem (sound);

                if (hasDrumPads && drumPad.doesExist ())
                {
                    start = 3;

                    final String padColor = DAWColor.getColorID (drumPad.getColor ());
                    final int y = this.allRows - 1 - sound;
                    padGrid.lightEx (0, y, padColor);
                    // Mute
                    padGrid.lightEx (1, y, drumPad.isMute () ? OxiOneColorManager.OXI_ONE_COLOR_YELLOW : OxiOneColorManager.OXI_ONE_COLOR_DARKER_YELLOW);
                    // Solo
                    padGrid.lightEx (2, y, drumPad.isSolo () ? OxiOneColorManager.OXI_ONE_COLOR_BLUE : OxiOneColorManager.OXI_ONE_COLOR_DARKER_BLUE);
                }
                else
                    start = 0;

                for (int col = start; col < this.clipCols; col++)
                {
                    final int x = col % this.numColumns;
                    final int y = this.lanes - 1 - sound;
                    padGrid.lightEx (x, y, black);
                }
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
            if (velocity != 0 || !this.isActive () || !this.primary.hasDrumPads ())
                return;

            final int index = note - this.surface.getPadGrid ().getStartNote ();
            final int x = index % this.numColumns;
            final int y = index / this.numColumns;
            final int sound = y;

            final IDrumPadBank drumPadBank = this.primary.getDrumPadBank ();
            final IDrumPad drumPad = drumPadBank.getItem (sound);
            if (!drumPad.doesExist ())
                return;

            switch (x)
            {
                // Select
                case 0:
                    drumPad.select ();
                    if (!this.surface.getModeManager ().isActive (Modes.DEVICE_LAYER))
                        this.surface.getDisplay ().notify (drumPad.getName (12));
                    break;

                // Mute
                case 1:
                    drumPad.toggleMute ();
                    break;

                // Solo
                case 2:
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
                final int index = note - this.surface.getPadGrid ().getStartNote ();
                final int x = index % this.numColumns;
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
    public void onGridNoteLongPress (final int note)
    {
        if (!this.isActive ())
            return;

        final int index = note - this.surface.getPadGrid ().getStartNote ();
        this.surface.getButton (ButtonID.get (ButtonID.PAD1, index)).setConsumed ();

        final int stepX = index % this.numColumns;
        final int stepY = this.scales.getDrumOffset () + index / this.numColumns;

        final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), stepX, stepY);
        this.editNote (this.getClip (), notePosition, false);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (ButtonID.isSceneButton (buttonID))
            this.onSceneButton (buttonID, event);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final NotePosition notePosition, final int row, final int velocity, final int accentVelocity)
    {
        if (this.surface.isPressed (ButtonID.REPEAT) || this.surface.isPressed (ButtonID.ACCENT))
        {
            if (velocity > 0)
                this.handleSequencerAreaRepeatOperator (clip, notePosition, velocity, this.surface.isPressed (ButtonID.REPEAT));
            return true;
        }

        return super.handleSequencerAreaButtonCombinations (clip, notePosition, row, velocity, accentVelocity);
    }
}