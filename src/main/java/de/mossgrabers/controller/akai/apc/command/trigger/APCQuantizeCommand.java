// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apc.command.trigger;

import de.mossgrabers.controller.akai.apc.APCConfiguration;
import de.mossgrabers.controller.akai.apc.controller.APCControlSurface;
import de.mossgrabers.framework.command.trigger.clip.QuantizeCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ICursorTrack;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to quantize the currently selected clip. Toggle pinning if shifted.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class APCQuantizeCommand extends QuantizeCommand<APCControlSurface, APCConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public APCQuantizeCommand (final IModel model, final APCControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        final ICursorTrack cursorTrack = this.model.getCursorTrack ();
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        if (!cursorTrack.doesExist () || !cursorDevice.doesExist ())
            return;
        final boolean pinned = cursorDevice.isPinned ();
        cursorDevice.togglePinned ();
        final boolean cursorTrackPinned = cursorTrack.isPinned ();
        if (pinned == cursorTrackPinned)
            cursorTrack.togglePinned ();
    }
}
