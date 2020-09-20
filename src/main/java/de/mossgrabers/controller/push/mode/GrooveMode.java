// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode;

import de.mossgrabers.controller.push.controller.Push1Display;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IGroove;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameterprovider.FixedParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Editing of groove parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GrooveMode extends BaseMode
{
    private static final String [] MENU =
    {
        "Quantize",
        "Groove",
        " ",
        " ",
        " ",
        " ",
        " ",
        " "
    };


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public GrooveMode (final PushControlSurface surface, final IModel model)
    {
        super ("Groove", surface, model);

        final List<IParameter> params = new ArrayList<> (8);
        params.add (EmptyParameter.INSTANCE);
        params.add (EmptyParameter.INSTANCE);
        Collections.addAll (params, this.model.getGroove ().getParameters ());
        final IParameter [] parameters = new IParameter [8];
        params.toArray (parameters);
        this.setParameters (new FixedParameterProvider (parameters));
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();

        this.setActive (true);
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        super.onDeactivate ();

        this.setActive (false);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        if (index < 2)
            return;

        final IParameter [] parameters = this.model.getGroove ().getParameters ();
        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);
            parameters[index - 2].resetValue ();
        }

        parameters[index - 2].touchValue (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        if (index == 0)
            this.surface.getModeManager ().setActiveMode (Modes.REC_ARM);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final IParameter [] parameters = this.model.getGroove ().getParameters ();
        for (int i = 0; i < parameters.length; i++)
        {
            display.setCell (0, 2 + i, parameters[i].getName (8));
            display.setCell (1, 2 + i, parameters[i].getDisplayedValue (8));
            display.setCell (2, 2 + i, parameters[i].getValue (), Format.FORMAT_VALUE);
        }
        display.setCell (0, 0, MENU[0]);
        display.setCell (0, 1, Push1Display.SELECT_ARROW + MENU[1]);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        display.addOptionElement ("", MENU[0], false, null, "", "", false, null, true);
        display.addOptionElement ("", MENU[1], true, null, "", "", false, null, true);

        final IParameter [] parameters = this.model.getGroove ().getParameters ();
        for (int i = 0; i < parameters.length; i++)
            display.addParameterElement (" ", false, "", (ChannelType) null, null, false, parameters[i].getName (10), parameters[i].getValue (), parameters[i].getDisplayedValue (8), this.isKnobTouched[i], -1);

        for (int i = parameters.length + 2; i < 8; i++)
            display.addEmptyElement (true);
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        final int index = this.isButtonRow (1, buttonID);
        if (index >= 0)
        {
            if (index == 0)
                return AbstractMode.BUTTON_COLOR_ON;
            if (index == 1)
                return AbstractMode.BUTTON_COLOR_HI;
        }
        return AbstractMode.BUTTON_COLOR_OFF;
    }


    private void setActive (final boolean enable)
    {
        final IGroove groove = this.model.getGroove ();
        groove.enableObservers (enable);
        groove.setIndication (enable);
    }
}