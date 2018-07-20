// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.scale.Scales;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Base class for DAW models.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractModel implements IModel
{
    protected IHost                    host;
    protected IApplication             application;
    protected IArranger                arranger;
    protected IMixer                   mixer;
    protected ITransport               transport;
    protected IGroove                  groove;
    protected IProject                 project;
    protected IBrowser                 browser;

    protected ITrackBank               currentTrackBank;
    protected ITrackBank               trackBank;
    protected ITrackBank               effectTrackBank;
    protected IMasterTrack             masterTrack;

    protected ICursorDevice            primaryDevice;
    protected ICursorDevice            cursorDevice;
    protected ICursorDevice            drumDevice64;
    protected Map<String, ICursorClip> cursorClips = new HashMap<> ();

    protected Scales                   scales;
    protected ColorManager             colorManager;
    protected IValueChanger            valueChanger;

    protected int                      numTracks;
    protected int                      numScenes;
    protected int                      numSends;
    protected int                      numFilterColumnEntries;
    protected int                      numResults;
    protected int                      numParams;
    protected int                      numDevicesInBank;
    protected int                      numDeviceLayers;
    protected int                      numDrumPadLayers;
    protected boolean                  hasFlatTrackList;


    /**
     * Constructor.
     *
     * @param colorManager The color manager
     * @param valueChanger The value changer
     * @param scales The scales object
     * @param numTracks The number of track to monitor (per track bank)
     * @param numScenes The number of scenes to monitor (per scene bank)
     * @param numSends The number of sends to monitor
     * @param numFilterColumnEntries The number of entries in one filter column to monitor
     * @param numResults The number of search results in the browser to monitor
     * @param hasFlatTrackList Don't navigate groups, all tracks are flat
     * @param numParams The number of parameter of a device to monitor
     * @param numDevicesInBank The number of devices to monitor
     * @param numDeviceLayers The number of device layers to monitor
     * @param numDrumPadLayers The number of drum pad layers to monitor
     */
    public AbstractModel (final ColorManager colorManager, final IValueChanger valueChanger, final Scales scales, final int numTracks, final int numScenes, final int numSends, final int numFilterColumnEntries, final int numResults, final boolean hasFlatTrackList, final int numParams, final int numDevicesInBank, final int numDeviceLayers, final int numDrumPadLayers)
    {
        this.colorManager = colorManager;
        this.valueChanger = valueChanger;
        this.scales = scales;

        this.numTracks = numTracks < 0 ? 8 : numTracks;
        this.numScenes = numScenes < 0 ? 8 : numScenes;
        this.numSends = numSends < 0 ? 6 : numSends;
        this.numFilterColumnEntries = numFilterColumnEntries < 0 ? 16 : numFilterColumnEntries;
        this.numResults = numResults < 0 ? 16 : numResults;
        this.numParams = numParams >= 0 ? numParams : 8;
        this.numDevicesInBank = numDevicesInBank >= 0 ? numDevicesInBank : 8;
        this.numDeviceLayers = numDeviceLayers >= 0 ? numDeviceLayers : 8;
        this.numDrumPadLayers = numDrumPadLayers >= 0 ? numDrumPadLayers : 16;
        this.hasFlatTrackList = hasFlatTrackList ? true : false;
    }


    /** {@inheritDoc} */
    @Override
    public IHost getHost ()
    {
        return this.host;
    }


    /** {@inheritDoc} */
    @Override
    public IValueChanger getValueChanger ()
    {
        return this.valueChanger;
    }


    /** {@inheritDoc} */
    @Override
    public IArranger getArranger ()
    {
        return this.arranger;
    }


    /** {@inheritDoc} */
    @Override
    public IMixer getMixer ()
    {
        return this.mixer;
    }


    /** {@inheritDoc} */
    @Override
    public ColorManager getColorManager ()
    {
        return this.colorManager;
    }


    /** {@inheritDoc} */
    @Override
    public IProject getProject ()
    {
        return this.project;
    }


    /** {@inheritDoc} */
    @Override
    public ITransport getTransport ()
    {
        return this.transport;
    }


    /** {@inheritDoc} */
    @Override
    public IGroove getGroove ()
    {
        return this.groove;
    }


    /** {@inheritDoc} */
    @Override
    public IMasterTrack getMasterTrack ()
    {
        return this.masterTrack;
    }


    /** {@inheritDoc} */
    @Override
    public Scales getScales ()
    {
        return this.scales;
    }


    /** {@inheritDoc} */
    @Override
    public ICursorDevice getCursorDevice ()
    {
        return this.cursorDevice;
    }


    /** {@inheritDoc} */
    @Override
    public ICursorDevice getPrimaryDevice ()
    {
        return this.primaryDevice;
    }


    /** {@inheritDoc} */
    @Override
    public ICursorDevice getDrumDevice64 ()
    {
        return this.drumDevice64;
    }


    /** {@inheritDoc} */
    @Override
    public ICursorClip getCursorClip ()
    {
        return this.getCursorClip (this.numTracks, this.numScenes);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleCurrentTrackBank ()
    {
        this.currentTrackBank = this.currentTrackBank == this.trackBank && this.effectTrackBank != null ? this.effectTrackBank : this.trackBank;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isEffectTrackBankActive ()
    {
        return this.currentTrackBank == this.effectTrackBank;
    }


    /** {@inheritDoc} */
    @Override
    public ITrackBank getCurrentTrackBank ()
    {
        return this.currentTrackBank;
    }


    /** {@inheritDoc} */
    @Override
    public ITrackBank getTrackBank ()
    {
        return this.trackBank;
    }


    /** {@inheritDoc} */
    @Override
    public ITrackBank getEffectTrackBank ()
    {
        return this.effectTrackBank;
    }


    /** {@inheritDoc} */
    @Override
    public IApplication getApplication ()
    {
        return this.application;
    }


    /** {@inheritDoc} */
    @Override
    public IBrowser getBrowser ()
    {
        return this.browser;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasSelectedDevice ()
    {
        return this.cursorDevice.doesExist ();
    }


    /** {@inheritDoc} */
    @Override
    public ISceneBank getSceneBank ()
    {
        return this.trackBank.getSceneBank ();
    }


    /** {@inheritDoc} */
    @Override
    public void createClip (final ISlot slot, final int clipLength)
    {
        slot.create ((int) (clipLength < 2 ? Math.pow (2, clipLength) : Math.pow (2, clipLength - 2.0) * this.transport.getQuartersPerMeasure ()));
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasRecordingState ()
    {
        return this.transport.isRecording () || this.transport.isLauncherOverdub () || this.currentTrackBank.isClipRecording ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canSelectedTrackHoldNotes ()
    {
        final ITrack t = this.getCurrentTrackBank ().getSelectedItem ();
        return t != null && t.canHoldNotes ();
    }


    /** {@inheritDoc} */
    @Override
    public ITrack getSelectedTrack ()
    {
        final ITrackBank tb = this.getCurrentTrackBank ();
        return tb == null ? null : tb.getSelectedItem ();
    }


    /** {@inheritDoc} */
    @Override
    public ISlot getSelectedSlot ()
    {
        final ITrack track = this.getSelectedTrack ();
        return track == null ? null : track.getSlotBank ().getSelectedItem ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canConvertClip ()
    {
        final ITrack selectedTrack = this.getSelectedTrack ();
        if (selectedTrack == null)
            return false;
        final List<ISlot> slots = selectedTrack.getSlotBank ().getSelectedItems ();
        if (slots.isEmpty ())
            return false;
        for (final ISlot slot: slots)
        {
            if (slot.hasContent ())
                return true;
        }
        return false;
    }
}
