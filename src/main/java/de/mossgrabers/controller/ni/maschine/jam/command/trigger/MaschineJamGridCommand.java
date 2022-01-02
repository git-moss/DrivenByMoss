// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.command.trigger;

import de.mossgrabers.controller.ni.maschine.core.command.trigger.EncoderMode;
import de.mossgrabers.controller.ni.maschine.core.controller.EncoderModeManager;
import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.command.trigger.clip.QuantizeCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to tap the tempo.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MaschineJamGridCommand extends QuantizeCommand<MaschineJamControlSurface, MaschineJamConfiguration>
{
    private final EncoderModeManager<MaschineJamControlSurface, MaschineJamConfiguration> encoderManager;


    /**
     * Constructor.
     *
     * @param encoderManager The encoder manager
     * @param model The model
     * @param surface The surface
     */
    public MaschineJamGridCommand (final EncoderModeManager<MaschineJamControlSurface, MaschineJamConfiguration> encoderManager, final IModel model, final MaschineJamControlSurface surface)
    {
        super (model, surface);

        this.encoderManager = encoderManager;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (this.surface.isShiftPressed () || this.surface.isSelectPressed ())
        {
            super.execute (event, velocity);
            return;
        }

        if (event == ButtonEvent.DOWN)
            this.encoderManager.enableTemporaryEncodeMode (EncoderMode.TEMPORARY_PLAY_POSITION);
        else if (event == ButtonEvent.UP)
            this.encoderManager.disableTemporaryEncodeMode ();
    }
}
