// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.intuitiveinstruments.exquis.mode;

import de.mossgrabers.controller.intuitiveinstruments.exquis.ExquisConfiguration;
import de.mossgrabers.controller.intuitiveinstruments.exquis.controller.ExquisControlSurface;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FourKnobProvider;


/**
 * Provide access to 4 parameters at a time out of 8.
 *
 * @author Jürgen Moßgraber
 */
public class ExquisFourKnobProvider extends FourKnobProvider<ExquisControlSurface, ExquisConfiguration>
{
    private boolean bound1To4 = true;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param parameterProvider The provider to wrap
     */
    public ExquisFourKnobProvider (final ExquisControlSurface surface, final IParameterProvider parameterProvider)
    {
        super (surface, parameterProvider, null);
    }


    /**
     * Check if parameters 1..4 or 5..8 are currently bound.
     *
     * @return True if parameters 1..4 are bound
     */
    public boolean are1To4Bound ()
    {
        return this.bound1To4;
    }


    /**
     * Toggle between using parameters 1-4 and 5-8.
     */
    public void toggle ()
    {
        this.bound1To4 = !this.bound1To4;
    }


    /** {@inheritDoc} */
    @Override
    protected int getIndex (final int index)
    {
        return this.bound1To4 ? index : this.size () + index;
    }
}
