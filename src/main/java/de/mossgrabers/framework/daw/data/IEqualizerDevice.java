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
     * Get the type value.
     *
     * @param index 0 .. number of frequency bands - 1
     * @return The parameter
     */
    EqualizerBandType getTypeID (int index);


    /**
     * Set the type value.
     *
     * @param index 0 .. number of frequency bands - 1
     * @param type The type
     */
    void setType (int index, EqualizerBandType type);


    /**
     * Get one of the type parameters.
     *
     * @param index 0 .. number of frequency bands - 1
     * @return The parameter
     */
    IParameter getType (int index);


    /**
     * Get one of the gain parameters.
     *
     * @param index 0 .. number of frequency bands - 1
     * @return The parameter
     */
    IParameter getGain (int index);


    /**
     * Get one of the frequency parameters.
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
