// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import de.mossgrabers.framework.parameter.IParameter;

import java.util.ArrayList;
import java.util.List;


/**
 * Interface to a equalizer device.
 *
 * @author Jürgen Moßgraber
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
    IParameter getTypeParameter (int index);


    /**
     * Get the type parameters.
     *
     * @return The parameters
     */
    default List<IParameter> getTypeParameters ()
    {
        final List<IParameter> parameters = new ArrayList<> (8);
        for (int i = 0; i < 8; i++)
            parameters.add (this.getTypeParameter (i));
        return parameters;
    }


    /**
     * Get one of the gain parameters.
     *
     * @param index 0 .. number of frequency bands - 1
     * @return The parameter
     */
    IParameter getGainParameter (int index);


    /**
     * Get the gain parameters.
     *
     * @return The parameters
     */
    default List<IParameter> getGainParameters ()
    {
        final List<IParameter> parameters = new ArrayList<> (8);
        for (int i = 0; i < 8; i++)
            parameters.add (this.getGainParameter (i));
        return parameters;
    }


    /**
     * Get one of the frequency parameters.
     *
     * @param index 0 .. number of frequency bands - 1
     * @return The parameter
     */
    IParameter getFrequencyParameter (int index);


    /**
     * Get the frequency parameters.
     *
     * @return The parameters
     */
    default List<IParameter> getFrequencyParameters ()
    {
        final List<IParameter> parameters = new ArrayList<> (8);
        for (int i = 0; i < 8; i++)
            parameters.add (this.getFrequencyParameter (i));
        return parameters;
    }


    /**
     * Get one of the Q parameters.
     *
     * @param index 0 .. number of frequency bands - 1
     * @return The parameter
     */
    IParameter getQParameter (int index);


    /**
     * Get the Q parameters.
     *
     * @return The parameters
     */
    default List<IParameter> getQParameters ()
    {
        final List<IParameter> parameters = new ArrayList<> (8);
        for (int i = 0; i < 8; i++)
            parameters.add (this.getQParameter (i));
        return parameters;
    }
}
