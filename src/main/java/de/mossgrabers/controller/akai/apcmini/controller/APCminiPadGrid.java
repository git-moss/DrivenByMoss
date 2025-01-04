// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apcmini.controller;

import de.mossgrabers.controller.akai.apcmini.APCminiConfiguration;
import de.mossgrabers.controller.akai.apcmini.definition.IAPCminiControllerDefinition;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGridImpl;
import de.mossgrabers.framework.daw.midi.IMidiOutput;


/**
 * Implementation of the APCmini grid of pads.
 *
 * @author Jürgen Moßgraber
 */
public class APCminiPadGrid extends PadGridImpl
{
    private final IAPCminiControllerDefinition definition;
    private final APCminiConfiguration         configuration;


    /**
     * Constructor.
     *
     * @param colorManager The color manager for accessing specific colors to use
     * @param output The MIDI output which can address the pad states
     * @param configuration
     * @param definition
     */
    public APCminiPadGrid (final ColorManager colorManager, final IMidiOutput output, final APCminiConfiguration configuration, final IAPCminiControllerDefinition definition)
    {
        super (colorManager, output);

        this.definition = definition;
        this.configuration = configuration;
    }


    /** {@inheritDoc} */
    @Override
    protected void sendNoteState (final int channel, final int note, final int color)
    {
        int chn = channel;
        if (this.definition.hasBrightness ())
            chn += this.configuration.getPadBrightness ();
        this.output.sendNoteEx (chn, note, color);
    }


    /** {@inheritDoc} */
    @Override
    protected void sendBlinkState (final int channel, final int note, final int blinkColor, final boolean fast)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public int translateToGrid (final int note)
    {
        return note + 36;
    }


    /** {@inheritDoc} */
    @Override
    public int [] translateToController (final int note)
    {
        return new int []
        {
            0,
            note - 36
        };
    }
}