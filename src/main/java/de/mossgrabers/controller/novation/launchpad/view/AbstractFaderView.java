// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.view;

import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;


/**
 * Abstract base class for views with 8 faders.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractFaderView extends SessionView
{
    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     */
    protected AbstractFaderView (final String name, final LaunchpadControlSurface surface, final IModel model)
    {
        super (name, surface, model);
    }


    /**
     * A knob has been used (simulated fader).
     *
     * @param index The index of the knob
     * @param value The value the knob sent
     */
    public abstract void onValueKnob (int index, int value);


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        // Simulate faders
        final int num = note - 36;
        final int index = num % 8;
        final int row = num / 8;

        this.surface.moveFader (index, row, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNoteLongPress (final int note)
    {
        // Prevent clip selection
    }


    /**
     * Hook for getting the fader value.
     *
     * @param index The index of the fader
     * @return The fader value
     */
    protected abstract int getFaderValue (int index);


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();

        this.surface.clearFaders ();
        for (int i = 0; i < 8; i++)
            this.setupFader (i);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
    }


    /**
     * Setup a virtual pad fader with the color of the track with the same index.
     *
     * @param index The index of the fader
     */
    public abstract void setupFader (final int index);
}