// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.midi;

/**
 * Interface to a midi output
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IMidiOutput
{
    /**
     * Send a midi CC to the output on midi channel 1.
     *
     * @param cc The CC
     * @param value The value
     */
    void sendCC (int cc, int value);


    /**
     * Send a midi CC to the output.
     *
     * @param channel The midi channel
     * @param cc The CC
     * @param value The value
     */
    void sendCCEx (int channel, int cc, int value);


    /**
     * Send a midi note to the output on midi channel 1.
     *
     * @param note The note
     * @param velocity The velocity
     */
    void sendNote (int note, int velocity);


    /**
     * Send a midi note to the output.
     *
     * @param channel The midi channel
     * @param note The note
     * @param velocity The velocity
     */
    void sendNoteEx (int channel, int note, int velocity);


    /**
     * Send polyphonic aftertouch to the output on midi channel 1.
     *
     * @param data1 First data byte
     * @param data2 Second data byte
     */
    void sendPolyphonicAftertouch (int data1, int data2);


    /**
     * Send polyphonic aftertouch to the output on the given midi channel.
     *
     * @param channel The midi channel
     * @param data1 First data byte
     * @param data2 Second data byte
     */
    void sendPolyphonicAftertouch (int channel, int data1, int data2);


    /**
     * Send channel aftertouch to the output on midi channel 1.
     *
     * @param data1 First data byte
     * @param data2 Second data byte
     */
    void sendChannelAftertouch (int data1, int data2);


    /**
     * Send channel aftertouch to the output on the given midi channel.
     *
     * @param channel The midi channel
     * @param data1 First data byte
     * @param data2 Second data byte
     */
    void sendChannelAftertouch (int channel, int data1, int data2);


    /**
     * Send pitchbend to the output on midi channel 1.
     *
     * @param data1 First data byte
     * @param data2 Second data byte
     */
    void sendPitchbend (int data1, int data2);


    /**
     * Send pitchbend to the output on the given midi channel.
     *
     * @param channel The midi channel
     * @param data1 First data byte
     * @param data2 Second data byte
     */
    void sendPitchbend (int channel, int data1, int data2);


    /**
     * Send a program change to the output.
     *
     * @param bankMSB The most significant byte of the bank
     * @param bankLSB The least significant byte of the bank
     * @param value The program change value
     */
    void sendProgramChange (int bankMSB, int bankLSB, int value);


    /**
     * Send a program change to the output.
     *
     * @param channel The midi channel
     * @param bankMSB The most significant byte of the bank
     * @param bankLSB The least significant byte of the bank
     * @param value The program change value
     */
    void sendProgramChange (int channel, int bankMSB, int bankLSB, int value);


    /**
     * Send a system exclusive message to the output.
     *
     * @param data The data to send
     */
    void sendSysex (byte [] data);


    /**
     * Send a system exclusive message to the output.
     *
     * @param data The data to send, formatted as a hex string, e.g. F0 7E 7F 06 01 F7
     */
    void sendSysex (String data);
}