// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apc.mode;

import de.mossgrabers.controller.akai.apc.controller.APCControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.parameterprovider.track.SendParameterProvider;


/**
 * Panorama knob mode.
 *
 * @author Jürgen Moßgraber
 */
public class SendMode extends BaseMode<ITrack>
{
    private final int sendIndex;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param sendIndex The index of the send
     */
    public SendMode (final APCControlSurface surface, final IModel model, final int sendIndex)
    {
        super ("Send", surface, model, APCControlSurface.LED_MODE_VOLUME, model.getCurrentTrackBank ());

        this.sendIndex = sendIndex;

        this.setParameterProvider (new SendParameterProvider (model, this.sendIndex, 0));
    }
}
