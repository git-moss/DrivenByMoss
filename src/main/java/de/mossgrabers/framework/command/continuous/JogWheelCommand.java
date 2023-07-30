// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.continuous;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;


/**
 * Different commands to execute with the jog wheel depending on used combination keys.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class JogWheelCommand<S extends IControlSurface<C>, C extends Configuration> extends PlayPositionCommand<S, C>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public JogWheelCommand (final IModel model, final S surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        final boolean increase = this.model.getValueChanger ().isIncrease (value);

        // Scroll results in Browser
        final IBrowser browser = this.model.getBrowser ();
        if (browser.isActive ())
        {
            if (increase)
                browser.selectNextResult ();
            else
                browser.selectPreviousResult ();
            return;
        }

        // Change tempo with Select button
        if (this.surface.isPressed (ButtonID.SELECT))
        {
            this.model.getTransport ().changeTempo (increase, this.surface.isKnobSensitivitySlow ());
            return;
        }

        // Change Loop start with Control button
        if (this.surface.isPressed (ButtonID.CONTROL))
        {
            this.model.getTransport ().changeLoopStart (increase, this.surface.isKnobSensitivitySlow ());
            return;
        }

        // Change Loop length with ALT button
        if (this.surface.isPressed (ButtonID.ALT))
        {
            this.model.getTransport ().changeLoopLength (increase, this.surface.isKnobSensitivitySlow ());
            return;
        }

        // Zoom Arranger with Left/Right Cursor button
        if (this.surface.isPressed (ButtonID.ARROW_LEFT) || this.surface.isPressed (ButtonID.ARROW_RIGHT))
        {
            this.surface.setTriggerConsumed (ButtonID.ARROW_LEFT);
            this.surface.setTriggerConsumed (ButtonID.ARROW_RIGHT);

            if (increase)
                this.model.getApplication ().zoomIn ();
            else
                this.model.getApplication ().zoomOut ();
            return;
        }

        // Increase/decrease track heights with Up/Down Cursor button
        if (this.surface.isPressed (ButtonID.ARROW_UP) || this.surface.isPressed (ButtonID.ARROW_DOWN))
        {
            this.surface.setTriggerConsumed (ButtonID.ARROW_UP);
            this.surface.setTriggerConsumed (ButtonID.ARROW_DOWN);

            if (increase)
                this.model.getApplication ().incTrackHeight ();
            else
                this.model.getApplication ().decTrackHeight ();
            return;
        }

        super.execute (value);
    }
}
