// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.empty;

import de.mossgrabers.framework.daw.data.AbstractParameterImpl;
import de.mossgrabers.framework.daw.data.IParameter;


/**
 * Default data for an empty send.
 *
 * @author J&uuml;rgen Mo&szlig;graber
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
