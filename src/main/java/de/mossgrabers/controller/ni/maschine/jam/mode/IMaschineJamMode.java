// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.mode;

import de.mossgrabers.controller.ni.maschine.jam.controller.FaderConfig;


/**
 * Additional methods for Maschine Jam modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IMaschineJamMode
{
    /**
     * Setup a fader for this mode.
     *
     * @param index The index of the fader
     * @return The configuration
     */
    FaderConfig setupFader (final int index);
}
