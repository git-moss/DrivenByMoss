// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.osc;

/**
 * Interface for sending OSC messages.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IOpenSoundControlWriter
{
    /**
     * Flush out all values.
     *
     * @param dump Forces a flush if true otherwise only changed values are flushed
     */
    void flush (boolean dump);


    /**
     * Sends the message and calls flush.
     *
     * @param address The OSC address
     * @param numbers Integer parameters
     */
    void fastSendOSC (String address, int [] numbers);


    /**
     * Sends the message and calls flush.
     *
     * @param address The OSC address
     */
    void fastSendOSC (String address);
}
