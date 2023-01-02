// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.daw.constants.DeviceID;

import java.util.ArrayList;
import java.util.List;


/**
 * All configuration parameters for the model.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ModelSetup
{
    private boolean              hasFlatTrackList       = true;
    private boolean              hasFullFlatTrackList   = false;

    private int                  numTracks              = 8;
    private int                  numFxTracks            = -1;
    private int                  numScenes              = 8;
    private int                  numSends               = 8;

    private boolean              wantsDrumDevice        = true;
    private int []               additionalDrumDevices  = {};

    private int                  numDevicesInBank       = 8;
    private int                  numDeviceLayers        = 8;

    private int                  numParamPages          = 8;
    private int                  numParams              = 8;
    private int                  numDrumPadLayers       = 16;

    private int                  numFilterColumnEntries = 16;
    private int                  numResults             = 16;

    private int                  numMarkers             = 0;
    private int                  numUserPageSize        = 8;
    private int                  numUserPages           = 8;

    private final List<DeviceID> deviceIDs              = new ArrayList<> ();


    /**
     * Constructor.
     */
    public ModelSetup ()
    {
        // Intentionally empty
    }


    /**
     * Disable/enable the drum device (enabled by default).
     *
     * @param wantsDrumDevice True to enable
     */
    public void enableMainDrumDevice (final boolean wantsDrumDevice)
    {
        this.wantsDrumDevice = wantsDrumDevice;
    }


    /**
     * Enables the drum 64 device (disabled by default).
     */
    public void enableDrum64Device ()
    {
        this.setAdditionalDrumDevices (new int []
        {
            64
        });
    }


    /**
     * Dis-/enable the drum 64 device (disabled by default).
     *
     * @param additionalDrumDevices Add additional drum pad bank sizes which are different from the
     *            main one
     */
    public void setAdditionalDrumDevices (final int [] additionalDrumDevices)
    {
        this.additionalDrumDevices = additionalDrumDevices;
    }


    /**
     * Enable a specific device monitor.
     *
     * @param deviceID The ID of the device to monitor
     */
    public void enableDevice (final DeviceID deviceID)
    {
        this.deviceIDs.add (deviceID);
    }


    /**
     * Get the number of tracks to monitor (size of a track bank page).
     *
     * @return The number of tracks to monitor (size of a track bank page).
     */
    public int getNumTracks ()
    {
        return this.numTracks;
    }


    /**
     * Set the number of tracks to monitor (per track bank page).
     *
     * @param numTracks The number of track to monitor (per track bank page)
     */
    public void setNumTracks (final int numTracks)
    {
        this.numTracks = numTracks;
    }


    /**
     * Get the number of FX tracks to monitor (size of a track FX bank page). If not explicitly set,
     * returns the same as getNumTracks().
     *
     * @return The number of FX tracks to monitor (size of a track FX bank page)
     */
    public int getNumFxTracks ()
    {
        return this.numFxTracks == -1 ? this.numTracks : this.numFxTracks;
    }


    /**
     * Set the number of Fx tracks to monitor (per track bank page).
     *
     * @param numFxTracks The number of Fx tracks to monitor (per track bank page)
     */
    public void setNumFxTracks (final int numFxTracks)
    {
        this.numFxTracks = numFxTracks;
    }


    /**
     * Get the number of scenes to monitor (per scene bank).
     *
     * @return The number of scenes to monitor (per scene bank)
     */
    public int getNumScenes ()
    {
        return this.numScenes;
    }


    /**
     * Set the number of scenes to monitor (per scene bank).
     *
     * @param numScenes The number of scenes to monitor (per scene bank)
     */
    public void setNumScenes (final int numScenes)
    {
        this.numScenes = numScenes;
    }


    /**
     * Get the number of sends to monitor.
     *
     * @return The number of sends to monitor
     */
    public int getNumSends ()
    {
        return this.numSends;
    }


    /**
     * Set the number of sends to monitor.
     *
     * @param numSends The number of sends to monitor
     */
    public void setNumSends (final int numSends)
    {
        this.numSends = numSends;
    }


    /**
     * Get the number of parameter pages of a device to monitor.
     *
     * @return The number of parameter pages of a device to monitor
     */
    public int getNumParamPages ()
    {
        return this.numParamPages;
    }


    /**
     * Set the number of parameter pages of a device to monitor.
     *
     * @param numParamPages The number of parameter pages of a device to monitor
     */
    public void setNumParamPages (final int numParamPages)
    {
        this.numParamPages = numParamPages;
    }


    /**
     * Get the number of parameter of a device to monitor.
     *
     * @return The number of parameter of a device to monitor
     */
    public int getNumParams ()
    {
        return this.numParams;
    }


    /**
     * Set the number of parameter of a device to monitor.
     *
     * @param numParams The number of parameter of a device to monitor
     */
    public void setNumParams (final int numParams)
    {
        this.numParams = numParams;
    }


    /**
     * Get the number of parameter of a device to monitor.
     *
     * @return The number of parameter of a device to monitor
     */
    public int getNumDevicesInBank ()
    {
        return this.numDevicesInBank;
    }


    /**
     * Set the number of parameter of a device to monitor.
     *
     * @param numDevicesInBank The number of parameter of a device to monitor
     */
    public void setNumDevicesInBank (final int numDevicesInBank)
    {
        this.numDevicesInBank = numDevicesInBank;
    }


    /**
     * Get the number of parameter of a device to monitor.
     *
     * @return The number of parameter of a device to monitor
     */
    public int getNumDeviceLayers ()
    {
        return this.numDeviceLayers;
    }


    /**
     * Set the number of device layers to monitor.
     *
     * @param numDeviceLayers The number of device layers to monitor
     */
    public void setNumDeviceLayers (final int numDeviceLayers)
    {
        this.numDeviceLayers = numDeviceLayers;
    }


    /**
     * Get the number of drum pad layers to monitor.
     *
     * @return The number of drum pad layers to monitor
     */
    public int getNumDrumPadLayers ()
    {
        return this.numDrumPadLayers;
    }


    /**
     * Set the number of drum pad layers to monitor.
     *
     * @param numDrumPadLayers The number of drum pad layers to monitor
     */
    public void setNumDrumPadLayers (final int numDrumPadLayers)
    {
        this.numDrumPadLayers = numDrumPadLayers;
    }


    /**
     * Get the number of entries in one filter column to monitor.
     *
     * @return The number of entries in one filter column to monitor
     */
    public int getNumFilterColumnEntries ()
    {
        return this.numFilterColumnEntries;
    }


    /**
     * Set the number of entries in one filter column to monitor.
     *
     * @param numFilterColumnEntries The number of entries in one filter column to monitor
     */
    public void setNumFilterColumnEntries (final int numFilterColumnEntries)
    {
        this.numFilterColumnEntries = numFilterColumnEntries;
    }


    /**
     * Get the number of search results in the browser to monitor
     *
     * @return The number of search results in the browser to monitor
     */
    public int getNumResults ()
    {
        return this.numResults;
    }


    /**
     * Set the number of search results in the browser to monitor
     *
     * @param numResults The number of search results in the browser to monitor
     */
    public void setNumResults (final int numResults)
    {
        this.numResults = numResults;
    }


    /**
     * Get if the track navigation should be flat.
     *
     * @return True if the track navigation should be flat
     */
    public boolean hasFlatTrackList ()
    {
        return this.hasFlatTrackList;
    }


    /**
     * Set if the track navigation should be flat.
     *
     * @param hasFlatTrackList Don't navigate groups, all tracks are flat
     */
    public void setHasFlatTrackList (final boolean hasFlatTrackList)
    {
        this.hasFlatTrackList = hasFlatTrackList;
    }


    /**
     * Get if the track navigation should include effect and master tracks if flat.
     *
     * @return True if the track navigation should include effect and master tracks if flat
     */
    public boolean hasFullFlatTrackList ()
    {
        return this.hasFullFlatTrackList;
    }


    /**
     * Set if the track navigation should include effect and master tracks if flat.
     *
     * @param hasFullFlatTrackList True if the track navigation should include effect and master
     *            tracks if flat
     */
    public void setHasFullFlatTrackList (final boolean hasFullFlatTrackList)
    {
        this.hasFullFlatTrackList = hasFullFlatTrackList;
    }


    /**
     * Get the number of markers of a page in a markers bank.
     *
     * @return The number of markers of a page in a markers bank
     */
    public int getNumMarkers ()
    {
        return this.numMarkers;
    }


    /**
     * Set the number of markers of a page in a markers bank.
     *
     * @param numMarkers The number of markers of a page in a markers bank
     */
    public void setNumMarkers (final int numMarkers)
    {
        this.numMarkers = numMarkers;
    }


    /**
     * Get the number of user pages.
     *
     * @return The number of user pages
     */
    public int getNumUserPages ()
    {
        return this.numUserPages;
    }


    /**
     * Set the number of user pages.
     *
     * @param numUserPages The number of user pages
     */
    public void setNumUserPages (final int numUserPages)
    {
        this.numUserPages = numUserPages;
    }


    /**
     * Get the number of the size of a user page.
     *
     * @return The number of the size of a user page
     */
    public int getNumUserPageSize ()
    {
        return this.numUserPageSize;
    }


    /**
     * Set the number of the size of a user page.
     *
     * @param numUserPageSize The number of the size of a user page
     */
    public void setNumUserPageSize (final int numUserPageSize)
    {
        this.numUserPageSize = numUserPageSize;
    }


    /**
     * Get if a drum device should be created.
     *
     * @return True if a drum device should be created
     */
    public boolean wantsMainDrumDevice ()
    {
        return this.wantsDrumDevice;
    }


    /**
     * Get if a drum 64 device should be created.
     *
     * @return True if a drum 64 device should be created
     */
    public int [] wantsAdditionalDrumDevices ()
    {
        return this.additionalDrumDevices;
    }


    /**
     * Get the device IDs of additional devices to monitor.
     *
     * @return The device IDs
     */
    public List<DeviceID> getDeviceIDs ()
    {
        return new ArrayList<> (this.deviceIDs);
    }
}
