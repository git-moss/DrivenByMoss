// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchkey.maxi.command.trigger;

import de.mossgrabers.controller.launchkey.maxi.LaunchkeyMk3Configuration;
import de.mossgrabers.controller.launchkey.maxi.controller.LaunchkeyMk3ColorManager;
import de.mossgrabers.controller.launchkey.maxi.controller.LaunchkeyMk3ControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.command.trigger.track.RecArmCommand;
import de.mossgrabers.framework.command.trigger.track.SelectCommand;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to trigger the 8 buttons for select and rec arm.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ButtonAreaCommand extends AbstractTriggerCommand<LaunchkeyMk3ControlSurface, LaunchkeyMk3Configuration>
{
    private static boolean       isSelect = true;

    private final int            column;
    private final TriggerCommand selectCommand;
    private final TriggerCommand recArmCommand;


    /**
     * Constructor.
     *
     * @param column The column of the button (0-7)
     * @param model The model
     * @param surface The surface
     */
    public ButtonAreaCommand (final int column, final IModel model, final LaunchkeyMk3ControlSurface surface)
    {
        super (model, surface);

        this.column = column;

        this.selectCommand = new SelectCommand<> (column, model, surface);
        this.recArmCommand = new RecArmCommand<> (column, model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (isSelect)
            this.selectCommand.execute (event, velocity);
        else
            this.recArmCommand.execute (event, velocity);
    }


    /**
     * Get the color to use for the button.
     *
     * @return THe color index
     */
    public int getButtonColor ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack t = tb.getItem (this.column);
        if (isSelect)
            return this.model.getColorManager ().getColorIndex (DAWColor.getColorIndex (t.getColor ()));
        return t.isRecArm () ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_RED : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREY_LO;
    }


    /**
     * Toggle between select and rec-arm.
     */
    public static void toggleSelect ()
    {
        isSelect = !isSelect;
    }


    /**
     * Test if track selection or track rec-arm is active.
     *
     * @return True if selection is active
     */
    public static boolean isSelect ()
    {
        return isSelect;
    }
}
