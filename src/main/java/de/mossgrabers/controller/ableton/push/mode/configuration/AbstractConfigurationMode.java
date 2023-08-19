// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode.configuration;

import de.mossgrabers.controller.ableton.push.PushVersion;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.controller.ableton.push.mode.BaseMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Base class for all configuration modes used by Push 2/3.
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractConfigurationMode extends BaseMode<IItem>
{
    protected final String [] menu       = new String []
    {
        "Info",
        "Setup",
        "MPE",
        "Audio"
    };

    private final Modes []    modesPush2 = new Modes []
    {
        Modes.INFO,
        Modes.SETUP
    };

    private final Modes []    modesPush3 = new Modes []
    {
        Modes.INFO,
        Modes.SETUP,
        Modes.CONFIGURATION,
        Modes.AUDIO
    };

    private final Modes []    modes;

    private final int         page;


    /**
     * Constructor.
     * 
     * @param page The page of the configuration
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     */
    protected AbstractConfigurationMode (final int page, final String name, final PushControlSurface surface, final IModel model)
    {
        super (name, surface, model);

        this.page = page;

        if (surface.getConfiguration ().getPushVersion () != PushVersion.VERSION_3)
        {
            this.menu[2] = " ";
            this.menu[3] = " ";
            this.modes = this.modesPush2;
        }
        else
            this.modes = this.modesPush3;
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        final int index = this.isButtonRow (1, buttonID);
        if (index < 0 || index >= this.modes.length)
            return AbstractFeatureGroup.BUTTON_COLOR_OFF;
        return index == this.page ? AbstractMode.BUTTON_COLOR_HI : AbstractFeatureGroup.BUTTON_COLOR_ON;
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event == ButtonEvent.UP && index >= 0 && index < this.modes.length)
            this.surface.getModeManager ().setTemporary (this.modes[index]);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        // Intentionally empty - mode is only for Push 2
    }
}
