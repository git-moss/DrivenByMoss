// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode;

import de.mossgrabers.controller.ableton.push.controller.Push1Display;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.GrooveParameterID;
import de.mossgrabers.framework.daw.IGroove;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Editing of groove parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GrooveMode extends BaseMode<IItem>
{
    private static final String TAG_GROOVE = "Groove";

    final IParameter []         params     = new IParameter [8];


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public GrooveMode (final PushControlSurface surface, final IModel model)
    {
        super (TAG_GROOVE, surface, model);

        final IGroove groove = this.model.getGroove ();

        this.params[2] = groove.getParameter (GrooveParameterID.SHUFFLE_AMOUNT);
        this.params[3] = groove.getParameter (GrooveParameterID.SHUFFLE_RATE);

        this.params[5] = groove.getParameter (GrooveParameterID.ACCENT_AMOUNT);
        this.params[6] = groove.getParameter (GrooveParameterID.ACCENT_PHASE);
        this.params[7] = groove.getParameter (GrooveParameterID.ACCENT_RATE);

        for (int i = 0; i < this.params.length; i++)
        {
            if (this.params[i] == null)
                this.params[i] = EmptyParameter.INSTANCE;
        }

        this.setParameterProvider (new FixedParameterProvider (this.params));
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
        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);
            this.params[index].resetValue ();
        }

        this.params[index].touchValue (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        if (index == 0)
        {
            final IParameter parameter = this.model.getGroove ().getParameter (GrooveParameterID.ENABLED);
            parameter.setNormalizedValue (parameter.getValue () > 0 ? 0 : 1);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        if (index == 0)
            this.surface.getModeManager ().setActive (Modes.REC_ARM);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        display.setCell (0, 0, "Quantize");
        display.setCell (0, 1, Push1Display.SELECT_ARROW + TAG_GROOVE);

        final IGroove groove = this.model.getGroove ();
        final IParameter enabledParameter = groove.getParameter (GrooveParameterID.ENABLED);
        if (enabledParameter != null)
            display.setCell (3, 0, enabledParameter.getValue () == 0 ? "  Off" : "Enabled");

        display.setCell (2, 1, "Shuffle:");
        this.displayParameter (display, GrooveParameterID.SHUFFLE_AMOUNT, 2);
        this.displayParameter (display, GrooveParameterID.SHUFFLE_RATE, 3);

        final boolean hasAccent = groove.getParameter (GrooveParameterID.ACCENT_AMOUNT) != EmptyParameter.INSTANCE;
        display.setCell (2, 4, hasAccent ? " Accent:" : "");
        this.displayParameter (display, GrooveParameterID.ACCENT_AMOUNT, 5);
        this.displayParameter (display, GrooveParameterID.ACCENT_PHASE, 6);
        this.displayParameter (display, GrooveParameterID.ACCENT_RATE, 7);
    }


    private void displayParameter (final ITextDisplay display, final GrooveParameterID paramID, final int index)
    {
        IParameter p = this.model.getGroove ().getParameter (paramID);
        if (p == null)
            p = EmptyParameter.INSTANCE;
        if (!p.doesExist ())
            return;
        display.setCell (0, index, p.getName (8));
        display.setCell (1, index, p.getDisplayedValue (8));
        display.setCell (2, index, p.getValue (), Format.FORMAT_VALUE);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final IGroove groove = this.model.getGroove ();
        final IParameter enabledParameter = groove.getParameter (GrooveParameterID.ENABLED);

        String paramText;
        if (enabledParameter == null)
            paramText = "";
        else
            paramText = enabledParameter.getValue () == 0 ? "Off" : "Enabled";

        display.addOptionElement ("", "Quantize", false, null, "", paramText, enabledParameter != null && enabledParameter.getValue () > 0, null, true);
        display.addOptionElement ("", TAG_GROOVE, true, null, "      Shuffle", "", false, null, true);

        this.displayParameter (display, GrooveParameterID.SHUFFLE_AMOUNT, 2);
        this.displayParameter (display, GrooveParameterID.SHUFFLE_RATE, 3);

        final boolean hasAccent = groove.getParameter (GrooveParameterID.ACCENT_AMOUNT) != EmptyParameter.INSTANCE;
        display.addOptionElement ("", hasAccent ? "" : " ", false, null, hasAccent ? "      Accent" : "", "", false, null, true);

        this.displayParameter (display, GrooveParameterID.ACCENT_AMOUNT, 5);
        this.displayParameter (display, GrooveParameterID.ACCENT_PHASE, 6);
        this.displayParameter (display, GrooveParameterID.ACCENT_RATE, 7);
    }


    private void displayParameter (final IGraphicDisplay display, final GrooveParameterID paramID, final int index)
    {
        IParameter p = this.model.getGroove ().getParameter (paramID);
        if (p == null)
            p = EmptyParameter.INSTANCE;
        display.addParameterElement (" ", false, "", (ChannelType) null, null, false, p.getName (10), p.getValue (), p.getDisplayedValue (8), this.isKnobTouched (index), -1);
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        int index = this.isButtonRow (1, buttonID);
        if (index >= 0)
        {
            if (index == 0)
                return AbstractFeatureGroup.BUTTON_COLOR_ON;
            if (index == 1)
                return AbstractMode.BUTTON_COLOR_HI;
        }
        else
        {
            index = this.isButtonRow (0, buttonID);
            if (index == 0)
            {
                final IParameter parameter = this.model.getGroove ().getParameter (GrooveParameterID.ENABLED);
                if (parameter != null)
                    return parameter.getValue () > 0 ? AbstractMode.BUTTON_COLOR_HI : AbstractFeatureGroup.BUTTON_COLOR_ON;
            }
        }
        return AbstractFeatureGroup.BUTTON_COLOR_OFF;
    }


    private void setActive (final boolean enable)
    {
        final IGroove groove = this.model.getGroove ();
        groove.enableObservers (enable);
        groove.setIndication (enable);
    }
}