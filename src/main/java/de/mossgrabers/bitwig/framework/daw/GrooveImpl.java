// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.daw.data.ParameterImpl;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.GrooveParameterID;
import de.mossgrabers.framework.daw.IGroove;
import de.mossgrabers.framework.parameter.IParameter;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Groove;

import java.util.EnumMap;
import java.util.Map;


/**
 * Encapsulates the Groove instance.
 *
 * @author Jürgen Moßgraber
 */
public class GrooveImpl implements IGroove
{
    private final Groove                             groove;
    private final Map<GrooveParameterID, IParameter> parameters = new EnumMap<> (GrooveParameterID.class);


    /**
     * Constructor.
     *
     * @param host The host
     * @param valueChanger The value changer
     */
    public GrooveImpl (final ControllerHost host, final IValueChanger valueChanger)
    {
        this.groove = host.createGroove ();

        this.parameters.put (GrooveParameterID.ENABLED, new ParameterImpl (valueChanger, this.groove.getEnabled (), 0));
        this.parameters.put (GrooveParameterID.SHUFFLE_AMOUNT, new ParameterImpl (valueChanger, this.groove.getShuffleAmount (), 1));
        this.parameters.put (GrooveParameterID.SHUFFLE_RATE, new ParameterImpl (valueChanger, this.groove.getShuffleRate (), 2));
        this.parameters.put (GrooveParameterID.ACCENT_AMOUNT, new ParameterImpl (valueChanger, this.groove.getAccentAmount (), 3));
        this.parameters.put (GrooveParameterID.ACCENT_RATE, new ParameterImpl (valueChanger, this.groove.getAccentRate (), 4));
        this.parameters.put (GrooveParameterID.ACCENT_PHASE, new ParameterImpl (valueChanger, this.groove.getAccentPhase (), 5));
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        for (final IParameter parameter: this.parameters.values ())
            parameter.enableObservers (enable);
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getParameter (final GrooveParameterID id)
    {
        return this.parameters.get (id);
    }


    /** {@inheritDoc} */
    @Override
    public void setIndication (final boolean enable)
    {
        for (final IParameter p: this.parameters.values ())
            p.setIndication (enable);
    }
}