// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.view;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractDrum64View;


/**
 * The Drum 128 view.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneDrumView128 extends AbstractDrum64View<OxiOneControlSurface, OxiOneConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public OxiOneDrumView128 (final OxiOneControlSurface surface, final IModel model)
    {
        super (surface, model, 16, 8);

        this.drumStartKey = 0;
        this.offsetY = 0;
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (ButtonID.isSceneButton (buttonID))
            this.onSceneButton (buttonID, event);
    }
}
