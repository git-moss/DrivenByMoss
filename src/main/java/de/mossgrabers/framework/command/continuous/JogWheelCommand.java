// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.continuous;

import java.util.Optional;

import de.mossgrabers.framework.command.core.AbstractContinuousCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.parameter.IFocusedParameter;


/**
 * Different commands to execute with the jog wheel depending on used combination keys.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class JogWheelCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractContinuousCommand<S, C>
{
    private boolean             controlLastParamActive = false;
    private final ITransport    transport;
    private final IApplication  application;
    private final IValueChanger valueChanger;
    private int                 slowDownCounter        = 1;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public JogWheelCommand (final IModel model, final S surface)
    {
        this (model, surface, model.getValueChanger ());
    }


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param valueChanger The value changer to use
     */
    public JogWheelCommand (final IModel model, final S surface, final IValueChanger valueChanger)
    {
        super (model, surface);

        this.valueChanger = valueChanger;
        this.transport = this.model.getTransport ();
        this.application = this.model.getApplication ();
    }


    /**
     * Dis-/enable controlling the last touched/clicked parameter.
     */
    public void toggleControlLastParamActive ()
    {
        this.controlLastParamActive = !this.controlLastParamActive;
    }


    /**
     * De-/activate controlling the last touched/clicked parameter .
     *
     * @param active True to activate
     */
    public void setControlLastParamActive (final boolean active)
    {
        this.controlLastParamActive = active;
    }


    /**
     * Is controlling the last touched/clicked parameter active?
     *
     * @return True if active
     */
    public boolean isControlLastParamActive ()
    {
        return this.controlLastParamActive;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        // Slow down option for fast encoders
        final int encoderKnobSlowDown = this.surface.getConfiguration ().getEncoderKnobSlowDown ();
        if (encoderKnobSlowDown > 0)
        {
            if (this.slowDownCounter % (encoderKnobSlowDown + 1) == 0)
                this.slowDownCounter = 1;
            else
            {
                this.slowDownCounter++;
                return;
            }
        }

        // Control the last touched/clicked parameter
        if (this.controlLastParamActive)
        {
            final Optional<IFocusedParameter> parameterOpt = this.model.getFocusedParameter ();
            if (parameterOpt.isPresent () && parameterOpt.get ().doesExist ())
            {
                double increment = this.valueChanger.calcKnobChange (value);
                increment *= this.surface.isShiftPressed () ? 10 : 50;
                parameterOpt.get ().inc (increment);
            }
            return;
        }

        final boolean increase = this.valueChanger.isIncrease (value);

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
            this.transport.changeTempo (increase, this.surface.isKnobSensitivitySlow ());
            return;
        }

        // Change Loop start with Control button
        if (this.surface.isPressed (ButtonID.CONTROL))
        {
            this.transport.changeLoopStart (increase, this.surface.isKnobSensitivitySlow ());
            return;
        }

        // Change Loop length with ALT button
        if (this.surface.isPressed (ButtonID.ALT))
        {
            this.transport.changeLoopLength (increase, this.surface.isKnobSensitivitySlow ());
            return;
        }

        // Zoom Arranger with Left/Right Cursor button
        if (this.surface.isPressed (ButtonID.ARROW_LEFT) || this.surface.isPressed (ButtonID.ARROW_RIGHT))
        {
            this.surface.setTriggerConsumed (ButtonID.ARROW_LEFT);
            this.surface.setTriggerConsumed (ButtonID.ARROW_RIGHT);

            if (increase)
                this.application.zoomIn ();
            else
                this.application.zoomOut ();
            return;
        }

        // Increase/decrease track heights with Up/Down Cursor button
        if (this.surface.isPressed (ButtonID.ARROW_UP) || this.surface.isPressed (ButtonID.ARROW_DOWN))
        {
            this.surface.setTriggerConsumed (ButtonID.ARROW_UP);
            this.surface.setTriggerConsumed (ButtonID.ARROW_DOWN);

            if (increase)
                this.application.incTrackHeight ();
            else
                this.application.decTrackHeight ();
            return;
        }

        this.transport.changePosition (this.valueChanger.isIncrease (value), this.surface.isKnobSensitivitySlow ());
    }
}
