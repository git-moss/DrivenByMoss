// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command;

import de.mossgrabers.framework.command.core.AbstractContinuousCommand;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to change the tempo with a continuous or trigger widget.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TempoCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractContinuousCommand<S, C> implements TriggerCommand
{
    protected final ITransport transport;
    protected final boolean    doIncrease;
    protected final boolean    isAbsolute;
    protected final int        minTempo;
    protected final int        tempoRange;

    protected boolean          isTempoChange;


    /**
     * Constructor for relative continuous.
     *
     * @param model The model
     * @param surface The surface
     */
    public TempoCommand (final IModel model, final S surface)
    {
        this (model, surface, false, false, -1, -1);
    }


    /**
     * Constructor for absolute continuous. The range of the tempo can be limited to the given
     * minimum and maximum tempo which is helpful for an e.g. absolute knob.
     *
     * @param model The model
     * @param surface The surface
     * @param minTempo The minimum tempo to scale the absolute value to
     * @param maxTempo The maximum tempo to scale the absolute value to
     */
    public TempoCommand (final IModel model, final S surface, final int minTempo, final int maxTempo)
    {
        this (model, surface, false, true, minTempo, maxTempo);
    }


    /**
     * Constructor for triggers.
     *
     * @param doIncrease True increases the tempo, false decreases it
     * @param model The model
     * @param surface The surface
     */
    public TempoCommand (final boolean doIncrease, final IModel model, final S surface)
    {
        this (model, surface, doIncrease, false, -1, -1);
    }


    /**
     * Constructor for triggers.
     *
     * @param doIncrease True increases the tempo, false decreases it
     * @param model The model
     * @param surface The surface
     * @param isAbsolute True for absolute values otherwise relative
     * @param minTempo The minimum tempo to scale the absolute value to
     * @param maxTempo The maximum tempo to scale the absolute value to
     */
    protected TempoCommand (final IModel model, final S surface, final boolean doIncrease, final boolean isAbsolute, final int minTempo, final int maxTempo)
    {
        super (model, surface);

        this.doIncrease = doIncrease;
        this.isAbsolute = isAbsolute;

        this.minTempo = minTempo == -1 ? 20 : minTempo;
        final int max = maxTempo == -1 ? 666 : maxTempo;
        this.tempoRange = max - this.minTempo;

        this.transport = this.model.getTransport ();
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        final IValueChanger valueChanger = this.model.getValueChanger ();

        if (this.isAbsolute)
        {
            double tempo = this.minTempo + valueChanger.toNormalizedValue (value) * this.tempoRange;
            if (!this.surface.isShiftPressed ())
                tempo = Math.round (tempo);
            this.transport.setTempo (tempo);
            return;
        }

        this.transport.changeTempo (valueChanger.isIncrease (value), this.surface.isKnobSensitivitySlow ());
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event == ButtonEvent.DOWN)
            this.isTempoChange = true;
        else if (event == ButtonEvent.UP)
            this.isTempoChange = false;
        this.doChangeTempo ();
    }


    private void doChangeTempo ()
    {
        if (!this.isTempoChange)
            return;
        this.transport.changeTempo (this.doIncrease, this.surface.isKnobSensitivitySlow ());
        this.surface.scheduleTask (this::doChangeTempo, 200);
    }
}
