// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii.controller;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.LightGuideImpl;
import de.mossgrabers.framework.daw.midi.IMidiOutput;


/**
 * Implementation of the SLMkIII light guide.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SLMkIIILightGuide extends LightGuideImpl
{
    /**
     * Constructor.
     *
     * @param colorManager The color manager for accessing specific colors to use
     * @param output The midi output which can address the pad states
     */
    public SLMkIIILightGuide (final ColorManager colorManager, final IMidiOutput output)
    {
        super (54, 61, colorManager, output);
    }


    /** {@inheritDoc} */
    @Override
    protected void sendNoteState (final int channel, final int note, final int color)
    {
        this.output.sendNoteEx (15, note, color);
    }
}
