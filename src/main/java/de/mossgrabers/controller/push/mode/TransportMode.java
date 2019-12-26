// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode;

import de.mossgrabers.controller.push.controller.Push1Display;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.constants.TransportConstants;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Editing of transport parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TransportMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public TransportMode (final PushControlSurface surface, final IModel model)
    {
        super ("Transport", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        // Deactivated knobs to prevent accidental changes when using the small knobs
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        if (index == 0)
            this.model.getTransport ().setPreroll (TransportConstants.PREROLL_NONE);
        else if (index == 1)
            this.model.getTransport ().setPreroll (TransportConstants.PREROLL_1_BAR);
        else if (index == 2)
            this.model.getTransport ().setPreroll (TransportConstants.PREROLL_2_BARS);
        else if (index == 3)
            this.model.getTransport ().setPreroll (TransportConstants.PREROLL_4_BARS);
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        if (index == 0)
            this.model.getTransport ().togglePrerollMetronome ();
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
        {
            final String preroll = this.model.getTransport ().getPreroll ();
            if (index == 0)
                return TransportConstants.PREROLL_NONE.equals (preroll) ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON;
            if (index == 1)
                return TransportConstants.PREROLL_1_BAR.equals (preroll) ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON;
            if (index == 2)
                return TransportConstants.PREROLL_2_BARS.equals (preroll) ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON;
            if (index == 3)
                return TransportConstants.PREROLL_4_BARS.equals (preroll) ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON;
            return AbstractMode.BUTTON_COLOR_OFF;
        }

        index = this.isButtonRow (1, buttonID);
        if (index >= 0)
        {
            final ITransport transport = this.model.getTransport ();
            if (index == 0)
                return transport.isPrerollMetronomeEnabled () ? AbstractMode.BUTTON_COLOR2_HI : AbstractMode.BUTTON_COLOR_ON;
            return AbstractMode.BUTTON_COLOR_OFF;
        }

        return AbstractMode.BUTTON_COLOR_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final ITransport transport = this.model.getTransport ();
        final String preroll = transport.getPreroll ();
        final double tempo = transport.getTempo ();
        display.setBlock (2, 0, "Pre-roll");
        display.setCell (3, 0, (TransportConstants.PREROLL_NONE.equals (preroll) ? Push1Display.SELECT_ARROW : " ") + "None");
        display.setCell (3, 1, (TransportConstants.PREROLL_1_BAR.equals (preroll) ? Push1Display.SELECT_ARROW : " ") + "1 Bar");
        display.setCell (3, 2, (TransportConstants.PREROLL_2_BARS.equals (preroll) ? Push1Display.SELECT_ARROW : " ") + "2 Bars");
        display.setCell (3, 3, (TransportConstants.PREROLL_4_BARS.equals (preroll) ? Push1Display.SELECT_ARROW : " ") + "4 Bars");
        display.setBlock (0, 0, "Play Metro during").setBlock (0, 1, "Pre-roll?");
        display.setCell (1, 0, transport.isPrerollMetronomeEnabled () ? " Yes" : " No");
        display.setCell (0, 4, "Tempo").setCell (1, 4, transport.formatTempo (tempo)).setCell (2, 4, formatTempoBars (tempo));
        display.setCell (0, 5, "Time Sig.").setCell (1, 5, transport.getNumerator () + " / " + transport.getDenominator ());
        display.setBlock (0, 3, "Play Position").setBlock (1, 3, transport.getPositionText ());
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final ITransport transport = this.model.getTransport ();
        final String preroll = transport.getPreroll ();
        final double tempo = transport.getTempo ();

        display.addOptionElement ("Play Metronome during Pre-Roll?", transport.isPrerollMetronomeEnabled () ? "Yes" : "No", transport.isPrerollMetronomeEnabled (), "Pre-roll", "None", TransportConstants.PREROLL_NONE.equals (preroll), false);
        display.addOptionElement ("", "", false, "", "1 Bar", TransportConstants.PREROLL_1_BAR.equals (preroll), false);
        display.addOptionElement ("", "", false, "", "2 Bars", TransportConstants.PREROLL_2_BARS.equals (preroll), false);
        display.addOptionElement ("", "", false, "", "4 Bars", TransportConstants.PREROLL_4_BARS.equals (preroll), false);
        display.addOptionElement ("Time Sig.", "", false, "   " + transport.getNumerator () + " / " + transport.getDenominator (), "", false, false);
        display.addOptionElement ("Play Position", "", false, null, transport.getPositionText (), "", false, null, false, this.isKnobTouched[6]);
        display.addOptionElement ("", "", false, "", "", false, false);
        display.addParameterElement ("Tempo", (int) transport.rescaleTempo (tempo, this.model.getValueChanger ().getUpperBound ()), transport.formatTempo (tempo), this.isKnobTouched[4], -1);
    }


    private static String formatTempoBars (final double value)
    {
        final double v = value - TransportConstants.MIN_TEMPO;
        final int noOfBars = (int) Math.round (16 * v / (TransportConstants.MAX_TEMPO - TransportConstants.MIN_TEMPO));
        final StringBuilder n = new StringBuilder ();
        for (int j = 0; j < noOfBars / 2; j++)
            n.append (Push1Display.BARS_TWO);
        if (noOfBars % 2 == 1)
            n.append (Push1Display.BARS_ONE);
        return StringUtils.pad (n.toString (), 8, Push1Display.BARS_NON.charAt (0));
    }
}