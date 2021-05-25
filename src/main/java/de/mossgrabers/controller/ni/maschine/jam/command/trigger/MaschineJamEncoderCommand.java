// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.command.trigger;

import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.EncoderModeManager;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Support for the NI Maschine controller series.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MaschineJamEncoderCommand extends AbstractTriggerCommand<MaschineJamControlSurface, MaschineJamConfiguration>
{
    private final EncoderModeManager manager;
    private EncoderMode              encoderMode;


    /**
     * Constructor.
     *
     * @param manager The encoder manager
     * @param encoderMode The mode to trigger with this button
     * @param model The model
     * @param surface The surface
     */
    public MaschineJamEncoderCommand (final EncoderModeManager manager, final EncoderMode encoderMode, final IModel model, final MaschineJamControlSurface surface)
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
        return this.manager.isActiveEncoderMode (this.encoderMode);
    }
}
