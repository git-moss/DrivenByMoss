// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad.view;

import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.launchpad.controller.LaunchpadColors;
import de.mossgrabers.launchpad.controller.LaunchpadControlSurface;


/**
 * Edit remote parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceView extends AbstractFaderView
{
    private ICursorDevice cursorDevice;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DeviceView (final LaunchpadControlSurface surface, final IModel model)
    {
        super (surface, model);
        this.cursorDevice = this.model.getCursorDevice ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    protected void delayedUpdateArrowButtons ()
    {
        final boolean hasClips = this.model.getHost ().hasClips ();
        this.surface.setButton (this.surface.getSessionButton (), hasClips ? LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setButton (this.surface.getNoteButton (), LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        this.surface.setButton (this.surface.getDeviceButton (), LaunchpadColors.LAUNCHPAD_COLOR_AMBER);
        this.surface.setButton (this.surface.getUserButton (), LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
    }


    /** {@inheritDoc} */
    @Override
    public void setupFader (final int index)
    {
        this.surface.setupFader (index, LaunchpadColors.BITWIG_INDICATOR_COLORS[index]);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        this.cursorDevice.setParameter (index, value);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        for (int i = 0; i < 8; i++)
        {
            final IParameter param = this.cursorDevice.getFXParam (i);
            this.surface.getOutput ().sendCC (LaunchpadControlSurface.LAUNCHPAD_FADER_1 + i, param.getValue ());
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        // Intentionally empty
    }
}