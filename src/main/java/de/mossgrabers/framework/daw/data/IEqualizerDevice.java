// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

/**
 * Interface to a equalizer device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IEqualizerDevice extends ISpecificDevice
{
    /**
     * Get the number of frequency bands which can be controlled by the equalizer.
     *
     * @return The number of frequency bands
     */
    int getBandCount ();


    /**
     * Get the type values.
     *
     * @param index 0 .. number of frequency bands - 1
     * @return The parameter
     */
    String getType (int index);


    /**
     * Set the type values.
     *
     * @param index 0 .. number of frequency bands - 1
     * @param type The type
     */
    void setType (int index, String type);


    /**
     * Get one of the gain parameters.
     *
     * @param index 0 .. number of frequency bands - 1
     * @return The parameter
     */
    IParameter getGain (int index);


    /**
     * Get one of the frquency parameters.
     *
     * @param index 0 .. number of frequency bands - 1
     * @return The parameter
     */
    IParameter getFrequency (int index);


    /**
     * Get one of the Q parameters.
     *
     * @param index 0 .. number of frequency bands - 1
     * @return The parameter
     */
    IParameter getQ (int index);
}
