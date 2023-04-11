// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.clip;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.IClip;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to quantize the currently selected clip.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class QuantizeCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public QuantizeCommand (final IModel model, final S surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        if (this.surface.isPressed (ButtonID.SELECT))
            this.selectRecordQuantization (false);
        else
            this.quantize ();
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.selectRecordQuantization (true);
    }


    protected void quantize ()
    {
        final IClip clip = this.model.getCursorClip ();
        if (clip.doesExist ())
            clip.quantize (this.surface.getConfiguration ().getQuantizeAmount () / 100.0);
    }


    protected void selectRecordQuantization (final boolean next)
    {
        // Toggle through all record quantization settings...
        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (cursorTrack.doesExist ())
        {
            if (next)
                cursorTrack.nextRecordQuantization ();
            else
                cursorTrack.previousRecordQuantization ();
            this.mvHelper.delayDisplay ( () -> "Record Quantization: " + cursorTrack.getRecordQuantizationGrid ().getName ());
        }
    }
}
