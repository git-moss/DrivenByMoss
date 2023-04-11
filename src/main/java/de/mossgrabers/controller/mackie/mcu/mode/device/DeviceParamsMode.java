// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.mode.device;

import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.controller.mackie.mcu.mode.BaseMode;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IEqualizerDevice;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.RangeFilterParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.VolumeParameterProvider;

import java.util.Optional;


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
    protected void drawTrackNameHeader ()
    {
        if (this.pinFXtoLastDevice)
            super.drawTrackNameHeader ();
        else
            this.drawParameterHeader ();
    }
}