// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.daw.data.TrackImpl;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.observer.IIndexedValueObserver;

import com.bitwig.extension.controller.api.ClipLauncherSlotBank;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;


/**
 * An abstract track bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractTrackBankImpl extends AbstractChannelBankImpl<TrackBank, ITrack> implements ITrackBank
{
    private final ApplicationImpl application;
    protected final CursorTrack   cursorTrack;
    private final Track           rootGroup;


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
    public AbstractTrackBankImpl (final IHost host, final IValueChanger valueChanger, final TrackBank bank, final CursorTrack cursorTrack, final Track rootGroup, final ApplicationImpl application, final int numTracks, final int numScenes, final int numSends)
    {
        super (host, valueChanger, bank, numTracks, numScenes, numSends);

        this.application = application;
        this.cursorTrack = cursorTrack;
        this.rootGroup = rootGroup;

        this.initItems ();

        this.sceneBank = new SceneBankImpl (host, valueChanger, this.numScenes == 0 ? null : this.bank.sceneBank (), this.numScenes);
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
    public void setIndication (final boolean enable)
    {
        for (int index = 0; index < this.getPageSize (); index++)
        {
            final ClipLauncherSlotBank bank = this.bank.getItemAt (index).clipLauncherSlotBank ();
            if (bank != null)
                bank.setIndication (enable);
        }
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
            for (int s = 0; s < this.numScenes; s++)
            {
                if (this.items.get (t).getSlotBank ().getItem (s).isRecording ())
                    return true;
            }
        }
        return false;
    }


    /**
     * Create all track data and setup observers.
     */
    @Override
    protected void initItems ()
    {
        for (int i = 0; i < this.pageSize; i++)
            this.items.add (new TrackImpl (this.host, this.valueChanger, this.application, this.cursorTrack, this.rootGroup, this.bank.getItemAt (i), i, this.numSends, this.numScenes));
    }
}