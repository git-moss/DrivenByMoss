// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.command.trigger;

import de.mossgrabers.controller.ni.maschine.mk3.MaschineConfiguration;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.command.trigger.transport.MetronomeCommand;
import de.mossgrabers.framework.command.trigger.transport.TapTempoCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to handle the metronome and tempo tap.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineTapMetroCommand extends AbstractTriggerCommand<MaschineControlSurface, MaschineConfiguration>
{
    final MetronomeCommand<MaschineControlSurface, MaschineConfiguration> metroCommand;
    final TapTempoCommand<MaschineControlSurface, MaschineConfiguration>  tapTempoCommand;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public MaschineTapMetroCommand (final IModel model, final MaschineControlSurface surface)
    {
        super (model, surface);

        this.metroCommand = new MetronomeCommand<> (this.model, surface, false);
        this.tapTempoCommand = new TapTempoCommand<> (this.model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.UP)
            return;
        if (this.surface.isShiftPressed ())
        {
            this.surface.setStopConsumed ();
            this.metroCommand.executeNormal (event);
        }
        else
            this.tapTempoCommand.execute (ButtonEvent.DOWN, velocity);
    }
}
