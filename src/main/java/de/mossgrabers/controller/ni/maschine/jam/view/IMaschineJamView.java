// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.view;

import de.mossgrabers.controller.ni.maschine.jam.command.trigger.EncoderMode;


/**
 * Additional methods for views.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IMaschineJamView
{
    /**
     * Change an option of the view.
     *
     * @param temporaryEncoderMode The parameter mode
     * @param control The value change
     */
    void changeOption (EncoderMode temporaryEncoderMode, int control);
}
