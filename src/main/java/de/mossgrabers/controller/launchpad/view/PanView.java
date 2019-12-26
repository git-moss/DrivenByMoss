// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.view;

import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;


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
    protected int smoothFaderValue (final int index, final int row, final int value)
    {
        return row == 3 || row == 4 ? 64 : value;
    }


    /** {@inheritDoc} */
    @Override
    protected int getFaderValue (final int index)
    {
        return this.model.getCurrentTrackBank ().getItem (index).getPan ();
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
        for (int i = 0; i < 8; i++)
        {
            final ITrack track = tb.getItem (i);
            final int color = cm.getColorIndex (DAWColor.getColorIndex (track.getColor ()));
            if (this.trackColors[i] != color)
            {
                this.trackColors[i] = color;
                this.setupFader (i);
            }
            this.surface.setFaderValue (i, track.doesExist () ? track.getPan () : 64);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setupFader (final int index)
    {
        final ITrack track = this.model.getCurrentTrackBank ().getItem (index);
        this.surface.setupFader (index, this.model.getColorManager ().getColorIndex (DAWColor.getColorIndex (track.getColor ())), true);
    }
}