// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.clip;

/**
 * Enumeration for the state of a step.
 *
 * @author Jürgen Moßgraber
 */
public enum StepState
{
    /** Step contains no note. */
    OFF,
    /** A note starts at this step. */
    START,
    /** A started note continues at that step. */
    CONTINUE;
}
