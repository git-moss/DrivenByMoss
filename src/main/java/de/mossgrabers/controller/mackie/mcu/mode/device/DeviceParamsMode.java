// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.mode.device;

import java.util.Arrays;
import java.util.Optional;

import de.mossgrabers.controller.mackie.mcu.MCUConfiguration.MainDisplay;
import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.controller.mackie.mcu.mode.BaseMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IDevice;
import de.mossgrabers.framework.daw.data.IEqualizerDevice;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IDeviceBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.RangeFilterParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.VolumeParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for editing device remote control parameters.
 *
 * @author Jürgen Moßgraber
 */
public class DeviceParamsMode extends BaseMode<IParameter>
{
    private final ISpecificDevice device;


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

        final IParameterProvider parameterProvider;
        if (this.pinFXtoLastDevice)
            parameterProvider = new VolumeParameterProvider (model.getEffectTrackBank ());
        else
        {
            final int surfaceID = surface.getSurfaceID ();
            parameterProvider = new RangeFilterParameterProvider (new BankParameterProvider (device.getParameterBank ()), surfaceID * 8, 8);

            if (this.device instanceof IEqualizerDevice)
            {
                this.model.getTrackBank ().addSelectionObserver (this::trackSelectionChanged);
                final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
                if (effectTrackBank != null)
                    effectTrackBank.addSelectionObserver (this::trackSelectionChanged);
            }
        }
        this.setParameterProvider (parameterProvider);
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
        final boolean isControlPressed = this.surface.isPressed (ButtonID.CONTROL);
        if (isControlPressed || this.surface.isSelectPressed ())
        {
            final ColorEx [] colors = new ColorEx [8];
            final ITextDisplay d = this.surface.getTextDisplay ().clear ();
            final int textLength = this.getTextLength ();
            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            if (isControlPressed)
            {
                final IDeviceBank deviceBank = cursorDevice.getDeviceBank ();
                final int extenderOffset = this.getExtenderOffset ();
                for (int i = 0; i < 8; i++)
                {
                    final IDevice device = deviceBank.getItem (extenderOffset + i);
                    if (device.doesExist ())
                    {
                        d.setCell (0, i, StringUtils.shortenAndFixASCII (device.getName (), textLength));
                        colors[i] = ColorEx.WHITE;
                    }
                    else
                        colors[i] = ColorEx.BLACK;
                }
            }
            else
            {
                final IParameterPageBank pageBank = cursorDevice.getParameterBank ().getPageBank ();
                final int extenderOffset = this.getExtenderOffset ();
                for (int i = 0; i < 8; i++)
                {
                    final String page = pageBank.getItem (extenderOffset + i);
                    if (page != null && !page.isBlank ())
                    {
                        d.setCell (0, i, StringUtils.shortenAndFixASCII (page, textLength));
                        colors[i] = ColorEx.WHITE;
                    }
                    else
                        colors[i] = ColorEx.BLACK;
                }
            }

            d.allDone ();

            this.surface.sendDisplayColor (colors);
            return;
        }

        super.updateDisplay ();

        final int [] indices = new int [8];
        Arrays.fill (indices, 0);
        if (this.getExtenderOffset () == 0)
        {
            final ITrack selectedTrack = this.model.getCursorTrack ();
            if (selectedTrack.doesExist ())
                indices[0] = selectedTrack.getPosition () + 1;
            if (this.device.doesExist ())
            {
                indices[1] = this.device.getPosition () + 1;
                indices[2] = this.device.getParameterBank ().getPageBank ().getSelectedItemIndex () + 1;
            }
        }
        this.surface.setItemIndices (indices);
    }


    /** {@inheritDoc} */
    @Override
    protected void drawTrackNameHeader ()
    {
        if (this.pinFXtoLastDevice)
        {
            super.drawTrackNameHeader ();
            return;
        }

        this.drawParameterHeader ();

        if (this.surface.getConfiguration ().getMainDisplayType () == MainDisplay.ASPARION && this.surface.getSurfaceID () == 0)
        {
            final ITextDisplay display = this.surface.getTextDisplay ();
            display.clearRow (0);

            final ITrack selectedTrack = this.model.getCursorTrack ();
            if (selectedTrack.doesExist ())
            {
                final int textLength = this.getTextLength ();
                display.setCell (0, 0, StringUtils.shortenAndFixASCII (selectedTrack.getName (), textLength));

                if (this.device.doesExist ())
                {
                    display.setCell (0, 1, StringUtils.shortenAndFixASCII (this.device.getName (), textLength));

                    final IParameterPageBank pageBank = this.device.getParameterBank ().getPageBank ();
                    final Optional<String> selectedPage = pageBank.getSelectedItem ();
                    if (selectedPage.isPresent ())
                    {
                        display.setCell (0, 2, StringUtils.shortenAndFixASCII (selectedPage.get (), textLength));
                        display.setCell (0, 3, "Page " + (pageBank.getSelectedItemIndex () + 1) + "/" + pageBank.getItemCount ());
                    }
                }
            }

            display.done (0);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (row == 0)
        {
            if (this.surface.isPressed (ButtonID.CONTROL))
            {
                if (event == ButtonEvent.DOWN)
                {
                    final ICursorDevice cursorDevice = this.model.getCursorDevice ();
                    if (cursorDevice.doesExist ())
                    {
                        final IDeviceBank deviceBank = cursorDevice.getDeviceBank ();
                        final int pageSize = deviceBank.getPageSize ();
                        final int pos = cursorDevice.getPosition () / pageSize * pageSize;
                        deviceBank.selectItemAtPosition (pos + this.getExtenderOffset () + index);
                    }
                }
                return;
            }
            else if (this.surface.isSelectPressed ())
            {
                if (event == ButtonEvent.DOWN)
                {
                    final ICursorDevice cursorDevice = this.model.getCursorDevice ();
                    if (cursorDevice.doesExist ())
                    {
                        final IParameterPageBank pageBank = cursorDevice.getParameterBank ().getPageBank ();
                        final int pageSize = pageBank.getPageSize ();
                        final int pos = pageBank.getSelectedItemPosition () / pageSize * pageSize;
                        pageBank.selectItemAtPosition (pos + this.getExtenderOffset () + index);
                    }
                }
                return;
            }
        }

        super.onButton (row, index, event);
    }


    /** {@inheritDoc} */
    @Override
    public void updateKnobLEDs ()
    {
        final boolean isControlPressed = this.surface.isPressed (ButtonID.CONTROL);
        if (isControlPressed || this.surface.isSelectPressed ())
        {
            final int upperBound = this.model.getValueChanger ().getUpperBound ();
            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            final int extenderOffset = this.getExtenderOffset ();

            if (isControlPressed)
            {
                final IDeviceBank deviceBank = cursorDevice.getDeviceBank ();
                final int selectedPosition = cursorDevice.getPosition ();
                for (int i = 0; i < 8; i++)
                {
                    final IDevice device = deviceBank.getItem (extenderOffset + i);
                    this.surface.setKnobLED (i, MCUControlSurface.KNOB_LED_MODE_WRAP, device.doesExist () && device.getPosition () == selectedPosition ? upperBound - 1 : 0, upperBound);
                }
            }
            else
            {
                final IParameterPageBank pageBank = cursorDevice.getParameterBank ().getPageBank ();
                final int selectedPosition = pageBank.getSelectedItemPosition ();
                for (int i = 0; i < 8; i++)
                {
                    final String page = pageBank.getItem (extenderOffset + i);
                    this.surface.setKnobLED (i, MCUControlSurface.KNOB_LED_MODE_WRAP, page != null && !page.isBlank () && extenderOffset + i == selectedPosition ? upperBound - 1 : 0, upperBound);
                }
            }

            return;
        }

        super.updateKnobLEDs ();
    }
}