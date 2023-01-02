// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.midi;

/**
 * An abstract implementation for a MIDI output
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractMidiOutput implements IMidiOutput
{
    /** The MIDI status byte for MPE Zone 1. */
    public static final int    ZONE_1                                  = 0xB0;
    /** The MIDI status byte for MPE Zone 2. */
    public static final int    ZONE_2                                  = 0xBF;

    protected static final int REGISTERED_PARAMETER_NUMBER_MSB         = 0x65;
    protected static final int REGISTERED_PARAMETER_NUMBER_LSB         = 0x64;
    protected static final int DATA_ENTRY_MPE                          = 0x06;
    protected static final int PARAMETER_MPE_CONFIG_MSB                = 0x00;
    protected static final int PARAMETER_MPE_CONFIG_LSB                = 0x06;
    protected static final int PARAMETER_MPE_PITCHBEND_SENSITIVITY_MSB = 0x00;
    protected static final int PARAMETER_MPE_PITCHBEND_SENSITIVITY_LSB = 0x00;


    /** {@inheritDoc} */
    @Override
    public void sendCC (final int cc, final int value)
    {
        this.sendMidiShort (MidiConstants.CMD_CC, cc, value);
    }


    /** {@inheritDoc} */
    @Override
    public void sendCCEx (final int channel, final int cc, final int value)
    {
        this.sendMidiShort (MidiConstants.CMD_CC + channel, cc, value);
    }


    /** {@inheritDoc} */
    @Override
    public void sendNote (final int note, final int velocity)
    {
        this.sendMidiShort (MidiConstants.CMD_NOTE_ON, note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void sendNoteEx (final int channel, final int note, final int velocity)
    {
        this.sendMidiShort (MidiConstants.CMD_NOTE_ON + channel, note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void sendPolyphonicAftertouch (final int data1, final int data2)
    {
        this.sendMidiShort (MidiConstants.CMD_POLY_AFTERTOUCH, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendPolyphonicAftertouch (final int channel, final int data1, final int data2)
    {
        this.sendMidiShort (MidiConstants.CMD_POLY_AFTERTOUCH + channel, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendChannelAftertouch (final int data1, final int data2)
    {
        this.sendMidiShort (MidiConstants.CMD_CHANNEL_AFTERTOUCH, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendChannelAftertouch (final int channel, final int data1, final int data2)
    {
        this.sendMidiShort (MidiConstants.CMD_CHANNEL_AFTERTOUCH + channel, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendPitchbend (final int data1, final int data2)
    {
        this.sendMidiShort (MidiConstants.CMD_PITCHBEND, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendPitchbend (final int channel, final int data1, final int data2)
    {
        this.sendMidiShort (MidiConstants.CMD_PITCHBEND + channel, data1, data2);
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
        this.sendMidiShort (MidiConstants.CMD_PROGRAM_CHANGE + channel, value, 0);
    }


    /** {@inheritDoc} */
    @Override
    public void configureMPE (final int zone, final int numberOfChannels)
    {
        this.sendMidiShort (zone, 79, 0);
        this.sendMidiShort (zone, REGISTERED_PARAMETER_NUMBER_MSB, PARAMETER_MPE_CONFIG_MSB);
        this.sendMidiShort (zone, REGISTERED_PARAMETER_NUMBER_LSB, PARAMETER_MPE_CONFIG_LSB);
        this.sendMidiShort (zone, DATA_ENTRY_MPE, numberOfChannels);
    }


    /** {@inheritDoc} */
    @Override
    public void sendMPEPitchbendRange (final int zone, final int range)
    {
        this.sendMidiShort (zone, REGISTERED_PARAMETER_NUMBER_MSB, PARAMETER_MPE_PITCHBEND_SENSITIVITY_MSB);
        this.sendMidiShort (zone, REGISTERED_PARAMETER_NUMBER_LSB, PARAMETER_MPE_PITCHBEND_SENSITIVITY_LSB);
        this.sendMidiShort (zone, DATA_ENTRY_MPE, range);
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