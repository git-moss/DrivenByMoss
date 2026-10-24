// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.mini.mode;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import de.mossgrabers.controller.electra.one.mini.controller.ElectraOneMiniControlSurface;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IDeviceBank;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The device mode. The knobs control 8 parameters of the current device, project of track page.
 *
 * 
 * @author Jürgen Moßgraber
 */
public class ElectraOneMiniDeviceMode extends AbstractElectraOneMiniMode
{
    private static final int                 FIRST_TRACK_GROUP = 108;

    private static final Map<Modes, String>  MAIN_MODES        = new EnumMap<> (Modes.class);
    private static final Map<Modes, ColorEx> MAIN_COLORS       = new EnumMap<> (Modes.class);
    static
    {
        MAIN_MODES.put (Modes.DEVICE_PARAMS, "SEL. DEVICE");
        MAIN_MODES.put (Modes.PROJECT_PARAMETERS, "PROJECT");
        MAIN_MODES.put (Modes.TRACK_PARAMETERS, "TRACK");

        MAIN_COLORS.put (Modes.VOLUME, ColorEx.BLUE);
        MAIN_COLORS.put (Modes.PAN, ColorEx.DARK_PURPLE);
        MAIN_COLORS.put (Modes.TRACK, ColorEx.DARK_YELLOW);
    }

    private final BankParameterProvider bankParameterProvider;
    private final BankParameterProvider projectParameterProvider;
    private final BankParameterProvider trackParameterProvider;

    private IParameterBank              parameterBank;
    private Modes                       turnMode = Modes.DEVICE_PARAMS;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public ElectraOneMiniDeviceMode (final ElectraOneMiniControlSurface surface, final IModel model)
    {
        super (1, Modes.NAME_PARAMETERS, surface, model);

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        this.parameterBank = cursorDevice.getParameterBank ();

        this.bankParameterProvider = new BankParameterProvider (cursorDevice.getParameterBank ());
        this.projectParameterProvider = new BankParameterProvider (this.model.getProject ().getParameterBank ());
        this.trackParameterProvider = new BankParameterProvider (this.model.getCursorTrack ().getParameterBank ());

        this.setParameterProvider (this.bankParameterProvider);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int column, final ButtonEvent event)
    {
        // The 4 function buttons of the Mini
        if (row == 1)
        {
            this.handleFunctionButtons (column, event);
            return;
        }

        // The push buttons of the knobs

        if (event != ButtonEvent.DOWN)
            return;

        final Optional<ITrack> trackOpt = this.getTrack (column);
        if (trackOpt.isEmpty ())
            return;

        if (this.surface.isShiftPressed ())
        {
            // Select device 1-8 on the current track
            final IDevice device = this.model.getCursorDevice ().getDeviceBank ().getItem (column);

            if (device.doesExist () && !device.isSelected ())
                device.select ();
            this.setParameterProvider (this.bankParameterProvider);
            this.bindControls ();
            this.turnMode = Modes.DEVICE_PARAMS;
            return;
        }

        this.getParameterProvider ().get (column).resetValue ();
    }


    private void handleFunctionButtons (final int column, final ButtonEvent event)
    {
        switch (column)
        {
            // Toggle Device, Track and Project parameters
            case 0:
                if (event == ButtonEvent.DOWN)
                {
                    final BankParameterProvider currentProvider;
                    switch (this.turnMode)
                    {
                        case DEVICE_PARAMS:
                            currentProvider = this.projectParameterProvider;
                            this.turnMode = Modes.PROJECT_PARAMETERS;
                            this.parameterBank = this.model.getProject ().getParameterBank ();
                            break;

                        case PROJECT_PARAMETERS:
                            currentProvider = this.trackParameterProvider;
                            this.turnMode = Modes.TRACK_PARAMETERS;
                            this.parameterBank = this.model.getCursorTrack ().getParameterBank ();
                            break;

                        default:
                            currentProvider = this.bankParameterProvider;
                            this.turnMode = Modes.DEVICE_PARAMS;
                            this.parameterBank = this.model.getCursorDevice ().getParameterBank ();
                            break;
                    }

                    this.setParameterProvider (currentProvider);
                    this.bindControls ();
                }
                break;

            // Toggle the device window (if any)
            case 1:
                if (event == ButtonEvent.DOWN)
                {
                    final ICursorDevice cursorDevice = this.model.getCursorDevice ();
                    if (cursorDevice.doesExist ())
                    {
                        if (this.surface.isShiftPressed ())
                            cursorDevice.toggleEnabledState ();
                        else
                            cursorDevice.toggleWindowOpen ();
                    }
                }
                break;

            // Move parameter page left/right
            case 2:
                if (event == ButtonEvent.DOWN)
                {
                    if (this.surface.isShiftPressed ())
                        this.parameterBank.selectPreviousItem ();
                    else
                        this.parameterBank.selectNextItem ();
                }
                break;

            // Toggle SHIFT buttons
            case 3:
                if (event != ButtonEvent.LONG)
                    this.surface.updateShift (event == ButtonEvent.DOWN);
                break;

            // There are no more
            default:
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final IParameterPageBank parameterPageBank = this.parameterBank.getPageBank ();
        final IDeviceBank siblingBank = cursorDevice.getDeviceBank ();
        final IParameterProvider parameterProvider = this.getParameterProvider ();

        final Optional<String> paramPage = parameterPageBank.getSelectedItem ();

        for (int column = 0; column < 8; column++)
        {
            final IParameter param = parameterProvider.get (column);

            final boolean exists = paramPage.isPresent () && param.doesExist ();
            this.pageCache.updateElement (0, column, exists ? StringUtils.fixASCII (paramPage.get () + ": " + param.getName ()) : " ", exists ? ColorEx.ORANGE : ColorEx.BLACK, Boolean.valueOf (exists));
            this.pageCache.updateValue (0, column, param.getValue (), exists ? StringUtils.optimizeName (StringUtils.fixASCII (param.getDisplayedValue ()), 15) : " ");

            // Set device names
            final IDevice device = siblingBank.getItem (column);
            final boolean deviceExists = device.doesExist ();
            this.pageCache.updateGroupLabel (FIRST_TRACK_GROUP + column, deviceExists ? (device.getPosition () + 1) + ": " + StringUtils.fixASCII (device.getName ()) : "None");
        }

        this.pageCache.updateElement (1, 0, MAIN_MODES.get (this.turnMode), MAIN_COLORS.get (this.turnMode), Boolean.TRUE);

        this.pageCache.flush ();
    }
}