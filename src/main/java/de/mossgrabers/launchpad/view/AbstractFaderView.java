// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad.view;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.daw.BitwigColors;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.launchpad.controller.LaunchpadColors;
import de.mossgrabers.launchpad.controller.LaunchpadControlSurface;


/**
 * Abstract base class for views with 8 faders.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractFaderView extends SessionView
{
    protected final int [] trackColors = new int [8];


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public AbstractFaderView (final LaunchpadControlSurface surface, final Model model)
    {
        super (surface, model);
    }


    /**
     * A knob has been used.
     *
     * @param index The index of the knob
     * @param value The value the knob sent
     */
    public abstract void onValueKnob (int index, int value);


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        // Intentionally empty
    }


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
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE1, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE2, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE3, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE4, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE5, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE6, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE7, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE8, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
    }


    /**
     * Setup a virtual pad fader with the color of the track with the same index.
     *
     * @param index
     */
    public void setupFader (final int index)
    {
        final TrackData track = this.model.getCurrentTrackBank ().getTrack (index);
        final int color = this.model.getColorManager ().getColor (BitwigColors.getColorIndex (track.getColor ()));
        this.surface.setupFader (index, color);
    }
}