// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mkii.command.trigger;

import de.mossgrabers.controller.ni.kontrol.mkii.KontrolProtocolConfiguration;
import de.mossgrabers.controller.ni.kontrol.mkii.KontrolProtocolConfiguration.SwitchButton;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Switch mode instead of the normal command depending on the respective setting.
 *
 * @author Jürgen Moßgraber
 */
public class ModeSwitcherCommand implements TriggerCommand
{
    private final TriggerCommand               normalCommand;
    private final TriggerCommand               switcherCommand;
    private final SwitchButton                 switchButton;
    private final KontrolProtocolConfiguration configuration;


    /**
     * Constructor.
     * 
     * @param normalCommand The command to execute normally
     * @param switcherCommand The mode switcher to execute if configured in the settings
     * @param switchButton The switch button which triggers the switching if configured
     * @param configuration The configuration
     */
    public ModeSwitcherCommand (final TriggerCommand normalCommand, final TriggerCommand switcherCommand, final KontrolProtocolConfiguration.SwitchButton switchButton, final KontrolProtocolConfiguration configuration)
    {
        this.normalCommand = normalCommand;
        this.switcherCommand = switcherCommand;
        this.switchButton = switchButton;
        this.configuration = configuration;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (this.configuration.getModeSwitchButton () == this.switchButton)
            this.switcherCommand.execute (event, velocity);
        else
            this.normalCommand.execute (event, velocity);
    }
}
