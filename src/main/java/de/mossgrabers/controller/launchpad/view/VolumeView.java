// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.view;

import de.mossgrabers.controller.launchpad.controller.LaunchpadColors;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.DAWColors;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
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
    public void drawGrid ()
    {
        final ColorManager cm = this.model.getColorManager ();
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final IMidiOutput output = this.surface.getOutput ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack track = tb.getItem (i);
            final int color = cm.getColor (DAWColors.getColorIndex (track.getColor ()));
            if (this.trackColors[i] != color)
            {
                this.trackColors[i] = color;
                this.setupFader (i);
            }
            output.sendCC (LaunchpadControlSurface.LAUNCHPAD_FADER_1 + i, track.getVolume ());
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        final ColorManager cm = this.model.getColorManager ();
        final IMasterTrack track = this.model.getMasterTrack ();
        final int sceneMax = 9 * track.getVolume () / this.model.getValueChanger ().getUpperBound ();
        for (int i = 0; i < 8; i++)
        {
            final int color = cm.getColor (DAWColors.getColorIndex (track.getColor ()));
            this.surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE8 + 10 * i, i < sceneMax ? color : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int scene, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final IMasterTrack track = this.model.getMasterTrack ();
        track.setVolume (Math.min (127, (7 - scene) * this.model.getValueChanger ().getUpperBound () / 7));
    }
}