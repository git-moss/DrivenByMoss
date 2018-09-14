// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.view;

import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.DAWColors;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.IMidiOutput;


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
        this.model.getCurrentTrackBank ().getItem (index).setPan (value);
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
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final IMidiOutput output = this.surface.getOutput ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack track = tb.getItem (i);
            final int color = cm.getColor (DAWColors.getColorIndex (track.getColor ()));
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
        final ITrack track = this.model.getCurrentTrackBank ().getItem (index);
        this.surface.setupPanFader (index, this.model.getColorManager ().getColor (DAWColors.getColorIndex (track.getColor ())));
    }
}