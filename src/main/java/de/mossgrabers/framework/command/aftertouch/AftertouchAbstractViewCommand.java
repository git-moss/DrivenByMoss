// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.aftertouch;

import de.mossgrabers.framework.command.core.AbstractAftertouchCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.AbstractView;

import java.util.List;


/**
 * Command to handle the aftertouch on a view.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class AftertouchAbstractViewCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractAftertouchCommand<S, C>
{
    protected AbstractView<S, C> view;


    /**
     * Constructor.
     *
     * @param view The play view
     * @param model The model
     * @param surface The surface
     */
    public AftertouchAbstractViewCommand (final AbstractView<S, C> view, final IModel model, final S surface)
    {
        super (model, surface);
        this.view = view;
    }


    /** {@inheritDoc} */
    @Override
    public void onPolyAftertouch (final int note, final int value)
    {
        // Only allow aftertouch for pads with notes
        final int n = this.view.getKeyManager ().getMidiNoteFromGrid (note);
        if (n == -1)
            return;

        final Configuration config = this.surface.getConfiguration ();
        switch (config.getConvertAftertouch ())
        {
            case -3:
                // Filter poly aftertouch
                break;

            case -2:
                // Translate notes of Poly aftertouch to current note mapping
                this.surface.sendMidiEvent (0xA0, n, value);
                break;

            case -1:
                // Convert to Channel Aftertouch
                this.surface.sendMidiEvent (0xD0, value, 0);
                break;

            default:
                // Midi CC
                this.surface.sendMidiEvent (0xB0, config.getConvertAftertouch (), value);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onChannelAftertouch (final int value)
    {
        final Configuration config = this.surface.getConfiguration ();
        if (config.getConvertAftertouch () == -2)
        {
            final List<Integer> keys = this.view.getKeyManager ().getPressedKeys ();
            for (final Integer key: keys)
                this.onPolyAftertouch (key.intValue (), value);
        }
        else
            this.onPolyAftertouch (0, value);
    }
}
