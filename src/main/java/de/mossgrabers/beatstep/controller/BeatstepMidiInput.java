// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.beatstep.controller;

import de.mossgrabers.framework.midi.MidiInput;

import com.bitwig.extension.controller.api.NoteInput;


/**
 * The Launchpad midi input
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BeatstepMidiInput extends MidiInput
{
    private final String inputName;
    private boolean      isPro;


    /**
     * Constructor.
     *
     * @param isPro Is Pro or MkII?
     */
    public BeatstepMidiInput (final boolean isPro)
    {
        this.isPro = isPro;
        this.inputName = isPro ? "Novation Launchpad Pro" : "Novation Launchpad MkII";
    }


    /** {@inheritDoc} */
    @Override
    public NoteInput createNoteInput ()
    {
        // Control Mode is expected on channel 3 for Pro
        final NoteInput noteInput = this.createNoteInputBase (this.inputName, this.isPro ? "Control" : "Beatstep", this.isPro ? "82????" : "80????", this.isPro ? "92????" : "90????", this.isPro ? "A2????" : "A0????", this.isPro ? "B2????" : "B0????");

        // Setup the 2 note sequencers and 1 drum sequencer
        if (this.isPro)
        {
            // Sequencer 1 is on channel 1
            final NoteInput seq1Port = this.createNoteInputBase ("Seq. 1", "90????", "80????");
            seq1Port.setShouldConsumeEvents (false);
            // Sequencer 2 is on channel 2
            final NoteInput seq2Port = this.createNoteInputBase ("Seq. 2", "91????", "81????");
            seq2Port.setShouldConsumeEvents (false);
            // Drum Sequencer is on channel 10
            final NoteInput drumPort = this.createNoteInputBase ("Drums", "99????", "89????");
            drumPort.setShouldConsumeEvents (false);
        }

        return noteInput;
    }
}