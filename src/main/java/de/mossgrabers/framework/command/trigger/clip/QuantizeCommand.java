// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.clip;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IClip;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.RecordQuantization;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to quantize the currently selected clip.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
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
        if (event == ButtonEvent.DOWN)
            this.quantize ();
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (!cursorTrack.doesExist ())
            return;

        // Toggle through all record quantization settings...

        final RecordQuantization [] values = RecordQuantization.values ();
        final RecordQuantization recordQuantization = cursorTrack.getRecordQuantizationGrid ();
        int index = 0;
        for (int i = 0; i < values.length; i++)
        {
            if (recordQuantization == values[i])
            {
                index = i + 1;
                if (index >= values.length)
                    index = 0;
                break;
            }
        }
        cursorTrack.setRecordQuantizationGrid (values[index]);
        this.surface.getDisplay ().notify ("Record Quantization: " + values[index].getName ());
    }


    protected void quantize ()
    {
        final IClip clip = this.model.getCursorClip ();
        if (clip.doesExist ())
            clip.quantize (this.surface.getConfiguration ().getQuantizeAmount () / 100.0);
    }
}
