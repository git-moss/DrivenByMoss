// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.command.trigger;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.controller.oxi.one.mode.IOxiModeDisplay;
import de.mossgrabers.framework.command.trigger.mode.ModeMultiSelectCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for the Back button.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneBackCommand extends ModeMultiSelectCommand<OxiOneControlSurface, OxiOneConfiguration>
{
    private boolean       hasKnobBeenUsed = false;
    private final boolean isMk2;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param isMk2 True if it is the Mk2 otherwise Mk1
     */
    public OxiOneBackCommand (final IModel model, final OxiOneControlSurface surface, final boolean isMk2)
    {
        super (model, surface, Modes.TRACK, Modes.DEVICE_LAYER, Modes.DEVICE_PARAMS);

        this.isMk2 = isMk2;
    }


    /**
     * Set the 'knob has been used' state.
     *
     * @param hasKnobBeenUsed The state
     */
    public void setHasKnobBeenUsed (final boolean hasKnobBeenUsed)
    {
        this.hasKnobBeenUsed = hasKnobBeenUsed;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (this.surface.isShiftPressed ())
        {
            if (event == ButtonEvent.DOWN)
            {
                final IMode active = this.surface.getModeManager ().getActive ();
                if (active instanceof final IOxiModeDisplay modeDisplay)
                    modeDisplay.toggleDisplay ();
            }
            return;
        }

        switch (event)
        {
            case DOWN:
                this.surface.setKnobSensitivityIsSlow (true);
                if (!this.isMk2)
                    this.hasKnobBeenUsed = false;
                break;

            case UP:
                this.surface.setKnobSensitivityIsSlow (false);

                if (this.hasKnobBeenUsed)
                {
                    if (!this.isMk2)
                        this.hasKnobBeenUsed = false;
                }
                else
                {
                    final ModeManager modeManager = this.surface.getModeManager ();
                    if (modeManager.isTemporary ())
                        modeManager.restore ();
                    else if (!this.isMk2)
                        this.switchMode (!this.surface.isShiftPressed (), event);
                }
                break;

            case LONG:
                // Ignore
                break;
        }
    }
}
