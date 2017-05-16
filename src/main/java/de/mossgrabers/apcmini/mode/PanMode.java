// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apcmini.mode;

import de.mossgrabers.apcmini.controller.APCminiControlSurface;
import de.mossgrabers.framework.Model;


/**
 * The panorama mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PanMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public PanMode (final APCminiControlSurface surface, final Model model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        this.model.getCurrentTrackBank ().setPan (index, value);
    }
}