// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.view;

import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.featuregroup.FeatureGroupManager;
import de.mossgrabers.framework.featuregroup.IFeatureGroup;

import java.util.function.IntSupplier;


/**
 * Get a color as an integer from the active feature group by calling its'
 * {@link IFeatureGroup#getButtonColor(ButtonID)} method. Use e.g. in combination with a
 * {@link ViewButtonCommand}.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FeatureGroupButtonColorSupplier implements IntSupplier
{
    private final FeatureGroupManager<?, ?> viewManager;
    private final ButtonID                  buttonID;


    /**
     * Constructor.
     *
     * @param manager The feature group manager
     * @param buttonID A button ID
     */
    public FeatureGroupButtonColorSupplier (final FeatureGroupManager<?, ?> manager, final ButtonID buttonID)
    {
        this.viewManager = manager;
        this.buttonID = buttonID;
    }


    /** {@inheritDoc} */
    @Override
    public int getAsInt ()
    {
        final IFeatureGroup activeView = this.viewManager.getActive ();
        return activeView != null ? activeView.getButtonColor (this.buttonID) : 0;
    }
}
