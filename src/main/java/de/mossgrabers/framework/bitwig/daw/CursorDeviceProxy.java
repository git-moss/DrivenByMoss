// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.bitwig.daw;

import de.mossgrabers.framework.bitwig.daw.data.ChannelImpl;
import de.mossgrabers.framework.bitwig.daw.data.ParameterImpl;
import de.mossgrabers.framework.controller.ValueChanger;
import de.mossgrabers.framework.daw.BitwigColors;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.IParameter;

import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.CursorDeviceLayer;
import com.bitwig.extension.controller.api.CursorRemoteControlsPage;
import com.bitwig.extension.controller.api.Device;
import com.bitwig.extension.controller.api.DeviceBank;
import com.bitwig.extension.controller.api.DeviceLayer;
import com.bitwig.extension.controller.api.DeviceLayerBank;
import com.bitwig.extension.controller.api.DrumPadBank;
import com.bitwig.extension.controller.api.PinnableCursorDevice;
import com.bitwig.extension.controller.api.RemoteControl;
import com.bitwig.extension.controller.api.SettableIntegerValue;


/**
 * Proxy to the Bitwig Cursor device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CursorDeviceProxy implements ICursorDevice
{
    private IHost                    host;
    private PinnableCursorDevice     cursorDevice;
    private DeviceBank               siblings;
    private CursorRemoteControlsPage remoteControls;
    private CursorDeviceLayer        cursorDeviceLayer;
    private ValueChanger             valueChanger;

    private int                      numParams;
    private int                      numDevicesInBank;
    private int                      numDeviceLayers;
    private int                      numDrumPadLayers;

    private String []                parameterPageNames = new String [0];
    private IParameter []            fxparams;
    private DeviceLayerBank          layerBank;
    private IChannel []              deviceLayers;
    private IChannel []              drumPadLayers;
    private DeviceBank []            deviceBanks;
    private DrumPadBank              drumPadBank;
    private DeviceBank []            drumPadBanks;


    /**
     * Constructor.
     *
     * @param host The host
     * @param cursorDevice The cursor device
     * @param valueChanger The value changer
     * @param numSends The number of sends
     * @param numParams The number of parameters
     * @param numDevicesInBank The number of devices
     * @param numDeviceLayers The number of layers
     * @param numDrumPadLayers The number of drum pad layers
     */
    public CursorDeviceProxy (final IHost host, final PinnableCursorDevice cursorDevice, final ValueChanger valueChanger, final int numSends, final int numParams, final int numDevicesInBank, final int numDeviceLayers, final int numDrumPadLayers)
    {
        this.host = host;
        this.cursorDevice = cursorDevice;
        this.valueChanger = valueChanger;

        this.numParams = numParams > 0 ? numParams : 8;
        this.numDevicesInBank = numDevicesInBank > 0 ? numDevicesInBank : 8;
        this.numDeviceLayers = numDeviceLayers > 0 ? numDeviceLayers : 8;
        this.numDrumPadLayers = numDrumPadLayers > 0 ? numDrumPadLayers : 16;

        this.cursorDevice.exists ().markInterested ();
        this.cursorDevice.isEnabled ().markInterested ();
        this.cursorDevice.name ().markInterested ();
        this.cursorDevice.isPlugin ().markInterested ();
        this.cursorDevice.position ().markInterested ();
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

        this.remoteControls = this.cursorDevice.createCursorRemoteControlsPage (this.numParams);
        this.remoteControls.hasPrevious ().markInterested ();
        this.remoteControls.hasNext ().markInterested ();
        this.remoteControls.selectedPageIndex ().markInterested ();
        this.remoteControls.pageNames ().addValueObserver (this::handlePageNames);

        this.fxparams = new IParameter [this.numParams];
        for (int i = 0; i < this.numParams; i++)
        {
            final RemoteControl p = this.getParameter (i);
            this.fxparams[i] = new ParameterImpl (p, valueChanger.getUpperBound ());
        }

        // Monitor the sibling devices of the cursor device
        this.siblings = this.cursorDevice.createSiblingsDeviceBank (this.numDevicesInBank);
        for (int i = 0; i < this.numDevicesInBank; i++)
        {
            final Device device = this.siblings.getDevice (i);
            device.exists ().markInterested ();
            device.name ().markInterested ();
        }

        this.cursorDeviceLayer = this.cursorDevice.createCursorLayer ();
        this.cursorDeviceLayer.hasPrevious ().markInterested ();
        this.cursorDeviceLayer.hasNext ().markInterested ();

        // Monitor the layers of a container device (if any)
        this.layerBank = this.cursorDevice.createLayerBank (this.numDeviceLayers);
        this.deviceLayers = new IChannel [this.numDeviceLayers];
        this.deviceBanks = new DeviceBank [this.numDeviceLayers];
        for (int i = 0; i < this.numDeviceLayers; i++)
        {
            final Channel layer = this.layerBank.getChannel (i);
            this.deviceLayers[i] = new ChannelImpl (layer, valueChanger, i, numSends);
            this.deviceBanks[i] = layer.createDeviceBank (this.numDevicesInBank);

            final int index = i;
            layer.addIsSelectedInEditorObserver (this.deviceLayers[index]::setSelected);
        }

        // Monitor the drum pad layers of a container device (if any)
        this.drumPadBank = this.cursorDevice.createDrumPadBank (this.numDrumPadLayers);
        this.drumPadLayers = new IChannel [this.numDrumPadLayers];
        this.drumPadBanks = new DeviceBank [this.numDrumPadLayers];
        for (int i = 0; i < this.numDrumPadLayers; i++)
        {
            final Channel layer = this.drumPadBank.getChannel (i);
            this.drumPadLayers[i] = new ChannelImpl (layer, valueChanger, i, numSends);
            this.drumPadBanks[i] = layer.createDeviceBank (this.numDevicesInBank);

            final int index = i;
            layer.addIsSelectedInEditorObserver (this.drumPadLayers[index]::setSelected);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        this.cursorDevice.exists ().setIsSubscribed (enable);
        this.cursorDevice.isEnabled ().setIsSubscribed (enable);
        this.cursorDevice.isPlugin ().setIsSubscribed (enable);
        this.cursorDevice.position ().setIsSubscribed (enable);
        this.cursorDevice.name ().setIsSubscribed (enable);
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

        this.remoteControls.hasPrevious ().setIsSubscribed (enable);
        this.remoteControls.hasNext ().setIsSubscribed (enable);
        this.remoteControls.selectedPageIndex ().setIsSubscribed (enable);
        this.remoteControls.pageNames ().setIsSubscribed (enable);

        for (int i = 0; i < this.numParams; i++)
            this.fxparams[i].enableObservers (enable);

        for (int i = 0; i < this.numDevicesInBank; i++)
        {
            final Device device = this.siblings.getDevice (i);
            device.exists ().setIsSubscribed (enable);
            device.name ().setIsSubscribed (enable);
        }

        this.cursorDeviceLayer.hasPrevious ().setIsSubscribed (enable);
        this.cursorDeviceLayer.hasNext ().setIsSubscribed (enable);

        for (int i = 0; i < this.numDeviceLayers; i++)
            this.deviceLayers[i].enableObservers (enable);
        for (int i = 0; i < this.numDrumPadLayers; i++)
            this.drumPadLayers[i].enableObservers (enable);
    }


    /** {@inheritDoc} */
    @Override
    public void browseToReplaceDevice ()
    {
        this.cursorDevice.browseToReplaceDevice ();
    }


    /** {@inheritDoc} */
    @Override
    public void browseToInsertBeforeDevice ()
    {
        this.cursorDevice.browseToInsertBeforeDevice ();
    }


    /** {@inheritDoc} */
    @Override
    public void browseToInsertAfterDevice ()
    {
        this.cursorDevice.browseToInsertAfterDevice ();
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


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return this.cursorDevice.exists ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isEnabled ()
    {
        return this.cursorDevice.isEnabled ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.cursorDevice.name ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPlugin ()
    {
        return this.cursorDevice.isPlugin ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public int getPositionInChain ()
    {
        return this.cursorDevice.position ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public int getPositionInBank ()
    {
        return this.getPositionInChain () % this.numDevicesInBank;
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
    public void togglePinned ()
    {
        this.cursorDevice.isPinned ().toggle ();
    }


    /**
     * Get a parameter.
     *
     * @param index The index of the parameter
     * @return The parameter
     */
    private RemoteControl getParameter (final int index)
    {
        return this.remoteControls.getParameter (index);
    }


    /** {@inheritDoc} */
    @Override
    public void changeParameter (final int index, final int control)
    {
        this.getParameter (index).inc (Double.valueOf (this.valueChanger.calcKnobSpeed (control)), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void setParameter (final int index, final int value)
    {
        this.getParameter (index).set (Integer.valueOf (value), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void resetParameter (final int index)
    {
        this.getParameter (index).reset ();
    }


    /** {@inheritDoc} */
    @Override
    public void indicateParameter (final int index, final boolean indicate)
    {
        this.getParameter (index).setIndication (indicate);
    }


    /** {@inheritDoc} */
    @Override
    public void touchParameter (final int index, final boolean indicate)
    {
        this.getParameter (index).touch (indicate);
    }


    /** {@inheritDoc} */
    @Override
    public void previousParameterPage ()
    {
        this.remoteControls.selectPreviousPage (true);
    }


    /** {@inheritDoc} */
    @Override
    public void nextParameterPage ()
    {
        this.remoteControls.selectNextPage (true);
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousParameterPage ()
    {
        return this.remoteControls.hasPrevious ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextParameterPage ()
    {
        return this.remoteControls.hasNext ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String [] getParameterPageNames ()
    {
        return this.parameterPageNames;
    }


    /** {@inheritDoc} */
    @Override
    public String getSelectedParameterPageName ()
    {
        final int sel = this.getSelectedParameterPage ();
        return sel >= 0 && sel < this.parameterPageNames.length ? this.parameterPageNames[sel] : "";
    }


    /** {@inheritDoc} */
    @Override
    public int getSelectedParameterPage ()
    {
        return this.remoteControls.selectedPageIndex ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setSelectedParameterPage (final int index)
    {
        this.remoteControls.selectedPageIndex ().set (index);
    }


    /** {@inheritDoc} */
    @Override
    public void setSelectedParameterPageInBank (final int index)
    {
        final SettableIntegerValue pageIndex = this.remoteControls.selectedPageIndex ();
        pageIndex.set (pageIndex.get () / 8 * 8 + index);
    }


    /** {@inheritDoc} */
    @Override
    public void previousParameterPageBank ()
    {
        final SettableIntegerValue index = this.remoteControls.selectedPageIndex ();
        index.set (Math.max (index.get () - 8, 0));
    }


    /** {@inheritDoc} */
    @Override
    public void nextParameterPageBank ()
    {
        final SettableIntegerValue index = this.remoteControls.selectedPageIndex ();
        index.set (Math.min (index.get () + 8, this.parameterPageNames.length - 1));
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
    public void selectPrevious ()
    {
        final boolean moveBank = this.getPositionInBank () == 0;
        this.cursorDevice.selectPrevious ();
        if (moveBank)
            this.selectPreviousBank ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNext ()
    {
        final boolean moveBank = this.getPositionInBank () == 7;
        this.cursorDevice.selectNext ();
        if (moveBank)
            this.selectNextBank ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesSiblingExist (final int index)
    {
        return this.siblings.getDevice (index).exists ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getSiblingDeviceName (final int index)
    {
        return this.siblings.getDevice (index).name ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectSibling (final int index)
    {
        this.siblings.getDevice (index).selectInEditor ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousBank ()
    {
        this.siblings.scrollPageUp ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextBank ()
    {
        this.siblings.scrollPageDown ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasSelectedDevice ()
    {
        return this.cursorDevice.exists ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getFXParam (final int index)
    {
        return this.fxparams[index];
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
    public IChannel getLayerOrDrumPad (final int index)
    {
        return this.hasDrumPads () ? this.getDrumPad (index) : this.getLayer (index);
    }


    /** {@inheritDoc} */
    @Override
    public IChannel getSelectedLayerOrDrumPad ()
    {
        return this.hasDrumPads () ? this.getSelectedDrumPad () : this.getSelectedLayer ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectLayerOrDrumPad (final int index)
    {
        if (this.hasDrumPads ())
            this.selectDrumPad (index);
        else
            this.selectLayer (index);
    }


    /** {@inheritDoc} */
    @Override
    public void previousLayerOrDrumPad ()
    {
        if (this.hasDrumPads ())
            this.previousDrumPad ();
        else
            this.previousLayer ();
    }


    /** {@inheritDoc} */
    @Override
    public void nextLayerOrDrumPad ()
    {
        if (this.hasDrumPads ())
            this.nextDrumPad ();
        else
            this.nextLayer ();
    }


    /** {@inheritDoc} */
    @Override
    public void previousLayerOrDrumPadBank ()
    {
        if (this.hasDrumPads ())
            this.previousDrumPadBank ();
        else
            this.previousLayerBank ();
    }


    /** {@inheritDoc} */
    @Override
    public void nextLayerOrDrumPadBank ()
    {
        if (this.hasDrumPads ())
            this.nextDrumPadBank ();
        else
            this.nextLayerBank ();
    }


    /** {@inheritDoc} */
    @Override
    public void enterLayerOrDrumPad (final int index)
    {
        if (this.hasDrumPads ())
            this.enterDrumPad (index);
        else
            this.enterLayer (index);
    }


    /** {@inheritDoc} */
    @Override
    public void selectFirstDeviceInLayerOrDrumPad (final int index)
    {
        if (this.hasDrumPads ())
            this.selectFirstDeviceInDrumPad (index);
        else
            this.selectFirstDeviceInLayer (index);
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollLayersOrDrumPadsUp ()
    {
        return this.hasDrumPads () ? this.canScrollDrumPadsUp () : this.canScrollLayersUp ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollLayersOrDrumPadsDown ()
    {
        return this.hasDrumPads () ? this.canScrollDrumPadsDown () : this.canScrollLayersDown ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollLayersOrDrumPadsPageUp ()
    {
        if (this.hasDrumPads ())
            this.scrollDrumPadsPageUp ();
        else
            this.scrollLayersPageUp ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollLayersOrDrumPadsPageDown ()
    {
        if (this.hasDrumPads ())
            this.scrollDrumPadsPageDown ();
        else
            this.scrollLayersPageDown ();
    }


    /** {@inheritDoc} */
    @Override
    public void setLayerOrDrumPadColor (final int index, final double red, final double green, final double blue)
    {
        if (this.hasDrumPads ())
            this.setDrumPadColor (index, red, green, blue);
        else
            this.setLayerColor (index, red, green, blue);
    }


    /** {@inheritDoc} */
    @Override
    public String getLayerOrDrumPadColorEntry (final int index)
    {
        return BitwigColors.getColorIndex (this.getLayerOrDrumPad (index).getColor ());
    }


    /** {@inheritDoc} */
    @Override
    public void changeLayerOrDrumPadVolume (final int index, final int control)
    {
        if (this.hasDrumPads ())
            this.changeDrumPadVolume (index, control);
        else
            this.changeLayerVolume (index, control);
    }


    /** {@inheritDoc} */
    @Override
    public void setLayerOrDrumPadVolume (final int index, final int value)
    {
        if (this.hasDrumPads ())
            this.setDrumPadVolume (index, value);
        else
            this.setLayerVolume (index, value);
    }


    /** {@inheritDoc} */
    @Override
    public void resetLayerOrDrumPadVolume (final int index)
    {
        if (this.hasDrumPads ())
            this.resetDrumPadVolume (index);
        else
            this.resetLayerVolume (index);
    }


    /** {@inheritDoc} */
    @Override
    public void touchLayerOrDrumPadVolume (final int index, final boolean isBeingTouched)
    {
        if (this.hasDrumPads ())
            this.touchDrumPadVolume (index, isBeingTouched);
        else
            this.touchLayerVolume (index, isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void changeLayerOrDrumPadPan (final int index, final int control)
    {
        if (this.hasDrumPads ())
            this.changeDrumPadPan (index, control);
        else
            this.changeLayerPan (index, control);
    }


    /** {@inheritDoc} */
    @Override
    public void setLayerOrDrumPadPan (final int index, final int value)
    {
        if (this.hasDrumPads ())
            this.setDrumPadPan (index, value);
        else
            this.setLayerPan (index, value);
    }


    /** {@inheritDoc} */
    @Override
    public void resetLayerOrDrumPadPan (final int index)
    {
        if (this.hasDrumPads ())
            this.resetDrumPadPan (index);
        else
            this.resetLayerPan (index);
    }


    /** {@inheritDoc} */
    @Override
    public void touchLayerOrDrumPadPan (final int index, final boolean isBeingTouched)
    {
        if (this.hasDrumPads ())
            this.touchDrumPadPan (index, isBeingTouched);
        else
            this.touchLayerPan (index, isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void changeLayerOrDrumPadSend (final int index, final int send, final int control)
    {
        if (this.hasDrumPads ())
            this.changeDrumPadSend (index, send, control);
        else
            this.changeLayerSend (index, send, control);
    }


    /** {@inheritDoc} */
    @Override
    public void setLayerOrDrumPadSend (final int index, final int send, final int value)
    {
        if (this.hasDrumPads ())
            this.setDrumPadSend (index, send, value);
        else
            this.setLayerSend (index, send, value);
    }


    /** {@inheritDoc} */
    @Override
    public void resetLayerOrDrumPadSend (final int index, final int send)
    {
        if (this.hasDrumPads ())
            this.resetDrumPadSend (index, send);
        else
            this.resetLayerSend (index, send);
    }


    /** {@inheritDoc} */
    @Override
    public void touchLayerOrDrumPadSend (final int index, final int send, final boolean isBeingTouched)
    {
        if (this.hasDrumPads ())
            this.touchDrumPadSend (index, send, isBeingTouched);
        else
            this.touchLayerSend (index, send, isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleLayerOrDrumPadIsActivated (final int index)
    {
        if (this.hasDrumPads ())
            this.toggleDrumPadIsActivated (index);
        else
            this.toggleLayerIsActivated (index);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleLayerOrDrumPadMute (final int index)
    {
        if (this.hasDrumPads ())
            this.toggleDrumPadMute (index);
        else
            this.toggleLayerMute (index);
    }


    /** {@inheritDoc} */
    @Override
    public void setLayerOrDrumPadMute (final int index, final boolean value)
    {
        if (this.hasDrumPads ())
            this.setDrumPadMute (index, value);
        else
            this.setLayerMute (index, value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleLayerOrDrumPadSolo (final int index)
    {
        if (this.hasDrumPads ())
            this.toggleDrumPadSolo (index);
        else
            this.toggleLayerSolo (index);
    }


    /** {@inheritDoc} */
    @Override
    public void setLayerOrDrumPadSolo (final int index, final boolean value)
    {
        if (this.hasDrumPads ())
            this.setDrumPadSolo (index, value);
        else
            this.setLayerSolo (index, value);
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasZeroLayers ()
    {
        for (int i = 0; i < this.numDeviceLayers; i++)
            if (this.deviceLayers[i].doesExist ())
                return false;
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public IChannel getLayer (final int index)
    {
        return this.deviceLayers[index];
    }


    /** {@inheritDoc} */
    @Override
    public IChannel getSelectedLayer ()
    {
        for (final IChannel deviceLayer: this.deviceLayers)
        {
            if (deviceLayer.isSelected ())
                return deviceLayer;
        }
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public void selectLayer (final int index)
    {
        if (index >= this.numDeviceLayers)
            return;
        final DeviceLayer channel = this.layerBank.getChannel (index);
        if (channel != null)
            channel.selectInEditor ();
    }


    /** {@inheritDoc} */
    @Override
    public void previousLayer ()
    {
        final IChannel sel = this.getSelectedLayer ();
        final int index = sel == null ? 0 : sel.getIndex () - 1;
        if (index == -1)
            this.previousLayerBank ();
        else
            this.selectLayer (index);
    }


    /** {@inheritDoc} */
    @Override
    public void nextLayer ()
    {
        final IChannel sel = this.getSelectedLayer ();
        final int index = sel == null ? 0 : sel.getIndex () + 1;
        if (index == this.numDeviceLayers)
            this.nextLayerBank ();
        else
            this.selectLayer (index);
    }


    /** {@inheritDoc} */
    @Override
    public void previousLayerBank ()
    {
        if (!this.canScrollLayersUp ())
            return;
        this.scrollLayersPageUp ();
        this.host.scheduleTask ( () -> this.selectLayer (this.numDeviceLayers - 1), 75);
    }


    /** {@inheritDoc} */
    @Override
    public void nextLayerBank ()
    {
        if (!this.canScrollLayersDown ())
            return;
        this.scrollLayersPageDown ();
        this.host.scheduleTask ( () -> this.selectLayer (0), 75);
    }


    /** {@inheritDoc} */
    @Override
    public void enterLayer (final int index)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).selectInMixer ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectFirstDeviceInLayer (final int index)
    {
        this.cursorDevice.selectDevice (this.deviceBanks[index].getDevice (0));
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollLayersUp ()
    {
        return this.cursorDeviceLayer.hasPrevious ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollLayersDown ()
    {
        return this.cursorDeviceLayer.hasNext ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollLayersPageUp ()
    {
        this.layerBank.scrollChannelsPageUp ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollLayersPageDown ()
    {
        this.layerBank.scrollChannelsPageDown ();
    }


    /** {@inheritDoc} */
    @Override
    public void setLayerColor (final int index, final double red, final double green, final double blue)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).color ().set ((float) red, (float) green, (float) blue);
    }


    /** {@inheritDoc} */
    @Override
    public void changeLayerVolume (final int index, final int control)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).volume ().inc (Double.valueOf (this.valueChanger.calcKnobSpeed (control)), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void setLayerVolume (final int index, final int value)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).volume ().set (Integer.valueOf (value), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void resetLayerVolume (final int index)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).volume ().reset ();
    }


    /** {@inheritDoc} */
    @Override
    public void touchLayerVolume (final int index, final boolean isBeingTouched)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).volume ().touch (isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void changeLayerPan (final int index, final int control)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).pan ().inc (Double.valueOf (this.valueChanger.calcKnobSpeed (control)), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void setLayerPan (final int index, final int value)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).pan ().set (Integer.valueOf (value), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void resetLayerPan (final int index)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).pan ().reset ();
    }


    /** {@inheritDoc} */
    @Override
    public void touchLayerPan (final int index, final boolean isBeingTouched)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).pan ().touch (isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void changeLayerSend (final int index, final int sendIndex, final int control)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).sendBank ().getItemAt (sendIndex).inc (Double.valueOf (this.valueChanger.calcKnobSpeed (control)), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void setLayerSend (final int index, final int sendIndex, final int value)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).sendBank ().getItemAt (sendIndex).set (Integer.valueOf (value), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void resetLayerSend (final int index, final int sendIndex)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).sendBank ().getItemAt (sendIndex).reset ();
    }


    /** {@inheritDoc} */
    @Override
    public void touchLayerSend (final int index, final int sendIndex, final boolean isBeingTouched)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).sendBank ().getItemAt (sendIndex).touch (isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleLayerIsActivated (final int index)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).isActivated ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleLayerMute (final int index)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).mute ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void setLayerMute (final int index, final boolean value)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).mute ().set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleLayerSolo (final int index)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).solo ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void setLayerSolo (final int index, final boolean value)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).solo ().set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void setDrumPadIndication (final boolean enable)
    {
        this.drumPadBank.setIndication (enable);
    }


    /** {@inheritDoc} */
    @Override
    public IChannel getDrumPad (final int index)
    {
        return this.drumPadLayers[index];
    }


    /** {@inheritDoc} */
    @Override
    public IChannel getSelectedDrumPad ()
    {
        for (final IChannel drumPadLayer: this.drumPadLayers)
        {
            if (drumPadLayer.isSelected ())
                return drumPadLayer;
        }
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public void selectDrumPad (final int index)
    {
        if (index >= this.numDrumPadLayers)
            return;
        final Channel channel = this.drumPadBank.getChannel (index);
        if (channel != null)
            channel.selectInEditor ();
    }


    /** {@inheritDoc} */
    @Override
    public void previousDrumPad ()
    {
        final IChannel sel = this.getSelectedDrumPad ();
        int index = sel == null ? 0 : sel.getIndex () - 1;
        while (index > 0 && !this.getDrumPad (index).doesExist ())
            index--;
        if (index == -1)
            this.previousDrumPadBank ();
        else
            this.selectDrumPad (index);
    }


    /** {@inheritDoc} */
    @Override
    public void nextDrumPad ()
    {
        final IChannel sel = this.getSelectedDrumPad ();
        int index = sel == null ? 0 : sel.getIndex () + 1;
        while (index < this.numDrumPadLayers - 1 && !this.getDrumPad (index).doesExist ())
            index++;
        if (index == this.numDrumPadLayers)
            this.nextDrumPadBank ();
        else
            this.selectDrumPad (index);
    }


    /** {@inheritDoc} */
    @Override
    public void previousDrumPadBank ()
    {
        if (!this.canScrollDrumPadsUp ())
            return;
        this.scrollDrumPadsPageUp ();
        this.host.scheduleTask ( () -> this.selectDrumPad (this.numDrumPadLayers - 1), 75);
    }


    /** {@inheritDoc} */
    @Override
    public void nextDrumPadBank ()
    {
        if (!this.canScrollDrumPadsDown ())
            return;
        this.scrollDrumPadsPageDown ();
        this.host.scheduleTask ( () -> this.selectDrumPad (0), 75);
    }


    /** {@inheritDoc} */
    @Override
    public void enterDrumPad (final int index)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).selectInMixer ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectFirstDeviceInDrumPad (final int index)
    {
        this.cursorDevice.selectDevice (this.drumPadBanks[index].getDevice (0));
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollDrumPadsUp ()
    {
        return this.canScrollLayersUp ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollDrumPadsDown ()
    {
        return this.canScrollLayersDown ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollDrumPadsPageUp ()
    {
        this.drumPadBank.scrollChannelsPageUp ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollDrumPadsPageDown ()
    {
        this.drumPadBank.scrollChannelsPageDown ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollDrumPadsUp ()
    {
        this.drumPadBank.scrollChannelsUp ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollDrumPadsDown ()
    {
        this.drumPadBank.scrollChannelsDown ();
    }


    /** {@inheritDoc} */
    @Override
    public void setDrumPadColor (final int index, final double red, final double green, final double blue)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).color ().set ((float) red, (float) green, (float) blue);
    }


    /** {@inheritDoc} */
    @Override
    public void changeDrumPadVolume (final int index, final int control)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).volume ().inc (Double.valueOf (this.valueChanger.calcKnobSpeed (control)), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void setDrumPadVolume (final int index, final int value)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).volume ().set (Integer.valueOf (value), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void resetDrumPadVolume (final int index)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).volume ().reset ();
    }


    /** {@inheritDoc} */
    @Override
    public void touchDrumPadVolume (final int index, final boolean isBeingTouched)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).volume ().touch (isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void changeDrumPadPan (final int index, final int control)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).pan ().inc (Double.valueOf (this.valueChanger.calcKnobSpeed (control)), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void setDrumPadPan (final int index, final int value)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).pan ().set (Integer.valueOf (value), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void resetDrumPadPan (final int index)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).pan ().reset ();
    }


    /** {@inheritDoc} */
    @Override
    public void touchDrumPadPan (final int index, final boolean isBeingTouched)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).pan ().touch (isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void changeDrumPadSend (final int index, final int sendIndex, final int control)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).sendBank ().getItemAt (sendIndex).inc (Double.valueOf (this.valueChanger.calcKnobSpeed (control)), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void setDrumPadSend (final int index, final int sendIndex, final int value)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).sendBank ().getItemAt (sendIndex).set (Integer.valueOf (value), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void resetDrumPadSend (final int index, final int sendIndex)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).sendBank ().getItemAt (sendIndex).reset ();
    }


    /** {@inheritDoc} */
    @Override
    public void touchDrumPadSend (final int index, final int sendIndex, final boolean isBeingTouched)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).sendBank ().getItemAt (sendIndex).touch (isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleDrumPadIsActivated (final int index)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).isActivated ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleDrumPadMute (final int index)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).mute ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void setDrumPadMute (final int index, final boolean value)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).mute ().set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleDrumPadSolo (final int index)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).solo ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void setDrumPadSolo (final int index, final boolean value)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).solo ().set (value);
    }


    private void handlePageNames (final String [] pageNames)
    {
        this.parameterPageNames = pageNames;
    }


    /** {@inheritDoc} */
    @Override
    public int getNumDeviceLayers ()
    {
        return this.numDeviceLayers;
    }


    /** {@inheritDoc} */
    @Override
    public int getNumParameters ()
    {
        return this.numParams;
    }
}