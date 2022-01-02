// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.bitwig.framework.daw.ApplicationImpl;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.ICursorTrack;

import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.SettableBooleanValue;
import com.bitwig.extension.controller.api.Track;


/**
 * The cursor track.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CursorTrackImpl extends TrackImpl implements ICursorTrack
{
    private final SettableBooleanValue isPinnedAttr;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The valueChanger
     * @param application The application
     * @param cursorTrack The cursor track
     * @param rootGroup The root track
     * @param numSends The number of sends of a bank
     * @param numScenes The number of scenes of a bank
     */
    public CursorTrackImpl (final IHost host, final IValueChanger valueChanger, final CursorTrack cursorTrack, final Track rootGroup, final ApplicationImpl application, final int numSends, final int numScenes)
    {
        super (host, valueChanger, application, cursorTrack, rootGroup, cursorTrack, -1, numSends, numScenes);

        this.isPinnedAttr = cursorTrack.isPinned ();

        this.isPinnedAttr.markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        super.enableObservers (enable);

        Util.setIsSubscribed (this.isPinnedAttr, enable);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPinned ()
    {
        return this.isPinnedAttr.get ();
    }


    /** {@inheritDoc} */
    @Override
    public void togglePinned ()
    {
        this.isPinnedAttr.toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void setPinned (final boolean isPinned)
    {
        this.isPinnedAttr.set (isPinned);
    }
}