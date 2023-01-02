// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.bitwig.framework.daw.data.bank.DrumPadBankImpl;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IDrumPad;

import com.bitwig.extension.controller.api.DrumPad;


/**
 * The data of a channel.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumPadImpl extends LayerImpl implements IDrumPad
{
    private final DrumPad drumPad;


    /**
     * Constructor.
     *
     * @param drumPadBankImpl The bank to which this drum pad belongs
     * @param host The DAW host
     * @param valueChanger The valueChanger
     * @param drumPad The drum pad
     * @param index The index of the channel in the page
     * @param numSends The number of sends of a bank
     * @param numDevices The number of devices of a bank
     */
    public DrumPadImpl (final DrumPadBankImpl drumPadBankImpl, final IHost host, final IValueChanger valueChanger, final DrumPad drumPad, final int index, final int numSends, final int numDevices)
    {
        super (drumPadBankImpl, host, valueChanger, drumPad, index, numSends, numDevices);

        this.drumPad = drumPad;
    }


    /**
     * Get the Bitwig drum pad.
     *
     * @return The drum pad
     */
    public DrumPad getDrumPad ()
    {
        return this.drumPad;
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        this.deviceChain.selectInEditor ();
    }
}
