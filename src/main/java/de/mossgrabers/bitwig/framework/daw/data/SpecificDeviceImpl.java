// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.bitwig.framework.daw.data.bank.DrumPadBankImpl;
import de.mossgrabers.bitwig.framework.daw.data.bank.LayerBankImpl;
import de.mossgrabers.bitwig.framework.daw.data.bank.ParameterBankImpl;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.daw.data.bank.ILayerBank;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.observer.IValueObserver;

import com.bitwig.extension.controller.api.CursorRemoteControlsPage;
import com.bitwig.extension.controller.api.Device;

import java.util.ArrayList;
import java.util.List;


/**
 * Encapsulates the data of a drum machine device.
 *
 * @author Jürgen Moßgraber
 */
public class SpecificDeviceImpl extends DeviceImpl implements ISpecificDevice
{
    private final IParameterBank                parameterBank;
    private final ILayerBank                    layerBank;
    private final IDrumPadBank                  drumPadBank;
    private final List<IValueObserver<Boolean>> hasDrumPadsObservers = new ArrayList<> ();


    /**
     * Constructor.
     *
     * @param host The host
     * @param valueChanger The value changer
     * @param device The device to encapsulate
     * @param numSends The number of sends
     * @param numParamPages The number of parameter pages
     * @param numParams The number of parameters
     * @param numDevicesInBank The number of devices
     * @param numDeviceLayers The number of layers
     * @param numDrumPadLayers The number of drum pad layers
     */
    public SpecificDeviceImpl (final IHost host, final IValueChanger valueChanger, final Device device, final int numSends, final int numParamPages, final int numParams, final int numDevicesInBank, final int numDeviceLayers, final int numDrumPadLayers)
    {
        super (device, -1);

        this.device.isPlugin ().markInterested ();
        this.device.isExpanded ().markInterested ();
        this.device.isRemoteControlsSectionVisible ().markInterested ();
        this.device.isWindowOpen ().markInterested ();
        this.device.isNested ().markInterested ();
        this.device.hasDrumPads ().markInterested ();
        this.device.hasLayers ().markInterested ();
        this.device.hasSlots ().markInterested ();
        this.device.slotNames ().markInterested ();

        final int checkedNumDevices = numDevicesInBank >= 0 ? numDevicesInBank : 8;
        final int checkedNumParamPages = numParamPages >= 0 ? numParamPages : 8;
        final int checkedNumParams = numParams >= 0 ? numParams : 8;
        final int checkedNumDeviceLayers = numDeviceLayers >= 0 ? numDeviceLayers : 8;
        final int checkedNumDrumPadLayers = numDrumPadLayers >= 0 ? numDrumPadLayers : 16;

        if (checkedNumParams > 0)
        {
            final CursorRemoteControlsPage remoteControlsPage = this.device.createCursorRemoteControlsPage (checkedNumParams);
            this.parameterBank = new ParameterBankImpl (host, valueChanger, remoteControlsPage, checkedNumParamPages, checkedNumParams);
        }
        else
            this.parameterBank = null;

        // Monitor the layers of a container device (if any)
        this.layerBank = new LayerBankImpl (host, valueChanger, checkedNumDeviceLayers > 0 ? this.device.createLayerBank (checkedNumDeviceLayers) : null, this.device.createCursorLayer (), checkedNumDeviceLayers, numSends, checkedNumDevices);

        // Monitor the drum pad layers of a container device (if any)
        this.drumPadBank = new DrumPadBankImpl (host, valueChanger, checkedNumDrumPadLayers > 0 ? this.device.createDrumPadBank (checkedNumDrumPadLayers) : null, checkedNumDrumPadLayers, numSends, checkedNumDevices);
        this.drumPadBank.setIndication (false);

        this.device.hasDrumPads ().addValueObserver (this::callbackHasDrumPads);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        super.enableObservers (enable);

        Util.setIsSubscribed (this.device.isPlugin (), enable);
        Util.setIsSubscribed (this.device.isExpanded (), enable);
        Util.setIsSubscribed (this.device.isRemoteControlsSectionVisible (), enable);
        Util.setIsSubscribed (this.device.isWindowOpen (), enable);
        Util.setIsSubscribed (this.device.isNested (), enable);
        Util.setIsSubscribed (this.device.hasDrumPads (), enable);
        Util.setIsSubscribed (this.device.hasLayers (), enable);
        Util.setIsSubscribed (this.device.hasSlots (), enable);
        Util.setIsSubscribed (this.device.slotNames (), enable);

        if (this.parameterBank != null)
            this.parameterBank.enableObservers (enable);
        this.layerBank.enableObservers (enable);
        this.drumPadBank.enableObservers (enable);
    }


    /** {@inheritDoc} */
    @Override
    public String getID ()
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPlugin ()
    {
        return this.device.isPlugin ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isExpanded ()
    {
        return this.device.isExpanded ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleExpanded ()
    {
        this.device.isExpanded ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isParameterPageSectionVisible ()
    {
        return this.device.isRemoteControlsSectionVisible ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleParameterPageSectionVisible ()
    {
        this.device.isRemoteControlsSectionVisible ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isWindowOpen ()
    {
        return this.device.isWindowOpen ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleWindowOpen ()
    {
        this.device.isWindowOpen ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isNested ()
    {
        return this.device.isNested ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasDrumPads ()
    {
        return this.device.hasDrumPads ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void addHasDrumPadsObserver (final IValueObserver<Boolean> observer)
    {
        this.hasDrumPadsObservers.add (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void removeHasDrumPadsObserver (final IValueObserver<Boolean> observer)
    {
        this.hasDrumPadsObservers.remove (observer);
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasLayers ()
    {
        return this.device.hasLayers ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasSlots ()
    {
        return this.device.hasSlots ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public IParameterBank getParameterBank ()
    {
        return this.parameterBank;
    }


    /** {@inheritDoc} */
    @Override
    public ILayerBank getLayerBank ()
    {
        return this.layerBank;
    }


    /** {@inheritDoc} */
    @Override
    public IDrumPadBank getDrumPadBank ()
    {
        return this.drumPadBank;
    }


    private void callbackHasDrumPads (final boolean hasDrumPads)
    {
        final Boolean v = Boolean.valueOf (hasDrumPads);
        this.hasDrumPadsObservers.forEach (observer -> observer.update (v));
    }
}
