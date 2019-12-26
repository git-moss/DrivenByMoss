// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.daw.data.DrumPadImpl;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IDrumPadBank;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IDrumPad;
import de.mossgrabers.framework.daw.data.ILayer;

import com.bitwig.extension.controller.api.DrumPad;
import com.bitwig.extension.controller.api.DrumPadBank;


/**
 * Encapsulates the data of a drumpad bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumPadBankImpl extends AbstractChannelBankImpl<DrumPadBank, IDrumPad> implements IDrumPadBank
{
    private int numDevices;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param layerBank The layer bank
     * @param numLayers The number of layers in the page of the bank
     * @param numSends The number of sends
     * @param numDevices The number of devices
     */
    public DrumPadBankImpl (final IHost host, final IValueChanger valueChanger, final DrumPadBank layerBank, final int numLayers, final int numSends, final int numDevices)
    {
        super (host, valueChanger, layerBank, numLayers, 0, numSends);

        this.numDevices = numDevices;

        this.initItems ();
    }


    /** {@inheritDoc} */
    @Override
    protected void initItems ()
    {
        for (int i = 0; i < this.pageSize; i++)
        {
            final DrumPad deviceLayer = this.bank.getItemAt (i);
            this.items.add (new DrumPadImpl (this.host, this.valueChanger, deviceLayer, i, this.numSends, this.numDevices));
        }
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        super.enableObservers (enable);

        for (int i = 0; i < this.getPageSize (); i++)
            this.getItem (i).enableObservers (enable);
    }


    /** {@inheritDoc} */
    @Override
    public void setIndication (final boolean shouldIndicate)
    {
        if (this.bank != null)
            this.bank.setIndication (shouldIndicate);
    }


    /** {@inheritDoc} */
    @Override
    public String getSelectedChannelColorEntry ()
    {
        final ILayer sel = this.getSelectedItem ();
        if (sel == null)
            return DAWColor.COLOR_OFF.name ();
        return DAWColor.getColorIndex (sel.getColor ());
    }


    /** {@inheritDoc} */
    @Override
    public void clearMute ()
    {
        this.bank.clearMutedPads ();
    }


    /** {@inheritDoc} */
    @Override
    public void clearSolo ()
    {
        this.bank.clearSoloedPads ();
    }
}