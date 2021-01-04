// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.hui.mode.track;

import de.mossgrabers.controller.hui.controller.HUIControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;


/**
 * Mode for editing a Send volumes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
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

    private int                    sendIndex;


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
        this.model.getCurrentTrackBank ().getItem (index).getSendBank ().getItem (this.sendIndex).changeValue (value);
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
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final int upperBound = this.model.getValueChanger ().getUpperBound ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);
            this.surface.setKnobLED (i, HUIControlSurface.KNOB_LED_MODE_WRAP, t.getSendBank ().getItem (this.sendIndex).getValue (), upperBound);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void resetParameter (final int index)
    {
        this.model.getCurrentTrackBank ().getItem (index).getSendBank ().getItem (this.sendIndex).resetValue ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        final ITrack selectedTrack = this.model.getTrackBank ().getSelectedItem ();
        String name = "-";
        if (selectedTrack != null)
        {
            final ISend send = selectedTrack.getSendBank ().getItem (this.sendIndex);
            if (send.doesExist ())
                name = send.getName ();
        }
        return super.getName () + ": " + name;
    }
}