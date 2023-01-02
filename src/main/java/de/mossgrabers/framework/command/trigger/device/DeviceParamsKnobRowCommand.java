// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.device;

import de.mossgrabers.framework.command.core.AbstractContinuousCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;

import java.util.Date;


/**
 * Command to change a device parameter. Slows down the knob.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceParamsKnobRowCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractContinuousCommand<S, C>
{
    private final int index;
    private long      moveStartTime;
    private boolean   isKnobMoving;


    /**
     * Constructor.
     *
     * @param index The index of the button
     * @param model The model
     * @param surface The surface
     */
    public DeviceParamsKnobRowCommand (final int index, final IModel model, final S surface)
    {
        super (model, surface);
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        final ICursorDevice cd = this.model.getCursorDevice ();
        if (!cd.doesExist ())
            return;
        cd.getParameterBank ().getItem (this.index).setValue (value);

        this.moveStartTime = new Date ().getTime ();
        if (this.isKnobMoving)
            return;

        this.isKnobMoving = true;
        this.startCheckKnobMovement ();
    }


    /**
     * Returns true if knob is moving.
     *
     * @return True if knob is moving
     */
    public boolean isKnobMoving ()
    {
        return this.isKnobMoving;
    }


    private void checkKnobMovement ()
    {
        if (!this.isKnobMoving)
            return;
        if (new Date ().getTime () - this.moveStartTime > 200)
            this.isKnobMoving = false;
        else
            this.startCheckKnobMovement ();
    }


    private void startCheckKnobMovement ()
    {
        this.surface.scheduleTask (this::checkKnobMovement, 100);
    }
}
