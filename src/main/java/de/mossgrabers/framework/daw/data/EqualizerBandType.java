// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

/**
 * Enumeration for equalizer band types.
 *
 * @author Jürgen Moßgraber
 */
public enum EqualizerBandType
{
    /** The band is off. */
    OFF,
    /** The band is a low cut filter. */
    LOWCUT,
    /** The band is a low shelf filter. */
    LOWSHELF,
    /** The band is a bell filter. */
    BELL,
    /** The band is a high cut filter. */
    HIGHCUT,
    /** The band is a high shelf filter. */
    HIGHSHELF,
    /** The band is a notch filter. */
    NOTCH
}
