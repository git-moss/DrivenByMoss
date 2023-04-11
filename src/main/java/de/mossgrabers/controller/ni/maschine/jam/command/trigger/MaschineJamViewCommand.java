// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.command.trigger;

import de.mossgrabers.controller.ni.maschine.core.command.trigger.EncoderMode;
import de.mossgrabers.controller.ni.maschine.core.controller.EncoderModeManager;
import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Command to change the groove swing amount.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineJamViewCommand extends AbstractTriggerCommand<MaschineJamControlSurface, MaschineJamConfiguration>
{
    private final EncoderModeManager<MaschineJamControlSurface, MaschineJamConfiguration> encoderManager;
    private final EncoderMode                                                             encoderMode;
    private boolean                                                                       wasUsed = false;


    /**
     * Constructor.
     *
     * @param encoderManager The encoder manager
     * @param encoderMode The encoder mode
     * @param model The model
     * @param surface The surface
     */
    public MaschineJamViewCommand (final EncoderModeManager<MaschineJamControlSurface, MaschineJamConfiguration> encoderManager, final EncoderMode encoderMode, final IModel model, final MaschineJamControlSurface surface)
    {
        super (model, surface);

        this.encoderManager = encoderManager;
        this.encoderMode = encoderMode;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        switch (event)
        {
            case LONG:
                this.wasUsed = true;
                return;

            case DOWN:
                this.wasUsed = false;
                this.encoderManager.enableTemporaryEncodeMode (this.encoderMode);
                if (this.encoderMode == EncoderMode.TEMPORARY_LOCK)
                    this.surface.getViewManager ().setTemporary (Views.CONTROL);
                break;

            case UP:
                this.encoderManager.disableTemporaryEncodeMode ();
                if (!this.wasUsed && this.encoderMode == EncoderMode.TEMPORARY_LOCK)
                {
                    final MaschineJamConfiguration configuration = this.surface.getConfiguration ();
                    configuration.setAccentEnabled (!configuration.isAccentActive ());
                }
                if (this.encoderMode == EncoderMode.TEMPORARY_LOCK)
                    this.surface.getViewManager ().restore ();
                break;
        }
    }
}
