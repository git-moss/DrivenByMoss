// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.core.view;

import de.mossgrabers.controller.ni.maschine.core.command.trigger.EncoderMode;
import de.mossgrabers.framework.daw.clip.ISessionAlternative;


/**
 * Additional methods for views.
 *
 * @author Jürgen Moßgraber
 */
public interface IMaschineView extends ISessionAlternative
{
    /**
     * Change an option of the view.
     *
     * @param temporaryEncoderMode The parameter mode
     * @param control The value change
     */
    void changeOption (EncoderMode temporaryEncoderMode, int control);
}
