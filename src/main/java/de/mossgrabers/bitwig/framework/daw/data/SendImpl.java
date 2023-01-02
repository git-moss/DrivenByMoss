// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.bank.ISendBank;

import com.bitwig.extension.controller.api.Send;
import com.bitwig.extension.controller.api.SettableColorValue;


/**
 * Encapsulates the data of a send.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendImpl extends ParameterImpl implements ISend
{
    private final ISendBank sendBank;
    private final Send      send;


    /**
     * Constructor.
     *
     * @param sendBank The bank in which this send is located, for getting the position
     * @param valueChanger The value changer
     * @param send The send
     * @param index The index of the send
     */
    public SendImpl (final ISendBank sendBank, final IValueChanger valueChanger, final Send send, final int index)
    {
        super (valueChanger, send, index);

        this.sendBank = sendBank;

        this.send = send;

        this.send.sendChannelColor ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        super.enableObservers (enable);

        Util.setIsSubscribed (this.send.sendChannelColor (), enable);
    }


    /** {@inheritDoc} */
    @Override
    public int getPosition ()
    {
        return this.sendBank.getScrollPosition () + this.getIndex ();
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        // Sends cannot be selected but should also not crash
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColor ()
    {
        final SettableColorValue color = this.send.sendChannelColor ();
        return new ColorEx (color.red (), color.green (), color.blue ());
    }
}
