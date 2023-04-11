// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.hardware;

import de.mossgrabers.bitwig.framework.daw.data.ParameterImpl;
import de.mossgrabers.bitwig.framework.daw.data.Util;

import com.bitwig.extension.controller.api.ContinuousHardwareControl;
import com.bitwig.extension.controller.api.DoubleValue;
import com.bitwig.extension.controller.api.StringValue;


/**
 * Helper functions for the Bitwig hardware API.
 *
 * @author Jürgen Moßgraber
 */
public class HwUtils
{
    /**
     * Private due to utility class.
     */
    private HwUtils ()
    {
        // Intentionally empty
    }


    /**
     * Mark all target value parameters of a continuous hardware control.
     *
     * @param control The control to mark
     */
    public static void markInterested (final ContinuousHardwareControl<?> control)
    {
        control.targetName ().markInterested ();
        control.targetDisplayedValue ().markInterested ();
        control.targetValue ().markInterested ();
        control.modulatedTargetValue ().markInterested ();
    }


    /**
     * Un-/subscribe from all target value parameters of a continuous hardware control.
     *
     * @param enable True to subscribe
     * @param control The continuous hardware control
     * @param param The parameter which is mapped to the control
     */
    public static void enableObservers (final boolean enable, final ContinuousHardwareControl<?> control, final ParameterImpl param)
    {
        final StringValue targetName = control.targetName ();
        final StringValue targetDisplayedValue = control.targetDisplayedValue ();
        final DoubleValue targetValue = control.targetValue ();
        final DoubleValue modulatedTargetValue = control.modulatedTargetValue ();

        Util.setIsSubscribed (targetName, enable);
        Util.setIsSubscribed (targetDisplayedValue, enable);
        Util.setIsSubscribed (targetValue, enable);
        Util.setIsSubscribed (modulatedTargetValue, enable);

        if (param == null)
            return;
        if (enable)
            param.setTargetInfo (targetName, targetDisplayedValue, targetValue, modulatedTargetValue);
        else
            param.setTargetInfo (null, null, null, null);
    }
}
