// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.controller.ValueChanger;
import de.mossgrabers.framework.daw.data.ChannelData;
import de.mossgrabers.framework.daw.data.ParameterData;

import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorDevice;
import com.bitwig.extension.controller.api.CursorDeviceLayer;
import com.bitwig.extension.controller.api.CursorRemoteControlsPage;
import com.bitwig.extension.controller.api.Device;
import com.bitwig.extension.controller.api.DeviceBank;
import com.bitwig.extension.controller.api.DeviceLayer;
import com.bitwig.extension.controller.api.DeviceLayerBank;
import com.bitwig.extension.controller.api.DrumPadBank;
import com.bitwig.extension.controller.api.RemoteControl;
import com.bitwig.extension.controller.api.SettableIntegerValue;


/**
 * Proxy to the Bitwig Cursor device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CursorDeviceProxy
{
    private ControllerHost           host;
    private CursorDevice             cursorDevice;
    private DeviceBank               siblings;
    private CursorRemoteControlsPage remoteControls;
    private CursorDeviceLayer        cursorDeviceLayer;
    private ValueChanger             valueChanger;

    private int                      numParams;
    private int                      numDevicesInBank;
    private int                      numDeviceLayers;
    private int                      numDrumPadLayers;

    private String []                parameterPageNames = new String [0];
    private ParameterData []         fxparams;
    private DeviceLayerBank          layerBank;
    private ChannelData []           deviceLayers;
    private ChannelData []           drumPadLayers;
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
    public CursorDeviceProxy (final ControllerHost host, final CursorDevice cursorDevice, final ValueChanger valueChanger, final int numSends, final int numParams, final int numDevicesInBank, final int numDeviceLayers, final int numDrumPadLayers)
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

        this.remoteControls = this.cursorDevice.createCursorRemoteControlsPage (this.numParams);
        this.remoteControls.hasPrevious ().markInterested ();
        this.remoteControls.hasNext ().markInterested ();
        this.remoteControls.selectedPageIndex ().markInterested ();
        this.remoteControls.pageNames ().addValueObserver (this::handlePageNames);

        this.fxparams = new ParameterData [this.numParams];
        for (int i = 0; i < this.numParams; i++)
        {
            final RemoteControl p = this.getParameter (i);
            this.fxparams[i] = new ParameterData (p, valueChanger.getUpperBound ());
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
        this.deviceLayers = new ChannelData [this.numDeviceLayers];
        this.deviceBanks = new DeviceBank [this.numDeviceLayers];
        for (int i = 0; i < this.numDeviceLayers; i++)
        {
            final Channel layer = this.layerBank.getChannel (i);
            this.deviceLayers[i] = new ChannelData (layer, valueChanger.getUpperBound (), i, numSends);
            this.deviceBanks[i] = layer.createDeviceBank (this.numDevicesInBank);

            final int index = i;
            layer.addIsSelectedInEditorObserver (isSelected -> {
                this.deviceLayers[index].setSelected (isSelected);
            });
        }

        // Monitor the drum pad layers of a container device (if any)
        this.drumPadBank = this.cursorDevice.createDrumPadBank (this.numDrumPadLayers);
        this.drumPadLayers = new ChannelData [this.numDrumPadLayers];
        this.drumPadBanks = new DeviceBank [this.numDrumPadLayers];
        for (int i = 0; i < this.numDrumPadLayers; i++)
        {
            final Channel layer = this.drumPadBank.getChannel (i);
            this.drumPadLayers[i] = new ChannelData (layer, valueChanger.getUpperBound (), i, numSends);
            this.drumPadBanks[i] = layer.createDeviceBank (this.numDevicesInBank);

            final int index = i;
            layer.addIsSelectedInEditorObserver (isSelected -> {
                this.drumPadLayers[index].setSelected (isSelected);
            });
        }
    }


    /**
     * Dis-/Enable all attributes. They are enabled by default. Use this function if values are
     * currently not needed to improve performance.
     *
     * @param enable True to enable
     */
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


    /**
     * Start the browser to replace a device.
     */
    public void browseToReplaceDevice ()
    {
        this.cursorDevice.browseToReplaceDevice ();
    }


    /**
     * Start the browser to insert a new device before the current one.
     */
    public void browseToInsertBeforeDevice ()
    {
        this.cursorDevice.browseToInsertBeforeDevice ();
    }


    /**
     * Start the browser to insert a new device after the current one.
     */
    public void browseToInsertAfterDevice ()
    {
        this.cursorDevice.browseToInsertAfterDevice ();
    }


    /**
     * Select the parent of the device.
     */
    public void selectParent ()
    {
        this.cursorDevice.selectParent ();
    }


    /**
     * Select the channel which hosts the device.
     */
    public void selectChannel ()
    {
        this.cursorDevice.channel ().selectInEditor ();
    }


    /**
     * Returns true if the cursor device exists.
     *
     * @return True if the cursor device exists
     */
    public boolean doesExist ()
    {
        return this.cursorDevice.exists ().get ();
    }


    /**
     * Returns true if the cursor device is enabled.
     *
     * @return True if the cursor device is enabled
     */
    public boolean isEnabled ()
    {
        return this.cursorDevice.isEnabled ().get ();
    }


    /**
     * Get the name of the cursor device.
     *
     * @return The name
     */
    public String getName ()
    {
        return this.cursorDevice.name ().get ();
    }


    /**
     * Returns true if the cursor device is a non-DAW plugin.
     *
     * @return True if the cursor device is a non-DAW plugin
     */
    public boolean isPlugin ()
    {
        return this.cursorDevice.isPlugin ().get ();
    }


    /**
     * Get the position of the device in the chain.
     *
     * @return The position of the device in the chain.
     */
    public int getPositionInChain ()
    {
        return this.cursorDevice.position ().get ();
    }


    /**
     * Get the position of the device in the bank page.
     *
     * @return The position of the device in the bank page.
     */
    public int getPositionInBank ()
    {
        return this.getPositionInChain () % this.numDevicesInBank;
    }


    /**
     * Is there a previous device?
     *
     * @return True if there is a previous device
     */
    public boolean canSelectPreviousFX ()
    {
        return this.cursorDevice.hasPrevious ().get ();
    }


    /**
     * Is there a next device?
     *
     * @return True if there is a next device
     */
    public boolean canSelectNextFX ()
    {
        return this.cursorDevice.hasNext ().get ();
    }


    /**
     * Is the device expanded?
     *
     * @return True if the device is expanded
     */
    public boolean isExpanded ()
    {
        return this.cursorDevice.isExpanded ().get ();
    }


    /**
     * Is the remote control section of the device expanded?
     *
     * @return True if the remote control section of the device is expanded
     */
    public boolean isParameterPageSectionVisible ()
    {
        return this.cursorDevice.isRemoteControlsSectionVisible ().get ();
    }


    /**
     * Is the device window open?
     *
     * @return True if the device window is open
     */
    public boolean isWindowOpen ()
    {
        return this.cursorDevice.isWindowOpen ().get ();
    }


    /**
     * Is the device nested?
     *
     * @return True if the device is nested into another device
     */
    public boolean isNested ()
    {
        return this.cursorDevice.isNested ().get ();
    }


    /**
     * Does the device have drum pads?
     *
     * @return True if the device has drum pads
     */
    public boolean hasDrumPads ()
    {
        return this.cursorDevice.hasDrumPads ().get ();
    }


    /**
     * Does the device support layers? Might still have no layer.
     *
     * @return True if the device has layers
     */
    public boolean hasLayers ()
    {
        return this.cursorDevice.hasLayers ().get ();
    }


    /**
     * Does the device have slots?
     *
     * @return True if the device has slots
     */
    public boolean hasSlots ()
    {
        return this.cursorDevice.hasSlots ().get ();
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


    /**
     * Change a parameter.
     *
     * @param index The index of the parameter
     * @param control The control value
     */
    public void changeParameter (final int index, final int control)
    {
        this.getParameter (index).inc (Double.valueOf (this.valueChanger.calcKnobSpeed (control)), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /**
     * Set a parameter.
     *
     * @param index The index of the parameter
     * @param value The parameter
     */
    public void setParameter (final int index, final int value)
    {
        this.getParameter (index).set (Integer.valueOf (value), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /**
     * Reset a parameter.
     *
     * @param index The index of the parameter
     */
    public void resetParameter (final int index)
    {
        this.getParameter (index).reset ();
    }


    /**
     * Set parameter indication.
     *
     * @param index The index of the parameter
     * @param indicate True to enable indication
     */
    public void indicateParameter (final int index, final boolean indicate)
    {
        this.getParameter (index).setIndication (indicate);
    }


    /**
     * Touch parameter indication.
     *
     * @param index The index of the parameter
     * @param indicate True to enable touch indication
     */
    public void touchParameter (final int index, final boolean indicate)
    {
        this.getParameter (index).touch (indicate);
    }


    /**
     * Select the previous parameter page, cycles to the last page from the first.
     */
    public void previousParameterPage ()
    {
        this.remoteControls.selectPreviousPage (true);
    }


    /**
     * Select the next parameter page, cycles to the first page from the last.
     */
    public void nextParameterPage ()
    {
        this.remoteControls.selectNextPage (true);
    }


    /**
     * Returns true if there is a previous parameter page.
     *
     * @return True if there is a previous parameter page
     */
    public boolean hasPreviousParameterPage ()
    {
        return this.remoteControls.hasPrevious ().get ();
    }


    /**
     * Returns true if there is a next parameter page.
     *
     * @return True if there is a next parameter page
     */
    public boolean hasNextParameterPage ()
    {
        return this.remoteControls.hasNext ().get ();
    }


    /**
     * Get the names of the parameter pages.
     *
     * @return The names of the parameter pages
     */
    public String [] getParameterPageNames ()
    {
        return this.parameterPageNames;
    }


    /**
     * Get the name of the selected parameter page.
     *
     * @return The name of the selected parameter page
     */
    public String getSelectedParameterPageName ()
    {
        final int sel = this.getSelectedParameterPage ();
        return sel >= 0 && sel < this.parameterPageNames.length ? this.parameterPageNames[sel] : "";
    }


    /**
     * Get the index of the selected parameter page.
     *
     * @return The index of the selected parameter page
     */
    public int getSelectedParameterPage ()
    {
        return this.remoteControls.selectedPageIndex ().get ();
    }


    /**
     * Set the index of the selected parameter page.
     *
     * @param index The index of the selected parameter page
     */
    public void setSelectedParameterPage (final int index)
    {
        this.remoteControls.selectedPageIndex ().set (index);
    }


    /**
     * Set the index of the selected parameter page relative to the current bank.
     *
     * @param index The index of the selected parameter page
     */
    public void setSelectedParameterPageInBank (final int index)
    {
        final SettableIntegerValue pageIndex = this.remoteControls.selectedPageIndex ();
        pageIndex.set (pageIndex.get () / 8 * 8 + index);
    }


    /**
     * Select the previous parameter page bank.
     */
    public void previousParameterPageBank ()
    {
        final SettableIntegerValue index = this.remoteControls.selectedPageIndex ();
        index.set (Math.max (index.get () - 8, 0));
    }


    /**
     * Select the next parameter page bank.
     */
    public void nextParameterPageBank ()
    {
        final SettableIntegerValue index = this.remoteControls.selectedPageIndex ();
        index.set (Math.min (index.get () + 8, this.parameterPageNames.length - 1));
    }


    /**
     * Toggle the device on/off.
     */
    public void toggleEnabledState ()
    {
        this.cursorDevice.isEnabled ().toggle ();
    }


    /**
     * Toggle the window of an external device on/off.
     */
    public void toggleWindowOpen ()
    {
        this.cursorDevice.isWindowOpen ().toggle ();
    }


    /**
     * Select the previous device (if any).
     */
    public void selectPrevious ()
    {
        final boolean moveBank = this.getPositionInBank () == 0;
        this.cursorDevice.selectPrevious ();
        if (moveBank)
            this.selectPreviousBank ();
    }


    /**
     * Select the next device (if any).
     */
    public void selectNext ()
    {
        final boolean moveBank = this.getPositionInBank () == 7;
        this.cursorDevice.selectNext ();
        if (moveBank)
            this.selectNextBank ();
    }


    /**
     * Does the sibling at the given index exist?
     *
     * @param index The index of a sibling
     * @return True if it has a sibling
     */
    public boolean doesSiblingExist (final int index)
    {
        return this.siblings.getDevice (index).exists ().get ();
    }


    /**
     * Get the name of the sibling at the given index.
     *
     * @param index The index of a sibling
     * @return The name
     */
    public String getSiblingDeviceName (final int index)
    {
        return this.siblings.getDevice (index).name ().get ();
    }


    /**
     * Select the sibling at the given index.
     *
     * @param index The index of the sibling
     */
    public void selectSibling (final int index)
    {
        this.siblings.getDevice (index).selectInEditor ();
    }


    /**
     * Select the previous sibling page.
     */
    public void selectPreviousBank ()
    {
        this.siblings.scrollPageUp ();
    }


    /**
     * Select the next sibling page.
     */
    public void selectNextBank ()
    {
        this.siblings.scrollPageDown ();
    }


    /**
     * Is there a current device?
     *
     * @return True if there is a device
     */
    public boolean hasSelectedDevice ()
    {
        return this.cursorDevice.exists ().get ();
    }


    /**
     * Get the parameter data.
     *
     * @param index The index of the parameter
     * @return The parameter data
     */
    public ParameterData getFXParam (final int index)
    {
        return this.fxparams[index];
    }


    /**
     * Toggle the expanded state of the device.
     */
    public void toggleExpanded ()
    {
        this.cursorDevice.isExpanded ().toggle ();
    }


    /**
     * Toggle the parameter section visibility state of the device.
     */
    public void toggleParameterPageSectionVisible ()
    {
        this.cursorDevice.isRemoteControlsSectionVisible ().toggle ();
    }


    /**
     * Get the layer or drum pad depending on the device.
     *
     * @param index The index
     * @return The layer or drum pad
     */
    public ChannelData getLayerOrDrumPad (final int index)
    {
        return this.hasDrumPads () ? this.getDrumPad (index) : this.getLayer (index);
    }


    /**
     * Get the selected layer or drum pad depending on the device.
     *
     * @return The selected layer or drum pad
     */
    public ChannelData getSelectedLayerOrDrumPad ()
    {
        return this.hasDrumPads () ? this.getSelectedDrumPad () : this.getSelectedLayer ();
    }


    /**
     * Select a layer or drum pad depending on the device.
     *
     * @param index The index
     */
    public void selectLayerOrDrumPad (final int index)
    {
        if (this.hasDrumPads ())
            this.selectDrumPad (index);
        else
            this.selectLayer (index);
    }


    /**
     * Select the previous layer or drum pad depending on the device, if any.
     */
    public void previousLayerOrDrumPad ()
    {
        if (this.hasDrumPads ())
            this.previousDrumPad ();
        else
            this.previousLayer ();
    }


    /**
     * Select the next layer or drum pad depending on the device, if any.
     */
    public void nextLayerOrDrumPad ()
    {
        if (this.hasDrumPads ())
            this.nextDrumPad ();
        else
            this.nextLayer ();
    }


    /**
     * Select the previous layer or drum pad bank page depending on the device, if any.
     */
    public void previousLayerOrDrumPadBank ()
    {
        if (this.hasDrumPads ())
            this.previousDrumPadBank ();
        else
            this.previousLayerBank ();
    }


    /**
     * Select the next layer or drum pad bank page depending on the device, if any.
     */
    public void nextLayerOrDrumPadBank ()
    {
        if (this.hasDrumPads ())
            this.nextDrumPadBank ();
        else
            this.nextLayerBank ();
    }


    /**
     * Enter a layer or drum pad depending on the device.
     *
     * @param index The index
     */
    public void enterLayerOrDrumPad (final int index)
    {
        if (this.hasDrumPads ())
            this.enterDrumPad (index);
        else
            this.enterLayer (index);
    }


    /**
     * Select the first device in a layer or drum pad depending on the device.
     *
     * @param index The index
     */
    public void selectFirstDeviceInLayerOrDrumPad (final int index)
    {
        if (this.hasDrumPads ())
            this.selectFirstDeviceInDrumPad (index);
        else
            this.selectFirstDeviceInLayer (index);
    }


    /**
     * Can the layers or drum pads scrolled up depending on the device.
     *
     * @return True if scrolling is possible
     */
    public boolean canScrollLayersOrDrumPadsUp ()
    {
        return this.hasDrumPads () ? this.canScrollDrumPadsUp () : this.canScrollLayersUp ();
    }


    /**
     * Can the layers or drum pads scrolled down depending on the device.
     *
     * @return True if scrolling is possible
     */
    public boolean canScrollLayersOrDrumPadsDown ()
    {
        return this.hasDrumPads () ? this.canScrollDrumPadsDown () : this.canScrollLayersDown ();
    }


    /**
     * Scroll the layers or drum pads up by one page depending on the device.
     */
    public void scrollLayersOrDrumPadsPageUp ()
    {
        if (this.hasDrumPads ())
            this.scrollDrumPadsPageUp ();
        else
            this.scrollLayersPageUp ();
    }


    /**
     * Scroll the layers or drum pads down by one page depending on the device.
     */
    public void scrollLayersOrDrumPadsPageDown ()
    {
        if (this.hasDrumPads ())
            this.scrollDrumPadsPageDown ();
        else
            this.scrollLayersPageDown ();
    }


    /**
     * Set the layer or drum pad color depending on the device.
     *
     * @param index The index
     * @param red The red
     * @param green The green
     * @param blue The blue
     */
    public void setLayerOrDrumPadColor (final int index, final double red, final double green, final double blue)
    {
        if (this.hasDrumPads ())
            this.setDrumPadColor (index, red, green, blue);
        else
            this.setLayerColor (index, red, green, blue);
    }


    /**
     * Get the layer or drum pad color ID depending on the device.
     *
     * @param index The index
     * @return The ID
     */
    public String getLayerOrDrumPadColorEntry (final int index)
    {
        return BitwigColors.getColorIndex (this.getLayerOrDrumPad (index).getColor ());
    }


    /**
     * Change the layer or drum pad volume depending on the device.
     *
     * @param index The index
     * @param control The control value
     */
    public void changeLayerOrDrumPadVolume (final int index, final int control)
    {
        if (this.hasDrumPads ())
            this.changeDrumPadVolume (index, control);
        else
            this.changeLayerVolume (index, control);
    }


    /**
     * Set the layer or drum pad volume depending on the device.
     *
     * @param index The index
     * @param value The value
     */
    public void setLayerOrDrumPadVolume (final int index, final int value)
    {
        if (this.hasDrumPads ())
            this.setDrumPadVolume (index, value);
        else
            this.setLayerVolume (index, value);
    }


    /**
     * Reset the layer or drum pad volume to its default value depending on the device.
     *
     * @param index The index
     */
    public void resetLayerOrDrumPadVolume (final int index)
    {
        if (this.hasDrumPads ())
            this.resetDrumPadVolume (index);
        else
            this.resetLayerVolume (index);
    }


    /**
     * Signal touch to the layer or drum pad volume depending on the device.
     *
     * @param index The index
     * @param isBeingTouched True if touched
     */
    public void touchLayerOrDrumPadVolume (final int index, final boolean isBeingTouched)
    {
        if (this.hasDrumPads ())
            this.touchDrumPadVolume (index, isBeingTouched);
        else
            this.touchLayerVolume (index, isBeingTouched);
    }


    /**
     * Change the layer or drum pad panorama depending on the device.
     *
     * @param index The index
     * @param control The control value
     */
    public void changeLayerOrDrumPadPan (final int index, final int control)
    {
        if (this.hasDrumPads ())
            this.changeDrumPadPan (index, control);
        else
            this.changeLayerPan (index, control);
    }


    /**
     * Set the layer or drum pad panorama depending on the device.
     *
     * @param index The index
     * @param value The value
     */
    public void setLayerOrDrumPadPan (final int index, final int value)
    {
        if (this.hasDrumPads ())
            this.setDrumPadPan (index, value);
        else
            this.setLayerPan (index, value);
    }


    /**
     * Reset the layer or drum pad panorama to its default value depending on the device.
     *
     * @param index The index
     */
    public void resetLayerOrDrumPadPan (final int index)
    {
        if (this.hasDrumPads ())
            this.resetDrumPadPan (index);
        else
            this.resetLayerPan (index);
    }


    /**
     * Signal touch to the layer or drum pad panorama depending on the device.
     *
     * @param index The index
     * @param isBeingTouched True if touched
     */
    public void touchLayerOrDrumPadPan (final int index, final boolean isBeingTouched)
    {
        if (this.hasDrumPads ())
            this.touchDrumPadPan (index, isBeingTouched);
        else
            this.touchLayerPan (index, isBeingTouched);
    }


    /**
     * Change the layer or drum pad send volume depending on the device.
     *
     * @param index The index
     * @param send The index of the send
     * @param control The control value
     */
    public void changeLayerOrDrumPadSend (final int index, final int send, final int control)
    {
        if (this.hasDrumPads ())
            this.changeDrumPadSend (index, send, control);
        else
            this.changeLayerSend (index, send, control);
    }


    /**
     * Set the layer or drum pad send volume depending on the device.
     *
     * @param index The index
     * @param send The index of the send
     * @param value The value
     */
    public void setLayerOrDrumPadSend (final int index, final int send, final int value)
    {
        if (this.hasDrumPads ())
            this.setDrumPadSend (index, send, value);
        else
            this.setLayerSend (index, send, value);
    }


    /**
     * Reset the layer or drum pad send volume to its default value depending on the device.
     *
     * @param index The index
     * @param send The index of the send
     */
    public void resetLayerOrDrumPadSend (final int index, final int send)
    {
        if (this.hasDrumPads ())
            this.resetDrumPadSend (index, send);
        else
            this.resetLayerSend (index, send);
    }


    /**
     * Signal touch to the layer or drum pad send volume depending on the device.
     *
     * @param index The index
     * @param send The index of the send
     * @param isBeingTouched True if touched
     */
    public void touchLayerOrDrumPadSend (final int index, final int send, final boolean isBeingTouched)
    {
        if (this.hasDrumPads ())
            this.touchDrumPadSend (index, send, isBeingTouched);
        else
            this.touchLayerSend (index, send, isBeingTouched);
    }


    /**
     * Toggle if the the layer or drum pad is active depending on the device.
     *
     * @param index The index
     */
    public void toggleLayerOrDrumPadIsActivated (final int index)
    {
        if (this.hasDrumPads ())
            this.toggleDrumPadIsActivated (index);
        else
            this.toggleLayerIsActivated (index);
    }


    /**
     * Toggle the the layer or drum pad mute depending on the device.
     *
     * @param index The index
     */
    public void toggleLayerOrDrumPadMute (final int index)
    {
        if (this.hasDrumPads ())
            this.toggleDrumPadMute (index);
        else
            this.toggleLayerMute (index);
    }


    /**
     * Set the layer or drum pad mute depending on the device.
     *
     * @param index The index
     * @param value The value
     */
    public void setLayerOrDrumPadMute (final int index, final boolean value)
    {
        if (this.hasDrumPads ())
            this.setDrumPadMute (index, value);
        else
            this.setLayerMute (index, value);
    }


    /**
     * Toggle the layer or drum pad solo depending on the device.
     *
     * @param index The index
     */
    public void toggleLayerOrDrumPadSolo (final int index)
    {
        if (this.hasDrumPads ())
            this.toggleDrumPadSolo (index);
        else
            this.toggleLayerSolo (index);
    }


    /**
     * Set the layer or drum pad solo depending on the device.
     *
     * @param index The index
     * @param value The value
     */
    public void setLayerOrDrumPadSolo (final int index, final boolean value)
    {
        if (this.hasDrumPads ())
            this.setDrumPadSolo (index, value);
        else
            this.setLayerSolo (index, value);
    }


    /**
     * Check if there is at least 1 existing layer.
     *
     * @return True if there are no layers
     */
    public boolean hasZeroLayers ()
    {
        for (int i = 0; i < this.numDeviceLayers; i++)
            if (this.deviceLayers[i].doesExist ())
                return false;
        return true;
    }


    /**
     * Get the layer.
     *
     * @param index The index
     * @return The layer
     */
    public ChannelData getLayer (final int index)
    {
        return this.deviceLayers[index];
    }


    /**
     * Get the selected layer.
     *
     * @return The selected layer
     */
    public ChannelData getSelectedLayer ()
    {
        for (final ChannelData deviceLayer: this.deviceLayers)
        {
            if (deviceLayer.isSelected ())
                return deviceLayer;
        }
        return null;
    }


    /**
     * Select a layer.
     *
     * @param index The index
     */
    public void selectLayer (final int index)
    {
        if (index >= this.numDeviceLayers)
            return;
        final DeviceLayer channel = this.layerBank.getChannel (index);
        if (channel != null)
            channel.selectInEditor ();
    }


    /**
     * Select the previous layer, if any.
     */
    public void previousLayer ()
    {
        final ChannelData sel = this.getSelectedLayer ();
        final int index = sel == null ? 0 : sel.getIndex () - 1;
        if (index == -1)
            this.previousLayerBank ();
        else
            this.selectLayer (index);
    }


    /**
     * Select the next layer, if any.
     */
    public void nextLayer ()
    {
        final ChannelData sel = this.getSelectedLayer ();
        final int index = sel == null ? 0 : sel.getIndex () + 1;
        if (index == this.numDeviceLayers)
            this.nextLayerBank ();
        else
            this.selectLayer (index);
    }


    /**
     * Select the previous layer bank page, if any.
     */
    public void previousLayerBank ()
    {
        if (!this.canScrollLayersUp ())
            return;
        this.scrollLayersPageUp ();
        this.host.scheduleTask ( () -> this.selectLayer (this.numDeviceLayers - 1), 75);
    }


    /**
     * Select the next layer bank page, if any.
     */
    public void nextLayerBank ()
    {
        if (!this.canScrollLayersDown ())
            return;
        this.scrollLayersPageDown ();
        this.host.scheduleTask ( () -> this.selectLayer (0), 75);
    }


    /**
     * Enter a layer.
     *
     * @param index The index
     */
    public void enterLayer (final int index)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).selectInMixer ();
    }


    /**
     * Select the first device in a layer.
     *
     * @param index The index
     */
    public void selectFirstDeviceInLayer (final int index)
    {
        this.cursorDevice.selectDevice (this.deviceBanks[index].getDevice (0));
    }


    /**
     * Can the layers be scrolled up?
     *
     * @return True if the layers can be scrolled up
     */
    public boolean canScrollLayersUp ()
    {
        return this.cursorDeviceLayer.hasPrevious ().get ();
    }


    /**
     * Can the layers be scrolled down?
     *
     * @return True if the layers can be scrolled down
     */
    public boolean canScrollLayersDown ()
    {
        return this.cursorDeviceLayer.hasNext ().get ();
    }


    /**
     * Scroll the layers up by one page.
     */
    public void scrollLayersPageUp ()
    {
        this.layerBank.scrollChannelsPageUp ();
    }


    /**
     * Scroll the layers down by one page.
     */
    public void scrollLayersPageDown ()
    {
        this.layerBank.scrollChannelsPageDown ();
    }


    /**
     * Set the layer color.
     *
     * @param index The index
     * @param red The red
     * @param green The green
     * @param blue The blue
     */
    public void setLayerColor (final int index, final double red, final double green, final double blue)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).color ().set ((float) red, (float) green, (float) blue);
    }


    /**
     * Change the layer volume.
     *
     * @param index The index
     * @param control The control value
     */
    public void changeLayerVolume (final int index, final int control)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).volume ().inc (Double.valueOf (this.valueChanger.calcKnobSpeed (control)), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /**
     * Set the drum pad volume.
     *
     * @param index The index
     * @param value The value
     */
    public void setLayerVolume (final int index, final int value)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).volume ().set (Integer.valueOf (value), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /**
     * Reset the layer volume to its default value.
     *
     * @param index The index
     */
    public void resetLayerVolume (final int index)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).volume ().reset ();
    }


    /**
     * Signal touch to the layer volume.
     *
     * @param index The index
     * @param isBeingTouched True if touched
     */
    public void touchLayerVolume (final int index, final boolean isBeingTouched)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).volume ().touch (isBeingTouched);
    }


    /**
     * Change the layer panorama.
     *
     * @param index The index
     * @param control The control value
     */
    public void changeLayerPan (final int index, final int control)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).pan ().inc (Double.valueOf (this.valueChanger.calcKnobSpeed (control)), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /**
     * Set the drum pad panorama.
     *
     * @param index The index
     * @param value The value
     */
    public void setLayerPan (final int index, final int value)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).pan ().set (Integer.valueOf (value), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /**
     * Reset the layer panorama to its default value.
     *
     * @param index The index
     */
    public void resetLayerPan (final int index)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).pan ().reset ();
    }


    /**
     * Signal touch to the layer panorama.
     *
     * @param index The index
     * @param isBeingTouched True if touched
     */
    public void touchLayerPan (final int index, final boolean isBeingTouched)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).pan ().touch (isBeingTouched);
    }


    /**
     * Change the layer send volume.
     *
     * @param index The index
     * @param sendIndex The index of the send
     * @param control The control value
     */
    public void changeLayerSend (final int index, final int sendIndex, final int control)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).sendBank ().getItemAt (sendIndex).inc (Double.valueOf (this.valueChanger.calcKnobSpeed (control)), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /**
     * Set the drum pad send volume.
     *
     * @param index The index
     * @param sendIndex The index of the send
     * @param value The value
     */
    public void setLayerSend (final int index, final int sendIndex, final int value)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).sendBank ().getItemAt (sendIndex).set (Integer.valueOf (value), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /**
     * Reset the layer send volume to its default value.
     *
     * @param index The index
     * @param sendIndex The index of the send
     */
    public void resetLayerSend (final int index, final int sendIndex)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).sendBank ().getItemAt (sendIndex).reset ();
    }


    /**
     * Signal touch to the layer send volume.
     *
     * @param index The index
     * @param sendIndex The index of the send
     * @param isBeingTouched True if touched
     */
    public void touchLayerSend (final int index, final int sendIndex, final boolean isBeingTouched)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).sendBank ().getItemAt (sendIndex).touch (isBeingTouched);
    }


    /**
     * Toggle if the the layer is active.
     *
     * @param index The index
     */
    public void toggleLayerIsActivated (final int index)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).isActivated ().toggle ();
    }


    /**
     * Toggle the the layer mute.
     *
     * @param index The index
     */
    public void toggleLayerMute (final int index)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).mute ().toggle ();
    }


    /**
     * Set the layer mute.
     *
     * @param index The index
     * @param value The value
     */
    public void setLayerMute (final int index, final boolean value)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).mute ().set (value);
    }


    /**
     * Toggle the layer solo.
     *
     * @param index The index
     */
    public void toggleLayerSolo (final int index)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).solo ().toggle ();
    }


    /**
     * Set the layer solo.
     *
     * @param index The index
     * @param value The value
     */
    public void setLayerSolo (final int index, final boolean value)
    {
        if (index < this.numDeviceLayers)
            this.layerBank.getChannel (index).solo ().set (value);
    }


    /**
     * Set indication for drum pads.
     *
     * @param enable True to enable
     */
    public void setDrumPadIndication (final boolean enable)
    {
        this.drumPadBank.setIndication (enable);
    }


    /**
     * Get the drum pad.
     *
     * @param index The index
     * @return The drum pad
     */
    public ChannelData getDrumPad (final int index)
    {
        return this.drumPadLayers[index];
    }


    /**
     * Get the selected drum pad.
     *
     * @return The selected drum pad
     */
    public ChannelData getSelectedDrumPad ()
    {
        for (final ChannelData drumPadLayer: this.drumPadLayers)
        {
            if (drumPadLayer.isSelected ())
                return drumPadLayer;
        }
        return null;
    }


    /**
     * Select a drum pad.
     *
     * @param index The index
     */
    public void selectDrumPad (final int index)
    {
        if (index >= this.numDrumPadLayers)
            return;
        final Channel channel = this.drumPadBank.getChannel (index);
        if (channel != null)
            channel.selectInEditor ();
    }


    /**
     * Select the previous drum pad, if any.
     */
    public void previousDrumPad ()
    {
        final ChannelData sel = this.getSelectedDrumPad ();
        int index = sel == null ? 0 : sel.getIndex () - 1;
        while (index > 0 && !this.getDrumPad (index).doesExist ())
            index--;
        if (index == -1)
            this.previousDrumPadBank ();
        else
            this.selectDrumPad (index);
    }


    /**
     * Select the next drum pad, if any.
     */
    public void nextDrumPad ()
    {
        final ChannelData sel = this.getSelectedDrumPad ();
        int index = sel == null ? 0 : sel.getIndex () + 1;
        while (index < this.numDrumPadLayers - 1 && !this.getDrumPad (index).doesExist ())
            index++;
        if (index == this.numDrumPadLayers)
            this.nextDrumPadBank ();
        else
            this.selectDrumPad (index);
    }


    /**
     * Select the previous drum pad bank page, if any.
     */
    public void previousDrumPadBank ()
    {
        if (!this.canScrollDrumPadsUp ())
            return;
        this.scrollDrumPadsPageUp ();
        this.host.scheduleTask ( () -> this.selectDrumPad (this.numDrumPadLayers - 1), 75);
    }


    /**
     * Select the next drum pad bank page, if any.
     */
    public void nextDrumPadBank ()
    {
        if (!this.canScrollDrumPadsDown ())
            return;
        this.scrollDrumPadsPageDown ();
        this.host.scheduleTask ( () -> this.selectDrumPad (0), 75);
    }


    /**
     * Enter a drum pad.
     *
     * @param index The index
     */
    public void enterDrumPad (final int index)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).selectInMixer ();
    }


    /**
     * Select the first device in a drum pad.
     *
     * @param index The index
     */
    public void selectFirstDeviceInDrumPad (final int index)
    {
        this.cursorDevice.selectDevice (this.drumPadBanks[index].getDevice (0));
    }


    /**
     * Can the drum pads scrolled up.
     *
     * @return True if scrolling is possible
     */
    public boolean canScrollDrumPadsUp ()
    {
        return this.canScrollLayersUp ();
    }


    /**
     * Can the drum pads scrolled down.
     *
     * @return True if scrolling is possible
     */
    public boolean canScrollDrumPadsDown ()
    {
        return this.canScrollLayersDown ();
    }


    /**
     * Scroll the drum pads up by one page.
     */
    public void scrollDrumPadsPageUp ()
    {
        this.drumPadBank.scrollChannelsPageUp ();
    }


    /**
     * Scroll the drum pads down by one page.
     */
    public void scrollDrumPadsPageDown ()
    {
        this.drumPadBank.scrollChannelsPageDown ();
    }


    /**
     * Scroll the drum pads up by one.
     */
    public void scrollDrumPadsUp ()
    {
        this.drumPadBank.scrollChannelsUp ();
    }


    /**
     * Scroll the drum pads down by one.
     */
    public void scrollDrumPadsDown ()
    {
        this.drumPadBank.scrollChannelsDown ();
    }


    /**
     * Set the drum pad color.
     *
     * @param index The index
     * @param red The red
     * @param green The green
     * @param blue The blue
     */
    public void setDrumPadColor (final int index, final double red, final double green, final double blue)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).color ().set ((float) red, (float) green, (float) blue);
    }


    /**
     * Change the drum pad volume.
     *
     * @param index The index
     * @param control The control value
     */
    public void changeDrumPadVolume (final int index, final int control)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).volume ().inc (Double.valueOf (this.valueChanger.calcKnobSpeed (control)), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /**
     * Set the drum pad volume.
     *
     * @param index The index
     * @param value The value
     */
    public void setDrumPadVolume (final int index, final int value)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).volume ().set (Integer.valueOf (value), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /**
     * Reset the drum pad volume to its default value.
     *
     * @param index The index
     */
    public void resetDrumPadVolume (final int index)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).volume ().reset ();
    }


    /**
     * Signal touch to the drum pad volume.
     *
     * @param index The index
     * @param isBeingTouched True if touched
     */
    public void touchDrumPadVolume (final int index, final boolean isBeingTouched)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).volume ().touch (isBeingTouched);
    }


    /**
     * Change the drum pad panorama.
     *
     * @param index The index
     * @param control The control value
     */
    public void changeDrumPadPan (final int index, final int control)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).pan ().inc (Double.valueOf (this.valueChanger.calcKnobSpeed (control)), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /**
     * Set the drum pad panorama.
     *
     * @param index The index
     * @param value The value
     */
    public void setDrumPadPan (final int index, final int value)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).pan ().set (Integer.valueOf (value), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /**
     * Reset the drum pad panorama to its default value.
     *
     * @param index The index
     */
    public void resetDrumPadPan (final int index)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).pan ().reset ();
    }


    /**
     * Signal touch to the drum pad panorama.
     *
     * @param index The index
     * @param isBeingTouched True if touched
     */
    public void touchDrumPadPan (final int index, final boolean isBeingTouched)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).pan ().touch (isBeingTouched);
    }


    /**
     * Change the drum pad send volume.
     *
     * @param index The index
     * @param sendIndex The index of the send
     * @param control The control value
     */
    public void changeDrumPadSend (final int index, final int sendIndex, final int control)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).sendBank ().getItemAt (sendIndex).inc (Double.valueOf (this.valueChanger.calcKnobSpeed (control)), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /**
     * Set the drum pad send volume.
     *
     * @param index The index
     * @param sendIndex The index of the send
     * @param value The value
     */
    public void setDrumPadSend (final int index, final int sendIndex, final int value)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).sendBank ().getItemAt (sendIndex).set (Integer.valueOf (value), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /**
     * Reset the drum pad send volume to its default value.
     *
     * @param index The index
     * @param sendIndex The index of the send
     */
    public void resetDrumPadSend (final int index, final int sendIndex)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).sendBank ().getItemAt (sendIndex).reset ();
    }


    /**
     * Signal touch to the drum pad send volume.
     *
     * @param index The index
     * @param sendIndex The index of the send
     * @param isBeingTouched True if touched
     */
    public void touchDrumPadSend (final int index, final int sendIndex, final boolean isBeingTouched)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).sendBank ().getItemAt (sendIndex).touch (isBeingTouched);
    }


    /**
     * Toggle if the the drum pad is active.
     *
     * @param index The index
     */
    public void toggleDrumPadIsActivated (final int index)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).isActivated ().toggle ();
    }


    /**
     * Toggle the the drum pad mute.
     *
     * @param index The index
     */
    public void toggleDrumPadMute (final int index)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).mute ().toggle ();
    }


    /**
     * Set the drum pad mute.
     *
     * @param index The index
     * @param value The value
     */
    public void setDrumPadMute (final int index, final boolean value)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).mute ().set (value);
    }


    /**
     * Toggle the drum pad solo.
     *
     * @param index The index
     */
    public void toggleDrumPadSolo (final int index)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).solo ().toggle ();
    }


    /**
     * Set the drum pad solo.
     *
     * @param index The index
     * @param value The value
     */
    public void setDrumPadSolo (final int index, final boolean value)
    {
        if (index < this.numDrumPadLayers)
            this.drumPadBank.getChannel (index).solo ().set (value);
    }


    private void handlePageNames (final String [] pageNames)
    {
        this.parameterPageNames = pageNames;
    }


    /**
     * Get the number of a page in the device layers bank.
     *
     * @return The number
     */
    public int getNumDeviceLayers ()
    {
        return this.numDeviceLayers;
    }


    /**
     * Get the number of a page in the parameters bank.
     *
     * @return The number
     */
    public int getNumParameters ()
    {
        return this.numParams;
    }
}