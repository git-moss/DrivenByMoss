// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.controller;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGridImpl;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.IExpressionView;


/**
 * Implementation of the Push grid of pads with specific MPE extension for Push 3.
 *
 * @author Jürgen Moßgraber
 */
public class PushPadGrid extends PadGridImpl
{
    private PushControlSurface surface;
    private PushConfiguration  configuration;


    /**
     * Constructor.
     *
     * @param colorManager The color manager for accessing specific colors to use
     * @param output The MIDI output which can address the pad states
     */
    public PushPadGrid (final ColorManager colorManager, final IMidiOutput output)
    {
        super (colorManager, output);
    }


    /** {@inheritDoc} */
    @Override
    public int [] translateToController (final int note)
    {
        return new int []
        {
            this.surface != null && this.surface.getViewManager ().getActive () instanceof IExpressionView && this.configuration.isMPEEnabled () ? -1 : 0,
            note
        };
    }


    /**
     * Set the surface.
     *
     * @param surface The surface
     */
    public void setSurface (final PushControlSurface surface)
    {
        this.surface = surface;
        this.configuration = surface.getConfiguration ();
    }
}