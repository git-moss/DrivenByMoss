// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
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

import java.util.Optional;


/**
 * Encapsulates the data of a drum pad bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumPadBankImpl extends AbstractChannelBankImpl<DrumPadBank, ILayer> implements IDrumPadBank
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

        if (this.bank.isEmpty ())
            return;

        final DrumPadBank drumPadBank = this.bank.get ();
        for (int i = 0; i < this.getPageSize (); i++)
        {
            final DrumPad deviceLayer = drumPadBank.getItemAt (i);
            final DrumPadImpl drumPadImpl = new DrumPadImpl (this, this.host, this.valueChanger, deviceLayer, i, this.numSends, this.numDevices);
            this.items.add (drumPadImpl);

            final int index = i;
            drumPadImpl.getDeviceChain ().addIsSelectedInEditorObserver (isSelected -> this.notifySelectionObservers (index, isSelected));
        }

        drumPadBank.hasSoloedPads ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        super.enableObservers (enable);

        for (int i = 0; i < this.getPageSize (); i++)
            this.getItem (i).enableObservers (enable);

        if (this.bank.isPresent ())
        {
            Util.setIsSubscribed (this.bank.get ().hasMutedPads (), enable);
            Util.setIsSubscribed (this.bank.get ().hasSoloedPads (), enable);
        }
    }


    /** {@inheritDoc} */
    @Override
    public IDrumPad getItem (final int index)
    {
        return (IDrumPad) super.getItem (index);
    }


    /** {@inheritDoc} */
    @Override
    public String getSelectedChannelColorEntry ()
    {
        final Optional<ILayer> sel = this.getSelectedItem ();
        if (sel.isEmpty ())
            return DAWColor.COLOR_OFF.name ();
        return DAWColor.getColorID (sel.get ().getColor ());
    }


    /** {@inheritDoc} */
    @Override
    public void clearMute ()
    {
        if (this.bank.isPresent ())
            this.bank.get ().clearMutedPads ();
    }


    /** {@inheritDoc} */
    @Override
    public void clearSolo ()
    {
        if (this.bank.isPresent ())
            this.bank.get ().clearSoloedPads ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasSoloedPads ()
    {
        return this.bank.isPresent () && this.bank.get ().hasSoloedPads ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasMutedPads ()
    {
        return this.bank.isPresent () && this.bank.get ().hasMutedPads ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setIndication (final boolean enable)
    {
        if (this.bank.isPresent ())
            this.bank.get ().setIndication (enable);
    }


    /** {@inheritDoc} */
    @Override
    public boolean canEditSend (final int sendIndex)
    {
        return this.getItem (0).getSendBank ().getItem (sendIndex).doesExist ();
    }


    /** {@inheritDoc} */
    @Override
    public String getEditSendName (final int sendIndex)
    {
        return this.getItem (0).getSendBank ().getItem (sendIndex).getName ();
    }
}