// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.intuitiveinstruments.exquis.mode;

import de.mossgrabers.controller.intuitiveinstruments.exquis.ExquisConfiguration;
import de.mossgrabers.controller.intuitiveinstruments.exquis.controller.ExquisControlSurface;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.midi.INoteRepeat;
import de.mossgrabers.framework.featuregroup.AbstractMode;


/**
 * The configuration mode to change the note repeat (arpeggiator) settings.
 *
 * @author Jürgen Moßgraber
 */
public class ExquisArpeggiatorMode extends AbstractMode<ExquisControlSurface, ExquisConfiguration>
{
    private final Configuration configuration;
    private final INoteRepeat   noteRepeat;
    private final IHost         host;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public ExquisArpeggiatorMode (final ExquisControlSurface surface, final IModel model)
    {
        super ("Arp", surface, model, false);

        this.host = this.surface.getHost ();
        this.configuration = this.surface.getConfiguration ();
        this.noteRepeat = this.surface.getMidiInput ().getDefaultNoteInput ().getNoteRepeat ();
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        if (!isTouched)
            return;
        switch (index)
        {
            case 0:
                this.configuration.toggleNoteRepeatActive ();
                this.mvHelper.delayDisplay ( () -> "Note Repeat: " + (this.configuration.isNoteRepeatActive () ? "On" : "Off"));
                break;

            case 3:
                if (this.host.supports (Capability.NOTE_REPEAT_LATCH))
                    this.noteRepeat.toggleLatchActive ();
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final IValueChanger valueChanger = this.model.getValueChanger ();
        final boolean isInc = valueChanger.isIncrease (value);

        switch (index)
        {
            case 0:
                final int sel = Resolution.change (Resolution.getMatch (this.configuration.getNoteRepeatPeriod ().getValue ()), isInc);
                this.configuration.setNoteRepeatPeriod (Resolution.values ()[sel]);
                this.mvHelper.delayDisplay ( () -> "Period: " + this.configuration.getNoteRepeatPeriod ().getName ());
                break;

            case 1:
                if (this.host.supports (Capability.NOTE_REPEAT_LENGTH))
                {
                    final int sel2 = Resolution.change (Resolution.getMatch (this.configuration.getNoteRepeatLength ().getValue ()), valueChanger.calcKnobChange (value) > 0);
                    this.configuration.setNoteRepeatLength (Resolution.values ()[sel2]);
                    this.mvHelper.delayDisplay ( () -> "Length: " + this.configuration.getNoteRepeatLength ().getName ());
                }
                break;

            case 2:
                if (this.host.supports (Capability.NOTE_REPEAT_MODE))
                {
                    this.configuration.setPrevNextNoteRepeatMode (valueChanger.isIncrease (value));
                    this.mvHelper.delayDisplay ( () -> "Mode: " + this.configuration.getNoteRepeatMode ().getName ());
                }
                break;

            case 3:
                if (this.host.supports (Capability.NOTE_REPEAT_OCTAVES))
                {
                    this.configuration.setNoteRepeatOctave (this.configuration.getNoteRepeatOctave () + (valueChanger.calcKnobChange (value) > 0 ? 1 : -1));
                    this.mvHelper.delayDisplay ( () -> "Octave: " + this.configuration.getNoteRepeatOctave ());
                }
                break;

            default:
                // Not used
                return;
        }
    }
}
