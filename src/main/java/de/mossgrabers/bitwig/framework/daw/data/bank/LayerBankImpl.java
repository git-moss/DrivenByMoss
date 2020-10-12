// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
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

        if (this.bank != null)
        {
            for (int i = 0; i < this.getPageSize (); i++)
            {
                final DeviceLayer deviceLayer = this.bank.getItemAt (i);
                this.items.add (new LayerImpl (this, this.host, this.valueChanger, deviceLayer, i, this.numSends, this.numDevices));
            }

            // Note: cursorIndex is defined for all banks but currently only works for track banks
            this.bank.cursorIndex ().addValueObserver (index -> {
                for (int i = 0; i < this.getPageSize (); i++)
                {
                    final boolean isSelected = index == i;
                    if (this.items.get (i).isSelected () != isSelected)
                        this.handleBankSelection (i, isSelected);
                }
            });
        }
    }


    private void handleBankSelection (final int index, final boolean isSelected)
    {
        this.notifySelectionObservers (index, isSelected);
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
    public boolean hasZeroLayers ()
    {
        for (int i = 0; i < this.getPageSize (); i++)
        {
            if (this.getItem (i).doesExist ())
                return false;
        }
        return true;
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
}