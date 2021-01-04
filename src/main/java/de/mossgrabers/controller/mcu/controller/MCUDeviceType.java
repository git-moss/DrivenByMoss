// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu.controller;

/**
 * The type of MCU device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum MCUDeviceType
{
    /** A main device with transport control, buttons, etc. **/
    MAIN,
    /** An extender which has only a mixer section. */
    EXTENDER,
    /** Extender with a mixer section but uses the specific Mackie extender protocol. */
    MACKIE_EXTENDER
}
