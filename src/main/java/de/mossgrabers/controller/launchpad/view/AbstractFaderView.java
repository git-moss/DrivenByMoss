// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.view;

import de.mossgrabers.controller.launchpad.controller.LaunchpadColors;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.daw.DAWColors;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Abstract base class for views with 8 faders.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractFaderView extends SessionView
{
    private static final int    PAD_VALUE_AMOUNT     = 19;

    protected final int []      trackColors          = new int [8];

    private final int []        faderMoveDelay       = new int [8];
    private final int []        faderMoveDestination = new int [8];

    // @formatter:off
    private static final int [] SPEED_SCALE          =
    {
        1,   1,  1,  1,  1,  1,  1,  1,
        1,   1,  1,  1,  1,  1,  1,  1,
        1,   1,  1,  1,  1,  1,  1,  1,
        2,   2,  2,  2,  2,  2,  2,  2,
        2,   2,  2,  2,  2,  2,  2,  2,
        2,   2,  2,  2,  2,  2,  2,  2,
        3,   3,  3,  3,  3,  3,  3,  3,
        3,   3,  3,  3,  3,  3,  3,  3,

        4,   4,  4,  4,  4,  4,  4,  4,
        5,   5,  5,  5,  5,  5,  5,  5,
        6,   6,  6,  6,  7,  7,  7,  7,
        8,   8,  8,  8,  9,  9,  9,  9,
        10, 10, 10, 10, 11, 11, 11, 11,
        12, 12, 12, 12, 13, 13, 13, 13,
        14, 14, 15, 15, 16, 16, 17, 17,
        18, 19, 20, 21, 22, 23, 24, 25
    };
    // @formatter:on


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public AbstractFaderView (final LaunchpadControlSurface surface, final IModel model)
    {
        super (surface, model);
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
        if (velocity == 0 || this.surface.hasFaderSupport ())
            return;

        // Simulate faders
        final int num = note - 36;
        final int index = num % 8;
        final int row = num / 8;

        // About 3 seconds on softest velocity
        this.faderMoveDelay[index] = SPEED_SCALE[velocity];
        this.faderMoveDestination[index] = this.smoothFaderValue (index, row, Math.min (127, row * PAD_VALUE_AMOUNT));

        this.moveFaderToDestination (index);
    }


    protected void moveFaderToDestination (final int index)
    {
        final int current = this.getFaderValue (index);
        if (current < this.faderMoveDestination[index])
            this.onValueKnob (index, Math.min (current + this.faderMoveDelay[index], this.faderMoveDestination[index]));
        else if (current > this.faderMoveDestination[index])
            this.onValueKnob (index, Math.max (current - this.faderMoveDelay[index], this.faderMoveDestination[index]));
        else
            return;

        this.model.getHost ().scheduleTask ( () -> this.moveFaderToDestination (index), 0);
    }


    /**
     * Special handling of pads for smoothing the fader, e.g. special handling of 1st row.
     *
     * @param index The fader index
     * @param row The row of the pressed pad
     * @param value The calculated value
     * @return The smoothed value
     */
    protected int smoothFaderValue (final int index, final int row, final int value)
    {
        final int oldValue = this.getFaderValue (index);
        return row == 0 && oldValue == 0 ? 15 : value;
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
    public void onScene (final int scene, final ButtonEvent event)
    {
        // Intentionally empty
    }


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
    public void switchLaunchpadMode ()
    {
        this.surface.setLaunchpadToFaderMode ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        this.surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE1, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE2, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE3, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE4, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE5, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE6, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE7, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE8, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
    }


    /**
     * Setup a virtual pad fader with the color of the track with the same index.
     *
     * @param index The index of the fader
     */
    public void setupFader (final int index)
    {
        final ITrack track = this.model.getCurrentTrackBank ().getItem (index);
        final int color = this.model.getColorManager ().getColor (DAWColors.getColorIndex (track.getColor ()));
        this.surface.setupFader (index, color, false);
    }
}