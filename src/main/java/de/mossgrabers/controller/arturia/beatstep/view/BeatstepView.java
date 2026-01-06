// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.arturia.beatstep.view;

/**
 * Additional interface for Beatstep views.
 *
 * @author Jürgen Moßgraber
 */
public interface BeatstepView
{
    /**
     * A knob has been turned.
     *
     * @param index The index of the knob
     * @param value The knobs value
     */
    void onKnob (int index, int value);
}
