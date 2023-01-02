// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data.bank;

import de.mossgrabers.bitwig.framework.daw.data.LayerImpl;
import de.mossgrabers.bitwig.framework.daw.data.Util;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.bank.ILayerBank;

import com.bitwig.extension.controller.api.CursorDeviceLayer;
import com.bitwig.extension.controller.api.DeviceLayer;
import com.bitwig.extension.controller.api.DeviceLayerBank;

import java.util.Optional;


/**
 * Encapsulates the data of a layer bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LayerBankImpl extends AbstractChannelBankImpl<DeviceLayerBank, ILayer> implements ILayerBank
{
    private final CursorDeviceLayer cursorDeviceLayer;
    private int                     numDevices;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param layerBank The layer bank
     * @param cursorDeviceLayer The cursor device layer
     * @param numLayers The number of layers in the page of the bank
     * @param numSends The number of sends
     * @param numDevices The number of devices
     */
    public LayerBankImpl (final IHost host, final IValueChanger valueChanger, final DeviceLayerBank layerBank, final CursorDeviceLayer cursorDeviceLayer, final int numLayers, final int numSends, final int numDevices)
    {
        super (host, valueChanger, layerBank, numLayers, 0, numSends);

        this.cursorDeviceLayer = cursorDeviceLayer;
        this.cursorDeviceLayer.hasPrevious ().markInterested ();
        this.cursorDeviceLayer.hasNext ().markInterested ();

        this.numDevices = numDevices;

        if (this.bank.isEmpty ())
            return;

        final DeviceLayerBank deviceLayerBank = this.bank.get ();
        for (int i = 0; i < this.getPageSize (); i++)
        {
            final DeviceLayer deviceLayer = deviceLayerBank.getItemAt (i);
            final LayerImpl layerImpl = new LayerImpl (this, this.host, this.valueChanger, deviceLayer, i, this.numSends, this.numDevices);
            this.items.add (layerImpl);

            final int index = i;
            layerImpl.getDeviceChain ().addIsSelectedInEditorObserver (isSelected -> this.notifySelectionObservers (index, isSelected));
        }
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        super.enableObservers (enable);

        Util.setIsSubscribed (this.cursorDeviceLayer.hasPrevious (), enable);
        Util.setIsSubscribed (this.cursorDeviceLayer.hasNext (), enable);

        for (int i = 0; i < this.getPageSize (); i++)
            this.getItem (i).enableObservers (enable);
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
    public void setIndication (final boolean enable)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageBackwards ()
    {
        return this.cursorDeviceLayer.hasPrevious ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageForwards ()
    {
        return this.cursorDeviceLayer.hasNext ().get ();
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