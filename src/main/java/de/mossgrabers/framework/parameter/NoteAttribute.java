// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameter;

/**
 * Enumeration of all attributes of a note which can be edited.
 *
 * @author Jürgen Moßgraber
 */
public enum NoteAttribute
{
    /** Edit note pitch. */
    PITCH,
    /** Edit note gain. */
    GAIN,
    /** Edit note panorama. */
    PANORAMA,
    /** Edit note duration. */
    DURATION,
    /** Edit note velocity. */
    VELOCITY,
    /** Edit note release velocity. */
    RELEASE_VELOCITY,
    /** Edit note velocity spread. */
    VELOCITY_SPREAD,
    /** Edit note mute. */
    MUTE,
    /** Edit note pressure. */
    PRESSURE,
    /** Edit note timbre. */
    TIMBRE,
    /** Edit note transpose. */
    TRANSPOSE,
    /** Edit note chance. */
    CHANCE,
    /** Edit note repeat. */
    REPEAT,
    /** Edit note repeat curve. */
    REPEAT_CURVE,
    /** Edit note repeat velocity curve. */
    REPEAT_VELOCITY_CURVE,
    /** Edit note repeat velocity end. */
    REPEAT_VELOCITY_END,
    /** Edit note occurrence. */
    OCCURRENCE,
    /** Edit note recurrence length. */
    RECURRENCE_LENGTH
}
