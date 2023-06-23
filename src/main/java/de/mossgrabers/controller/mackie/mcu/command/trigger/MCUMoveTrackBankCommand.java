// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.command.trigger;

import de.mossgrabers.controller.mackie.mcu.MCUConfiguration;
import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.controller.mackie.mcu.mode.device.UserMode;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ICursorTrack;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.bank.IBank;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.FeatureGroupManager;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Optional;


/**
 * Command to move the window of the track bank by 1 or 8.
 *
 * @author Jürgen Moßgraber
 */
public class MCUMoveTrackBankCommand extends AbstractTriggerCommand<MCUControlSurface, MCUConfiguration>
{
    protected final boolean moveLeft;
    protected final boolean moveBy1;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param moveBy1 If true the bank window moves by 1 otherwise by 8
     * @param moveLeft If true the bank window is moved left otherwise to the right
     */
    public MCUMoveTrackBankCommand (final IModel model, final MCUControlSurface surface, final boolean moveBy1, final boolean moveLeft)
    {
        super (model, surface);

        this.moveBy1 = moveBy1;
        this.moveLeft = moveLeft;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        if (this.surface.isSelectPressed ())
        {
            if (this.moveBy1)
            {
                final ICursorTrack cursorTrack = this.model.getCursorTrack ();
                if (this.moveLeft)
                    cursorTrack.swapWithPrevious ();
                else
                    cursorTrack.swapWithNext ();
            }
            else
            {
                final ICursorDevice cursorDevice = this.model.getCursorDevice ();
                if (this.moveLeft)
                    cursorDevice.swapWithPrevious ();
                else
                    cursorDevice.swapWithNext ();
            }
            return;
        }

        final ModeManager modeManager = this.surface.getModeManager ();
        final Modes activeID = modeManager.getActiveID ();
        switch (activeID)
        {
            case EQ_DEVICE_PARAMS, INSTRUMENT_DEVICE_PARAMS, DEVICE_PARAMS:
                final ISpecificDevice device = this.getDevice (activeID);
                if (this.moveBy1)
                    this.handleBankMovement (device.getParameterBank ());
                else if (device instanceof final ICursorDevice cursorDevice)
                {
                    if (this.moveLeft)
                        cursorDevice.selectPrevious ();
                    else
                        cursorDevice.selectNext ();
                }
                this.notifySelectedDeviceAndParameterPage ();
                break;

            case USER:
                final UserMode userMode = (UserMode) modeManager.get (Modes.USER);
                if (this.moveBy1)
                    this.handleBankMovement (userMode.getParameterBank ());
                else
                {
                    userMode.setMode (this.moveLeft);
                    // Also change state on other MCU devices
                    for (final FeatureGroupManager<Modes, IMode> mm: modeManager.getConnectedManagers ())
                        ((UserMode) mm.get (Modes.USER)).setMode (this.moveLeft);
                }
                this.notifySelectedProjectAndParameterPage ();
                break;

            case MARKERS:
                this.handleBankMovement (this.model.getMarkerBank ());
                break;

            case DEVICE_LAYER, DEVICE_LAYER_VOLUME, DEVICE_LAYER_PAN, DEVICE_LAYER_SEND1, DEVICE_LAYER_SEND2, DEVICE_LAYER_SEND3, DEVICE_LAYER_SEND4, DEVICE_LAYER_SEND5, DEVICE_LAYER_SEND6, DEVICE_LAYER_SEND7, DEVICE_LAYER_SEND8:
                final ICursorDevice cursorDevice = this.model.getCursorDevice ();
                this.handleBankMovement (cursorDevice.hasDrumPads () ? cursorDevice.getDrumPadBank () : cursorDevice.getLayerBank ());
                break;

            default:
                this.handleBankMovement (this.model.getCurrentTrackBank ());
                break;
        }
    }


    /** {@index} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN || !this.surface.getConfiguration ().shouldPinFXTracksToLastController ())
            return;

        final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
        if (effectTrackBank != null)
            this.handleBankMovement (effectTrackBank);
    }


    private void handleBankMovement (final IBank<?> bank)
    {
        if (this.moveBy1)
        {
            if (this.moveLeft)
                bank.scrollBackwards ();
            else
                bank.scrollForwards ();
        }
        else
        {
            if (this.moveLeft)
                bank.selectPreviousPage ();
            else
                bank.selectNextPage ();
        }
    }


    private ISpecificDevice getDevice (final Modes modeID)
    {
        switch (modeID)
        {
            case EQ_DEVICE_PARAMS:
                return this.model.getSpecificDevice (DeviceID.EQ);

            case INSTRUMENT_DEVICE_PARAMS:
                return this.model.getSpecificDevice (DeviceID.FIRST_INSTRUMENT);

            default:
                return this.model.getCursorDevice ();
        }
    }


    private void notifySelectedDeviceAndParameterPage ()
    {
        this.mvHelper.delayDisplay ( () -> {

            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            if (!cursorDevice.doesExist ())
                return "No device selected";

            String text = StringUtils.pad (StringUtils.shortenAndFixASCII (cursorDevice.getName (), 27) + " ", 28);

            final Optional<String> selectedItem = cursorDevice.getParameterBank ().getPageBank ().getSelectedItem ();
            if (selectedItem.isPresent ())
            {
                String pageName = selectedItem.get ();
                if (pageName == null || pageName.isBlank ())
                    pageName = "None";
                text += pageName;
            }

            return text;
        });
    }


    private void notifySelectedProjectAndParameterPage ()
    {
        this.mvHelper.delayDisplay ( () -> {

            final UserMode userMode = (UserMode) this.surface.getModeManager ().get (Modes.USER);

            String text;
            if (userMode.isProjectMode ())
                text = "Project";
            else
            {
                text = "Track: ";
                final ICursorTrack cursorTrack = this.model.getCursorTrack ();
                if (cursorTrack.doesExist ())
                    text = text + cursorTrack.getName ();
                else
                    text = text + "None";
            }

            text = StringUtils.pad (text, 28);

            final Optional<String> selectedItem = ((IParameterBank) userMode.getBank ()).getPageBank ().getSelectedItem ();
            if (selectedItem.isPresent ())
            {
                String pageName = selectedItem.get ();
                if (pageName == null || pageName.isBlank ())
                    pageName = "None";
                text += pageName;
            }
            return text;
        });
    }
}
