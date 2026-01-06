// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.mode;

import java.util.Optional;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.graphics.canvas.component.simple.TitleValueMenuComponent;
import de.mossgrabers.framework.mode.device.ParameterMode;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FourKnobProvider;


/**
 * The device parameter mode. The knobs control the value of the parameter on the parameter page.
 * device.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneParameterMode extends ParameterMode<OxiOneControlSurface, OxiOneConfiguration> implements IOxiModeDisplay
{
    private int                         selectedKnobIndex     = 0;
    private final IParameterProvider [] paramProviders        = new IParameterProvider [3];
    private int                         selectedProviderIndex = 0;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public OxiOneParameterMode (final OxiOneControlSurface surface, final IModel model)
    {
        super (surface, model, false);

        this.setControls (ContinuousID.createSequentialList (ContinuousID.KNOB1, 4));

        this.paramProviders[0] = new FourKnobProvider<> (surface, new BankParameterProvider (model.getCursorDevice ().getParameterBank ()), ButtonID.SHIFT);
        this.paramProviders[1] = new FourKnobProvider<> (surface, new BankParameterProvider (model.getProject ().getParameterBank ()), ButtonID.SHIFT);
        this.paramProviders[2] = new FourKnobProvider<> (surface, new BankParameterProvider (model.getCursorTrack ().getParameterBank ()), ButtonID.SHIFT);
        this.setMode ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        if (this.selectedProviderIndex == 0)
            this.model.getCursorDevice ().selectPrevious ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        if (this.selectedProviderIndex == 0)
            this.model.getCursorDevice ().selectNext ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItem ()
    {
        return this.selectedProviderIndex == 0 && this.model.getCursorDevice ().canSelectPrevious ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItem ()
    {
        return this.selectedProviderIndex == 0 && this.model.getCursorDevice ().canSelectNext ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleDisplay ()
    {
        this.selectedProviderIndex = (this.selectedProviderIndex + 1) % 3;
        this.setMode ();
    }


    /** {@inheritDoc} */
    @Override
    public void setTouchedKnob (final int knobIndex, final boolean isTouched)
    {
        super.setTouchedKnob (knobIndex, isTouched);

        if (isTouched)
            this.selectedKnobIndex = knobIndex;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final IGraphicDisplay display = this.surface.getGraphicsDisplay ();

        final int offset = this.surface.isShiftPressed () ? 4 : 0;

        final String name;
        if (this.selectedProviderIndex == 0)
            name = this.model.hasSelectedDevice () ? this.model.getCursorDevice ().getName (8) : "No device";
        else if (this.selectedProviderIndex == 1)
            name = "Project";
        else
            name = "Track";

        final IParameterBank paramBank = this.getParamBank ();
        final Optional<String> pageName = paramBank.getPageBank ().getSelectedItem ();
        final String desc = name + ": " + (pageName.isPresent () ? pageName.get () : "None");

        final IParameter p = this.bank.getItem (offset + this.selectedKnobIndex);
        String paramLine = p.getName (5);
        int value = -1;
        if (!paramLine.isEmpty ())
        {
            value = p.getValue ();
            paramLine += ": " + p.getDisplayedValue (6);
        }

        final String [] menu = new String [4];
        for (int i = 0; i < 4; i++)
            menu[i] = paramBank.getItem (offset + i).getName (4);

        display.addElement (new TitleValueMenuComponent (desc, paramLine, menu, value, false));
        display.send ();
    }


    /**
     * Set the device, project or track parameters.
     */
    private void setMode ()
    {
        this.switchBanks (this.getParamBank ());
        this.setParameterProvider (this.paramProviders[this.selectedProviderIndex]);
        this.bindControls ();
    }


    private IParameterBank getParamBank ()
    {
        if (this.selectedProviderIndex == 0)
            return this.model.getCursorDevice ().getParameterBank ();
        if (this.selectedProviderIndex == 1)
            return this.model.getProject ().getParameterBank ();
        return this.model.getCursorTrack ().getParameterBank ();
    }
}