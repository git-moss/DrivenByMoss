// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu.command.trigger;

import de.mossgrabers.controller.mcu.MCUConfiguration;
import de.mossgrabers.controller.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.command.continuous.FootswitchCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for assignable functions.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class AssignableCommand extends FootswitchCommand<MCUControlSurface, MCUConfiguration>
{
    private final int          index;
    private final ModeSwitcher switcher;


    /**
     * Constructor.
     *
     * @param index The index of the assignable button
     * @param model The model
     * @param surface The surface
     */
    public AssignableCommand (final int index, final IModel model, final MCUControlSurface surface)
    {
        super (model, surface);
        this.index = index;
        this.switcher = new ModeSwitcher (surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        switch (this.getSetting ())
        {
            case MCUConfiguration.FOOTSWITCH_2_PREV_MODE:
                if (event == ButtonEvent.DOWN)
                    this.switcher.scrollDown ();
                break;

            case MCUConfiguration.FOOTSWITCH_2_NEXT_MODE:
                if (event == ButtonEvent.DOWN)
                    this.switcher.scrollUp ();
                break;

            case MCUConfiguration.FOOTSWITCH_2_SHOW_MARKER_MODE:
                if (event == ButtonEvent.DOWN)
                    this.surface.getModeManager ().setActiveMode (Modes.MARKERS);
                break;

            case MCUConfiguration.FOOTSWITCH_2_USE_FADERS_LIKE_EDIT_KNOBS:
                if (event == ButtonEvent.DOWN)
                    this.surface.getConfiguration ().toggleUseFadersAsKnobs ();
                break;

            default:
                super.execute (event, velocity);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    protected int getSetting ()
    {
        return this.surface.getConfiguration ().getAssignable (this.index);
    }
}
