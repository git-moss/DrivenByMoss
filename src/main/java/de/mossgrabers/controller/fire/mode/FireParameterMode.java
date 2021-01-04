// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.fire.mode;

import de.mossgrabers.controller.fire.FireConfiguration;
import de.mossgrabers.controller.fire.controller.FireControlSurface;
import de.mossgrabers.controller.fire.graphics.canvas.component.TitleValueComponent;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.mode.device.ParameterMode;
import de.mossgrabers.framework.parameterprovider.BankParameterProvider;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The device parameter mode. The knobs control the value of the parameter on the parameter page.
 * device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
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

        this.setParameters (new Fire4KnobProvider (surface, new BankParameterProvider (model.getCursorDevice ().getParameterBank ())));
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
            paramLine = "";

            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            desc = cursorDevice.getName (5) + ": " + StringUtils.optimizeName (cursorDevice.getParameterPageBank ().getSelectedItem (), 5);

            final int touchedKnob = this.getTouchedKnob ();
            if (touchedKnob > -1)
            {
                final IParameterBank parameterBank = cursorDevice.getParameterBank ();
                final IParameter p = parameterBank.getItem (touchedKnob);
                paramLine = p.getName (5);
                if (!paramLine.isEmpty ())
                {
                    value = p.getValue ();
                    paramLine += ": " + p.getDisplayedValue (6);
                }
            }
        }

        display.addElement (new TitleValueComponent (desc, paramLine, value, false));
        display.send ();
    }
}