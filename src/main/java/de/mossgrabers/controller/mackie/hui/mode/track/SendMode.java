// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.hui.mode.track;

import de.mossgrabers.controller.mackie.hui.controller.HUIControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;

import java.util.Optional;


/**
 * Mode for editing a Send volumes.
 *
 * @author Jürgen Moßgraber
 */
public class SendMode extends AbstractTrackMode
{
    private static final String [] SEND_NAMES       =
    {
        "Send A",
        "Send B",
        "Send C",
        "Send D",
        "Send E",
        "Send F"
    };

    private static final String [] SEND_NAMES_SHORT =
    {
        "SndA",
        "SndB",
        "SndC",
        "SndD",
        "SndE",
        "SndF"
    };

    private final int              sendIndex;


    /**
     * Constructor.
     *
     * @param sendIndex The send index
     * @param surface The control surface
     * @param model The model
     */
    public SendMode (final int sendIndex, final HUIControlSurface surface, final IModel model)
    {
        super (SEND_NAMES[sendIndex], surface, model);
        this.sendIndex = sendIndex;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final Optional<ITrack> track = this.getTrack (index);
        if (track.isPresent ())
            track.get ().getSendBank ().getItem (this.sendIndex).changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        if (this.surface.getConfiguration ().hasDisplay1 ())
            this.drawTrackHeader ().setCell (0, 8, SEND_NAMES_SHORT[this.sendIndex]).done (0);
    }


    /** {@inheritDoc} */
    @Override
    public void updateKnobLEDs ()
    {
        final int upperBound = this.model.getValueChanger ().getUpperBound ();
        for (int i = 0; i < 8; i++)
        {
            final Optional<ITrack> track = this.getTrack (i);
            this.surface.setKnobLED (i, HUIControlSurface.KNOB_LED_MODE_WRAP, track.isPresent () ? track.get ().getSendBank ().getItem (this.sendIndex).getValue () : 0, upperBound);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void resetParameter (final int index)
    {
        final Optional<ITrack> track = this.getTrack (index);
        if (track.isPresent ())
            track.get ().getSendBank ().getItem (this.sendIndex).resetValue ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        final Optional<ITrack> selectedTrack = this.model.getTrackBank ().getSelectedItem ();
        String name = "-";
        if (selectedTrack.isPresent ())
        {
            final ISend send = selectedTrack.get ().getSendBank ().getItem (this.sendIndex);
            if (send.doesExist ())
                name = send.getName ();
        }
        return super.getName () + ": " + name;
    }
}