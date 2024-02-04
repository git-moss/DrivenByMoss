// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import com.bitwig.extension.controller.api.ClipLauncherSlot;
import com.bitwig.extension.controller.api.ClipLauncherSlotBank;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.SceneBank;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;

import de.mossgrabers.framework.daw.IClipLauncherNavigator;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;


/**
 * Implementation for a clip launcher navigator.
 *
 * @author Jürgen Moßgraber
 */
public class ClipLauncherNavigatorImpl implements IClipLauncherNavigator
{
    private final ControllerHost   host;
    private final IModel           model;
    private final CursorTrack      cursorTrack;
    private final ClipLauncherSlot theClip;
    private final Track            theTrack;
    protected TrackBank            singleTrackBank;
    protected SceneBank            sceneBank;


    /**
     * Constructor.
     *
     * @param host The controller host
     * @param model The model
     */
    ClipLauncherNavigatorImpl (final ControllerHost host, final IModel model)
    {
        this.host = host;
        this.model = model;

        this.singleTrackBank = host.createTrackBank (1, 0, 1);
        this.singleTrackBank.scrollPosition ().markInterested ();
        this.cursorTrack = host.createCursorTrack (1, 1);
        this.singleTrackBank.followCursorTrack (this.cursorTrack);
        this.theTrack = this.singleTrackBank.getItemAt (0);

        final ClipLauncherSlotBank slotBank = this.theTrack.clipLauncherSlotBank ();
        this.singleTrackBank.setShouldShowClipLauncherFeedback (true);
        this.theClip = slotBank.getItemAt (0);

        this.theClip.sceneIndex ().addValueObserver (position -> {
            final ISceneBank isceneBank = this.model.getSceneBank ();
            final int pageSize = isceneBank.getPageSize ();
            final int scrollPosition = isceneBank.getScrollPosition ();
            final int newPosition = position / pageSize * pageSize;
            if (scrollPosition != newPosition)
                isceneBank.scrollTo (newPosition);
            isceneBank.getItem (position % pageSize).select ();
        });

        this.sceneBank = this.singleTrackBank.sceneBank ();
        this.sceneBank.cursorIndex ().markInterested ();
        this.sceneBank.setIndication (false);
    }


    /** {@inheritDoc} */
    @Override
    public void navigateScenes (final boolean isLeft)
    {
        this.navigateClips (isLeft);
        this.host.scheduleTask ( () -> this.sceneBank.getItemAt (0).selectInEditor (), 100);
    }


    /** {@inheritDoc} */
    @Override
    public void navigateClips (final boolean isLeft)
    {
        if (isLeft)
            this.sceneBank.scrollBackwards ();
        else
            this.sceneBank.scrollForwards ();
        this.theClip.select ();
        this.theClip.showInEditor ();
    }


    /** {@inheritDoc} */
    @Override
    public void navigateTracks (final boolean isLeft)
    {
        if (isLeft)
            this.singleTrackBank.scrollBackwards ();
        else
            this.singleTrackBank.scrollForwards ();
        // this.singleTrackBank.scrollBy (isLeft ? -1 : 1);
        this.theClip.select ();
        this.theClip.showInEditor ();

        this.theTrack.selectInEditor ();
        this.theTrack.selectInMixer ();
        this.theTrack.makeVisibleInArranger ();
        this.theTrack.makeVisibleInMixer ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectTrack (final int index)
    {
        final ITrackBank trackBank = this.model.getTrackBank ();
        final ITrack track = trackBank.getItem (0);
        if (!track.doesExist ())
            return;
        this.singleTrackBank.scrollPosition ().set (track.getPosition () + index);
        trackBank.getItem (index).select ();
    }
}
