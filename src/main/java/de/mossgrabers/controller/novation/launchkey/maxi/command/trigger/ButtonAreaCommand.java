// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchkey.maxi.command.trigger;

import de.mossgrabers.controller.novation.launchkey.maxi.LaunchkeyMk3Configuration;
import de.mossgrabers.controller.novation.launchkey.maxi.controller.LaunchkeyMk3ColorManager;
import de.mossgrabers.controller.novation.launchkey.maxi.controller.LaunchkeyMk3ControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.command.trigger.track.RecArmCommand;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to trigger the 8 buttons for select and record arm.
 *
 * @author Jürgen Moßgraber
 */
public class ButtonAreaCommand extends AbstractTriggerCommand<LaunchkeyMk3ControlSurface, LaunchkeyMk3Configuration>
{
    private static boolean       isSelect = true;

    private final int            column;
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
        this.recArmCommand = new RecArmCommand<> (column, model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (isSelect)
        {
            if (event == ButtonEvent.DOWN)
                this.getColumnTrack ().selectOrExpandGroup ();
        }
        else
            this.recArmCommand.execute (event, velocity);
    }


    /**
     * Get the color to use for the button.
     *
     * @return The color index, negative if track is selected
     */
    public int getButtonColor ()
    {
        final ITrack t = this.getColumnTrack ();
        final int color;
        if (!t.doesExist ())
            color = LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK;
        else if (isSelect)
            color = this.model.getColorManager ().getColorIndex (DAWColor.getColorID (t.getColor ()));
        else
            color = t.isRecArm () ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_RED : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREY_LO;
        return t.isSelected () ? 0x1000 + color : color;
    }


    private ITrack getColumnTrack ()
    {
        return this.model.getCurrentTrackBank ().getItem (this.column);
    }


    /**
     * Toggle between select and record arm.
     */
    public static void toggleSelect ()
    {
        isSelect = !isSelect;
    }


    /**
     * Test if track selection or track record arm is active.
     *
     * @return True if selection is active
     */
    public static boolean isSelect ()
    {
        return isSelect;
    }
}
