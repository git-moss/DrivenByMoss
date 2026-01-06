// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.mode;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.daw.GrooveParameterID;
import de.mossgrabers.framework.daw.IGroove;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.graphics.canvas.component.simple.TitleValueMenuComponent;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameter.PlayPositionParameter;
import de.mossgrabers.framework.parameter.TempoParameter;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FourKnobProvider;


/**
 * The transport mode. Controls tempo, shuffle, play position and the metronome.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneTransportMode extends AbstractParameterMode<OxiOneControlSurface, OxiOneConfiguration, IItem>
{
    private static final String [] MENU              =
    {
        "Tmp",
        "Shf",
        "Mtr",
        "Pos"
    };

    private static final String [] SHIFTED_MENU      =
    {
        "Tick",
        "Shf",
        "Mtr",
        "Rept"
    };

    private int                    selectedParameter = 0;
    private final IParameter []    parameters        = new IParameter [4];
    private final ITransport       transport;
    private final IGroove          groove;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public OxiOneTransportMode (final OxiOneControlSurface surface, final IModel model)
    {
        super ("Transport", surface, model, false);

        this.transport = this.model.getTransport ();
        this.groove = this.model.getGroove ();

        this.setControls (ContinuousID.createSequentialList (ContinuousID.KNOB1, 4));

        this.parameters[0] = new TempoParameter (model.getValueChanger (), this.transport, surface);
        this.parameters[1] = this.model.getGroove ().getParameter (GrooveParameterID.SHUFFLE_AMOUNT);
        this.parameters[2] = this.transport.getMetronomeVolumeParameter ();
        this.parameters[3] = new PlayPositionParameter (model.getValueChanger (), this.transport, surface);

        this.setParameterProvider (new FourKnobProvider<> (surface, new CombinedParameterProvider ( // combine
                new FixedParameterProvider (this.parameters[0]), // PlayPosition
                new FixedParameterProvider (this.parameters[1]), // Tempo
                new FixedParameterProvider (this.parameters[2]), // Shuffle
                new FixedParameterProvider (this.parameters[3])), // Metronome
                ButtonID.SHIFT));
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


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.setTouchedKnob (index, isTouched);

        if (!isTouched)
            return;

        if (this.surface.isShiftPressed ())
        {
            switch (index)
            {
                case 0:
                    this.transport.toggleMetronomeTicks ();
                    this.mvHelper.notifyMetronomeTicks ();
                    break;
                case 1:
                    final IParameter enabledParameter = this.groove.getParameter (GrooveParameterID.ENABLED);
                    if (enabledParameter != null)
                    {
                        enabledParameter.setNormalizedValue (enabledParameter.getValue () == 0 ? 1 : 0);
                        this.mvHelper.notifyGrooveEnablement ();
                        return;
                    }
                    break;
                case 2:
                    this.transport.toggleMetronome ();
                    this.mvHelper.notifyMetronome ();
                    break;
                case 3:
                    this.transport.toggleLoop ();
                    this.mvHelper.notifyArrangerRepeat ();
                    break;
            }
        }
        else
            this.selectedParameter = index;
    }
}
