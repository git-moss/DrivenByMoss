// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad.view;

import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.BitwigColors;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.framework.midi.MidiOutput;
import de.mossgrabers.launchpad.controller.LaunchpadControlSurface;


/**
 * 8 panorama faders.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PanView extends AbstractFaderView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public PanView (final LaunchpadControlSurface surface, final Model model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        this.model.getCurrentTrackBank ().setPan (index, value);
    }


    /** {@inheritDoc} */
    @Override
    public void switchLaunchpadMode ()
    {
        this.surface.setLaunchpadToPanMode ();
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final ColorManager cm = this.model.getColorManager ();
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final MidiOutput output = this.surface.getOutput ();
        for (int i = 0; i < 8; i++)
        {
            final TrackData track = tb.getTrack (i);
            final int color = cm.getColor (BitwigColors.getColorIndex (track.getColor ()));
            if (this.trackColors[i] != color || !track.doesExist ())
                this.setupFader (i);
            this.trackColors[i] = color;
            output.sendCC (LaunchpadControlSurface.LAUNCHPAD_FADER_1 + i, track.getPan ());
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setupFader (final int index)
    {
        final TrackData track = this.model.getCurrentTrackBank ().getTrack (index);
        this.surface.setupPanFader (index, this.model.getColorManager ().getColor (BitwigColors.getColorIndex (track.getColor ())));
    }
}