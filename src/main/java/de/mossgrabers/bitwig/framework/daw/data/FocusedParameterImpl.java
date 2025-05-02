// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import com.bitwig.extension.controller.api.Parameter;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.parameter.IFocusedParameter;


/**
 * Encapsulates the data of a parameter in focus, e.g. hovered by the mouse or last touched.
 *
 * @author Jürgen Moßgraber
 */
public class FocusedParameterImpl extends ParameterImpl implements IFocusedParameter
{
    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param parameter The parameter
     */
    public FocusedParameterImpl (final IValueChanger valueChanger, final Parameter parameter)
    {
        super (valueChanger, parameter, 0);
    }
}
