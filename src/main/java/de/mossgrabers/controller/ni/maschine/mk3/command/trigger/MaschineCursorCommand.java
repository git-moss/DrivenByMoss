// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.command.trigger;

import de.mossgrabers.controller.ni.maschine.Maschine;
import de.mossgrabers.controller.ni.maschine.mk3.MaschineConfiguration;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.command.trigger.mode.ModeCursorCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeMultiSelectCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for navigating mode pages and items. Additional mode switcher for Maschine Studio.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MaschineCursorCommand extends ModeCursorCommand<MaschineControlSurface, MaschineConfiguration>
{
    private final ModeMultiSelectCommand<MaschineControlSurface, MaschineConfiguration> modeSelector;
    private final boolean                                                               isStudio;


    /**
     * Constructor.
     *
     * @param direction The direction of the pushed cursor arrow
     * @param model The model
     * @param surface The surface
     */
    public MaschineCursorCommand (final Direction direction, final IModel model, final MaschineControlSurface surface)
    {
        super (direction, model, surface, false);

        this.modeSelector = new ModeMultiSelectCommand<> (model, surface, Modes.POSITION, Modes.TEMPO, Modes.VOLUME);
        this.isStudio = this.surface.getMaschine () == Maschine.STUDIO;
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollLeft ()
    {
        if (this.isStudio && this.surface.isPressed (ButtonID.OVERDUB))
        {
            this.surface.setTriggerConsumed (ButtonID.OVERDUB);
            this.modeSelector.executeShifted (ButtonEvent.UP);
            return;
        }

        super.scrollLeft ();
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollRight ()
    {
        if (this.isStudio && this.surface.isPressed (ButtonID.OVERDUB))
        {
            this.surface.setTriggerConsumed (ButtonID.OVERDUB);
            this.modeSelector.executeNormal (ButtonEvent.UP);
            return;
        }

        super.scrollRight ();
    }
}
