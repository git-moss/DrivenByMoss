// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data.bank;

import de.mossgrabers.bitwig.framework.daw.data.SendImpl;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.bank.ISendBank;

import com.bitwig.extension.controller.api.SendBank;


/**
 * Encapsulates the data of a send bank.
 *
 * @author Jürgen Moßgraber
 */
public class SendBankImpl extends AbstractItemBankImpl<SendBank, ISend> implements ISendBank
{
    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param sendBank The send bank
     * @param numSends The number of sends in the page of the bank
     */
    public SendBankImpl (final IHost host, final IValueChanger valueChanger, final SendBank sendBank, final int numSends)
    {
        super (host, valueChanger, sendBank, numSends);

        if (this.bank.isEmpty ())
            return;

        final SendBank sb = this.bank.get ();
        for (int i = 0; i < this.getPageSize (); i++)
            this.items.add (new SendImpl (this, this.valueChanger, sb.getItemAt (i), i));
    }


    /** {@inheritDoc} */
    @Override
    public ISend getItem (final int index)
    {
        return this.items.get (index);
    }
}