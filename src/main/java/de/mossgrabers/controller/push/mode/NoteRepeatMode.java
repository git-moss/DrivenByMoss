// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode;

import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.controller.PushDisplay;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.INoteInput;
import de.mossgrabers.framework.graphics.display.DisplayModel;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractSequencerView;


/**
 * Editing the length of note repeat notes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoteRepeatMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public NoteRepeatMode (final PushControlSurface surface, final IModel model)
    {
        super ("Note Repeat", surface, model);
        this.isTemporary = true;
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (selectedTrack == null)
            return;
        final INoteInput defaultNoteInput = this.surface.getInput ().getDefaultNoteInput ();
        if (defaultNoteInput != null)
            defaultNoteInput.getNoteRepeat ().setPeriod (selectedTrack, AbstractSequencerView.RESOLUTIONS[index]);
        this.surface.getModeManager ().restoreMode ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final ColorManager colorManager = this.model.getColorManager ();
        final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
        for (int i = 0; i < 8; i++)
            this.surface.updateTrigger (20 + i, colorManager.getColor (this.isPeriodSelected (selectedTrack, i) ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON));
    }


    /**
     * Check if the n-th period is enabled.
     *
     * @param track The selected track on which the period should be checked
     * @param index The index of the period to check
     * @return True if selected
     */
    private boolean isPeriodSelected (final ITrack track, final int index)
    {
        if (track == null)
            return false;

        final INoteInput defaultNoteInput = this.surface.getInput ().getDefaultNoteInput ();
        if (defaultNoteInput == null)
            return false;

        return Math.abs (defaultNoteInput.getNoteRepeat ().getPeriod (track) - AbstractSequencerView.RESOLUTIONS[index]) < 0.001;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ().clear ();
        d.setBlock (2, 0, "Repeat Length:");
        final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
        for (int i = 0; i < 8; i++)
            d.setCell (3, i, (this.isPeriodSelected (selectedTrack, i) ? PushDisplay.SELECT_ARROW : "") + AbstractSequencerView.RESOLUTION_TEXTS[i]);
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final DisplayModel message = this.surface.getDisplay ().getModel ();
        final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
        for (int i = 0; i < 8; i++)
            message.addOptionElement ("", "", false, i == 0 ? "Repeat Length" : "", AbstractSequencerView.RESOLUTION_TEXTS[i], this.isPeriodSelected (selectedTrack, i), false);
        message.send ();
    }
}
