// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
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
    protected final IHost         host;
    protected final Scales        scales;
    protected final ColorManager  colorManager;
    protected final IValueChanger valueChanger;
    protected final ModelSetup    modelSetup;

    protected IApplication        application;
    protected IMixer              mixer;
    protected ITransport          transport;
    protected IGroove             groove;
    protected IProject            project;
    protected IBrowser            browser;
    protected IArranger           arranger;
    protected IMarkerBank         markerBank;
    protected ITrackBank          currentTrackBank;
    protected ITrackBank          trackBank;
    protected ITrackBank          effectTrackBank;
    protected IMasterTrack        masterTrack;
    protected ICursorDevice       instrumentDevice;
    protected ICursorDevice       cursorDevice;
    protected ICursorDevice       drumDevice64;
    protected IParameterBank      userParameterBank;
    protected Map<String, IClip>  cursorClips = new HashMap<> ();

    private int                   lastSelection;


    /**
     * Constructor.
     *
     * @param modelSetup The configuration parameters for the model
     * @param dataSetup Some setup variables
     * @param scales The scales object
     */
    public AbstractModel (final ModelSetup modelSetup, final DataSetup dataSetup, final Scales scales)
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
    public ICursorDevice getInstrumentDevice ()
    {
        return this.instrumentDevice;
    }


    /** {@inheritDoc} */
    @Override
    public ICursorDevice getDrumDevice64 ()
    {
        return this.drumDevice64;
    }


    /** {@inheritDoc} */
    @Override
    public IParameterBank getUserParameterBank ()
    {
        return this.userParameterBank;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleCurrentTrackBank ()
    {
        if (this.effectTrackBank == null)
            return;

        final ITrack selectedItem = this.getCurrentTrackBank ().getSelectedItem ();
        final int selPosition = selectedItem == null ? -1 : selectedItem.getPosition ();
        this.currentTrackBank = this.currentTrackBank == this.trackBank ? this.effectTrackBank : this.trackBank;
        this.currentTrackBank.selectItemAtPosition (this.lastSelection);
        this.lastSelection = selPosition;
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
        // Is a "normal" track selected?
        ITrackBank tb = this.getTrackBank ();
        ITrack sel = tb.getSelectedItem ();
        if (sel != null)
            return sel;

        // Is an effect track selected?
        tb = this.getEffectTrackBank ();
        if (tb != null)
        {
            sel = tb.getSelectedItem ();
            if (sel != null)
                return sel;
        }

        // Is the master track selected?
        return this.masterTrack.isSelected () ? this.masterTrack : null;
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
        if (selectedTrack == null || !selectedTrack.canHoldAudioData ())
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
