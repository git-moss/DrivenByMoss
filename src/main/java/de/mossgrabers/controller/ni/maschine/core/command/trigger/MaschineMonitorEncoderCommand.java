// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.core.command.trigger;

import de.mossgrabers.controller.ni.maschine.core.controller.EncoderModeManager;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Support for the NI Maschine controller series.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class MaschineMonitorEncoderCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    protected final EncoderModeManager<S, C> manager;
    protected EncoderMode                    encoderMode;


    /**
     * Constructor.
     *
     * @param manager The encoder manager
     * @param encoderMode The mode to trigger with this button
     * @param model The model
     * @param surface The surface
     */
    public MaschineMonitorEncoderCommand (final EncoderModeManager<S, C> manager, final EncoderMode encoderMode, final IModel model, final S surface)
    {
        super (model, surface);

        this.manager = manager;
        this.encoderMode = encoderMode;
    }


    /** {@inheritDoc} */
    @Override
    public synchronized void execute (final ButtonEvent event, final int velocity)
    {
        if (event == ButtonEvent.DOWN)
            this.manager.setActiveEncoderMode (this.encoderMode);
    }


    /**
     * Returns true if one of the encoder related buttons should be lit.
     *
     * @return True if lit
     */
    public boolean isLit ()
    {
        return this.manager.isLit (this.encoderMode);
    }
}
