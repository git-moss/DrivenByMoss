// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.mode;

import de.mossgrabers.controller.electra.one.ElectraOneConfiguration;
import de.mossgrabers.controller.electra.one.controller.ElectraOneColorManager;
import de.mossgrabers.controller.electra.one.controller.ElectraOneControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IProject;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ICursorTrack;
import de.mossgrabers.framework.daw.data.IDevice;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.bank.IDeviceBank;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.mode.track.DefaultTrackMode;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.EmptyParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.RangeFilterParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The device mode. The knobs control 8 parameters of the current page.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceMode extends DefaultTrackMode<ElectraOneControlSurface, ElectraOneConfiguration>
{
    private final PageCache    pageCache;
    private final ITransport   transport;
    private final IMasterTrack masterTrack;
    private final IProject     project;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public DeviceMode (final ElectraOneControlSurface surface, final IModel model)
    {
        super (Modes.NAME_PARAMETERS, surface, model, true, ElectraOneControlSurface.KNOB_IDS);

        this.pageCache = new PageCache (2, surface);

        this.transport = this.model.getTransport ();
        this.masterTrack = this.model.getMasterTrack ();
        this.project = this.model.getProject ();

        final BankParameterProvider bankParameterProvider = new BankParameterProvider (this.model.getCursorDevice ().getParameterBank ());
        final EmptyParameterProvider emptyParameterProvider = new EmptyParameterProvider (1);
        this.setParameterProvider (new CombinedParameterProvider (
                // Row 1
                emptyParameterProvider, new RangeFilterParameterProvider (bankParameterProvider, 0, 4), new FixedParameterProvider (this.masterTrack.getVolumeParameter ()),
                // Row 2
                emptyParameterProvider, new RangeFilterParameterProvider (bankParameterProvider, 4, 4), new FixedParameterProvider (this.project.getCueVolumeParameter ()),
                // These 4 rows only contain buttons
                new EmptyParameterProvider (4 * 6)));
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int column, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();

        switch (column)
        {
            case 0:
                switch (row)
                {
                    case 0:
                        cursorDevice.toggleEnabledState ();
                        break;
                    case 1:
                        cursorDevice.toggleWindowOpen ();
                        break;
                    case 2:
                        cursorDevice.getParameterPageBank ().selectNextPage ();
                        break;
                    case 3:
                        cursorDevice.getParameterPageBank ().selectPreviousPage ();
                        break;
                    case 4:
                        cursorDevice.getDeviceBank ().selectNextPage ();
                        break;
                    case 5:
                        cursorDevice.getDeviceBank ().selectPreviousPage ();
                        break;
                    default:
                        // Not more
                        break;
                }
                break;

            case 5:
                switch (row)
                {
                    case 2:
                        final ICursorTrack cursorTrack = this.model.getCursorTrack ();
                        final boolean pinned = cursorDevice.isPinned ();
                        cursorDevice.togglePinned ();
                        final boolean cursorTrackPinned = cursorTrack.isPinned ();
                        if (pinned == cursorTrackPinned)
                            cursorTrack.togglePinned ();
                        break;
                    case 3:
                        cursorDevice.toggleExpanded ();
                        break;
                    case 4:
                        this.model.getTransport ().startRecording ();
                        break;
                    case 5:
                        this.model.getTransport ().play ();
                        break;
                    default:
                        // Not used
                        break;
                }
                break;

            default:
                if (row < 2)
                    return;
                if (row == 2 || row == 3)
                    cursorDevice.getParameterPageBank ().selectPage ((row - 2) * 4 + column - 1);
                else
                    cursorDevice.getDeviceBank ().getItem ((row - 4) * 4 + column - 1).select ();
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        return ElectraOneColorManager.COLOR_BUTTON_STATE_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final IParameterBank parameterBank = cursorDevice.getParameterBank ();
        final IParameterPageBank parameterPageBank = cursorDevice.getParameterPageBank ();
        final IDeviceBank siblingBank = cursorDevice.getDeviceBank ();

        for (int i = 0; i < 8; i++)
        {
            final IParameter param = parameterBank.getItem (i);

            final boolean exists = param.doesExist ();
            int row = i / 4;
            final int column = 1 + i % 4;
            this.pageCache.updateElement (row, column, exists ? StringUtils.fixASCII (param.getName ()) : "", null, Boolean.valueOf (exists));
            this.pageCache.updateValue (row, column, param.getValue ());

            // TODO How to set the value text?
            // StringUtils.fixASCII (param.getDisplayedValue (8)), color, exists);

            // Set page names
            row += 2;
            final String paramPage = parameterPageBank.getItem (i);
            final boolean isSelected = parameterPageBank.getSelectedItemIndex () == i;
            this.pageCache.updateElement (row, column, StringUtils.fixASCII (paramPage), isSelected ? ElectraOneColorManager.PARAM_PAGE_SELECTED : ElectraOneColorManager.PARAM_PAGE, Boolean.valueOf (!paramPage.isBlank ()));

            // Set device names
            row += 2;
            final IDevice device = siblingBank.getItem (i);
            this.pageCache.updateElement (row, column, StringUtils.fixASCII (device.getName ()), cursorDevice.getIndex () == i ? ElectraOneColorManager.DEVICE_SELECTED : ElectraOneColorManager.DEVICE, Boolean.valueOf (device.doesExist ()));
        }

        this.pageCache.updateColor (0, 0, cursorDevice.isEnabled () ? ElectraOneColorManager.DEVICE_ON : ElectraOneColorManager.DEVICE_OFF);
        this.pageCache.updateColor (1, 0, cursorDevice.isWindowOpen () ? ElectraOneColorManager.WINDOW_OPEN : ElectraOneColorManager.WINDOW);

        this.pageCache.updateColor (2, 5, cursorDevice.isPinned () ? ElectraOneColorManager.PINNED_ON : ElectraOneColorManager.PINNED_OFF);
        this.pageCache.updateColor (3, 5, cursorDevice.isExpanded () ? ElectraOneColorManager.EXPANDED_ON : ElectraOneColorManager.EXPANDED_OFF);

        // Master
        this.pageCache.updateColor (0, 5, this.masterTrack.getColor ());
        this.pageCache.updateValue (0, 5, this.masterTrack.getVolume ());
        this.pageCache.updateValue (1, 5, this.project.getCueVolume ());

        // Transport
        this.pageCache.updateColor (4, 5, this.transport.isRecording () ? ElectraOneColorManager.RECORD_ON : ElectraOneColorManager.RECORD_OFF);
        this.pageCache.updateColor (5, 5, this.transport.isPlaying () ? ElectraOneColorManager.PLAY_ON : ElectraOneColorManager.PLAY_OFF);

        this.pageCache.flush ();
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.pageCache.reset ();

        super.onActivate ();
    }
}