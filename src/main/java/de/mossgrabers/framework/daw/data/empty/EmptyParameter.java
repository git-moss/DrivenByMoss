// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.empty;

import de.mossgrabers.framework.parameter.AbstractParameterImpl;
import de.mossgrabers.framework.parameter.IParameter;


/**
 * Default data for an empty send.
 *
 * @author Jürgen Moßgraber
 */
public class EmptyParameter extends AbstractParameterImpl
{
    /** The singleton. */
    public static final IParameter INSTANCE = new EmptyParameter ();


    /**
     * Constructor.
     */
    protected EmptyParameter ()
    {
        super (null, -1);
    }
}
