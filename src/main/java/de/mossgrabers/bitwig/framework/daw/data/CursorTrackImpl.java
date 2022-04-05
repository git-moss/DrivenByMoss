// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.bitwig.framework.daw.ApplicationImpl;
import de.mossgrabers.bitwig.framework.daw.ModelImpl;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.ICursorTrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;

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
    private final ModelImpl            model;
    private final SettableBooleanValue isPinnedAttr;


    /**
     * Constructor.
     * 
     * @param model The model to retrieve the current track bank
     * @param host The DAW host
     * @param valueChanger The valueChanger
     * @param application The application
     * @param cursorTrack The cursor track
     * @param rootGroup The root track
     * @param numSends The number of sends of a bank
     * @param numScenes The number of scenes of a bank
     */
    public CursorTrackImpl (final ModelImpl model, final IHost host, final IValueChanger valueChanger, final CursorTrack cursorTrack, final Track rootGroup, final ApplicationImpl application, final int numSends, final int numScenes)
    {
        super (host, valueChanger, application, cursorTrack, rootGroup, cursorTrack, -1, numSends, numScenes);

        this.model = model;

        this.isPinnedAttr = cursorTrack.isPinned ();
        this.isPinnedAttr.markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        super.enableObservers (enable);

        Util.setIsSubscribed (this.cursorTrack.hasPrevious (), enable);
        Util.setIsSubscribed (this.cursorTrack.hasNext (), enable);
        Util.setIsSubscribed (this.isPinnedAttr, enable);
    }


    /** {@inheritDoc} */
    @Override
    public int getIndex ()
    {
        return this.getPosition () % this.model.getCurrentTrackBank ().getPageSize ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPrevious ()
    {
        this.cursorTrack.selectPrevious ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canSelectPrevious ()
    {
        return this.cursorTrack.hasPrevious ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canSelectNext ()
    {
        return this.cursorTrack.hasNext ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNext ()
    {
        this.cursorTrack.selectNext ();
    }


    /** {@inheritDoc} */
    @Override
    public void swapWithPrevious ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        if (tb == null)
            return;
        final int index = this.getIndex ();
        if (index == 0)
            return;
        final TrackImpl prevTrack = (TrackImpl) tb.getItem (index - 1);
        this.track.afterTrackInsertionPoint ().moveTracks (prevTrack.track);
        prevTrack.track.selectInEditor ();
    }


    /** {@inheritDoc} */
    @Override
    public void swapWithNext ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        if (tb == null)
            return;
        final int index = this.getIndex ();
        if (index == tb.getPageSize () - 1)
            return;
        final TrackImpl nextTrack = (TrackImpl) tb.getItem (index + 1);
        this.track.beforeTrackInsertionPoint ().moveTracks (nextTrack.track);
        nextTrack.track.selectInEditor ();
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