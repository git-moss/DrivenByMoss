// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.valuechanger;

/**
 * Different encoding types for relative knob values.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum RelativeEncoding
{
    /**
     * @see <a href=
     *      "https://en.wikipedia.org/wiki/Two%27s_complement">https://en.wikipedia.org/wiki/Two%27s_complement</a>
     **/
    TWOS_COMPLEMENT,

    /**
     * @see <a href=
     *      "https://en.wikipedia.org/wiki/Offset_binary">https://en.wikipedia.org/wiki/Offset_binary</a>
     */
    OFFSET_BINARY,
    /** See Relative2ValueChanger. */
    SIGNED_BIT,
    /** See Relative4ValueChanger. */
    SIGNED_BIT2
}
