// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.controller;

import de.mossgrabers.controller.ni.maschine.core.command.trigger.EncoderMode;
import de.mossgrabers.controller.ni.maschine.core.controller.EncoderModeManager;
import de.mossgrabers.controller.ni.maschine.mk3.MaschineConfiguration;
import de.mossgrabers.framework.controller.hardware.IHwContinuousControl;
import de.mossgrabers.framework.daw.IModel;


/**
 * Manages the active encoder mode with some additions due to the limitations of the Maschine
 * Studios' MIDI mode. Since the LED strips do always display the value of the knob when turned, the
 * VU cannot be used together with the knob. Therefore, there are two modes: VU mode, in which the
 * knob is deactivated and the parameter mode where the knob is active and the LED strips display
 * the value of the selected parameter.
 *
 * @author Jürgen Moßgraber
 */
public class StudioEncoderModeManager extends EncoderModeManager<MaschineControlSurface, MaschineConfiguration>
{
    private boolean     parameterMode             = true;
    private EncoderMode previousActiveEncoderMode = EncoderMode.MASTER_VOLUME;


    /**
     * Constructor.
     *
     * @param encoder The encoder knob
     * @param model The model
     * @param surface The controller surface
     */
    public StudioEncoderModeManager (final IHwContinuousControl encoder, final IModel model, final MaschineControlSurface surface)
    {
        super (encoder, model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isLit (final EncoderMode encoderMode)
    {
        if (this.getActiveEncoderMode () == EncoderMode.OFF)
            return this.previousActiveEncoderMode == encoderMode;
        return this.isActiveEncoderMode (encoderMode);
    }


    /** {@inheritDoc} */
    @Override
    public void setActiveEncoderMode (final EncoderMode encoderMode)
    {
        if (this.parameterMode)
            super.setActiveEncoderMode (encoderMode);
        else
            this.previousActiveEncoderMode = encoderMode;
    }


    /**
     * Toggles between the VU mode and the parameter mode.
     */
    public void toggleMode ()
    {
        this.parameterMode = !this.parameterMode;

        if (this.parameterMode)
        {
            super.setActiveEncoderMode (this.previousActiveEncoderMode);
        }
        else
        {
            this.previousActiveEncoderMode = this.getActiveEncoderMode ();
            super.setActiveEncoderMode (EncoderMode.OFF);
        }
    }


    /**
     * Test if the parameter mode or the VU mode is active.
     *
     * @return True if the parameter mode is active otherwise the VU mode
     */
    public boolean isParameterMode ()
    {
        return this.parameterMode;
    }
}
