// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.beatstep.view;

/**
 * Additional interface for Beatstep views.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface BeatstepView
{
    /**
     * A knob has been turned.
     *
     * @param index The index of the knob
     * @param value The knobs value
     */
    void onKnob (final int index, final int value);
}
