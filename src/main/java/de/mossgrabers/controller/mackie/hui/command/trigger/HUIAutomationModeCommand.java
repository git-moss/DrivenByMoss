package de.mossgrabers.controller.mackie.hui.command.trigger;

import de.mossgrabers.controller.mackie.hui.HUIConfiguration;
import de.mossgrabers.controller.mackie.hui.controller.HUIControlSurface;
import de.mossgrabers.controller.mackie.hui.controller.HUIDisplay;
import de.mossgrabers.framework.command.trigger.transport.AutomationModeCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.AutomationMode;
import de.mossgrabers.framework.utils.ButtonEvent;

public class HUIAutomationModeCommand extends AutomationModeCommand<HUIControlSurface, HUIConfiguration>
{
    private final HUIDisplay display;

    public HUIAutomationModeCommand(final AutomationMode autoMode, final IModel model, final HUIControlSurface surface, final HUIDisplay display)
    {
        super(autoMode, model, surface);
        this.display = display;
    }

    @Override
    public void executeNormal(final ButtonEvent event)
    {
        super.executeNormal(event);

        if (event == ButtonEvent.DOWN)
        {
            final AutomationMode mode = this.model.getTransport().getAutomationWriteMode();
            this.display.notifyHUIDisplay("Automation mode: " + mode.name());
        }
    }
}