// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad.view;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.BitwigColors;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.launchpad.controller.LaunchpadColors;
import de.mossgrabers.launchpad.controller.LaunchpadControlSurface;


/**
 * 8 send faders.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendsView extends AbstractFaderView
{
    private int selectedSend;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SendsView (final LaunchpadControlSurface surface, final IModel model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        if (!this.model.isEffectTrackBankActive ())
            this.model.getTrackBank ().setSend (index, this.selectedSend, value);
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int scene, final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.selectedSend = scene;
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final ColorManager cm = this.model.getColorManager ();
        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final IMidiOutput output = this.surface.getOutput ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack track = tb.getTrack (i);
            final ISend send = track.getSends ()[this.selectedSend];
            final int color = cm.getColor (BitwigColors.getColorIndex (track.getColor ()));
            if (this.trackColors[i] != color || !track.doesExist () || send.getName ().isEmpty ())
                this.setupFader (i);
            this.trackColors[i] = color;
            output.sendCC (LaunchpadControlSurface.LAUNCHPAD_FADER_1 + i, send.getValue ());
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE1, this.selectedSend == 0 ? LaunchpadColors.LAUNCHPAD_COLOR_ORCHID : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE2, this.selectedSend == 1 ? LaunchpadColors.LAUNCHPAD_COLOR_ORCHID : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE3, this.selectedSend == 2 ? LaunchpadColors.LAUNCHPAD_COLOR_ORCHID : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE4, this.selectedSend == 3 ? LaunchpadColors.LAUNCHPAD_COLOR_ORCHID : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE5, this.selectedSend == 4 ? LaunchpadColors.LAUNCHPAD_COLOR_ORCHID : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE6, this.selectedSend == 5 ? LaunchpadColors.LAUNCHPAD_COLOR_ORCHID : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE7, this.selectedSend == 6 ? LaunchpadColors.LAUNCHPAD_COLOR_ORCHID : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE8, this.selectedSend == 7 ? LaunchpadColors.LAUNCHPAD_COLOR_ORCHID : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
    }


    /**
     * Get the selected send channel.
     *
     * @return The number of the channel
     */
    public int getSelectedSend ()
    {
        return this.selectedSend;
    }
}