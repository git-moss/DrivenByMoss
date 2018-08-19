// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode;

import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.controller.PushDisplay;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.graphics.display.DisplayModel;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Editing of transport parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TransportMode extends BaseMode
{
    private static final int MIN_TEMPO = 20;
    private static final int MAX_TEMPO = 666;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public TransportMode (final PushControlSurface surface, final IModel model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        // Deactivated knobs to prevent accidental changes when using the small knobs
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (final int index, final boolean isTouched)
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
            this.model.getTransport ().setPreroll (ITransport.PREROLL_NONE);
        else if (index == 1)
            this.model.getTransport ().setPreroll (ITransport.PREROLL_1_BAR);
        else if (index == 2)
            this.model.getTransport ().setPreroll (ITransport.PREROLL_2_BARS);
        else if (index == 3)
            this.model.getTransport ().setPreroll (ITransport.PREROLL_4_BARS);
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
    public void updateFirstRow ()
    {
        final ITransport transport = this.model.getTransport ();
        final String preroll = transport.getPreroll ();
        this.surface.updateButton (20, ITransport.PREROLL_NONE.equals (preroll) ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON);
        this.surface.updateButton (21, ITransport.PREROLL_1_BAR.equals (preroll) ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON);
        this.surface.updateButton (22, ITransport.PREROLL_2_BARS.equals (preroll) ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON);
        this.surface.updateButton (23, ITransport.PREROLL_4_BARS.equals (preroll) ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON);
        this.surface.updateButton (24, AbstractMode.BUTTON_COLOR_OFF);
        this.surface.updateButton (25, AbstractMode.BUTTON_COLOR_OFF);
        this.surface.updateButton (26, AbstractMode.BUTTON_COLOR_OFF);
        this.surface.updateButton (27, AbstractMode.BUTTON_COLOR_OFF);
    }


    /** {@inheritDoc} */
    @Override
    public void updateSecondRow ()
    {
        final ITransport transport = this.model.getTransport ();
        this.surface.updateButton (102, transport.isPrerollMetronomeEnabled () ? AbstractMode.BUTTON_COLOR2_HI : AbstractMode.BUTTON_COLOR_ON);
        this.surface.updateButton (103, AbstractMode.BUTTON_COLOR_OFF);
        this.surface.updateButton (104, AbstractMode.BUTTON_COLOR_OFF);
        this.surface.updateButton (105, AbstractMode.BUTTON_COLOR_OFF);
        this.surface.updateButton (106, AbstractMode.BUTTON_COLOR_OFF);
        this.surface.updateButton (107, AbstractMode.BUTTON_COLOR_OFF);
        this.surface.updateButton (108, AbstractMode.BUTTON_COLOR_OFF);
        this.surface.updateButton (109, AbstractMode.BUTTON_COLOR_OFF);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ();
        final ITransport transport = this.model.getTransport ();
        final String preroll = transport.getPreroll ();
        final double tempo = transport.getTempo ();
        d.clear ();
        d.setBlock (2, 0, "Pre-roll");
        d.setCell (3, 0, (preroll == ITransport.PREROLL_NONE ? PushDisplay.SELECT_ARROW : " ") + "None");
        d.setCell (3, 1, (preroll == ITransport.PREROLL_1_BAR ? PushDisplay.SELECT_ARROW : " ") + "1 Bar");
        d.setCell (3, 2, (preroll == ITransport.PREROLL_2_BARS ? PushDisplay.SELECT_ARROW : " ") + "2 Bars");
        d.setCell (3, 3, (preroll == ITransport.PREROLL_4_BARS ? PushDisplay.SELECT_ARROW : " ") + "4 Bars");
        d.setBlock (0, 0, "Play Metro during").setBlock (0, 1, "Pre-roll?");
        d.setCell (1, 0, transport.isPrerollMetronomeEnabled () ? " Yes" : " No");
        d.setCell (0, 4, "Tempo").setCell (1, 4, transport.formatTempo (tempo)).setCell (2, 4, formatTempoBars (tempo));
        d.setCell (0, 5, "Time Sig.").setCell (1, 5, transport.getNumerator () + " / " + transport.getDenominator ());
        d.setBlock (0, 3, "Play Position").setBlock (1, 3, transport.getPositionText ()).allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final ITransport transport = this.model.getTransport ();
        final String preroll = transport.getPreroll ();
        final double tempo = transport.getTempo ();

        final DisplayModel message = this.surface.getDisplay ().getModel ();
        message.addOptionElement ("Play Metronome during Pre-Roll?", transport.isPrerollMetronomeEnabled () ? "Yes" : "No", transport.isPrerollMetronomeEnabled (), "Pre-roll", "None", preroll == ITransport.PREROLL_NONE, false);
        message.addOptionElement ("", "", false, "", "1 Bar", preroll == ITransport.PREROLL_1_BAR, false);
        message.addOptionElement ("", "", false, "", "2 Bars", preroll == ITransport.PREROLL_2_BARS, false);
        message.addOptionElement ("", "", false, "", "4 Bars", preroll == ITransport.PREROLL_4_BARS, false);
        message.addParameterElement ("Tempo", (int) this.convertTempo (tempo), transport.formatTempo (tempo), this.isKnobTouched[0], -1);
        message.addOptionElement ("  Time Sig.", "", false, "       " + transport.getNumerator () + " / " + transport.getDenominator (), "", false, false);
        message.addOptionElement ("        Play Position", "", false, "        " + transport.getPositionText (), "", false, false);
        message.addOptionElement ("", "", false, "", "", false, false);
        message.send ();
    }


    private static String formatTempoBars (final double value)
    {
        final double v = value - TransportMode.MIN_TEMPO;
        final int noOfBars = (int) Math.round (16 * v / (TransportMode.MAX_TEMPO - TransportMode.MIN_TEMPO));
        final StringBuilder n = new StringBuilder ();
        for (int j = 0; j < noOfBars / 2; j++)
            n.append (PushDisplay.BARS_TWO);
        if (noOfBars % 2 == 1)
            n.append (PushDisplay.BARS_ONE);
        return PushDisplay.pad (n.toString (), 8, PushDisplay.BARS_NON);
    }


    private double convertTempo (final double value)
    {
        final double v = value - TransportMode.MIN_TEMPO;
        return v * (this.model.getValueChanger ().getUpperBound () - 1) / (TransportMode.MAX_TEMPO - TransportMode.MIN_TEMPO);
    }
}