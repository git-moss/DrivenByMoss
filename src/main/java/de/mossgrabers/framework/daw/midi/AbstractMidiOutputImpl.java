// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.midi;

/**
 * An abstract implementation for a midi output
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractMidiOutputImpl implements IMidiOutput
{
    /** {@inheritDoc} */
    @Override
    public void sendCC (final int cc, final int value)
    {
        this.sendMidiShort (0xB0, cc, value);
    }


    /** {@inheritDoc} */
    @Override
    public void sendCCEx (final int channel, final int cc, final int value)
    {
        this.sendMidiShort (0xB0 + channel, cc, value);
    }


    /** {@inheritDoc} */
    @Override
    public void sendNote (final int note, final int velocity)
    {
        this.sendMidiShort (0x90, note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void sendNoteEx (final int channel, final int note, final int velocity)
    {
        this.sendMidiShort (0x90 + channel, note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void sendPolyphonicAftertouch (final int data1, final int data2)
    {
        this.sendMidiShort (0xA0, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendPolyphonicAftertouch (final int channel, final int data1, final int data2)
    {
        this.sendMidiShort (0xA0 + channel, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendChannelAftertouch (final int data1, final int data2)
    {
        this.sendMidiShort (0xD0, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendChannelAftertouch (final int channel, final int data1, final int data2)
    {
        this.sendMidiShort (0xD0 + channel, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendPitchbend (final int data1, final int data2)
    {
        this.sendMidiShort (0xE0, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendPitchbend (final int channel, final int data1, final int data2)
    {
        this.sendMidiShort (0xE0 + channel, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendProgramChange (final int bankMSB, final int bankLSB, final int value)
    {
        this.sendProgramChange (0, bankMSB, bankLSB, value);
    }


    /** {@inheritDoc} */
    @Override
    public void sendProgramChange (final int channel, final int bankMSB, final int bankLSB, final int value)
    {
        this.sendCCEx (channel, 0, bankMSB);
        this.sendCCEx (channel, 32, bankLSB);
        this.sendMidiShort (0xC0 + channel, value, 0);
    }


    /**
     * Sends a MIDI message to the hardware device.
     *
     * @param status the status byte of the MIDI message
     * @param data1 the data1 part of the MIDI message
     * @param data2 the data2 part of the MIDI message
     */
    protected abstract void sendMidiShort (final int status, final int data1, final int data2);
}