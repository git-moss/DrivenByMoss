// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.mode.device;

import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.controller.mackie.mcu.mode.BaseMode;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IEqualizerDevice;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.RangeFilterParameterProvider;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Optional;


/**
 * Mode for editing device remote control parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceParamsMode extends BaseMode<IParameter>
{
    private final ISpecificDevice   device;

    private static final ColorEx [] COLORS =
    {
        ColorEx.SKY_BLUE,
        ColorEx.SKY_BLUE,
        ColorEx.SKY_BLUE,
        ColorEx.SKY_BLUE,
        ColorEx.SKY_BLUE,
        ColorEx.SKY_BLUE,
        ColorEx.SKY_BLUE,
        ColorEx.SKY_BLUE
    };


    /**
     * Constructor for editing the cursor device.
     *
     * @param surface The control surface
     * @param model The model
     */
    public DeviceParamsMode (final MCUControlSurface surface, final IModel model)
    {
        this ("Parameters", model.getCursorDevice (), surface, model);
    }


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param device The device to edit
     * @param surface The control surface
     * @param model The model
     */
    public DeviceParamsMode (final String name, final ISpecificDevice device, final MCUControlSurface surface, final IModel model)
    {
        super (name, surface, model, device.getParameterBank ());

        this.device = device;

        final int surfaceID = surface.getSurfaceID ();
        this.setParameterProvider (new RangeFilterParameterProvider (new BankParameterProvider (device.getParameterBank ()), surfaceID * 8, 8));

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
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.setTouchedKnob (index, isTouched);

        final IParameter param = this.device.getParameterBank ().getItem (index);
        if (param.doesExist ())
            param.touchValue (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        this.drawDisplay2 ();

        final ITextDisplay d = this.surface.getTextDisplay ().clear ();

        this.surface.sendDisplayColor (COLORS);

        if (!this.device.doesExist ())
        {
            d.notify ("Please select a device...    ");
            return;
        }

        // Row 1 & 2
        final int extenderOffset = this.surface.getExtenderOffset ();
        final IParameterBank parameterBank = this.device.getParameterBank ();
        final int textLength = this.getTextLength ();
        for (int i = 0; i < 8; i++)
        {
            final IParameter param = parameterBank.getItem (extenderOffset + i);
            d.setCell (0, i, param.doesExist () ? StringUtils.shortenAndFixASCII (param.getName (6), 6) : "").setCell (1, i, StringUtils.shortenAndFixASCII (param.getDisplayedValue (textLength), textLength));
        }

        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateKnobLEDs ()
    {
        final int upperBound = this.model.getValueChanger ().getUpperBound ();
        final int extenderOffset = this.surface.getExtenderOffset ();
        final IParameterBank parameterBank = this.device.getParameterBank ();
        for (int i = 0; i < 8; i++)
        {
            final IParameter param = parameterBank.getItem (extenderOffset + i);
            this.surface.setKnobLED (i, MCUControlSurface.KNOB_LED_MODE_SINGLE_DOT, param.doesExist () ? Math.max (1, param.getValue ()) : 0, upperBound);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void resetParameter (final int index)
    {
        final int extenderOffset = this.surface.getExtenderOffset ();
        this.resetParameter (this.device.getParameterBank ().getItem (extenderOffset + index));
    }
}