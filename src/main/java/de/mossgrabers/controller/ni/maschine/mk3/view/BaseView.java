// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.view;

import de.mossgrabers.controller.ni.maschine.mk3.MaschineConfiguration;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.AbstractView;


/**
 * Base class for views views.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class BaseView extends AbstractView<MaschineControlSurface, MaschineConfiguration>
{
    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The controller
     * @param model The model
     */
    protected BaseView (final String name, final MaschineControlSurface surface, final IModel model)
    {
        super (name, surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity > 0)
            this.executeFunction (note - 36);
    }


    /**
     * Implement to execute whatever function the view has.
     *
     * @param padIndex The index of the pressed pad (0-15)
     */
    protected abstract void executeFunction (int padIndex);
}