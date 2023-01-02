// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
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
     * Adds the message to the queue and calls flush.
     *
     * @param address The OSC address
     * @param numbers Integer parameters
     */
    void fastSendOSC (String address, int [] numbers);


    /**
     * Adds the message to the queue and calls flush.
     *
     * @param address The OSC address
     */
    void fastSendOSC (String address);


    /**
     * Send an OSC message with a color value. Tests if the value(s) of given message is identical
     * to that of the cache. If this is not the case or if dump is true, the message is added to the
     * messages list.The message will be sent when flush gets called.
     *
     * @param address The address of the OSC message
     * @param red The red component of the color [0-1]
     * @param green The green component of the color [0-1]
     * @param blue The blue component of the color [0-1]
     * @param dump True to dump (ignore cache)
     */
    void sendOSCColor (String address, double red, double green, double blue, boolean dump);


    /**
     * Send an OSC message with a boolean value. Tests if the value(s) of given message is identical
     * to that of the cache. If this is not the case or if dump is true, the message is added to the
     * messages list.The message will be sent when flush gets called.
     *
     * @param address The address of the OSC message
     * @param value The value to send
     * @param dump True to dump (ignore cache)
     */
    void sendOSC (String address, boolean value, boolean dump);


    /**
     * Send an OSC message with a double value. Tests if the value(s) of given message is identical
     * to that of the cache. If this is not the case or if dump is true, the message is added to the
     * messages list.The message will be sent when flush gets called.
     *
     * @param address The address of the OSC message
     * @param value The value to send
     * @param dump True to dump (ignore cache)
     */
    void sendOSC (String address, double value, boolean dump);


    /**
     * Send an OSC message with an integer value. Tests if the value(s) of given message is
     * identical to that of the cache. If this is not the case or if dump is true, the message is
     * added to the messages list.The message will be sent when flush gets called.
     *
     * @param address The address of the OSC message
     * @param value The value to send
     * @param dump True to dump (ignore cache)
     */
    void sendOSC (String address, int value, boolean dump);


    /**
     * Send an OSC message with a string value. Tests if the value(s) of given message is identical
     * to that of the cache. If this is not the case or if dump is true, the message is added to the
     * messages list.The message will be sent when flush gets called.
     *
     * @param address The address of the OSC message
     * @param value The value to send
     * @param dump True to dump (ignore cache)
     */
    void sendOSC (String address, String value, boolean dump);
}
