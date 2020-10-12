// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data.bank;

import de.mossgrabers.bitwig.framework.daw.data.DrumPadImpl;
import de.mossgrabers.bitwig.framework.daw.data.Util;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IDrumPad;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;

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

        for (int i = 0; i < this.getPageSize (); i++)
        {
            final DrumPad deviceLayer = this.bank.getItemAt (i);
            final DrumPadImpl drumPadImpl = new DrumPadImpl (this, this.host, this.valueChanger, deviceLayer, i, this.numSends, this.numDevices);
            this.items.add (drumPadImpl);

            final int index = i;
            drumPadImpl.getDeviceChain ().addIsSelectedInEditorObserver (isSelected -> this.notifySelectionObservers (index, isSelected));
        }

        if (this.bank != null)
            this.bank.hasSoloedPads ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        super.enableObservers (enable);

        for (int i = 0; i < this.getPageSize (); i++)
            this.getItem (i).enableObservers (enable);

        Util.setIsSubscribed (this.bank.hasSoloedPads (), enable);
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


    /** {@inheritDoc} */
    @Override
    public boolean hasSoloedPads ()
    {
        return this.bank.hasSoloedPads ().get ();
    }
}