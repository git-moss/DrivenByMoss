// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchcontrol.mode.buttons;

import de.mossgrabers.controller.novation.launchcontrol.LaunchControlXLConfiguration;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLColorManager;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.IStepInfo;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.mode.sequencer.AbstractSequencerMode;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.sequencer.AbstractSequencerView;

import java.util.Optional;


/**
 * The drum sequencer with 1 row of 8 steps.
 *
 * @author Jürgen Moßgraber
 */
public class XLDrumSequencerMode extends AbstractSequencerMode<LaunchControlXLControlSurface, LaunchControlXLConfiguration>
{
    protected int                 numColumns;
    protected int                 sequencerSteps;
    protected int                 sequencerLines;
    protected final Configuration configuration;
    protected int                 selectedPad = 0;
    protected final Scales        scales;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public XLDrumSequencerMode (final LaunchControlXLControlSurface surface, final IModel model)
    {
        super ("Note Sequencer", surface, model, true, 127, 8, false);

        this.configuration = this.surface.getConfiguration ();
        this.scales = model.getScales ();

        this.sequencerLines = 1;
        this.numColumns = this.clipCols;
        this.sequencerSteps = this.sequencerLines * this.numColumns;
    }


    /**
     * Set the selected drum pad.
     *
     * @param selectedPad The pad index (1-8)
     */
    public void setSelectedPad (final int selectedPad)
    {
        this.selectedPad = selectedPad;
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final int index = buttonID.ordinal () - ButtonID.ROW2_1.ordinal ();
        if (index < 0 || index >= 8)
            return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;

        final INoteClip clip = this.getClip ();
        final int noteRow = this.scales.getDrumOffset () + this.selectedPad;
        return this.getSequencerStepColor (clip, noteRow, index, Optional.empty ());
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        final int noteRow = this.scales.getDrumOffset () + this.selectedPad;
        final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), index, noteRow);
        this.getClip ().toggleStep (notePosition, 127);
    }


    /**
     * Draw the sequencer steps.
     *
     * @param clip The clip
     * @param noteRow The note for which to draw the row
     * @param column The step column
     * @param rowColor The color to use the notes of the row
     * @return The color index
     */
    protected int getSequencerStepColor (final INoteClip clip, final int noteRow, final int column, final Optional<ColorEx> rowColor)
    {
        final int step = clip.getCurrentStep ();
        final int hiStep = this.isInXRange (step) ? step % this.sequencerSteps : -1;
        final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), column, noteRow);
        final IStepInfo stepInfo = clip.getStep (notePosition);
        final boolean hilite = column == hiStep;
        final String colorID = this.isActive () ? this.getStepColor (stepInfo, hilite, rowColor, column) : AbstractSequencerView.COLOR_NO_CONTENT;
        return this.colorManager.getColorIndex (colorID);
    }


    private boolean isActive ()
    {
        return this.model.canSelectedTrackHoldNotes () && this.getClip ().doesExist ();
    }


    /**
     * Checks if the given number is in the current display.
     *
     * @param x The index to check
     * @return True if it should be displayed
     */
    protected boolean isInXRange (final int x)
    {
        final INoteClip clip = this.getClip ();
        final int stepSize = clip.getNumSteps ();
        final int start = clip.getEditPage () * stepSize;
        return x >= start && x < start + stepSize;
    }


    protected String getStepColor (final IStepInfo stepInfo, final boolean hilite, final Optional<ColorEx> rowColor, final int step)
    {
        switch (stepInfo.getState ())
        {
            // Note starts
            case START:
                if (hilite)
                    return AbstractSequencerView.COLOR_STEP_HILITE_CONTENT;
                if (stepInfo.isMuted ())
                    return AbstractSequencerView.COLOR_STEP_MUTED;
                return rowColor.isPresent () && this.useDawColors ? DAWColor.getColorID (rowColor.get ()) : AbstractSequencerView.COLOR_CONTENT;

            // Note continues
            case CONTINUE:
                if (hilite)
                    return AbstractSequencerView.COLOR_STEP_HILITE_CONTENT;
                if (stepInfo.isMuted ())
                    return AbstractSequencerView.COLOR_STEP_MUTED_CONT;
                return rowColor.isPresent () && this.useDawColors ? DAWColor.getColorID (ColorEx.darker (rowColor.get ())) : AbstractSequencerView.COLOR_CONTENT_CONT;

            // Empty
            default:
                if (hilite)
                    return AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT;
                return step / 4 % 2 == 1 ? AbstractSequencerView.COLOR_NO_CONTENT_4 : AbstractSequencerView.COLOR_NO_CONTENT;
        }
    }


    /**
     * @return the selectedPad
     */
    public int getSelectedPad ()
    {
        return this.selectedPad;
    }
}
