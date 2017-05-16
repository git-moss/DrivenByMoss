// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.daw.data.ParameterData;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Groove;


/**
 * Encapsulates the Groove instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GrooveProxy
{
    private Groove           groove;
    private ParameterData [] parameters = new ParameterData [6];


    /**
     * Constructor.
     *
     * @param host The host
     * @param maxParameterValue The maximum number for values (range is 0 till maxParameterValue-1)
     */
    public GrooveProxy (final ControllerHost host, final int maxParameterValue)
    {
        this.groove = host.createGroove ();

        this.parameters[0] = new ParameterData (this.groove.getEnabled (), maxParameterValue);
        this.parameters[1] = new ParameterData (this.groove.getShuffleAmount (), maxParameterValue);
        this.parameters[2] = new ParameterData (this.groove.getShuffleRate (), maxParameterValue);
        this.parameters[3] = new ParameterData (this.groove.getAccentAmount (), maxParameterValue);
        this.parameters[4] = new ParameterData (this.groove.getAccentRate (), maxParameterValue);
        this.parameters[5] = new ParameterData (this.groove.getAccentPhase (), maxParameterValue);
    }


    /**
     * Dis-/Enable all attributes. They are enabled by default. Use this function if values are
     * currently not needed to improve performance.
     *
     * @param enable True to enable
     */
    public void enableObservers (final boolean enable)
    {
        for (final ParameterData parameter: this.parameters)
            parameter.enableObservers (enable);
    }


    /**
     * Get all groove parameters.
     *
     * @return The groove parameters
     */
    public ParameterData [] getParameters ()
    {
        return this.parameters;
    }


    /**
     * Sets indication for all groove parameters.
     *
     * @param enable True to enable
     */
    public void setIndication (final boolean enable)
    {
        for (final ParameterData p: this.parameters)
            p.setIndication (enable);
    }
}