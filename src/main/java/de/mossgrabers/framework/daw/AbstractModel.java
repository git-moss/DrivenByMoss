// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ICursorTrack;
import de.mossgrabers.framework.daw.data.IDrumDevice;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IMarkerBank;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.observer.IValueObserver;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.FrameworkException;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


/**
 * Base class for DAW models.
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractModel implements IModel
{
    protected final IHost                           host;
    protected final Scales                          scales;
    protected final ColorManager                    colorManager;
    protected final IValueChanger                   valueChanger;
    protected final ModelSetup                      modelSetup;
    protected final Set<IValueObserver<ITrackBank>> trackBankObservers    = new HashSet<> ();

    protected IApplication                          application;
    protected IMixer                                mixer;
    protected ITransport                            transport;
    protected IGroove                               groove;
    protected IProject                              project;
    protected IBrowser                              browser;
    protected IArranger                             arranger;
    protected IMarkerBank                           markerBank;
    protected ITrackBank                            currentTrackBank;
    protected ITrackBank                            trackBank;
    protected ITrackBank                            effectTrackBank;
    protected ICursorTrack                          cursorTrack;
    protected IMasterTrack                          masterTrack;
    protected ICursorDevice                         cursorDevice;
    protected IDrumDevice                           drumDevice;
    protected Map<Integer, IDrumDevice>             additionalDrumDevices = new HashMap<> ();
    protected Map<String, INoteClip>                cursorClips           = new HashMap<> ();
    protected final Map<DeviceID, ISpecificDevice>  specificDevices       = new EnumMap<> (DeviceID.class);

    private int                                     lastSelection;


    /**
     * Constructor.
     *
     * @param modelSetup The configuration parameters for the model
     * @param dataSetup Some setup variables
     * @param scales The scales object
     */
    protected AbstractModel (final ModelSetup modelSetup, final DataSetup dataSetup, final Scales scales)
    {
        this.modelSetup = modelSetup;
        this.host = dataSetup.getHost ();
        this.colorManager = dataSetup.getColorManager ();
        this.valueChanger = dataSetup.getValueChanger ();
        this.scales = scales;
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
    public IMarkerBank getMarkerBank ()
    {
        return this.markerBank;
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
    public ICursorTrack getCursorTrack ()
    {
        return this.cursorTrack;
    }


    /** {@inheritDoc} */
    @Override
    public ISpecificDevice getSpecificDevice (final DeviceID deviceID)
    {
        return this.specificDevices.get (deviceID);
    }


    /** {@inheritDoc} */
    @Override
    public IDrumDevice getDrumDevice ()
    {
        return this.drumDevice;
    }


    /** {@inheritDoc} */
    @Override
    public IDrumDevice getDrumDevice (final int pageSize)
    {
        final IDrumDevice additionalDrumDevice = this.additionalDrumDevices.get (Integer.valueOf (pageSize));
        if (additionalDrumDevice == null)
            throw new FrameworkException ("Additional drum device of size " + pageSize + " was not configured!");
        return additionalDrumDevice;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleCurrentTrackBank ()
    {
        if (this.effectTrackBank == null)
            return;

        final Optional<ITrack> selectedItem = this.getCurrentTrackBank ().getSelectedItem ();
        final int selPosition = selectedItem.isEmpty () ? -1 : selectedItem.get ().getPosition ();
        this.currentTrackBank = this.currentTrackBank == this.trackBank ? this.effectTrackBank : this.trackBank;
        this.currentTrackBank.selectItemAtPosition (this.lastSelection);
        this.lastSelection = selPosition;

        this.trackBankObservers.forEach (observer -> observer.update (this.currentTrackBank));
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
    public void addTrackBankObserver (final IValueObserver<ITrackBank> observer)
    {
        this.trackBankObservers.add (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void removeTrackBankObserver (final IValueObserver<ITrackBank> observer)
    {
        this.trackBankObservers.remove (observer);
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
    public boolean hasRecordingState ()
    {
        return this.transport.isRecording () || this.transport.isLauncherOverdub () || this.currentTrackBank.isClipRecording ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canSelectedTrackHoldNotes ()
    {
        final Optional<ITrack> t = this.getCurrentTrackBank ().getSelectedItem ();
        return t.isPresent () && t.get ().canHoldNotes ();
    }


    /** {@inheritDoc} */
    @Override
    public Optional<ISlot> getSelectedSlot ()
    {
        final ITrack track = this.getCursorTrack ();
        return track == null ? Optional.empty () : track.getSlotBank ().getSelectedItem ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canConvertClip ()
    {
        final ITrack selectedTrack = this.getCursorTrack ();
        if (!selectedTrack.doesExist () || !selectedTrack.canHoldAudioData ())
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
