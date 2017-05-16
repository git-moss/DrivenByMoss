// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.mode;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.DisplayMessage;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.controller.PushDisplay;


/**
 * Configuration settings for Push 2.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SetupMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public SetupMode (final PushControlSurface surface, final Model model)
    {
        super (surface, model);
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        final PushConfiguration config = this.surface.getConfiguration ();
        switch (index)
        {
            case 2:
                config.changeDisplayBrightness (value);
                break;
            case 3:
                config.changeLEDBrightness (value);
                break;
            case 5:
                config.changePadSensitivity (value);
                break;
            case 6:
                config.changePadGain (value);
                break;
            case 7:
                config.changePadDynamics (value);
                break;
            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;
    }


    /** {@inheritDoc} */
    @Override
    public void updateSecondRow ()
    {
        this.surface.updateButton (102, AbstractMode.BUTTON_COLOR_HI);
        this.surface.updateButton (103, AbstractMode.BUTTON_COLOR_ON);
        for (int i = 2; i < 8; i++)
            this.surface.updateButton (102 + i, AbstractMode.BUTTON_COLOR_OFF);
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        if (index == 1)
            this.surface.getModeManager ().setActiveMode (Modes.MODE_INFO);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        // Intentionally empty - mode is only for Push 2
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final PushConfiguration config = this.surface.getConfiguration ();
        final DisplayMessage message = ((PushDisplay) this.surface.getDisplay ()).createMessage ();
        message.addOptionElement ("", "Setup", true, "", "", false, true);
        message.addOptionElement ("Brightness", "Info", false, "", "", false, true);
        for (int i = 2; i < 8; i++)
        {
            if (i == 4)
            {
                message.addOptionElement ("        Pads", "", false, "", "", false, false);
                continue;
            }

            message.addByte (DisplayMessage.GRID_ELEMENT_PARAMETERS);

            // The menu item
            message.addString ("");
            message.addBoolean (i == 0);

            message.addString ("");
            message.addString ("");
            message.addColor (null);
            message.addBoolean (false);

            switch (i)
            {
                case 2:
                    message.addString ("Display");
                    message.addInteger (config.getDisplayBrightness () * 1023 / 100);
                    message.addString (config.getDisplayBrightness () + "%");
                    break;
                case 3:
                    message.addString ("LEDs");
                    message.addInteger (config.getLedBrightness () * 1023 / 100);
                    message.addString (config.getLedBrightness () + "%");
                    break;
                case 5:
                    message.addString ("Sensitivity");
                    message.addInteger (config.getPadSensitivity () * 1023 / 10);
                    message.addString (Integer.toString (config.getPadSensitivity ()));
                    break;
                case 6:
                    message.addString ("Gain");
                    message.addInteger (config.getPadGain () * 1023 / 10);
                    message.addString (Integer.toString (config.getPadGain ()));
                    break;
                case 7:
                    message.addString ("Dynamics");
                    message.addInteger (config.getPadDynamics () * 1023 / 10);
                    message.addString (Integer.toString (config.getPadDynamics ()));
                    break;
                default:
                    message.addString ("");
                    message.addInteger (-1);
                    message.addString ("");
                    break;
            }

            message.addBoolean (this.isKnobTouched[i]);
            message.addInteger (-1);
        }
        message.send ();
    }
}