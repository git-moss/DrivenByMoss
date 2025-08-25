// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider.device;

import java.util.Optional;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.data.ICursorLayer;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.bank.IBank;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.empty.EmptyParameterBank;
import de.mossgrabers.framework.observer.IBankPageObserver;
import de.mossgrabers.framework.observer.IParametersAdjustObserver;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.AbstractParameterProvider;


/**
 * Get a number of parameters. This implementation provides all parameters from the current page of
 * the parameter bank of the selected device of the selected layer in a layer-bank.
 *
 * @author Jürgen Moßgraber
 */
public class SelectedLayerDeviceBankParameterProvider extends AbstractParameterProvider implements IBankPageObserver
{
    private final ICursorLayer       cursorLayer;
    private final EmptyParameterBank emptyParameterBank;
    private IBank<IParameter>        bank;


    /**
     * Constructor.
     *
     * @param cursorLayer The cursor layer
     * @param numParams The number of parameter of a page
     */
    public SelectedLayerDeviceBankParameterProvider (final ICursorLayer cursorLayer, final int numParams)
    {
        this.cursorLayer = cursorLayer;
        this.emptyParameterBank = EmptyParameterBank.getInstance (numParams);

        cursorLayer.getLayerBank ().addSelectionObserver ( (index, isSelected) -> {

            if (isSelected)
                this.configureCurrentBank ();

        });

        this.configureCurrentBank ();
    }


    /**
     * Update the current layer/device bank.
     */
    public void configureCurrentBank ()
    {
        final Optional<ISpecificDevice> specificDevice = this.cursorLayer.getSelectedDevice ();
        this.bank = specificDevice.isPresent () ? specificDevice.get ().getParameterBank () : this.emptyParameterBank;

        this.notifyParametersObservers ();
    }


    /**
     * Get the currently assigned bank.
     *
     * @return The bank
     */
    public IParameterBank getBank ()
    {
        return (IParameterBank) this.bank;
    }


    /** {@inheritDoc} */
    @Override
    public int size ()
    {
        return this.bank.getPageSize ();
    }


    /**
     * Throws a FrameworkException if the bank does not contain parameters.
     *
     * {@inheritDoc}
     */
    @Override
    public IParameter get (final int index)
    {
        return this.bank.getItem (index);
    }


    /** {@inheritDoc} */
    @Override
    public Optional<ColorEx> getColor (final int index)
    {
        return Optional.empty ();
    }


    /** {@inheritDoc} */
    @Override
    public void addParametersObserver (final IParametersAdjustObserver observer)
    {
        super.addParametersObserver (observer);

        this.bank.addPageObserver (this);
    }


    /** {@inheritDoc} */
    @Override
    public void removeParametersObserver (final IParametersAdjustObserver observer)
    {
        super.removeParametersObserver (observer);

        if (!this.hasObservers ())
            this.bank.removePageObserver (this);
    }


    /** {@inheritDoc} */
    @Override
    public void pageAdjusted ()
    {
        this.notifyParametersObservers ();
    }
}
