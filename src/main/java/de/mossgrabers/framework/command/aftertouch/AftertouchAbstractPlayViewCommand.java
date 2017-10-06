// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.aftertouch;

import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractAftertouchCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ControlSurface;
import de.mossgrabers.framework.view.AbstractPlayView;

import java.util.List;


/**
 * Command to handle the aftertouch of the play view.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class AftertouchAbstractPlayViewCommand<S extends ControlSurface<C>, C extends Configuration> extends AbstractAftertouchCommand<S, C>
{
    private AbstractPlayView<S, C> view;


    /**
     * Constructor.
     *
     * @param view The play view
     * @param model The model
     * @param surface The surface
     */
    public AftertouchAbstractPlayViewCommand (final AbstractPlayView<S, C> view, final Model model, final S surface)
    {
        super (model, surface);
        this.view = view;
    }


    /** {@inheritDoc} */
    @Override
    public void onPolyAftertouch (final int note, final int value)
    {
        final Configuration config = this.surface.getConfiguration ();
        switch (config.getConvertAftertouch ())
        {
            case -3:
                // Filter poly aftertouch
                break;

            case -2:
                // Translate notes of Poly aftertouch to current note mapping
                final int n = this.view.getMidiNoteFromGrid (note);
                if (n != -1)
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
            final List<Integer> keys = this.view.getPressedKeys ();
            for (int i = 0; i < keys.size (); i++) {
                this.onPolyAftertouch (keys.get(i), value);
            }
        }
        else
            this.onPolyAftertouch (0, value);
    }
}
