// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.mode;

import de.mossgrabers.controller.ni.maschine.mk3.MaschineConfiguration;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.framework.command.TempoCommand;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;


/**
 * The Tempo mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TempoMode extends BaseMode
{
    private final TempoCommand<MaschineControlSurface, MaschineConfiguration> tempoCommand;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public TempoMode (final MaschineControlSurface surface, final IModel model)
    {
        super ("Tempo", surface, model);

        this.tempoCommand = new TempoCommand<> (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        this.tempoCommand.execute (value);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ().clear ();

        final ITransport transport = this.model.getTransport ();
        final double tempo = transport.getTempo ();
        d.setCell (0, 0, "Tempo:").setBlock (0, 1, String.format ("> %.02f", Double.valueOf (tempo)));
        d.setBlock (0, 2, "Time:").setBlock (0, 3, "  " + transport.getPositionText ());
        d.setBlock (1, 2, "Position:").setBlock (1, 3, "  " + transport.getBeatText ());

        d.allDone ();
    }
}