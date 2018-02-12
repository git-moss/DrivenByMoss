// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ControlSurface;
import de.mossgrabers.framework.daw.IApplication;


/**
 * Command to show/hide panes.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PaneCommand<S extends ControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    /** The available panels. */
    public enum Panels
    {
        /** Note editor. */
        NOTE,
        /** Automation editor. */
        AUTOMATION,
        /** Toggle device pane. */
        DEVICE,
        /** Mixer. */
        MIXER
    }

    private Panels panel;


    /**
     * Constructor.
     *
     * @param panel The panel to toggle
     * @param model The model
     * @param surface The surface
     */
    public PaneCommand (final Panels panel, final Model model, final S surface)
    {
        super (model, surface);
        this.panel = panel;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final IApplication application = this.model.getApplication ();
        switch (this.panel)
        {
            case NOTE:
                application.toggleNoteEditor ();
                break;
            case AUTOMATION:
                application.toggleAutomationEditor ();
                break;
            case DEVICE:
                application.toggleDevices ();
                break;
            case MIXER:
                application.toggleMixer ();
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        switch (this.panel)
        {
            // Toggle device
            case DEVICE:
                this.model.getCursorDevice ().toggleWindowOpen ();
                break;
            default:
                // Not used
                break;
        }
    }
}
