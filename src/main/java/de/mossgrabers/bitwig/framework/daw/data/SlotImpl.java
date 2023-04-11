// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.data.AbstractItemImpl;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.observer.IValueObserver;

import com.bitwig.extension.controller.api.ClipLauncherSlot;
import com.bitwig.extension.controller.api.ColorValue;


/**
 * Encapsulates the data of a slot.
 *
 * @author Jürgen Moßgraber
 */
public class SlotImpl extends AbstractItemImpl implements ISlot
{
    private final ITrack           track;
    private final ClipLauncherSlot slot;


    /**
     * Constructor.
     *
     * @param track The track which contains the slot
     * @param slot The slot
     * @param index The index of the slot
     */
    public SlotImpl (final ITrack track, final ClipLauncherSlot slot, final int index)
    {
        super (index);

        this.track = track;
        this.slot = slot;

        slot.exists ().markInterested ();
        slot.sceneIndex ().markInterested ();
        slot.name ().markInterested ();
        slot.hasContent ().markInterested ();
        slot.color ().markInterested ();

        // States
        slot.isPlaying ().markInterested ();
        slot.isPlaybackQueued ().markInterested ();
        slot.isRecording ().markInterested ();
        slot.isRecordingQueued ().markInterested ();
        slot.isStopQueued ().markInterested ();
        slot.isSelected ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        Util.setIsSubscribed (this.slot.exists (), enable);
        Util.setIsSubscribed (this.slot.sceneIndex (), enable);
        Util.setIsSubscribed (this.slot.name (), enable);
        Util.setIsSubscribed (this.slot.hasContent (), enable);
        Util.setIsSubscribed (this.slot.color (), enable);
        Util.setIsSubscribed (this.slot.isPlaying (), enable);
        Util.setIsSubscribed (this.slot.isPlaybackQueued (), enable);
        Util.setIsSubscribed (this.slot.isRecording (), enable);
        Util.setIsSubscribed (this.slot.isRecordingQueued (), enable);
        Util.setIsSubscribed (this.slot.isStopQueued (), enable);
        Util.setIsSubscribed (this.slot.isSelected (), enable);
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return this.slot.exists ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public int getPosition ()
    {
        return this.slot.sceneIndex ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.slot.name ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName (final int limit)
    {
        return this.slot.name ().getLimited (limit);
    }


    /** {@inheritDoc} */
    @Override
    public void addNameObserver (final IValueObserver<String> observer)
    {
        this.slot.name ().addValueObserver (observer::update);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSelected ()
    {
        return this.slot.isSelected ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasContent ()
    {
        return this.slot.hasContent ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isRecording ()
    {
        return this.slot.isRecording ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPlaying ()
    {
        return this.slot.isPlaying ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPlayingQueued ()
    {
        return this.slot.isPlaybackQueued ().get () || this.slot.isRecordingQueued ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isRecordingQueued ()
    {
        return this.slot.isRecordingQueued ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isStopQueued ()
    {
        return this.slot.isStopQueued ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColor ()
    {
        // TODO API extension required - https://github.com/teotigraphix/Framework4Bitwig/issues/218
        if (this.track.isGroup ())
            return this.track.getColor ();

        final ColorValue color = this.slot.color ();
        return new ColorEx (color.red (), color.green (), color.blue ());
    }


    /** {@inheritDoc} */
    @Override
    public void setColor (final ColorEx color)
    {
        this.slot.color ().set ((float) color.getRed (), (float) color.getGreen (), (float) color.getBlue ());
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        this.slot.select ();
        this.slot.showInEditor ();
    }


    /** {@inheritDoc} */
    @Override
    public void launch (final boolean isPressed, final boolean isAlternative)
    {
        if (isPressed)
        {
            if (isAlternative)
                this.slot.launchAlt ();
            else
                this.slot.launch ();
            return;
        }

        if (isAlternative)
            this.slot.launchReleaseAlt ();
        else
            this.slot.launchRelease ();
    }


    /** {@inheritDoc} */
    @Override
    public void startRecording ()
    {
        this.slot.record ();
    }


    /** {@inheritDoc} */
    @Override
    public void remove ()
    {
        this.slot.deleteObject ();
    }


    /** {@inheritDoc} */
    @Override
    public void duplicate ()
    {
        this.slot.duplicateClip ();
    }


    /** {@inheritDoc} */
    @Override
    public void paste (final ISlot slot)
    {
        this.slot.replaceInsertionPoint ().copySlotsOrScenes (((SlotImpl) slot).getSlot ());
    }


    /**
     * Get the Bitwig slot.
     *
     * @return The slot
     */
    public ClipLauncherSlot getSlot ()
    {
        return this.slot;
    }
}
