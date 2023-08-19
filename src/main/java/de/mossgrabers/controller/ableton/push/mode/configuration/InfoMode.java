// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode.configuration;

import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.daw.IModel;


/**
 * Additional configuration settings for Push 2.
 *
 * @author Jürgen Moßgraber
 */
public class InfoMode extends AbstractConfigurationMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public InfoMode (final PushControlSurface surface, final IModel model)
    {
        super (0, "Info", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        display.addOptionElement ("  Firmware: " + this.surface.getMajorVersion () + "." + this.surface.getMinorVersion () + " Build " + this.surface.getBuildNumber (), this.menu[0], true, "", "", false, true);
        display.addOptionElement ("", this.menu[1], false, "", "", false, true);
        display.addOptionElement ("", this.menu[2], false, "", "", false, true);
        display.addOptionElement ("Board Revision: " + this.surface.getBoardRevision (), this.menu[3], false, "", "", false, true);
        display.addEmptyElement (true);
        display.addOptionElement ("        Serial Number: " + this.surface.getSerialNumber (), " ", false, "", "", false, true);
        display.addEmptyElement (true);
        display.addEmptyElement (true);
    }
}
