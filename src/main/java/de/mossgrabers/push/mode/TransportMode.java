// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.mode;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.TransportProxy;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.push.controller.DisplayMessage;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.controller.PushDisplay;


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
    public TransportMode (final PushControlSurface surface, final Model model)
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
        if (index == 2)
            this.model.getTransport ().setPreroll (TransportProxy.PREROLL_NONE);
        else if (index == 3)
            this.model.getTransport ().setPreroll (TransportProxy.PREROLL_2_BARS);
        else if (index == 5)
            this.model.getTransport ().togglePrerollMetronome ();
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        if (index == 2)
            this.model.getTransport ().setPreroll (TransportProxy.PREROLL_1_BAR);
        else if (index == 3)
            this.model.getTransport ().setPreroll (TransportProxy.PREROLL_4_BARS);
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final TransportProxy transport = this.model.getTransport ();
        final String preroll = transport.getPreroll ();
        this.surface.updateButton (20, AbstractMode.BUTTON_COLOR_OFF);
        this.surface.updateButton (21, AbstractMode.BUTTON_COLOR_OFF);
        this.surface.updateButton (22, TransportProxy.PREROLL_NONE.equals (preroll) ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON);
        this.surface.updateButton (23, TransportProxy.PREROLL_2_BARS.equals (preroll) ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON);
        this.surface.updateButton (24, AbstractMode.BUTTON_COLOR_OFF);
        this.surface.updateButton (25, transport.isPrerollMetronomeEnabled () ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON);
        this.surface.updateButton (26, AbstractMode.BUTTON_COLOR_OFF);
        this.surface.updateButton (27, AbstractMode.BUTTON_COLOR_OFF);
    }


    /** {@inheritDoc} */
    @Override
    public void updateSecondRow ()
    {
        final TransportProxy transport = this.model.getTransport ();
        final String preroll = transport.getPreroll ();
        this.surface.updateButton (102, AbstractMode.BUTTON_COLOR_OFF);
        this.surface.updateButton (103, AbstractMode.BUTTON_COLOR_OFF);
        this.surface.updateButton (104, TransportProxy.PREROLL_1_BAR.equals (preroll) ? AbstractMode.BUTTON_COLOR2_HI : AbstractMode.BUTTON_COLOR2_ON);
        this.surface.updateButton (105, TransportProxy.PREROLL_4_BARS.equals (preroll) ? AbstractMode.BUTTON_COLOR2_HI : AbstractMode.BUTTON_COLOR2_ON);
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
        final TransportProxy transport = this.model.getTransport ();
        final String preroll = transport.getPreroll ();
        final double tempo = transport.getTempo ();
        d.clear ().setCell (0, 0, "Tempo").setCell (1, 0, transport.formatTempo (tempo)).setCell (2, 0, formatTempoBars (tempo));
        d.setCell (0, 2, "Pre-Roll").setCell (2, 2, (preroll == TransportProxy.PREROLL_NONE ? PushDisplay.RIGHT_ARROW : " ") + "None");
        d.setCell (3, 2, (preroll == TransportProxy.PREROLL_1_BAR ? PushDisplay.RIGHT_ARROW : " ") + "1 Bar");
        d.setCell (2, 3, (preroll == TransportProxy.PREROLL_2_BARS ? PushDisplay.RIGHT_ARROW : " ") + "2 Bars");
        d.setCell (3, 3, (preroll == TransportProxy.PREROLL_4_BARS ? PushDisplay.RIGHT_ARROW : " ") + "4 Bars");
        d.setBlock (0, 2, "Play Metronome").setBlock (1, 2, "during Pre-Roll?").setCell (3, 5, transport.isPrerollMetronomeEnabled () ? "  Yes" : "  No").setBlock (0, 3, "Play Position").setBlock (1, 3, transport.getPositionText ()).allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final TransportProxy transport = this.model.getTransport ();
        final String preroll = transport.getPreroll ();
        final double tempo = transport.getTempo ();

        final DisplayMessage message = ((PushDisplay) this.surface.getDisplay ()).createMessage ();
        message.addByte (DisplayMessage.GRID_ELEMENT_PARAMETERS);
        message.addString ("");
        message.addBoolean (false);
        message.addString ("");
        message.addString ("");
        message.addColor (null);
        message.addBoolean (false);
        message.addString ("Tempo");
        message.addInteger ((int) this.convertTempo (tempo));
        message.addString (transport.formatTempo (tempo));
        message.addBoolean (this.isKnobTouched[0]);
        message.addInteger (-1);

        message.addOptionElement ("", "", false, "", "", false, false);
        message.addOptionElement ("Pre-", "1 Bar", preroll == TransportProxy.PREROLL_1_BAR, "Roll", "None", preroll == TransportProxy.PREROLL_NONE, false);
        message.addOptionElement ("", "4 Bars", preroll == TransportProxy.PREROLL_4_BARS, "", "2 Bars", preroll == TransportProxy.PREROLL_2_BARS, false);
        message.addOptionElement ("          Play Metronome", "", false, "          during Pre-Roll?", "", false, false);
        message.addOptionElement ("", "", false, "", transport.isPrerollMetronomeEnabled () ? "Yes" : "No", transport.isPrerollMetronomeEnabled (), false);
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