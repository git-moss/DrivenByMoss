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
 * Command to change the groove swing amount.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MaschineJamViewCommand extends AbstractTriggerCommand<MaschineJamControlSurface, MaschineJamConfiguration>
{
    private final EncoderModeManager encoderManager;
    private final EncoderMode        encoderMode;


    /**
     * Constructor.
     *
     * @param encoderManager The encoder manager
     * @param encoderMode The encoder mode
     * @param model The model
     * @param surface The surface
     */
    public MaschineJamViewCommand (final EncoderModeManager encoderManager, final EncoderMode encoderMode, final IModel model, final MaschineJamControlSurface surface)
    {
        super (model, surface);

        this.encoderManager = encoderManager;
        this.encoderMode = encoderMode;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.encoderManager.enableTemporaryEncodeMode (this.encoderMode);
        else if (event == ButtonEvent.UP)
            this.encoderManager.disableTemporaryEncodeMode ();
    }
}
