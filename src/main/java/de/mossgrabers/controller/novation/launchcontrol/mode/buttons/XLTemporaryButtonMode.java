package de.mossgrabers.controller.novation.launchcontrol.mode.buttons;

import de.mossgrabers.controller.novation.launchcontrol.LaunchControlXLConfiguration;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.parameter.IParameter;


/**
 * A temporary mode.
 *
 * @author Jürgen Moßgraber
 */
public class XLTemporaryButtonMode extends AbstractParameterMode<LaunchControlXLControlSurface, LaunchControlXLConfiguration, IParameter>
{
    private boolean hasBeenUsed = false;


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     */
    protected XLTemporaryButtonMode (final String name, final LaunchControlXLControlSurface surface, final IModel model)
    {
        super (name, surface, model, true);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();

        this.hasBeenUsed = false;
    }


    /**
     * Signal that the mode has been used.
     */
    public void setHasBeenUsed ()
    {
        this.hasBeenUsed = true;
    }


    /**
     * Has a function of this mode been executed during the last active phase of the mode?
     *
     * @return True if a page has been selected
     */
    public boolean hasBeenUsed ()
    {
        return this.hasBeenUsed;
    }
}
