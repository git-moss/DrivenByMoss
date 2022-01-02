// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.mode;

import de.mossgrabers.controller.mackie.mcu.MCUConfiguration;
import de.mossgrabers.controller.mackie.mcu.MCUControllerSetup;
import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.data.empty.EmptyTrack;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Optional;


/**
 * Base class for all modes used by MCU.
 *
 * @param <B> The type of the item bank
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class BaseMode<B extends IItem> extends AbstractMode<MCUControlSurface, MCUConfiguration, B>
{
    private final boolean useFxBank;


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

        final MCUConfiguration configuration = this.surface.getConfiguration ();
        this.useFxBank = configuration.shouldPinFXTracksToLastController () && this.surface.getSurfaceID () == configuration.getNumMCUDevices () - 1;
    }


    /** {@inheritDoc} */
    @Override
    protected void bindControls ()
    {
        if (!this.isActive || this.defaultParameterProvider == null)
            return;

        super.bindControls ();

        final IParameterProvider parameterProvider;
        if (this.surface.getConfiguration ().useFadersAsKnobs ())
            parameterProvider = this.getParameterProvider ();
        else
            parameterProvider = ((AbstractMode<?, ?, ?>) this.surface.getModeManager ().get (Modes.VOLUME)).getParameterProvider ();

        for (int i = 0; i < this.controls.size (); i++)
            this.surface.getContinuous (ContinuousID.get (ContinuousID.FADER1, i)).bind (parameterProvider.get (i));
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final int channel = this.getExtenderOffset () + index;

        if (row == 0)
        {
            // Mode specific
            this.resetParameter (index);
            return;
        }

        final ITrack track = this.model.getCurrentTrackBank ().getItem (channel);
        if (row == 1)
            track.toggleRecArm ();
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


    protected abstract void resetParameter (final int index);


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
     * Update the knob LED rings.
     */
    public abstract void updateKnobLEDs ();


    protected void drawDisplay2 ()
    {
        if (!this.surface.getConfiguration ().hasDisplay2 ())
            return;

        final ITrackBank tb = this.getTrackBank ();

        // Format track names
        final ITextDisplay d2 = this.surface.getTextDisplay (1);
        final int extenderOffset = this.getExtenderOffset ();

        final boolean isMainDevice = this.surface.isMainDevice ();

        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (extenderOffset + i);
            d2.setCell (0, i, StringUtils.shortenAndFixASCII (t.getName (), isMainDevice ? 6 : 7));
        }

        if (isMainDevice)
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
                selectedTrack = selectedItem.isPresent () ? selectedItem.get () : EmptyTrack.INSTANCE;
            }
            d2.setBlock (1, 0, "Sel. track:").setBlock (1, 1, selectedTrack == null ? "None" : StringUtils.shortenAndFixASCII (selectedTrack.getName (), 11));
            d2.setBlock (1, 2, "Sel. devce:").setBlock (1, 3, cursorDevice.doesExist () ? StringUtils.shortenAndFixASCII (cursorDevice.getName (), 11) : "None");
        }

        d2.done (1);
    }


    protected ITrackBank getTrackBank ()
    {
        if (this.useFxBank)
        {
            final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
            if (effectTrackBank != null)
                return effectTrackBank;
        }
        return this.model.getCurrentTrackBank ();
    }


    protected int getExtenderOffset ()
    {
        return this.useFxBank ? 0 : this.surface.getExtenderOffset ();
    }
}