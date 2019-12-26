// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.daw.data.SendImpl;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.data.ISend;

import com.bitwig.extension.controller.api.SendBank;


/**
 * Encapsulates the data of a send bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendBankImpl extends AbstractBankImpl<SendBank, ISend> implements ISendBank
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
        this.initItems ();
    }


    /** {@inheritDoc} */
    @Override
    protected void initItems ()
    {
        for (int i = 0; i < this.pageSize; i++)
            this.items.add (new SendImpl (this, this.valueChanger, this.bank.getItemAt (i), i));
    }
}