// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.mode;

import java.util.Optional;

import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.graphics.canvas.component.simple.TitleValueComponent;
import de.mossgrabers.framework.mode.device.ParameterMode;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FourKnobProvider;


/**
 * The device parameter mode. The knobs control the value of the parameter on the parameter page.
 * device.
 *
 * @author Jürgen Moßgraber
 */
public class FireParameterMode extends ParameterMode<FireControlSurface, FireConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public FireParameterMode (final FireControlSurface surface, final IModel model)
    {
        super (surface, model, false);

        this.setControls (ContinuousID.createSequentialList (ContinuousID.KNOB1, 4));

        this.setParameterProvider (new FourKnobProvider<> (surface, new BankParameterProvider (model.getCursorDevice ().getParameterBank ()), ButtonID.ALT));
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final IGraphicDisplay display = this.surface.getGraphicsDisplay ();

        String desc = "Select";
        String paramLine = "a device";
        int value = -1;

        if (this.model.hasSelectedDevice ())
        {
            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            final Optional<String> pageName = cursorDevice.getParameterBank ().getPageBank ().getSelectedItem ();
            desc = pageName.isPresent () ? pageName.get () : "None";

            int touchedKnob = this.getTouchedKnob ();
            touchedKnob = this.surface.isPressed (ButtonID.ALT) && touchedKnob > -1 ? 4 + touchedKnob : touchedKnob;
            if (touchedKnob > -1)
            {
                final IParameter p = this.bank.getItem (touchedKnob);
                paramLine = p.getName (5);
                if (!paramLine.isEmpty ())
                {
                    value = p.getValue ();
                    paramLine += ": " + p.getDisplayedValue (6);
                }
            }
            else
                paramLine = cursorDevice.getName ();
        }

        display.addElement (new TitleValueComponent (desc, paramLine, value, false));
        display.send ();
    }
}