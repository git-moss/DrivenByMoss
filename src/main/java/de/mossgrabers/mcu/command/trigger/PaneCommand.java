// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.mcu.MCUConfiguration;
import de.mossgrabers.mcu.controller.MCUControlSurface;


/**
 * Command to show/hide panes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PaneCommand extends AbstractTriggerCommand<MCUControlSurface, MCUConfiguration>
{
    private int index;


    /**
     * Constructor.
     *
     * @param index The pane index
     * @param model The model
     * @param surface The surface
     */
    public PaneCommand (final int index, final Model model, final MCUControlSurface surface)
    {
        super (model, surface);
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        switch (this.index)
        {
            // Note editor
            case 0:
                this.model.getApplication ().toggleNoteEditor ();
                break;
            // Automation editor
            case 1:
                this.model.getApplication ().toggleAutomationEditor ();
                break;
            // Toggle device pane
            case 2:
                this.model.getApplication ().toggleDevices ();
                break;
            // Mixer
            case 3:
                this.model.getApplication ().toggleMixer ();
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        switch (this.index)
        {
            // Toggle device
            case 2:
                this.model.getCursorDevice ().toggleWindowOpen ();
                break;
        }
    }
}
