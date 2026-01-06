// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.mode;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.graphics.canvas.component.simple.TitleValueMenuComponent;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.EmptyParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FourKnobProvider;


/**
 * The automation mode.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneAutomationMode extends AbstractParameterMode<OxiOneControlSurface, OxiOneConfiguration, IItem>
{
    private static final String [] MENU              =
    {
        "Mode",
        "",
        "",
        ""
    };

    private static final String [] SHIFTED_MENU      =
    {
        "",
        "",
        "",
        ""
    };

    private final int              selectedParameter = 0;
    private final IParameter []    parameters        = new IParameter [4];
    private final ITransport       transport;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public OxiOneAutomationMode (final OxiOneControlSurface surface, final IModel model)
    {
        super (Modes.NAME_AUTOMATION, surface, model, false);

        this.transport = this.model.getTransport ();

        this.setControls (ContinuousID.createSequentialList (ContinuousID.KNOB1, 4));

        this.parameters[0] = this.transport.getAutomationModeParameter ();
        this.setParameterProvider (new FourKnobProvider<> (surface, new CombinedParameterProvider ( // combine
                new FixedParameterProvider (this.parameters[0]), // Automation Mode
                new EmptyParameterProvider (3)), ButtonID.SHIFT));
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final IGraphicDisplay display = this.surface.getGraphicsDisplay ();
        final String desc = this.parameters[this.selectedParameter].getName ();
        final String label = this.parameters[this.selectedParameter].getDisplayedValue ();
        final int value = this.selectedParameter > 0 && this.selectedParameter < 3 ? this.parameters[this.selectedParameter].getValue () : 0;
        display.addElement (new TitleValueMenuComponent (desc, label, this.surface.isShiftPressed () ? SHIFTED_MENU : MENU, value, -1, -1, false));
        display.send ();
    }
}
