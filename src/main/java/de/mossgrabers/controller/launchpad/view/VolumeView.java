// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.view;

import de.mossgrabers.controller.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * 8 volume faders.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class VolumeView extends AbstractFaderView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public VolumeView (final LaunchpadControlSurface surface, final IModel model)
    {
        super (surface, model);
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
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack track = tb.getItem (i);
            final int color = track.doesExist () ? this.colorManager.getColorIndex (DAWColor.getColorIndex (track.getColor ())) : 0;
            if (this.trackColors[i] != color)
            {
                this.trackColors[i] = color;
                this.setupFader (i);
            }
            this.surface.setFaderValue (i, track.getVolume ());
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final int scene = 7 - (buttonID.ordinal () - ButtonID.SCENE1.ordinal ());

        final IMasterTrack track = this.model.getMasterTrack ();
        final int sceneMax = 9 * track.getVolume () / this.model.getValueChanger ().getUpperBound ();
        final int color = track.doesExist () ? this.colorManager.getColorIndex (DAWColor.getColorIndex (track.getColor ())) : 0;
        return scene < sceneMax ? color : LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN)
            return;
        final IMasterTrack track = this.model.getMasterTrack ();
        final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        track.setVolume (Math.min (127, (7 - index) * this.model.getValueChanger ().getUpperBound () / 7));
    }
}