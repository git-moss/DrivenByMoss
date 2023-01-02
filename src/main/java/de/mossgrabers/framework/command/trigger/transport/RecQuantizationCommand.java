// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.transport;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to switch to the previous/next record quantization resolution.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class RecQuantizationCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public RecQuantizationCommand (final IModel model, final S surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ITransport transport = this.model.getTransport ();

        if (this.surface.isSelectPressed ())
        {
            transport.nextLaunchQuantization ();
            this.mvHelper.delayDisplay ( () -> transport.getAutomationWriteMode ().getLabel ());
            return;
        }

        final boolean isShift = this.surface.isShiftPressed ();
        final boolean flipRecord = this.surface.getConfiguration ().isFlipRecord ();
        if (isShift && !flipRecord || !isShift && flipRecord)
            transport.toggleWriteClipLauncherAutomation ();
        else
            transport.toggleWriteArrangerAutomation ();
    }


    /**
     * Returns true if the automation button should be lit.
     *
     * @return True if lit
     */
    public boolean isLit ()
    {
        final boolean isShift = this.surface.isShiftPressed ();
        final boolean flipRecord = this.surface.getConfiguration ().isFlipRecord ();
        final ITransport transport = this.model.getTransport ();
        if (isShift && !flipRecord || !isShift && flipRecord)
            return transport.isWritingClipLauncherAutomation ();
        return transport.isWritingArrangerAutomation ();
    }
}
