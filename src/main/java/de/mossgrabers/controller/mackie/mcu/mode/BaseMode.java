// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.mode;

import java.util.Optional;

import de.mossgrabers.controller.mackie.mcu.MCUConfiguration;
import de.mossgrabers.controller.mackie.mcu.MCUConfiguration.MainDisplay;
import de.mossgrabers.controller.mackie.mcu.MCUConfiguration.SecondDisplay;
import de.mossgrabers.controller.mackie.mcu.MCUControllerSetup;
import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IBank;
import de.mossgrabers.framework.daw.data.bank.IChannelBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.data.empty.EmptyTrack;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Base class for all modes used by MCU.
 *
 * @param <B> The type of the item bank
 *
 * @author Jürgen Moßgraber
 */
public abstract class BaseMode<B extends IItem> extends AbstractParameterMode<MCUControlSurface, MCUConfiguration, B>
{
    protected static final ColorEx [] COLORS_WHITE        =
    {
        ColorEx.WHITE,
        ColorEx.WHITE,
        ColorEx.WHITE,
        ColorEx.WHITE,
        ColorEx.WHITE,
        ColorEx.WHITE,
        ColorEx.WHITE,
        ColorEx.WHITE
    };

    protected static final int []     LED_MODES_WRAP      =
    {
        MCUControlSurface.KNOB_LED_MODE_WRAP,
        MCUControlSurface.KNOB_LED_MODE_WRAP,
        MCUControlSurface.KNOB_LED_MODE_WRAP,
        MCUControlSurface.KNOB_LED_MODE_WRAP,
        MCUControlSurface.KNOB_LED_MODE_WRAP,
        MCUControlSurface.KNOB_LED_MODE_WRAP,
        MCUControlSurface.KNOB_LED_MODE_WRAP,
        MCUControlSurface.KNOB_LED_MODE_WRAP
    };

    protected static final int []     LED_MODES_BOOST_CUT =
    {
        MCUControlSurface.KNOB_LED_MODE_BOOST_CUT,
        MCUControlSurface.KNOB_LED_MODE_BOOST_CUT,
        MCUControlSurface.KNOB_LED_MODE_BOOST_CUT,
        MCUControlSurface.KNOB_LED_MODE_BOOST_CUT,
        MCUControlSurface.KNOB_LED_MODE_BOOST_CUT,
        MCUControlSurface.KNOB_LED_MODE_BOOST_CUT,
        MCUControlSurface.KNOB_LED_MODE_BOOST_CUT,
        MCUControlSurface.KNOB_LED_MODE_BOOST_CUT
    };

    protected final boolean           pinFXtoLastDevice;
    protected final MCUConfiguration  configuration;
    protected ColorEx                 defaultColor        = ColorEx.SKY_BLUE;


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     */
    protected BaseMode (final String name, final MCUControlSurface surface, final IModel model)
    {
        this (name, surface, model, null);
    }


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     * @param bank The bank
     */
    protected BaseMode (final String name, final MCUControlSurface surface, final IModel model, final IBank<B> bank)
    {
        super (name, surface, model, true, bank, DEFAULT_KNOB_IDS);

        this.configuration = this.surface.getConfiguration ();
        this.pinFXtoLastDevice = this.configuration.shouldPinFXTracksToLastController () && this.surface.isLastDevice ();
    }


    /** {@inheritDoc} */
    @Override
    protected void bindControls ()
    {
        if (!this.isActive || this.defaultParameterProvider == null)
            return;

        // Bind the knobs
        super.bindControls ();

        // Bind the faders
        final IParameterProvider parameterProvider;
        if (this.configuration.useFadersAsKnobs ())
            parameterProvider = this.getParameterProvider ();
        else
        {
            // Always binds providers from (layer-) volume mode, this includes the effect tracks of
            // the last device if setting is enabled
            final ModeManager modeManager = this.surface.getModeManager ();
            final boolean isLayerMode = Modes.isLayerMode (this.surface.getModeManager ().getActiveID ());
            parameterProvider = ((AbstractParameterMode<?, ?, ?>) modeManager.get (isLayerMode ? Modes.DEVICE_LAYER_VOLUME : Modes.VOLUME)).getParameterProvider ();
        }
        if (parameterProvider != null)
        {
            for (int i = 0; i < this.controls.size (); i++)
                this.surface.getContinuous (ContinuousID.get (ContinuousID.FADER1, i)).bind (parameterProvider.get (i));
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.setTouchedKnob (index, isTouched);

        final IParameterProvider parameterProvider = this.getParameterProvider ();
        if (parameterProvider != null)
        {
            final IParameter parameter = parameterProvider.get (index);
            if (parameter.doesExist ())
                parameter.touchValue (isTouched);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        // Mode specific
        if (row == 0)
        {
            this.resetParameter (index);
            return;
        }

        // Record Arm, Solo, Mute
        final ITrackBank tb = this.getTrackBank ();
        final ITrack track = tb.getItem (this.getExtenderOffset () + index);
        if (row == 1)
        {
            if (this.surface.isShiftPressed ())
                tb.toggleRecArm ();
            else
                track.toggleRecArm ();
        }
        else if (row == 2)
        {
            if (this.surface.isShiftPressed ())
                track.toggleAutoMonitor ();
            else if (this.surface.isSelectPressed ())
                this.model.getProject ().clearSolo ();
            else
                track.toggleSolo ();
        }
        else if (row == 3)
        {
            if (this.surface.isShiftPressed ())
                track.toggleMonitor ();
            else if (this.surface.isSelectPressed ())
                this.model.getProject ().clearMute ();
            else
                track.toggleMute ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final ITrackBank tb = this.getTrackBank ();
        final int extenderOffset = this.getExtenderOffset ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack track = tb.getItem (extenderOffset + i);

            final boolean exists = track.doesExist ();
            if (buttonID == ButtonID.get (ButtonID.ROW_SELECT_1, i))
                return exists && track.isSelected () ? MCUControllerSetup.MCU_BUTTON_STATE_ON : MCUControllerSetup.MCU_BUTTON_STATE_OFF;
            if (buttonID == ButtonID.get (ButtonID.ROW2_1, i))
                return exists && track.isRecArm () ? MCUControllerSetup.MCU_BUTTON_STATE_ON : MCUControllerSetup.MCU_BUTTON_STATE_OFF;
            if (buttonID == ButtonID.get (ButtonID.ROW3_1, i))
                return exists && track.isSolo () ? MCUControllerSetup.MCU_BUTTON_STATE_ON : MCUControllerSetup.MCU_BUTTON_STATE_OFF;
            if (buttonID == ButtonID.get (ButtonID.ROW4_1, i))
                return exists && track.isMute () ? MCUControllerSetup.MCU_BUTTON_STATE_ON : MCUControllerSetup.MCU_BUTTON_STATE_OFF;
        }

        return MCUControllerSetup.MCU_BUTTON_STATE_OFF;
    }


    /**
     * Implement to reset one of the controlled parameters.
     *
     * @param index The index of the parameter
     */
    protected void resetParameter (final int index)
    {
        final IParameterProvider parameterProvider = this.getParameterProvider ();
        if (parameterProvider != null)
            this.resetParameter (parameterProvider.get (index));
    }


    /**
     * Set a parameter to different values depending on the state of modifier keys. Control
     * minimizes the value, alternate maximizes the value and shift centers it. Without any modifier
     * button the value is reset.
     *
     * @param parameter The parameter to modify
     */
    protected void resetParameter (final IParameter parameter)
    {
        if (this.surface.isPressed (ButtonID.CONTROL))
            parameter.setNormalizedValue (0);
        else if (this.surface.isShiftPressed ())
            parameter.setNormalizedValue (0.5);
        else if (this.surface.isPressed (ButtonID.ALT))
            parameter.setNormalizedValue (1);
        else if (this.surface.isSelectPressed ())
        {
            if (parameter instanceof final ISend send)
                send.toggleEnabled ();
        }
        else
            parameter.resetValue ();
    }


    /**
     * Update the knob LED rings.
     */
    public void updateKnobLEDs ()
    {
        this.updateKnobLEDs (LED_MODES_WRAP);
    }


    /**
     * Set the knob LEDs depending of the value of the bound parameter.
     *
     * @param knobLedModes The LED modes to use (exactly 8)
     */
    protected void updateKnobLEDs (final int [] knobLedModes)
    {
        final IParameterProvider parameterProvider = this.getParameterProvider ();
        if (parameterProvider == null)
            return;
        final int upperBound = this.model.getValueChanger ().getUpperBound ();
        for (int i = 0; i < 8; i++)
        {
            final IParameter parameter = parameterProvider.get (i);
            if (parameter.doesExist ())
            {
                int value = parameter.getValue ();

                // Prevent LEDs to be turned off when full left
                if (knobLedModes[i] == MCUControlSurface.KNOB_LED_MODE_BOOST_CUT && value == 0)
                    value = 1;

                this.surface.setKnobLED (i, knobLedModes[i], value, upperBound);
            }
            else
                this.surface.setKnobLED (i, MCUControlSurface.KNOB_LED_MODE_WRAP, 0, upperBound);
        }
    }


    /**
     * Draw the parameter names in the 1st row of the main display.
     */
    protected void drawParameterHeader ()
    {
        this.drawParameterHeader (this.surface.getTextDisplay (), 0);
    }


    /**
     * Draw the parameter names in a specific row of the display.
     *
     * @param display The display to write to
     * @param row The index of the row to write to
     */
    protected void drawParameterHeader (final ITextDisplay display, final int row)
    {
        final IParameterProvider parameterProvider = this.getParameterProvider ();
        if (parameterProvider == null)
            return;
        display.clear ();
        final int textLength = Math.min (display.getNumberOfCellCharacters (), this.getTextLength ());
        for (int i = 0; i < 8; i++)
        {
            final IParameter parameter = parameterProvider.get (i);
            if (parameter.doesExist ())
                display.setCell (row, i, StringUtils.shortenAndFixASCII (parameter.getName (), textLength));
        }
        display.done (row);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final MainDisplay mainDisplayType = this.configuration.getMainDisplayType ();
        if (mainDisplayType == MainDisplay.OFF)
            return;
        final boolean isAsparion = mainDisplayType == MainDisplay.ASPARION;

        this.drawDisplay2 ();

        if (isAsparion || this.configuration.isDisplayTrackNames ())
            this.drawTrackNameHeader ();
        else
            this.drawParameterHeader ();

        final ITextDisplay d = this.surface.getTextDisplay ();
        final IParameterProvider parameterProvider = this.getParameterProvider ();
        if (parameterProvider == null)
            return;

        final ColorEx [] colors = new ColorEx [8];
        final int textLength = this.getTextLength ();
        for (int i = 0; i < 8; i++)
        {
            final IParameter parameter = parameterProvider.get (i);
            d.setCell (1, i, StringUtils.shortenAndFixASCII (parameter.getDisplayedValue (), textLength));
            final Optional<ColorEx> optColor = parameterProvider.getColor (i);
            final ColorEx color = optColor.isPresent () ? optColor.get () : this.defaultColor;
            final boolean exists = parameter.doesExist ();
            colors[i] = preventBlack (exists, exists ? color : ColorEx.BLACK);
        }
        d.done (1);

        this.surface.sendDisplayColor (colors);
    }


    /**
     * Fill the second display of the iCON QCon Pro / V1M.
     */
    protected void drawDisplay2 ()
    {
        final SecondDisplay secondDisplay = this.configuration.getSecondDisplayType ();
        switch (secondDisplay)
        {
            case ASPARION:
                this.drawParameterHeader (this.surface.getTextDisplay (1), 0);
                break;

            case QCON, V1M:
                final boolean isMainDevice = this.surface.isMainDevice ();
                final boolean isShort = isMainDevice && secondDisplay == SecondDisplay.QCON;
                final ITrackBank tb = this.getTrackBank ();
                final ITextDisplay d2 = this.surface.getTextDisplay (1);
                final int extenderOffset = this.getExtenderOffset ();
                final boolean isLayerMode = Modes.isLayerMode (this.surface.getModeManager ().getActiveID ());
                if (isLayerMode)
                {
                    final ICursorDevice cursorDevice = this.model.getCursorDevice ();
                    final IChannelBank<? extends IChannel> layerBank = cursorDevice.hasDrumPads () ? cursorDevice.getDrumPadBank () : cursorDevice.getLayerBank ();
                    for (int i = 0; i < 8; i++)
                    {
                        final IChannel c = layerBank.getItem (extenderOffset + i);
                        d2.setCell (0, i, StringUtils.shortenAndFixASCII (c.getName (), isShort ? 6 : 7));
                    }
                }
                else
                {
                    for (int i = 0; i < 8; i++)
                    {
                        final ITrack t = tb.getItem (extenderOffset + i);
                        d2.setCell (0, i, StringUtils.shortenAndFixASCII (t.getName (), isShort ? 6 : 7));
                    }
                }

                if (isShort)
                    d2.setCell (0, 8, "Maste");

                d2.done (0);
                d2.clearRow (1);

                if (isMainDevice)
                {
                    final IMasterTrack masterTrack = this.model.getMasterTrack ();
                    final ICursorDevice cursorDevice = this.model.getCursorDevice ();
                    final ITrack selectedTrack;
                    if (masterTrack.isSelected ())
                        selectedTrack = masterTrack;
                    else
                    {
                        final Optional<ITrack> selectedItem = tb.getSelectedItem ();
                        selectedTrack = selectedItem.isPresent () ? selectedItem.get () : EmptyTrack.getInstance (tb.getItem (0).getSendBank ().getPageSize ());
                    }
                    d2.setBlock (1, 0, "  Sel. track:").setBlock (1, 1, selectedTrack == null ? "None" : StringUtils.shortenAndFixASCII (selectedTrack.getName (), 11));
                    d2.setBlock (1, 2, "  Sel. devce:").setBlock (1, 3, cursorDevice.doesExist () ? StringUtils.shortenAndFixASCII (cursorDevice.getName (), 11) : "None");
                }

                d2.done (1);
                break;

            default:
            case OFF:
                // Off
                break;
        }
    }


    protected ITrackBank getTrackBank ()
    {
        if (this.pinFXtoLastDevice)
        {
            final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
            if (effectTrackBank != null)
                return effectTrackBank;
        }
        return this.model.getCurrentTrackBank ();
    }


    protected int getExtenderOffset ()
    {
        return this.pinFXtoLastDevice ? 0 : this.surface.getExtenderOffset ();
    }


    protected int getTextLength ()
    {
        switch (this.configuration.getMainDisplayType ())
        {
            case ASPARION:
                return 12;

            case MACKIE_6_CHARACTERS:
                return 6;

            case MACKIE_7_CHARACTERS:
                return 7;

            default:
            case OFF:
                return 0;
        }
    }


    /**
     * Prevents an item (track, device, ...) to vanish if it exists but its' color is set to black.
     *
     * @param doesExist The existence flag
     * @param color The color to check
     * @return The given color or gray to prevent black
     */
    protected static ColorEx preventBlack (final boolean doesExist, final ColorEx color)
    {
        return doesExist && ColorEx.BLACK.equals (color) ? ColorEx.GRAY : color;
    }


    /**
     * Fill the track name row in the display.
     */
    protected void drawTrackNameHeader ()
    {
        final ITrackBank tb = this.getTrackBank ();
        final int extenderOffset = this.getExtenderOffset ();

        final ITextDisplay d = this.surface.getTextDisplay ().clear ();

        // Format track names
        final int textLength = this.getTextLength ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (extenderOffset + i);
            d.setCell (0, i, StringUtils.shortenAndFixASCII (t.getName (), textLength));
        }
        d.done (0);
    }
}