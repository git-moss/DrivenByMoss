// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.command.trigger;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.Modes;
import de.mossgrabers.controller.push.mode.device.DeviceBrowserMode;
import de.mossgrabers.controller.push.mode.device.DeviceParamsMode;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.ModeManager;


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
    public PushCursorCommand (final Direction direction, final IModel model, final PushControlSurface surface)
    {
        super (direction, model, surface);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateArrowStates ()
    {
        final IChannelBank tb = this.model.getCurrentTrackBank ();
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
            final IBrowser browser = this.model.getBrowser ();
            this.canScrollLeft = browser.hasPreviousContentType ();
            this.canScrollRight = browser.hasNextContentType ();
            return;
        }

        if (Modes.isLayerMode (modeManager.getActiveModeId ()))
        {
            final ICursorDevice cd = this.model.getCursorDevice ();
            this.canScrollLeft = cd.canScrollLayersOrDrumPadsUp ();
            this.canScrollRight = cd.canScrollLayersOrDrumPadsDown ();
            return;
        }

        final ITrack sel = tb.getSelectedTrack ();
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
            ((DeviceBrowserMode) modeManager.getActiveMode ()).resetFilterColumn ();
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
            ((DeviceBrowserMode) modeManager.getActiveMode ()).resetFilterColumn ();
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
