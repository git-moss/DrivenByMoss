// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.mode;

import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.controller.akai.fire.graphics.canvas.component.TitleValueComponent;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.mode.device.UserMode;
import de.mossgrabers.framework.parameter.IParameter;


/**
 * The user parameter mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FireUserMode extends UserMode<FireControlSurface, FireConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public FireUserMode (final FireControlSurface surface, final IModel model)
    {
        super (surface, model, false, ContinuousID.createSequentialList (ContinuousID.KNOB1, 4));
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final IGraphicDisplay display = this.surface.getGraphicsDisplay ();

        final IParameterBank userBank = this.model.getUserParameterBank ();

        final int page = userBank.getScrollPosition () / userBank.getPageSize ();
        final String desc = "User Page: " + (page + 1);
        String paramLine = "";
        int value = -1;

        final int touchedKnob = this.getTouchedKnob ();
        if (touchedKnob > -1)
        {
            final IParameter p = userBank.getItem (touchedKnob);
            paramLine = p.getName (5);
            if (paramLine.isEmpty ())
                paramLine = "Not mapped";
            else
            {
                value = p.getValue ();
                paramLine += ": " + p.getDisplayedValue (6);
            }
        }

        display.addElement (new TitleValueComponent (desc, paramLine, value, false));
        display.send ();
    }
}