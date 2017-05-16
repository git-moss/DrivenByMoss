// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.command.trigger;

import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.BrowserProxy;
import de.mossgrabers.framework.daw.CursorDeviceProxy;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.mode.Modes;
import de.mossgrabers.push.mode.device.DeviceParamsMode;


/**
 * Command for cursor arrow keys.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PushCursorCommand extends de.mossgrabers.framework.command.trigger.CursorCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param direction The direction of the pushed cursor arrow
     * @param model The model
     * @param surface The surface
     */
    public PushCursorCommand (final Direction direction, final Model model, final PushControlSurface surface)
    {
        super (direction, model, surface);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateArrowStates ()
    {
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        this.canScrollUp = tb.canScrollScenesUp ();
        this.canScrollDown = tb.canScrollScenesDown ();

        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActiveMode (Modes.MODE_DEVICE_PARAMS))
        {
            final DeviceParamsMode mode = (DeviceParamsMode) modeManager.getActiveMode ();
            this.canScrollLeft = mode.canSelectPreviousPage ();
            this.canScrollRight = mode.canSelectNextPage ();
            return;
        }

        if (modeManager.isActiveMode (Modes.MODE_BROWSER))
        {
            final BrowserProxy browser = this.model.getBrowser ();
            final int index = browser.getSelectedContentTypeIndex ();
            this.canScrollLeft = index > 0;
            this.canScrollRight = index < browser.getContentTypeNames ().length - 1;
            return;
        }

        if (Modes.isLayerMode (modeManager.getActiveModeId ()))
        {
            final CursorDeviceProxy cd = this.model.getCursorDevice ();
            this.canScrollLeft = cd.canScrollLayersOrDrumPadsUp ();
            this.canScrollRight = cd.canScrollLayersOrDrumPadsDown ();
            return;
        }

        final TrackData sel = tb.getSelectedTrack ();
        final int selIndex = sel != null ? sel.getIndex () : -1;
        this.canScrollLeft = selIndex > 0 || tb.canScrollTracksUp ();
        this.canScrollRight = selIndex >= 0 && selIndex < 7 && tb.getTrack (selIndex + 1).doesExist () || tb.canScrollTracksDown ();
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollLeft ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActiveMode (Modes.MODE_DEVICE_PARAMS))
        {
            final DeviceParamsMode paramsMode = (DeviceParamsMode) modeManager.getActiveMode ();
            if (this.surface.isShiftPressed ())
                paramsMode.selectPreviousPageBank ();
            else
                paramsMode.selectPreviousPage ();
            return;
        }

        if (modeManager.isActiveMode (Modes.MODE_BROWSER))
        {
            this.model.getBrowser ().previousContentType ();
            return;
        }

        if (Modes.isLayerMode (modeManager.getActiveModeId ()))
        {
            if (this.surface.isShiftPressed ())
                this.model.getCursorDevice ().previousLayerOrDrumPadBank ();
            else
                this.model.getCursorDevice ().previousLayerOrDrumPad ();
            return;
        }

        this.scrollTracksLeft ();
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollRight ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActiveMode (Modes.MODE_DEVICE_PARAMS))
        {
            final DeviceParamsMode activeMode = (DeviceParamsMode) modeManager.getActiveMode ();
            if (this.surface.isShiftPressed ())
                activeMode.selectNextPageBank ();
            else
                activeMode.selectNextPage ();
            return;
        }

        if (modeManager.isActiveMode (Modes.MODE_BROWSER))
        {
            this.model.getBrowser ().nextContentType ();
            return;
        }

        if (Modes.isLayerMode (modeManager.getActiveModeId ()))
        {
            if (this.surface.isShiftPressed ())
                this.model.getCursorDevice ().nextLayerOrDrumPadBank ();
            else
                this.model.getCursorDevice ().nextLayerOrDrumPad ();
            return;
        }

        this.scrollTracksRight ();
    }
}
