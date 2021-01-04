// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.flexihandler;

import de.mossgrabers.controller.generic.GenericFlexiConfiguration;
import de.mossgrabers.controller.generic.controller.FlexiCommand;
import de.mossgrabers.controller.generic.controller.GenericFlexiControlSurface;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.daw.midi.INoteInput;
import de.mossgrabers.framework.daw.midi.INoteRepeat;
import de.mossgrabers.framework.scale.Scales;


/**
 * The handler for master channel commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoteInputHandler extends AbstractHandler
{
    private final IHost host;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param configuration The configuration
     * @param relative2ValueChanger The relative value changer variant 2
     * @param relative3ValueChanger The relative value changer variant 3
     */
    public NoteInputHandler (final IModel model, final GenericFlexiControlSurface surface, final GenericFlexiConfiguration configuration, final IValueChanger relative2ValueChanger, final IValueChanger relative3ValueChanger)
    {
        super (model, surface, configuration, relative2ValueChanger, relative3ValueChanger);

        this.host = this.model.getHost ();
    }


    /** {@inheritDoc} */
    @Override
    public FlexiCommand [] getSupportedCommands ()
    {
        return new FlexiCommand []
        {
            FlexiCommand.NOTE_INPUT_REPEAT_ACTIVE,
            FlexiCommand.NOTE_INPUT_REPEAT_PERIOD,
            FlexiCommand.NOTE_INPUT_REPEAT_LENGTH,
            FlexiCommand.NOTE_INPUT_REPEAT_MODE,
            FlexiCommand.NOTE_INPUT_REPEAT_OCTAVE,
            FlexiCommand.NOTE_INPUT_TRANSPOSE_OCTAVE_UP,
            FlexiCommand.NOTE_INPUT_TRANSPOSE_OCTAVE_DOWN
        };
    }


    /** {@inheritDoc} */
    @Override
    public int getCommandValue (final FlexiCommand command)
    {
        final INoteInput noteInput = this.surface.getMidiInput ().getDefaultNoteInput ();
        if (noteInput == null)
            return -1;
        final INoteRepeat noteRepeat = noteInput.getNoteRepeat ();

        final GenericFlexiConfiguration configuration = this.surface.getConfiguration ();

        switch (command)
        {
            case NOTE_INPUT_REPEAT_ACTIVE:
                return noteRepeat.isActive () ? 127 : 0;

            case NOTE_INPUT_REPEAT_PERIOD:
                return Resolution.getMatch (noteRepeat.getPeriod ());

            case NOTE_INPUT_REPEAT_LENGTH:
                return Resolution.getMatch (noteRepeat.getNoteLength ());

            case NOTE_INPUT_REPEAT_MODE:
                final ArpeggiatorMode am = noteRepeat.getMode ();
                final ArpeggiatorMode [] arpeggiatorModes = configuration.getArpeggiatorModes ();
                for (int i = 0; i < arpeggiatorModes.length; i++)
                {
                    if (am == arpeggiatorModes[i])
                        return i;
                }
                return 0;

            case NOTE_INPUT_REPEAT_OCTAVE:
                return noteRepeat.getOctaves ();

            case NOTE_INPUT_TRANSPOSE_OCTAVE_UP:
            case NOTE_INPUT_TRANSPOSE_OCTAVE_DOWN:
                return this.model.getScales ().getOctave ();

            default:
                return -1;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void handle (final FlexiCommand command, final int knobMode, final int value)
    {
        final boolean isButtonPressed = this.isButtonPressed (knobMode, value);
        final GenericFlexiConfiguration configuration = this.surface.getConfiguration ();
        final Resolution [] resolutions = Resolution.values ();
        final Scales scales = this.model.getScales ();
        switch (command)
        {
            // Note Repeat: Toggle Active
            case NOTE_INPUT_REPEAT_ACTIVE:
                if (isButtonPressed)
                {
                    configuration.toggleNoteRepeatActive ();
                    this.mvHelper.delayDisplay ( () -> "Repeat: " + (configuration.isNoteRepeatActive () ? "On" : "Off"));
                }
                break;

            // Note Repeat: Set Period
            case NOTE_INPUT_REPEAT_PERIOD:
                final int selPeriod;
                if (isAbsolute (knobMode))
                    selPeriod = (int) Math.min (Math.round (value / 127.0 * resolutions.length), resolutions.length - 1L);
                else
                    selPeriod = Resolution.change (Resolution.getMatch (configuration.getNoteRepeatPeriod ().getValue ()), this.getRelativeSpeed (knobMode, value) > 0);
                configuration.setNoteRepeatPeriod (resolutions[selPeriod]);
                this.mvHelper.delayDisplay ( () -> "Repeat Period: " + configuration.getNoteRepeatPeriod ().getName ());
                break;

            // Note Repeat: Set Length
            case NOTE_INPUT_REPEAT_LENGTH:
                if (this.model.getHost ().supports (Capability.NOTE_REPEAT_LENGTH))
                {
                    final int selLength;
                    if (isAbsolute (knobMode))
                        selLength = (int) Math.min (Math.round (value / 127.0 * resolutions.length), resolutions.length - 1L);
                    else
                        selLength = Resolution.change (Resolution.getMatch (configuration.getNoteRepeatLength ().getValue ()), this.getRelativeSpeed (knobMode, value) > 0);
                    configuration.setNoteRepeatLength (resolutions[selLength]);
                    this.mvHelper.delayDisplay ( () -> "Repeat Length: " + configuration.getNoteRepeatLength ().getName ());
                }
                break;

            case NOTE_INPUT_REPEAT_MODE:
                if (this.host.supports (Capability.NOTE_REPEAT_MODE))
                {
                    final ArpeggiatorMode [] modes = configuration.getArpeggiatorModes ();
                    final int newIndex;
                    if (isAbsolute (knobMode))
                    {
                        if (value >= modes.length)
                            return;
                        newIndex = value;
                    }
                    else
                    {
                        final ArpeggiatorMode arpMode = configuration.getNoteRepeatMode ();
                        final int modeIndex = configuration.lookupArpeggiatorModeIndex (arpMode);
                        final boolean increase = this.getRelativeSpeed (knobMode, value) > 0;
                        newIndex = Math.max (0, Math.min (modes.length - 1, modeIndex + (increase ? 1 : -1)));
                    }
                    configuration.setNoteRepeatMode (modes[newIndex]);
                    this.mvHelper.delayDisplay ( () -> "Repeat Mode: " + modes[newIndex].getName ());
                }
                break;

            case NOTE_INPUT_REPEAT_OCTAVE:
                if (this.host.supports (Capability.NOTE_REPEAT_OCTAVES))
                {
                    final int octave;
                    if (isAbsolute (knobMode))
                        octave = value;
                    else
                        octave = configuration.getNoteRepeatOctave () + (this.getRelativeSpeed (knobMode, value) > 0 ? 1 : -1);
                    configuration.setNoteRepeatOctave (octave);
                    this.mvHelper.delayDisplay ( () -> "Repeat Octave: " + octave);
                }
                break;

            case NOTE_INPUT_TRANSPOSE_OCTAVE_UP:
                if (isButtonPressed)
                {
                    scales.incOctave ();
                    this.updateOctave (scales);
                }
                break;

            case NOTE_INPUT_TRANSPOSE_OCTAVE_DOWN:
                if (isButtonPressed)
                {
                    scales.decOctave ();
                    this.updateOctave (scales);
                }
                break;

            default:
                throw new FlexiHandlerException (command);
        }
    }


    private void updateOctave (final Scales scales)
    {
        this.surface.scheduleTask ( () -> {
            this.surface.setKeyTranslationTable (scales.getNoteMatrix ());
            this.mvHelper.delayDisplay ( () -> "Octave: " + scales.getOctave ());
        }, 6);
    }
}
