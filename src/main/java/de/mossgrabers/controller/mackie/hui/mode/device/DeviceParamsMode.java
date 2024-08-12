// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.hui.mode.device;

import java.util.Optional;

import de.mossgrabers.controller.mackie.hui.HUIConfiguration;
import de.mossgrabers.controller.mackie.hui.controller.HUIControlSurface;
import de.mossgrabers.controller.mackie.hui.mode.HUIMode;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IEqualizerDevice;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for editing device remote control parameters.
 *
 * @author Jürgen Moßgraber
 */
public class DeviceParamsMode extends AbstractParameterMode<HUIControlSurface, HUIConfiguration, IParameter> implements HUIMode
{
    private final ISpecificDevice device;


    /**
     * Constructor for editing the cursor device.
     *
     * @param surface The control surface
     * @param model The model
     */
    public DeviceParamsMode (final HUIControlSurface surface, final IModel model)
    {
        this (Modes.NAME_PARAMETERS, model.getCursorDevice (), surface, model);
    }


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param device The device to edit
     * @param surface The control surface
     * @param model The model
     */
    public DeviceParamsMode (final String name, final ISpecificDevice device, final HUIControlSurface surface, final IModel model)
    {
        super (name, surface, model, false, device.getParameterBank ());

        this.device = device;

        if (this.device instanceof IEqualizerDevice)
        {
            this.model.getTrackBank ().addSelectionObserver (this::trackSelectionChanged);
            final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
            if (effectTrackBank != null)
                effectTrackBank.addSelectionObserver (this::trackSelectionChanged);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();

        this.trackSelectionChanged (-1, true);
    }


    /**
     * Add a equalizer device to a track which does not already contain one, if this mode is about
     * the EQ device.
     *
     * @param index The index of the selected or de-selected track
     * @param isSelected Is the track selected or de-selected?
     */
    private void trackSelectionChanged (final int index, final boolean isSelected)
    {
        if (!(isSelected && this.isActive && this.device instanceof IEqualizerDevice))
            return;

        // Add an equalizer if not present and this is the main device (no extender)
        if (!this.device.doesExist () && this.surface.getSurfaceID () == 0)
        {
            final Optional<ITrack> selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
            if (selectedTrack.isPresent ())
                selectedTrack.get ().addEqualizerDevice ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ().clear ();

        // Format parameter names
        final IParameterBank parameterBank = this.device.getParameterBank ();
        for (int i = 0; i < 8; i++)
        {
            final IParameter param = parameterBank.getItem (i);
            if (param.doesExist ())
                d.setCell (0, i, StringUtils.shortenAndFixASCII (param.getName (), 4));
        }

        d.done (0);
    }


    /** {@inheritDoc} */
    @Override
    public void updateKnobLEDs ()
    {
        final int upperBound = this.model.getValueChanger ().getUpperBound ();
        final IParameterBank parameterBank = this.device.getParameterBank ();
        if (this.surface.getExtenderOffset () == 0)
        {
            for (int i = 0; i < 8; i++)
            {
                final IParameter param = parameterBank.getItem (i);
                if (param.doesExist ())
                    this.surface.setKnobLED (i, HUIControlSurface.KNOB_LED_MODE_WRAP, Math.max (param.getValue (), 1), upperBound);
                else
                    this.surface.setKnobLED (i, HUIControlSurface.KNOB_LED_MODE_OFF, 0, 0);
            }
        }
        else
        {
            for (int i = 0; i < 8; i++)
                this.surface.setKnobLED (i, HUIControlSurface.KNOB_LED_MODE_OFF, 0, 0);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (row == 0)
            this.resetParameter (index);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        if (this.surface.getExtenderOffset () > 0)
            return;
        final IParameter param = this.device.getParameterBank ().getItem (index);
        if (param.doesExist ())
            param.changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void resetParameter (final int index)
    {
        if (this.surface.getExtenderOffset () > 0)
            return;
        final IParameter param = this.device.getParameterBank ().getItem (index);
        if (param.doesExist ())
            param.resetValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        this.model.getCursorDevice ().selectPrevious ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        this.model.getCursorDevice ().selectNext ();
    }
}