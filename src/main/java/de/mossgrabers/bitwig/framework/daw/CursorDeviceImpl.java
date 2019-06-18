// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.daw.data.DeviceImpl;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IDeviceBank;
import de.mossgrabers.framework.daw.IDrumPadBank;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ILayerBank;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.IParameterPageBank;

import com.bitwig.extension.controller.api.CursorDeviceLayer;
import com.bitwig.extension.controller.api.CursorRemoteControlsPage;
import com.bitwig.extension.controller.api.DeviceBank;
import com.bitwig.extension.controller.api.PinnableCursorDevice;

import java.util.HashMap;
import java.util.Map;


/**
 * Proxy to the Bitwig Cursor device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CursorDeviceImpl extends DeviceImpl implements ICursorDevice
{
    private final PinnableCursorDevice cursorDevice;
    private final CursorDeviceLayer    cursorDeviceLayer;

    private String []                  directParameterIds;
    private Map<String, String>        directParameterNames = new HashMap<> ();

    private final IDeviceBank          deviceBank;
    private final IParameterPageBank   parameterPageBank;
    private final IParameterBank       parameterBank;
    private final ILayerBank           layerBank;
    private final IDrumPadBank         drumPadBank;


    /**
     * Constructor.
     *
     * @param host The host
     * @param valueChanger The value changer
     * @param cursorDevice The cursor device
     * @param numSends The number of sends
     * @param numParams The number of parameters
     * @param numDevicesInBank The number of devices
     * @param numDeviceLayers The number of layers
     * @param numDrumPadLayers The number of drum pad layers
     */
    public CursorDeviceImpl (final IHost host, final IValueChanger valueChanger, final PinnableCursorDevice cursorDevice, final int numSends, final int numParams, final int numDevicesInBank, final int numDeviceLayers, final int numDrumPadLayers)
    {
        super (cursorDevice, -1);

        this.cursorDevice = cursorDevice;

        final int checkedNumParams = numParams >= 0 ? numParams : 8;
        final int checkedNumDevices = numDevicesInBank >= 0 ? numDevicesInBank : 8;
        final int checkedNumDeviceLayers = numDeviceLayers >= 0 ? numDeviceLayers : 8;
        final int checkedNumDrumPadLayers = numDrumPadLayers >= 0 ? numDrumPadLayers : 16;

        this.cursorDevice.isEnabled ().markInterested ();
        this.cursorDevice.isPlugin ().markInterested ();
        this.cursorDevice.hasPrevious ().markInterested ();
        this.cursorDevice.hasNext ().markInterested ();
        this.cursorDevice.isExpanded ().markInterested ();
        this.cursorDevice.isRemoteControlsSectionVisible ().markInterested ();
        this.cursorDevice.isWindowOpen ().markInterested ();
        this.cursorDevice.isNested ().markInterested ();
        this.cursorDevice.hasDrumPads ().markInterested ();
        this.cursorDevice.hasLayers ().markInterested ();
        this.cursorDevice.hasSlots ().markInterested ();
        this.cursorDevice.isPinned ().markInterested ();

        this.cursorDevice.addDirectParameterIdObserver (value -> this.directParameterIds = value);
        this.cursorDevice.addDirectParameterNameObserver (1024, (final String id, final String name) -> this.directParameterNames.put (id, name));

        this.cursorDeviceLayer = this.cursorDevice.createCursorLayer ();
        this.cursorDeviceLayer.hasPrevious ().markInterested ();
        this.cursorDeviceLayer.hasNext ().markInterested ();

        if (checkedNumParams > 0)
        {
            final CursorRemoteControlsPage remoteControlsPage = this.cursorDevice.createCursorRemoteControlsPage (checkedNumParams);
            // We use the same number of page entries (numParams) for the page bank, add a specific
            // parameter if there is one controller who wants that differently
            this.parameterPageBank = new ParameterPageBankImpl (remoteControlsPage, numParams);
            this.parameterBank = new ParameterBankImpl (host, valueChanger, this.parameterPageBank, remoteControlsPage, numParams);
        }
        else
        {
            this.parameterPageBank = null;
            this.parameterBank = null;
        }

        // Monitor the sibling devices of the cursor device
        final DeviceBank siblings = checkedNumDevices > 0 ? this.cursorDevice.createSiblingsDeviceBank (checkedNumDevices) : null;
        this.deviceBank = new DeviceBankImpl (host, valueChanger, this, siblings, checkedNumDevices);

        // Monitor the layers of a container device (if any)
        this.layerBank = new LayerBankImpl (host, valueChanger, checkedNumDeviceLayers > 0 ? this.cursorDevice.createLayerBank (checkedNumDeviceLayers) : null, this.cursorDeviceLayer, numDeviceLayers, numSends, checkedNumDevices);

        // Monitor the drum pad layers of a container device (if any)
        this.drumPadBank = new DrumPadBankImpl (host, valueChanger, checkedNumDrumPadLayers > 0 ? this.cursorDevice.createDrumPadBank (checkedNumDrumPadLayers) : null, checkedNumDrumPadLayers, numSends, checkedNumDevices);
        this.drumPadBank.setIndication (false);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        super.enableObservers (enable);

        this.cursorDevice.isEnabled ().setIsSubscribed (enable);
        this.cursorDevice.isPlugin ().setIsSubscribed (enable);
        this.cursorDevice.hasPrevious ().setIsSubscribed (enable);
        this.cursorDevice.hasNext ().setIsSubscribed (enable);
        this.cursorDevice.isExpanded ().setIsSubscribed (enable);
        this.cursorDevice.isRemoteControlsSectionVisible ().setIsSubscribed (enable);
        this.cursorDevice.isWindowOpen ().setIsSubscribed (enable);
        this.cursorDevice.isNested ().setIsSubscribed (enable);
        this.cursorDevice.hasDrumPads ().setIsSubscribed (enable);
        this.cursorDevice.hasLayers ().setIsSubscribed (enable);
        this.cursorDevice.hasSlots ().setIsSubscribed (enable);
        this.cursorDevice.isPinned ().setIsSubscribed (enable);

        if (this.parameterBank != null)
        {
            this.parameterBank.enableObservers (enable);
            this.parameterPageBank.enableObservers (enable);
        }
        this.deviceBank.enableObservers (enable);

        this.cursorDeviceLayer.hasPrevious ().setIsSubscribed (enable);
        this.cursorDeviceLayer.hasNext ().setIsSubscribed (enable);

        this.layerBank.enableObservers (enable);
        this.drumPadBank.enableObservers (enable);
    }


    /** {@inheritDoc} */
    @Override
    public String getID ()
    {
        if (this.directParameterIds.length == 0)
            return "";
        // Get the name of the first parameter. Currently, only works for Komplete Kontrol plugin
        final String id = this.directParameterNames.get (this.directParameterIds[0]);
        return id == null ? "" : id;
    }


    /** {@inheritDoc} */
    @Override
    public int getIndex ()
    {
        return this.getPosition () % this.deviceBank.getPageSize ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isEnabled ()
    {
        return this.cursorDevice.isEnabled ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPlugin ()
    {
        return this.cursorDevice.isPlugin ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isExpanded ()
    {
        return this.cursorDevice.isExpanded ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isParameterPageSectionVisible ()
    {
        return this.cursorDevice.isRemoteControlsSectionVisible ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isWindowOpen ()
    {
        return this.cursorDevice.isWindowOpen ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isNested ()
    {
        return this.cursorDevice.isNested ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasDrumPads ()
    {
        return this.cursorDevice.hasDrumPads ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasLayers ()
    {
        return this.cursorDevice.hasLayers ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasSlots ()
    {
        return this.cursorDevice.hasSlots ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPinned ()
    {
        return this.cursorDevice.isPinned ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canSelectPreviousFX ()
    {
        return this.cursorDevice.hasPrevious ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canSelectNextFX ()
    {
        return this.cursorDevice.hasNext ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPrevious ()
    {
        final boolean moveBank = this.getIndex () == 0;
        this.cursorDevice.selectPrevious ();
        if (moveBank)
            this.deviceBank.selectPreviousPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNext ()
    {
        final boolean moveBank = this.getIndex () == this.getDeviceBank ().getPageSize () - 1;
        this.cursorDevice.selectNext ();
        if (moveBank)
            this.deviceBank.selectNextPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void togglePinned ()
    {
        this.cursorDevice.isPinned ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleEnabledState ()
    {
        this.cursorDevice.isEnabled ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleWindowOpen ()
    {
        this.cursorDevice.isWindowOpen ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleExpanded ()
    {
        this.cursorDevice.isExpanded ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleParameterPageSectionVisible ()
    {
        this.cursorDevice.isRemoteControlsSectionVisible ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public IDeviceBank getDeviceBank ()
    {
        return this.deviceBank;
    }


    /** {@inheritDoc} */
    @Override
    public IParameterPageBank getParameterPageBank ()
    {
        return this.parameterPageBank;
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


    /** {@inheritDoc} */
    @Override
    public IChannelBank<?> getLayerOrDrumPadBank ()
    {
        return this.hasDrumPads () ? this.drumPadBank : this.layerBank;
    }


    /** {@inheritDoc} */
    @Override
    public void browseToReplaceDevice ()
    {
        this.cursorDevice.replaceDeviceInsertionPoint ().browse ();
    }


    /** {@inheritDoc} */
    @Override
    public void browseToInsertBeforeDevice ()
    {
        this.cursorDevice.beforeDeviceInsertionPoint ().browse ();
    }


    /** {@inheritDoc} */
    @Override
    public void browseToInsertAfterDevice ()
    {
        this.cursorDevice.afterDeviceInsertionPoint ().browse ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectParent ()
    {
        this.cursorDevice.selectParent ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectChannel ()
    {
        this.cursorDevice.channel ().selectInEditor ();
    }
}