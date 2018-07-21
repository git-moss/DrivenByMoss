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
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.Mode;
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
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ISceneBank sceneBank = tb.getSceneBank ();
        this.canScrollUp = sceneBank.canScrollBackwards ();
        this.canScrollDown = sceneBank.canScrollForwards ();

        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActiveOrTempMode (Modes.MODE_DEVICE_PARAMS))
        {
            final DeviceParamsMode mode = (DeviceParamsMode) modeManager.getActiveOrTempMode ();
            this.canScrollLeft = mode.canSelectPreviousPage ();
            this.canScrollRight = mode.canSelectNextPage ();
            return;
        }

        if (modeManager.isActiveOrTempMode (Modes.MODE_BROWSER))
        {
            final IBrowser browser = this.model.getBrowser ();
            this.canScrollLeft = browser.hasPreviousContentType ();
            this.canScrollRight = browser.hasNextContentType ();
            return;
        }

        if (Modes.isLayerMode (modeManager.getActiveOrTempModeId ()))
        {
            final ICursorDevice cd = this.model.getCursorDevice ();
            this.canScrollLeft = cd.canScrollLayersOrDrumPadsUp ();
            this.canScrollRight = cd.canScrollLayersOrDrumPadsDown ();
            return;
        }

        final ITrack sel = tb.getSelectedItem ();
        final int selIndex = sel != null ? sel.getIndex () : -1;
        this.canScrollLeft = selIndex > 0 || tb.canScrollBackwards ();
        this.canScrollRight = selIndex >= 0 && selIndex < 7 && tb.getItem (selIndex + 1).doesExist () || tb.canScrollForwards ();
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollLeft ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActiveOrTempMode (Modes.MODE_DEVICE_PARAMS))
        {
            final DeviceParamsMode paramsMode = (DeviceParamsMode) modeManager.getActiveOrTempMode ();
            if (this.surface.isShiftPressed ())
                paramsMode.selectPreviousPageBank ();
            else
                paramsMode.selectPreviousPage ();
            return;
        }

        if (modeManager.isActiveOrTempMode (Modes.MODE_BROWSER))
        {
            ((DeviceBrowserMode) modeManager.getActiveOrTempMode ()).resetFilterColumn ();
            this.model.getBrowser ().previousContentType ();
            return;
        }

        if (Modes.isLayerMode (modeManager.getActiveOrTempModeId ()))
        {
            if (this.surface.isShiftPressed ())
                this.model.getCursorDevice ().previousLayerOrDrumPadBank ();
            else
                this.model.getCursorDevice ().previousLayerOrDrumPad ();
            return;
        }

        final Mode activeMode = this.surface.getModeManager ().getActiveOrTempMode ();
        if (activeMode != null)
            activeMode.selectPreviousTrack ();
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollRight ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActiveOrTempMode (Modes.MODE_DEVICE_PARAMS))
        {
            final DeviceParamsMode activeMode = (DeviceParamsMode) modeManager.getActiveOrTempMode ();
            if (this.surface.isShiftPressed ())
                activeMode.selectNextPageBank ();
            else
                activeMode.selectNextPage ();
            return;
        }

        if (modeManager.isActiveOrTempMode (Modes.MODE_BROWSER))
        {
            ((DeviceBrowserMode) modeManager.getActiveOrTempMode ()).resetFilterColumn ();
            this.model.getBrowser ().nextContentType ();
            return;
        }

        if (Modes.isLayerMode (modeManager.getActiveOrTempModeId ()))
        {
            if (this.surface.isShiftPressed ())
                this.model.getCursorDevice ().nextLayerOrDrumPadBank ();
            else
                this.model.getCursorDevice ().nextLayerOrDrumPad ();
            return;
        }

        final Mode activeMode = this.surface.getModeManager ().getActiveOrTempMode ();
        if (activeMode != null)
            activeMode.selectNextTrack ();
    }
}
