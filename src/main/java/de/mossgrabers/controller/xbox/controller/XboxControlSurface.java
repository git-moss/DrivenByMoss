// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.xbox.controller;

import de.mossgrabers.controller.xbox.XboxConfiguration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;


/**
 * The Xbox game controler surface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class XboxControlSurface extends AbstractControlSurface<XboxConfiguration>
{
    /** The TODO button. */
    public static final int     XBOX_BUTTON_TODO = 0;

    private static final int [] XBOX_BUTTONS_ALL = {};


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     */
    public XboxControlSurface (final IHost host, final ColorManager colorManager, final XboxConfiguration configuration)
    {
        super (host, configuration, colorManager, null, null, null, XBOX_BUTTONS_ALL);

        // TODO
        // this.shiftButtonId = PUSH_BUTTON_SHIFT;
        // this.deleteButtonId = PUSH_BUTTON_DELETE;
        // this.soloButtonId = PUSH_BUTTON_SOLO;
        // this.muteButtonId = PUSH_BUTTON_MUTE;
        // this.leftButtonId = PUSH_BUTTON_LEFT;
        // this.rightButtonId = PUSH_BUTTON_RIGHT;
        // this.upButtonId = PUSH_BUTTON_UP;
        // this.downButtonId = PUSH_BUTTON_DOWN;
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setButton (final int button, final int state)
    {
        // TODO
        // this.output.sendCC (button, state);
    }
}