// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

/**
 * All configuration parameters for the model.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ModelSetup
{
    private int     numTracks              = 8;
    private int     numScenes              = 8;
    private int     numSends               = 8;
    private int     numDevicesInBank       = 8;
    private int     numDeviceLayers        = 8;
    private int     numDrumPadLayers       = 16;
    private int     numParams              = 8;
    private int     numFilterColumnEntries = 16;
    private int     numResults             = 16;
    private boolean hasFlatTrackList       = true;
    private boolean hasFullFlatTrackList   = false;
    private int     numMarkers             = 0;


    /**
     * Constructor.
     */
    public ModelSetup ()
    {
        // Intentionally empty
    }


    /**
     * Get the number of track to monitor (per track bank).
     *
     * @return The number of track to monitor (per track bank)
     */
    public int getNumTracks ()
    {
        return this.numTracks;
    }


    /**
     * Set the number of track to monitor (per track bank).
     *
     * @param numTracks The number of track to monitor (per track bank)
     */
    public void setNumTracks (final int numTracks)
    {
        this.numTracks = numTracks;
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
}
