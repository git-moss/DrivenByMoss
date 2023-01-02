// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apcmini.view;

import de.mossgrabers.controller.akai.apcmini.APCminiConfiguration;
import de.mossgrabers.controller.akai.apcmini.controller.APCminiColorManager;
import de.mossgrabers.controller.akai.apcmini.controller.APCminiControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.BrowserView;


/**
 * The Browser view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class APCMiniBrowserView extends BrowserView<APCminiControlSurface, APCminiConfiguration> implements APCminiView
{
    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public APCMiniBrowserView (final APCminiControlSurface surface, final IModel model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onSelectTrack (final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public int getTrackButtonColor (final int index)
    {
        return APCminiColorManager.APC_COLOR_BLACK;
    }
}