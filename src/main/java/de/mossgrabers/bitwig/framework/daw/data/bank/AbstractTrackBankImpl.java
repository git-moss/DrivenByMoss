// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data.bank;

import de.mossgrabers.bitwig.framework.daw.ApplicationImpl;
import de.mossgrabers.bitwig.framework.daw.data.CursorTrackImpl;
import de.mossgrabers.bitwig.framework.daw.data.TrackImpl;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IDeviceMetadata;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.observer.IIndexedValueObserver;

import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;

import java.util.Collections;
import java.util.List;


/**
 * An abstract track bank.
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractTrackBankImpl extends AbstractChannelBankImpl<TrackBank, ITrack> implements ITrackBank
{
    protected IApplication          application;
    protected final CursorTrackImpl cursorTrack;
    protected final Track           rootGroup;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param bank The bank to encapsulate
     * @param cursorTrack The cursor track assigned to this track bank
     * @param rootGroup The root track
     * @param application The application
     * @param numTracks The number of tracks of a bank page
     * @param numScenes The number of scenes of a bank page
     * @param numSends The number of sends of a bank page
     */
    protected AbstractTrackBankImpl (final IHost host, final IValueChanger valueChanger, final TrackBank bank, final CursorTrackImpl cursorTrack, final Track rootGroup, final ApplicationImpl application, final int numTracks, final int numScenes, final int numSends)
    {
        super (host, valueChanger, bank, numTracks, numScenes, numSends);

        this.application = application;
        this.cursorTrack = cursorTrack;
        this.rootGroup = rootGroup;

        if (this.bank.isEmpty ())
            return;

        final TrackBank trackBank = this.bank.get ();

        for (int i = 0; i < this.getPageSize (); i++)
            this.items.add (new TrackImpl (host, valueChanger, application, (CursorTrack) cursorTrack.getTrack (), rootGroup, trackBank.getItemAt (i), i, this.numSends, this.numScenes));

        this.sceneBank = new SceneBankImpl (host, valueChanger, this.numScenes == 0 ? null : trackBank.sceneBank (), this.numScenes, cursorTrack);

        // Note: cursorIndex is defined for all banks but currently only works for track banks
        trackBank.cursorIndex ().addValueObserver (this::handleBankSelection);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        super.enableObservers (enable);

        this.sceneBank.enableObservers (enable);
    }


    /** {@inheritDoc} */
    @Override
    public void stop (final boolean isAlternative)
    {
        if (this.sceneBank != null)
            this.sceneBank.stop (isAlternative);
    }


    /** {@inheritDoc} */
    @Override
    public void setIndication (final boolean enable)
    {
        if (this.bank.isEmpty ())
            return;

        final TrackBank trackBank = this.bank.get ();
        trackBank.setShouldShowClipLauncherFeedback (enable);
    }


    /** {@inheritDoc} */
    @Override
    public void addNameObserver (final IIndexedValueObserver<String> observer)
    {
        for (int index = 0; index < this.getPageSize (); index++)
        {
            final int i = index;
            this.getItem (index).addNameObserver (name -> observer.update (i, name));
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean isClipRecording ()
    {
        for (int t = 0; t < this.getPageSize (); t++)
        {
            final ISlotBank slotBank = this.items.get (t).getSlotBank ();
            for (int s = 0; s < this.numScenes; s++)
            {
                if (slotBank.getItem (s).isRecording ())
                    return true;
            }
        }
        return false;
    }


    /**
     * Handles bank selection changes. Notifies all registered observers.
     *
     * @param index The index of the newly de-/selected item
     */
    private void handleBankSelection (final int index)
    {
        for (int i = 0; i < this.getPageSize (); i++)
        {
            final boolean isSelected = index == i;
            final ITrack item = this.getItem (i);
            if (item.isSelected () != isSelected)
            {
                item.setSelected (isSelected);
                this.notifySelectionObservers (i, isSelected);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void addChannel (final ChannelType type)
    {
        this.addChannel (type, null);
    }


    /** {@inheritDoc} */
    @Override
    public void addChannel (final ChannelType type, final String name)
    {
        this.addChannel (type, name, DAWColor.getNextColor ().getColor ());
    }


    /** {@inheritDoc} */
    @Override
    public void addChannel (final ChannelType type, final String name, final ColorEx color)
    {
        this.addChannel (type, name, color, Collections.emptyList ());
    }


    /** {@inheritDoc} */
    @Override
    public void addChannel (final ChannelType type, final String name, final List<IDeviceMetadata> devices)
    {
        this.addChannel (type, name, DAWColor.getNextColor ().getColor (), devices);
    }


    private void addChannel (final ChannelType type, final String name, final ColorEx color, final List<IDeviceMetadata> devices)
    {
        this.addTrack (type);

        if (name == null && color == null)
            return;

        this.host.scheduleTask ( () -> {

            if (!this.cursorTrack.doesExist ())
                return;
            if (name != null)
                this.cursorTrack.setName (name);
            if (color != null)
                this.cursorTrack.setColor (color);

            this.bank.get ().scrollIntoView (this.cursorTrack.getPosition ());

            for (final IDeviceMetadata device: devices)
                this.cursorTrack.addDevice (device);

        }, 300);
    }


    /**
     * Adds a new track to this track bank.
     *
     * @param type The type of the track to add
     */
    protected void addTrack (final ChannelType type)
    {
        switch (type)
        {
            case HYBRID, INSTRUMENT:
                this.application.addInstrumentTrack ();
                break;

            case EFFECT:
                this.application.addEffectTrack ();
                break;

            default:
            case AUDIO:
                this.application.addAudioTrack ();
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean canEditSend (final int sendIndex)
    {
        return this.getItem (0).getSendBank ().getItem (sendIndex).doesExist ();
    }


    /** {@inheritDoc} */
    @Override
    public String getEditSendName (final int sendIndex)
    {
        return this.getItem (0).getSendBank ().getItem (sendIndex).getName ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleRecArm ()
    {
        if (this.items.isEmpty ())
            return;

        final boolean state = !this.items.get (0).isRecArm ();
        for (final ITrack track: this.items)
            track.setRecArm (state);
    }
}