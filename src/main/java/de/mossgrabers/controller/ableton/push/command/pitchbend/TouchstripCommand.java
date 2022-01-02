// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.command.pitchbend;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.command.core.AbstractPitchbendCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.INoteRepeat;
import de.mossgrabers.framework.view.Views;


/**
 * Command to handle pitch-bend.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TouchstripCommand extends AbstractPitchbendCommand<PushControlSurface, PushConfiguration>
{
    private int pitchValue = 0;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public TouchstripCommand (final IModel model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void onPitchbend (final int data1, final int data2)
    {
        if (this.surface.getViewManager ().isActive (Views.SESSION))
        {
            final int value = this.surface.isShiftPressed () ? 63 : data2;
            this.model.getTransport ().setCrossfade (this.model.getValueChanger ().toDAWValue (value));
            this.surface.setRibbonValue (value);
            return;
        }

        // Don't get in the way of configuration
        if (this.surface.isShiftPressed ())
            return;

        final PushConfiguration config = this.surface.getConfiguration ();
        final double scaled = data2 / 127.0;

        // Check if Note Repeat is active and its settings should be changed
        final int ribbonNoteRepeat = config.getRibbonNoteRepeat ();
        if (config.isNoteRepeatActive () && ribbonNoteRepeat > PushConfiguration.NOTE_REPEAT_OFF)
        {
            final Resolution [] values = Resolution.values ();
            final int index = (int) Math.round (scaled * (values.length - 1));
            final double value = values[values.length - 1 - index].getValue ();
            final INoteRepeat noteRepeat = this.surface.getMidiInput ().getDefaultNoteInput ().getNoteRepeat ();
            if (ribbonNoteRepeat == PushConfiguration.NOTE_REPEAT_PERIOD)
                noteRepeat.setPeriod (value);
            else
                noteRepeat.setNoteLength (value);
            return;
        }

        switch (config.getRibbonMode ())
        {
            case PushConfiguration.RIBBON_MODE_PITCH:
                this.surface.sendMidiEvent (0xE0, data1, data2);
                break;

            case PushConfiguration.RIBBON_MODE_CC:
                this.surface.sendMidiEvent (0xB0, config.getRibbonModeCCVal (), data2);
                this.pitchValue = data2;
                break;

            case PushConfiguration.RIBBON_MODE_CC_PB:
                if (data2 > 64)
                    this.surface.sendMidiEvent (0xE0, data1, data2);
                else if (data2 < 64)
                    this.surface.sendMidiEvent (0xB0, config.getRibbonModeCCVal (), 127 - data2 * 2);
                else
                {
                    this.surface.sendMidiEvent (0xE0, data1, data2);
                    this.surface.sendMidiEvent (0xB0, config.getRibbonModeCCVal (), 0);
                }
                break;

            case PushConfiguration.RIBBON_MODE_PB_CC:
                if (data2 > 64)
                    this.surface.sendMidiEvent (0xB0, config.getRibbonModeCCVal (), (data2 - 64) * 2);
                else if (data2 < 64)
                    this.surface.sendMidiEvent (0xE0, data1, data2);
                else
                {
                    this.surface.sendMidiEvent (0xE0, data1, data2);
                    this.surface.sendMidiEvent (0xB0, config.getRibbonModeCCVal (), 0);
                }
                break;

            case PushConfiguration.RIBBON_MODE_FADER:
                this.model.getCursorTrack ().setVolume (this.model.getValueChanger ().toDAWValue (data2));
                return;

            default:
                // Not used
                break;
        }

        this.surface.getMidiOutput ().sendPitchbend (data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void updateValue ()
    {
        if (this.surface.getViewManager ().isActive (Views.SESSION))
        {
            this.surface.setRibbonValue (this.model.getValueChanger ().toMidiValue (this.model.getTransport ().getCrossfade ()));
            return;
        }

        final PushConfiguration config = this.surface.getConfiguration ();

        // Check if Note Repeat is active and its settings should be changed
        final int ribbonNoteRepeat = config.getRibbonNoteRepeat ();
        if (config.isNoteRepeatActive () && ribbonNoteRepeat > PushConfiguration.NOTE_REPEAT_OFF)
        {
            final Resolution [] values = Resolution.values ();
            final INoteRepeat noteRepeat = this.surface.getMidiInput ().getDefaultNoteInput ().getNoteRepeat ();
            final double value = ribbonNoteRepeat == PushConfiguration.NOTE_REPEAT_PERIOD ? noteRepeat.getPeriod () : noteRepeat.getNoteLength ();
            final int index = Resolution.getMatch (value);
            this.surface.setRibbonValue (127 - (int) Math.round (index * 127.0 / (values.length - 1)));
            return;
        }

        switch (config.getRibbonMode ())
        {
            case PushConfiguration.RIBBON_MODE_CC:
                this.surface.setRibbonValue (this.pitchValue);
                break;

            case PushConfiguration.RIBBON_MODE_FADER:
                final ITrack t = this.model.getCursorTrack ();
                this.surface.setRibbonValue (this.model.getValueChanger ().toMidiValue (t.getVolume ()));
                break;

            default:
                this.surface.setRibbonValue (64);
                break;
        }
    }
}
