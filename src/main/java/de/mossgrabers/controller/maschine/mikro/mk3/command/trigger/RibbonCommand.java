// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mikro.mk3.command.trigger;

import de.mossgrabers.controller.maschine.mikro.mk3.MaschineMikroMk3Configuration;
import de.mossgrabers.controller.maschine.mikro.mk3.controller.MaschineMikroMk3ControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for toggling the ribbon mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class RibbonCommand extends AbstractTriggerCommand<MaschineMikroMk3ControlSurface, MaschineMikroMk3Configuration>
{
    private final int [] modes;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param modes The modes to toggle
     */
    public RibbonCommand (final IModel model, final MaschineMikroMk3ControlSurface surface, final int... modes)
    {
        super (model, surface);
        this.modes = modes;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final MaschineMikroMk3Configuration configuration = this.surface.getConfiguration ();
        final int ribbonMode = configuration.getRibbonMode ();

        int m = this.modes[0];

        for (int i = 0; i < this.modes.length; i++)
        {
            if (this.modes[i] == ribbonMode)
            {
                if (i + 1 < this.modes.length)
                    m = this.modes[i + 1];
                break;
            }
        }

        this.surface.getDisplay ().notify (MaschineMikroMk3Configuration.RIBBON_MODE_VALUES[m]);
        configuration.setRibbonMode (m);

        // Setting the LED strip does not work but keep it anyway...
        switch (m)
        {
            case MaschineMikroMk3Configuration.RIBBON_MODE_PITCH_DOWN_UP:
                this.surface.getMidiOutput ().sendCC (MaschineMikroMk3ControlSurface.MIKRO_3_TOUCHSTRIP, 64);
                break;

            case MaschineMikroMk3Configuration.RIBBON_MODE_MASTER_VOLUME:
                this.surface.getMidiOutput ().sendCC (MaschineMikroMk3ControlSurface.MIKRO_3_TOUCHSTRIP, this.model.getValueChanger ().toMidiValue (this.model.getMasterTrack ().getVolume ()));
                break;

            default:
                this.surface.getMidiOutput ().sendCC (MaschineMikroMk3ControlSurface.MIKRO_3_TOUCHSTRIP, 0);
                break;
        }

    }
}
