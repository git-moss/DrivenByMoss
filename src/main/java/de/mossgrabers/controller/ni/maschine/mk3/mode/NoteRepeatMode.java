// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.mode;

import de.mossgrabers.controller.ni.maschine.mk3.MaschineConfiguration;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.daw.midi.INoteInput;
import de.mossgrabers.framework.daw.midi.INoteRepeat;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Editing the length of note repeat notes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoteRepeatMode extends BaseMode
{
    private final IHost       host;
    private final INoteRepeat noteRepeat;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public NoteRepeatMode (final MaschineControlSurface surface, final IModel model)
    {
        super ("Note Repeat", surface, model);

        this.host = this.model.getHost ();

        final INoteInput defaultNoteInput = surface.getMidiInput ().getDefaultNoteInput ();
        this.noteRepeat = defaultNoteInput == null ? null : defaultNoteInput.getNoteRepeat ();
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final int idx = index < 0 ? this.selectedParam : index;

        final MaschineConfiguration configuration = this.surface.getConfiguration ();
        final IValueChanger valueChanger = this.model.getValueChanger ();
        switch (idx)
        {
            case 0:
            case 1:
                final int sel = Resolution.change (Resolution.getMatch (configuration.getNoteRepeatPeriod ().getValue ()), valueChanger.isIncrease (value));
                configuration.setNoteRepeatPeriod (Resolution.values ()[sel]);
                break;

            case 2:
            case 3:
                if (this.host.supports (Capability.NOTE_REPEAT_LENGTH))
                {
                    final int sel2 = Resolution.change (Resolution.getMatch (configuration.getNoteRepeatLength ().getValue ()), valueChanger.calcKnobChange (value) > 0);
                    configuration.setNoteRepeatLength (Resolution.values ()[sel2]);
                }
                break;

            case 4:
            case 5:
                if (this.host.supports (Capability.NOTE_REPEAT_MODE))
                    configuration.setPrevNextNoteRepeatMode (valueChanger.isIncrease (value));
                break;

            case 6:
            case 7:
                if (this.host.supports (Capability.NOTE_REPEAT_OCTAVES))
                    configuration.setNoteRepeatOctave (configuration.getNoteRepeatOctave () + (valueChanger.calcKnobChange (value) > 0 ? 1 : -1));
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);

            final MaschineConfiguration configuration = this.surface.getConfiguration ();
            switch (index)
            {
                case 0:
                case 1:
                    configuration.setNoteRepeatPeriod (Resolution.values ()[4]);
                    break;

                case 2:
                case 3:
                    if (this.host.supports (Capability.NOTE_REPEAT_LENGTH))
                        configuration.setNoteRepeatLength (Resolution.values ()[4]);
                    break;

                case 4:
                case 5:
                    if (this.host.supports (Capability.NOTE_REPEAT_MODE))
                        this.noteRepeat.setMode (ArpeggiatorMode.UP);
                    break;

                case 6:
                case 7:
                    if (this.host.supports (Capability.NOTE_REPEAT_OCTAVES))
                        this.noteRepeat.setOctaves (1);
                    break;

                default:
                    // Unused
                    break;
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ().clear ();

        final String [] names = Resolution.getNames ();

        final int selPeriodIndex = this.getSelectedPeriodIndex ();
        d.setBlock (0, 0, this.mark ("Period", 0));
        d.setBlock (1, 0, names[selPeriodIndex >= 0 && selPeriodIndex < names.length ? selPeriodIndex : 0]);

        if (this.host.supports (Capability.NOTE_REPEAT_LENGTH))
        {
            final int selLengthIndex = this.getSelectedNoteLengthIndex ();
            d.setBlock (0, 1, this.mark ("Length", 2));
            d.setBlock (1, 1, names[selLengthIndex >= 0 && selLengthIndex < names.length ? selLengthIndex : 0]);
        }

        if (this.host.supports (Capability.NOTE_REPEAT_MODE))
        {
            final ArpeggiatorMode mode = this.noteRepeat.getMode ();
            d.setBlock (0, 2, this.mark ("Mode", 4));
            d.setBlock (1, 2, StringUtils.optimizeName (mode.getName (), 12));
        }

        if (this.host.supports (Capability.NOTE_REPEAT_OCTAVES))
        {
            final int octaves = this.noteRepeat.getOctaves ();
            d.setBlock (0, 3, this.mark ("Octaves", 6));
            d.setBlock (1, 3, Integer.toString (octaves));
        }

        d.allDone ();
    }


    /**
     * Get the index of the selected period.
     *
     * @return The selected period index
     */
    private int getSelectedPeriodIndex ()
    {
        return this.noteRepeat == null ? -1 : Resolution.getMatch (this.noteRepeat.getPeriod ());
    }


    /**
     * Get the index of the selected length.
     *
     * @return The selected length index
     */
    private int getSelectedNoteLengthIndex ()
    {
        return this.noteRepeat == null ? -1 : Resolution.getMatch (this.noteRepeat.getNoteLength ());
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        super.selectPreviousItem ();

        if (this.selectedParam % 2 == 1)
            this.selectedParam--;
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        super.selectNextItem ();

        if (this.selectedParam % 2 == 1)
            this.selectedParam++;
        if (this.selectedParam > 6)
            this.selectedParam = 6;
    }
}
