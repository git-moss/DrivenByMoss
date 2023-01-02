// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.view;

import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IVirtualFader;
import de.mossgrabers.framework.controller.grid.IVirtualFaderCallback;
import de.mossgrabers.framework.controller.grid.VirtualFaderImpl;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * 8 volume faders.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class VolumeView extends AbstractFaderView implements IVirtualFaderCallback
{
    private final IVirtualFader masterFader;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public VolumeView (final LaunchpadControlSurface surface, final IModel model)
    {
        super ("Volume", surface, model);

        this.masterFader = new VirtualFaderImpl (model.getHost (), this);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        this.model.getCurrentTrackBank ().getItem (index).setVolume (value);
    }


    /** {@inheritDoc} */
    @Override
    protected int getFaderValue (final int index)
    {
        return this.model.getCurrentTrackBank ().getItem (index).getVolume ();
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        for (int i = 0; i < 8; i++)
            this.setupFader (i);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final IMasterTrack track = this.model.getMasterTrack ();

        final int color = track.doesExist () ? this.colorManager.getColorIndex (DAWColor.getColorID (track.getColor ())) : 0;
        this.masterFader.setup (color, false);
        this.masterFader.setValue (track.getVolume ());

        final int index = 7 - (buttonID.ordinal () - ButtonID.SCENE1.ordinal ());
        return this.masterFader.getColorState (index);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN)
            return;
        final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        this.masterFader.moveTo (7 - index, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void setupFader (final int index)
    {
        final ITrack track = this.model.getCurrentTrackBank ().getItem (index);
        final int color = this.colorManager.getColorIndex (DAWColor.getColorID (track.getColor ()));
        this.surface.setupFader (index, color, false);
        this.surface.setFaderValue (index, track.getVolume ());
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        return this.model.getMasterTrack ().getVolume ();
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final int value)
    {
        this.model.getMasterTrack ().setVolume (value);
    }
}