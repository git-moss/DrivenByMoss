// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.acvs.view;

import de.mossgrabers.controller.akai.acvs.ACVSConfiguration;
import de.mossgrabers.controller.akai.acvs.controller.ACVSControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.AbstractView;


/**
 * There are no real views in the ACVS protocol which can be controlled. Therefore, everything is in
 * one view.
 *
 * @author Jürgen Moßgraber
 */
public class ControlView extends AbstractView<ACVSControlSurface, ACVSConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public ControlView (final ACVSControlSurface surface, final IModel model)
    {
        super ("Control", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        // Not used
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        // Not used
    }
}
