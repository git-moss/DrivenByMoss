// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.aftertouch;

import de.mossgrabers.framework.command.core.AbstractAftertouchCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.midi.MidiConstants;
import de.mossgrabers.framework.featuregroup.IView;


/**
 * Command to handle the aftertouch on a view.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class AftertouchViewCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractAftertouchCommand<S, C>
{
    protected IView view;


    /**
     * Constructor.
     *
     * @param view The play view
     * @param model The model
     * @param surface The surface
     */
    public AftertouchViewCommand (final IView view, final IModel model, final S surface)
    {
        super (model, surface);
        this.view = view;
    }


    /** {@inheritDoc} */
    @Override
    public void onPolyAftertouch (final int note, final int value)
    {
        final Configuration config = this.surface.getConfiguration ();
        final int convertAftertouch = config.getConvertAftertouch ();
        switch (convertAftertouch)
        {
            // Filter poly aftertouch
            case AbstractConfiguration.AFTERTOUCH_CONVERT_OFF:
                break;

            // Translate notes of Poly aftertouch to current note mapping and only allow aftertouch
            // for pads with notes
            case AbstractConfiguration.AFTERTOUCH_CONVERT_POLY:
                final int n = this.view.getKeyManager ().getMidiNoteFromGrid (note);
                if (n == -1)
                    return;
                this.surface.sendMidiEvent (MidiConstants.CMD_POLY_AFTERTOUCH, n, value);
                break;

            // Convert to Channel Aftertouch
            case AbstractConfiguration.AFTERTOUCH_CONVERT_CHANNEL:
                this.surface.sendMidiEvent (MidiConstants.CMD_CHANNEL_AFTERTOUCH, value, 0);
                break;

            default:
                // MIDI CC
                this.surface.sendMidiEvent (MidiConstants.CMD_CC, convertAftertouch, value);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onChannelAftertouch (final int value)
    {
        final int convertAftertouch = this.surface.getConfiguration ().getConvertAftertouch ();
        if (convertAftertouch == AbstractConfiguration.AFTERTOUCH_CONVERT_OFF)
            return;

        if (convertAftertouch == AbstractConfiguration.AFTERTOUCH_CONVERT_POLY)
        {
            for (final Integer key: this.view.getKeyManager ().getPressedKeys ())
                this.onPolyAftertouch (key.intValue (), value);
        }
        else
            this.onPolyAftertouch (-1, value);
    }
}
