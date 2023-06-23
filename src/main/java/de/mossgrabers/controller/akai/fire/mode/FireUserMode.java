// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.mode;

import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.controller.akai.fire.graphics.canvas.component.TitleValueComponent;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.mode.device.ProjectParamsMode;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;

import java.util.Optional;


/**
 * The user parameter mode.
 *
 * @author Jürgen Moßgraber
 */
public class FireUserMode extends ProjectParamsMode<FireControlSurface, FireConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public FireUserMode (final FireControlSurface surface, final IModel model)
    {
        super (surface, model, false, ContinuousID.createSequentialList (ContinuousID.KNOB1, 4), surface::isShiftPressed, false);

        this.notifyPageChange = false;

        this.projectParameterProvider = new Fire4KnobProvider (surface, new BankParameterProvider (model.getProject ().getParameterBank ()));
        this.trackParameterProvider = new Fire4KnobProvider (surface, new BankParameterProvider (model.getCursorTrack ().getParameterBank ()));
        this.setParameterProvider (this.projectParameterProvider);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final IGraphicDisplay display = this.surface.getGraphicsDisplay ();

        final IParameterPageBank pageBank = ((IParameterBank) this.bank).getPageBank ();
        final Optional<String> selectedPage = pageBank.getSelectedItem ();

        final String desc = selectedPage.isPresent () ? selectedPage.get () : "None";
        String paramLine = "";
        int value = -1;

        int touchedKnob = this.getTouchedKnob ();
        touchedKnob = this.surface.isPressed (ButtonID.ALT) && touchedKnob > -1 ? 4 + touchedKnob : touchedKnob;
        if (touchedKnob > -1)
        {
            final IParameter p = ((IParameterBank) this.bank).getItem (touchedKnob);
            paramLine = p.getName (5);
            if (paramLine.isEmpty ())
                paramLine = "Not mapped";
            else
            {
                value = p.getValue ();
                paramLine += ": " + p.getDisplayedValue (6);
            }
        }
        else
            paramLine = this.isProjectMode ? "Project" : "Track";

        display.addElement (new TitleValueComponent (desc, paramLine, value, false));
        display.send ();
    }
}