// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.bitwig;

import de.mossgrabers.framework.daw.IGroove;
import de.mossgrabers.framework.daw.bitwig.data.ParameterImpl;
import de.mossgrabers.framework.daw.data.IParameter;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Groove;


/**
 * Encapsulates the Groove instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GrooveProxy implements IGroove
{
    private Groove        groove;
    private IParameter [] parameters = new IParameter [6];


    /**
     * Constructor.
     *
     * @param host The host
     * @param maxParameterValue The maximum number for values (range is 0 till maxParameterValue-1)
     */
    public GrooveProxy (final ControllerHost host, final int maxParameterValue)
    {
        this.groove = host.createGroove ();

        this.parameters[0] = new ParameterImpl (this.groove.getEnabled (), maxParameterValue);
        this.parameters[1] = new ParameterImpl (this.groove.getShuffleAmount (), maxParameterValue);
        this.parameters[2] = new ParameterImpl (this.groove.getShuffleRate (), maxParameterValue);
        this.parameters[3] = new ParameterImpl (this.groove.getAccentAmount (), maxParameterValue);
        this.parameters[4] = new ParameterImpl (this.groove.getAccentRate (), maxParameterValue);
        this.parameters[5] = new ParameterImpl (this.groove.getAccentPhase (), maxParameterValue);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        for (final IParameter parameter: this.parameters)
            parameter.enableObservers (enable);
    }


    /** {@inheritDoc} */
    @Override
    public IParameter [] getParameters ()
    {
        return this.parameters;
    }


    /** {@inheritDoc} */
    @Override
    public void setIndication (final boolean enable)
    {
        for (final IParameter p: this.parameters)
            p.setIndication (enable);
    }
}