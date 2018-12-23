// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mikro.mk3.mode;

import de.mossgrabers.controller.maschine.mikro.mk3.controller.MaschineMikroMk3ControlSurface;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;


/**
 * The browse mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BrowseMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public BrowseMode (final MaschineMikroMk3ControlSurface surface, final IModel model)
    {
        super (surface, model);

        this.isTemporary = true;
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        final IBrowser browser = this.model.getBrowser ();
        for (int i = 0; i < Math.abs (value); i++)
        {
            if (value < 0)
                browser.selectPreviousResult ();
            else
                browser.selectNextResult ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (final int index, final boolean isTouched)
    {
        if (!isTouched)
            return;
        this.model.getBrowser ().stopBrowsing (true);
    }
}