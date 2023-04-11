// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.hardware;

/**
 * Types of MIDI data which can be bound to a hardware element.
 *
 * @author Jürgen Moßgraber
 */
public enum BindType
{
    /** Bind to a MIDI CC. */
    CC,
    /** Bind to a MIDI note. */
    NOTE,
    /** Bind to MIDI pitchbend. */
    PITCHBEND
}
