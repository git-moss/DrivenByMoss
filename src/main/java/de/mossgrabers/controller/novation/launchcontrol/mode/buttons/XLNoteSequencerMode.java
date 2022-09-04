// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchcontrol.mode.buttons;

import de.mossgrabers.controller.novation.launchcontrol.LaunchControlXLConfiguration;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLColorManager;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.daw.StepState;
import de.mossgrabers.framework.mode.sequencer.AbstractSequencerMode;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.sequencer.AbstractSequencerView;


/**
 * The note sequencer with 8 steps.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class XLNoteSequencerMode extends AbstractSequencerMode<LaunchControlXLControlSurface, LaunchControlXLConfiguration>
{
    protected int                 numColumns;
    protected final Configuration configuration;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public XLNoteSequencerMode (final LaunchControlXLControlSurface surface, final IModel model)
    {
        super ("Note Sequencer", surface, model, true, 127, 8, false);

        this.configuration = this.surface.getConfiguration ();
        this.numColumns = this.clipCols;
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final int index = buttonID.ordinal () - ButtonID.ROW2_1.ordinal ();
        if (index < 0 || index >= 8)
            return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;

        final INoteClip clip = this.getClip ();
        return this.getSequencerStepColor (clip, index);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        final int channel = this.configuration.getMidiEditChannel ();
        final INoteClip clip = this.getClip ();
        int highestRow = clip.getHighestRow (channel, index);
        final IStepInfo step = clip.getStep (channel, index, highestRow);
        if (step.getState () == StepState.START)
            clip.updateStepMuteState (channel, index, highestRow, !step.isMuted ());
    }


    /**
     * Draw the sequencer steps.
     *
     * @param clip The clip
     * @param column The step column
     * @return The color index
     */
    protected int getSequencerStepColor (final INoteClip clip, final int column)
    {
        final int step = clip.getCurrentStep ();
        final int hiStep = this.isInXRange (step) ? step % this.numColumns : -1;
        final boolean hilite = column == hiStep;

        final int channel = this.configuration.getMidiEditChannel ();
        int highestRow = clip.getHighestRow (channel, column);
        final IStepInfo stepInfo = clip.getStep (channel, column, highestRow);

        String colorID;
        if (stepInfo.getState () == StepState.START && stepInfo.isMuted ())
            colorID = hilite ? AbstractSequencerView.COLOR_STEP_HILITE_CONTENT : AbstractSequencerView.COLOR_STEP_MUTED;
        else
            colorID = hilite ? AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT : AbstractSequencerView.COLOR_NO_CONTENT;

        return this.colorManager.getColorIndex (colorID);
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
}