// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.view;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.BitwigColors;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;


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
    public PanView (final LaunchpadControlSurface surface, final IModel model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        this.model.getCurrentTrackBank ().getTrack (index).setPan (value);
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
        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final IMidiOutput output = this.surface.getOutput ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack track = tb.getTrack (i);
            final int color = cm.getColor (BitwigColors.getColorIndex (track.getColor ()));
            if (this.trackColors[i] != color)
            {
                this.trackColors[i] = color;
                this.setupFader (i);
            }
            output.sendCC (LaunchpadControlSurface.LAUNCHPAD_FADER_1 + i, track.doesExist () ? track.getPan () : 64);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setupFader (final int index)
    {
        final ITrack track = this.model.getCurrentTrackBank ().getTrack (index);
        this.surface.setupPanFader (index, this.model.getColorManager ().getColor (BitwigColors.getColorIndex (track.getColor ())));
    }
}