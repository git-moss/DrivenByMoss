// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.scale.Scales;


/**
 * Interface to a factory for creating models and midi access.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface ISetupFactory
{
    /**
     * Create a new model.
     *
     * @param colorManager The color manager
     * @param valueChanger The value changer
     * @param scales The scales object
     * @param modelSetup The configuration parameters for the model
     * @return The model
     */
    IModel createModel (final ColorManager colorManager, final IValueChanger valueChanger, final Scales scales, final ModelSetup modelSetup);


    /**
     * Create the midi access object.
     *
     * @return The object
     */
    IMidiAccess createMidiAccess ();


    /**
     * Get all supported Arpeggiator modes.
     *
     * @return The modes
     */
    ArpeggiatorMode [] getArpeggiatorModes ();
}
