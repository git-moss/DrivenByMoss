// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.command.continuous;

import de.mossgrabers.controller.ni.maschine.core.RibbonMode;
import de.mossgrabers.controller.ni.maschine.mk3.MaschineConfiguration;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.framework.command.core.AbstractContinuousCommand;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.INoteRepeat;
import de.mossgrabers.framework.daw.midi.MidiConstants;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to handle touchstrip movement and touch as well as the LED updates.
 *
 * @author Jürgen Moßgraber
 */
public class TouchstripCommand extends AbstractContinuousCommand<MaschineControlSurface, MaschineConfiguration> implements TriggerCommand
{
    private int ribbonValue = 0;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public TouchstripCommand (final IModel model, final MaschineControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        this.ribbonValue = value;

        final MaschineConfiguration config = this.surface.getConfiguration ();
        final RibbonMode ribbonMode = config.getRibbonMode ();
        switch (ribbonMode)
        {
            case PITCH_DOWN:
                this.surface.sendMidiEvent (MidiConstants.CMD_PITCHBEND, 0, (127 - value) / 2);
                break;

            case PITCH_UP:
                this.surface.sendMidiEvent (MidiConstants.CMD_PITCHBEND, 0, 64 + value / 2);
                break;

            case PITCH_DOWN_UP:
                this.surface.sendMidiEvent (MidiConstants.CMD_PITCHBEND, 0, value);
                break;

            case CC_1:
                this.surface.sendMidiEvent (MidiConstants.CMD_CC, 1, value);
                break;

            case CC_11:
                this.surface.sendMidiEvent (MidiConstants.CMD_CC, 11, value);
                break;

            case MASTER_VOLUME:
                this.model.getMasterTrack ().setVolume (this.model.getValueChanger ().toDAWValue (value));
                break;

            case NOTE_REPEAT_PERIOD:
            case NOTE_REPEAT_LENGTH:
                final Resolution [] values = Resolution.values ();
                final double scaled = (127 - value) / 127.0;
                final int index = (int) Math.round (scaled * (values.length - 1));
                final double resolutionValue = values[values.length - 1 - index].getValue ();
                final INoteRepeat noteRepeat = this.surface.getMidiInput ().getDefaultNoteInput ().getNoteRepeat ();
                if (ribbonMode == RibbonMode.NOTE_REPEAT_PERIOD)
                    noteRepeat.setPeriod (resolutionValue);
                else
                    noteRepeat.setNoteLength (resolutionValue);
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.UP)
            return;

        final MaschineConfiguration config = this.surface.getConfiguration ();
        final RibbonMode ribbonMode = config.getRibbonMode ();
        switch (ribbonMode)
        {
            case PITCH_DOWN:
            case PITCH_UP:
                this.ribbonValue = 0;
                this.surface.sendMidiEvent (MidiConstants.CMD_PITCHBEND, 0, 64);
                break;

            case PITCH_DOWN_UP:
                this.ribbonValue = 64;
                this.surface.sendMidiEvent (MidiConstants.CMD_PITCHBEND, 0, 64);
                break;

            case CC_1:
            case CC_11:
            case MASTER_VOLUME:
            case NOTE_REPEAT_PERIOD:
            case NOTE_REPEAT_LENGTH:
                // No automatic reset
                break;

            default:
                // Not used
                break;
        }
    }


    /**
     * Reset the ribbon LED position to its default value depending on the currently selected ribbon
     * mode.
     *
     * @param ribbonMode The mode to reset
     */
    public void resetRibbonValue (final RibbonMode ribbonMode)
    {
        switch (ribbonMode)
        {
            case PITCH_DOWN:
            case PITCH_UP:
            case CC_1:
            case CC_11:
                this.ribbonValue = 0;
                break;

            case PITCH_DOWN_UP:
                this.ribbonValue = 64;
                break;

            default:
                // No change in other modes necessary
                break;
        }
    }


    /**
     * Update the LED value of the ribbon strip.
     */
    public void updateValue ()
    {
        final RibbonMode ribbonMode = this.surface.getConfiguration ().getRibbonMode ();
        switch (ribbonMode)
        {
            case PITCH_DOWN:
            case PITCH_UP:
            case PITCH_DOWN_UP:
            case CC_1:
            case CC_11:
                this.surface.setRibbonValue (this.ribbonValue);
                break;

            case MASTER_VOLUME:
                final ITrack t = this.model.getMasterTrack ();
                this.surface.setRibbonValue (t == null ? 0 : this.model.getValueChanger ().toMidiValue (t.getVolume ()));
                break;

            case NOTE_REPEAT_PERIOD:
            case NOTE_REPEAT_LENGTH:
                final Resolution [] values = Resolution.values ();
                final INoteRepeat noteRepeat = this.surface.getMidiInput ().getDefaultNoteInput ().getNoteRepeat ();
                final double value = ribbonMode == RibbonMode.NOTE_REPEAT_PERIOD ? noteRepeat.getPeriod () : noteRepeat.getNoteLength ();
                final int index = Resolution.getMatch (value);
                this.surface.setRibbonValue ((int) Math.round (index * 127.0 / (values.length - 1)));
                break;

            default:
                // Not used
                break;
        }
    }
}
