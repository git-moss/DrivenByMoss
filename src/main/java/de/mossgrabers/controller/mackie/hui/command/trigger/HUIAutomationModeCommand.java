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
    private final AutomationMode ourAutomationMode;

    public HUIAutomationModeCommand(final AutomationMode autoMode, final IModel model, final HUIControlSurface surface, final HUIDisplay display)
    {
        super(autoMode, model, surface);
        this.ourAutomationMode = autoMode;
        this.display = display;
    }


    @Override
    public void executeNormal(final ButtonEvent event)
    {
        super.executeNormal(event);

            final AutomationMode mode = this.model.getTransport().getAutomationWriteMode();
            if (mode == this.ourAutomationMode) {
                if (this.surface.getConfiguration().shouldNotifyAutomationMode()) {
                    this.display.notifyHUIDisplay("Automation: " + mode.name());
                }
            }
    }
}