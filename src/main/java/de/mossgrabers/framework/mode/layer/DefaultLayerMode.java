// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode.layer;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;


/**
 * Base mode for layer related modes.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public abstract class DefaultLayerMode<S extends IControlSurface<C>, C extends Configuration> extends AbstractParameterMode<S, C, ILayer>
{
    private final ISpecificDevice firstInstrument;


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happening with a setter otherwise relative
     *            change method is used
     */
    protected DefaultLayerMode (final String name, final S surface, final IModel model, final boolean isAbsolute)
    {
        this (name, surface, model, isAbsolute, null);
    }


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happening with a setter otherwise relative
     *            change method is used
     * @param controls The IDs of the knobs or faders to control this mode
     */
    protected DefaultLayerMode (final String name, final S surface, final IModel model, final boolean isAbsolute, final List<ContinuousID> controls)
    {
        this (name, surface, model, isAbsolute, controls, surface::isShiftPressed);
    }


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happening with a setter otherwise relative
     *            change method is used
     * @param controls The IDs of the knobs or faders to control this mode
     * @param isAlternativeFunction Callback function to execute the secondary function, e.g. a
     *            shift button
     */
    protected DefaultLayerMode (final String name, final S surface, final IModel model, final boolean isAbsolute, final List<ContinuousID> controls, final BooleanSupplier isAlternativeFunction)
    {
        super (name, surface, model, isAbsolute, model.getCursorDevice ().getLayerBank (), controls, isAlternativeFunction);

        this.firstInstrument = model.getSpecificDevice (DeviceID.FIRST_INSTRUMENT);
        this.firstInstrument.addHasDrumPadsObserver (hasDrumPads -> this.parametersAdjusted ());
    }


    /** {@inheritDoc} */
    @Override
    public Optional<String> getSelectedItemName ()
    {
        final Optional<ILayer> selectedItem = this.bank.getSelectedItem ();
        if (selectedItem.isEmpty ())
            return Optional.empty ();
        final ILayer layer = selectedItem.get ();
        return layer.doesExist () ? Optional.of (layer.getPosition () + 1 + ": " + layer.getName ()) : Optional.empty ();
    }


    /**
     * Get the layer for which to change the volume.
     *
     * @param index The index of the layer. If set to -1 the selected layer is used.
     * @return The selected layer
     */
    protected Optional<ILayer> getLayer (final int index)
    {
        return index < 0 ? this.bank.getSelectedItem () : Optional.of (this.bank.getItem (index));
    }


    /**
     * Test for button combinations like Delete, Duplicate and New.
     *
     * @param layer The layer to apply the button combinations
     * @return True if a button combination was detected and the applied method was executed
     */
    protected boolean isButtonCombination (final ILayer layer)
    {
        if (this.isButtonCombination (ButtonID.DELETE))
        {
            layer.remove ();
            return true;
        }

        if (this.isButtonCombination (ButtonID.DUPLICATE))
        {
            layer.duplicate ();
            return true;
        }

        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN || row != 0)
            return;
        final Optional<ILayer> layerOpt = this.getLayer (index);
        if (layerOpt.isEmpty ())
            return;

        final ILayer layer = layerOpt.get ();
        if (!this.isButtonCombination (layer))
            this.executeMethod (layer);
    }


    /** {@inheritDoc} */
    @Override
    public void parametersAdjusted ()
    {
        this.switchBanks (this.firstInstrument.hasDrumPads () ? this.firstInstrument.getDrumPadBank () : this.firstInstrument.getLayerBank ());

        super.parametersAdjusted ();
    }


    /**
     * Execute the button row 1 method.
     *
     * @param layer The layer for which to execute the method
     */
    protected abstract void executeMethod (final ILayer layer);
}