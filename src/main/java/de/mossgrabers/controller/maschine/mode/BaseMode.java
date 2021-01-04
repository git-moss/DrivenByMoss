// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mode;

import de.mossgrabers.controller.maschine.MaschineConfiguration;
import de.mossgrabers.controller.maschine.controller.MaschineControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.AbstractMode;


/**
 * Base class for all Maschine modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class BaseMode extends AbstractMode<MaschineControlSurface, MaschineConfiguration>
{
    protected int selectedParam = 0;


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     */
    public BaseMode (final String name, final MaschineControlSurface surface, final IModel model)
    {
        super (name, surface, model, false);
    }


    /**
     * Add a marker (>) if the index equals the selected pareameter.
     *
     * @param label The label to eventually add the marker
     * @param index The index
     * @return The formatted text
     */
    protected String mark (final String label, final int index)
    {
        if (this.selectedParam == index)
            return ">" + label;
        return label;
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        this.selectedParam = Math.max (0, this.selectedParam - 1);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        this.selectedParam = Math.min (7, this.selectedParam + 1);
    }


    /** {@inheritDoc} */
    @Override
    public void selectItem (final int index)
    {
        this.selectedParam = index;
    }


    /**
     * Get the selected item (edit index).
     *
     * @return THe edit index
     */
    public int getSelectedItem ()
    {
        return this.selectedParam;
    }
}
