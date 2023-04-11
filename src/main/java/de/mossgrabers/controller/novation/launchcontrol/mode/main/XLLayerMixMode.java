package de.mossgrabers.controller.novation.launchcontrol.mode.main;

import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLColorManager;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.controller.novation.launchcontrol.mode.buttons.XLTemporaryButtonMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.parameterprovider.device.PanLayerOrDrumPadParameterProvider;
import de.mossgrabers.framework.parameterprovider.device.SendLayerOrDrumPadParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.List;


/**
 * Mix mode for the LauchControl XL to control 2 sends and panorama of layers.
 *
 * @author Jürgen Moßgraber
 */
public class XLLayerMixMode extends XLAbstractMainMode<ILayer>
{
    private final ISpecificDevice firstInstrument;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public XLLayerMixMode (final LaunchControlXLControlSurface surface, final IModel model, final List<ContinuousID> controls)
    {
        super ("Layer", surface, model, model.getSpecificDevice (DeviceID.FIRST_INSTRUMENT).getLayerBank (), controls);

        this.defaultMode = Modes.DEVICE_LAYER_MUTE;

        this.firstInstrument = model.getSpecificDevice (DeviceID.FIRST_INSTRUMENT);

        final IParameterProvider sendParameterProvider1 = new SendLayerOrDrumPadParameterProvider (this.firstInstrument, 0);
        final IParameterProvider sendParameterProvider2 = new SendLayerOrDrumPadParameterProvider (this.firstInstrument, 1);
        final IParameterProvider panParameterProvider = new PanLayerOrDrumPadParameterProvider (this.firstInstrument);
        final IParameterProvider deviceParameterProvider = new BankParameterProvider (this.firstInstrument.getParameterBank ());

        this.setParameterProviders (
                // Control sends and pan
                new CombinedParameterProvider (sendParameterProvider1, sendParameterProvider2, panParameterProvider),
                // Control sends and device parameters
                new CombinedParameterProvider (sendParameterProvider1, sendParameterProvider2, deviceParameterProvider));

        this.firstInstrument.addHasDrumPadsObserver (hasDrumPads -> this.parametersAdjusted ());
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        final int row = index / 8;
        final int column = index % 8;
        final ILayer layer = this.bank.getItem (column);
        switch (row)
        {
            case 0:
                return layer.getSendBank ().getItem (0).getValue ();
            case 1:
                return layer.getSendBank ().getItem (1).getValue ();
            case 2:
                if (this.configuration.isDeviceActive ())
                    return this.firstInstrument.getParameterBank ().getItem (column).getValue ();
                return layer.getPan ();
            default:
                return 0;
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void executeRow0 (final int index)
    {
        final ILayer layer = this.bank.getItem (index);
        if (!layer.doesExist ())
            return;

        layer.select ();
        this.mvHelper.notifySelectedLayer ();
    }


    /** {@inheritDoc} */
    @Override
    protected void handleRow2 (final int index, final ButtonEvent event)
    {
        final ModeManager trackButtonModeManager = this.surface.getTrackButtonModeManager ();

        switch (index)
        {
            // Device button
            case 0:
                if (event == ButtonEvent.DOWN)
                {
                    trackButtonModeManager.setTemporary (Modes.INSTRUMENT_DEVICE_PARAMS);
                    return;
                }

                if (event == ButtonEvent.UP)
                {
                    if (!((XLTemporaryButtonMode) trackButtonModeManager.get (Modes.INSTRUMENT_DEVICE_PARAMS)).hasBeenUsed ())
                        this.toggleDeviceActive ();
                    trackButtonModeManager.restore ();
                }
                break;

            // Mute button
            case 1:
                if (event == ButtonEvent.DOWN)
                    trackButtonModeManager.setActive (Modes.DEVICE_LAYER_MUTE);
                break;

            // Solo button
            case 2:
                this.alternativeModeSelect (event, Modes.DEVICE_LAYER_SOLO, Modes.CLIP);
                break;

            // Record Arm button
            case 3:
                this.alternativeModeSelect (event, null, Modes.TRANSPORT);
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final ModeManager trackModeManager = this.surface.getTrackButtonModeManager ();
        switch (buttonID)
        {
            case DEVICE:
                return this.surface.getConfiguration ().isDeviceActive () ? 127 : 0;

            case MUTE:
                return trackModeManager.isActive (Modes.DEVICE_LAYER_MUTE) ? 127 : 0;

            case SOLO:
                return trackModeManager.isActive (Modes.DEVICE_LAYER_SOLO) ? 127 : 0;

            case REC_ARM:
                return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;

            case ROW1_1, ROW1_2, ROW1_3, ROW1_4, ROW1_5, ROW1_6, ROW1_7, ROW1_8:
                final int index = buttonID.ordinal () - ButtonID.ROW1_1.ordinal ();

                if (this.surface.isPressed (ButtonID.REC_ARM))
                    return super.getTransportButtonColor (index);

                final ILayer layer = this.bank.getItem (index);
                if (layer.doesExist ())
                    return layer.isSelected () ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_YELLOW : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_YELLOW_LO;
                return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;

            default:
                return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        for (int i = 0; i < 8; i++)
            this.bank.getItem (i).getSendBank ().selectPreviousPage ();
        this.mvHelper.notifySelectedSends (this.bank.getItem (0).getSendBank ());
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        for (int i = 0; i < 8; i++)
            this.bank.getItem (i).getSendBank ().selectNextPage ();
        this.mvHelper.notifySelectedSends (this.bank.getItem (0).getSendBank ());
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItem ()
    {
        boolean canScroll = false;
        for (int i = 0; i < 8; i++)
            canScroll |= this.bank.getItem (i).getSendBank ().canScrollPageBackwards ();
        return canScroll;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItem ()
    {
        boolean canScroll = false;
        for (int i = 0; i < 8; i++)
            canScroll |= this.bank.getItem (i).getSendBank ().canScrollPageForwards ();
        return canScroll;
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        if (this.isButtonCombination (ButtonID.SOLO))
        {
            if (this.surface.getTrackButtonModeManager ().getActive () instanceof final XLTemporaryButtonMode tempMode)
                tempMode.setHasBeenUsed ();
            this.model.getSceneBank ().selectPreviousPage ();
            this.mvHelper.notifyScenePage ();
            return;
        }

        super.selectPreviousItemPage ();
        this.mvHelper.notifySelectedLayer ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        if (this.surface.isPressed (ButtonID.SOLO))
        {
            if (this.surface.getTrackButtonModeManager ().getActive () instanceof final XLTemporaryButtonMode tempMode)
                tempMode.setHasBeenUsed ();
            this.model.getSceneBank ().selectNextPage ();
            this.mvHelper.notifyScenePage ();
            return;
        }

        super.selectNextItemPage ();
        this.mvHelper.notifySelectedLayer ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItemPage ()
    {
        if (this.surface.isPressed (ButtonID.SOLO))
            return this.model.getSceneBank ().canScrollPageBackwards ();

        return super.hasPreviousItemPage ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItemPage ()
    {
        if (this.surface.isPressed (ButtonID.SOLO))
            return this.model.getSceneBank ().canScrollPageForwards ();

        return super.hasNextItemPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void setKnobColor (final int row, final int column, final int value)
    {
        int green = 0;
        int red = 0;
        switch (row)
        {
            // Send A intensity in green
            case 0:
                green = value == 0 ? 0 : value / 42 + 1;
                break;

            // Send B intensity in red
            case 1:
                red = value == 0 ? 0 : value / 42 + 1;
                break;

            // Panorama in amber or Device parameters yellowish intensity in red
            case 2:
                green = value == 0 ? 0 : value / 42 + 1;
                if (this.configuration.isDeviceActive ())
                    red = green == 0 ? 0 : 1;
                else
                    red = green;
                break;

            default:
                // Not used
                return;
        }
        this.surface.setKnobLEDColor (row, column, green, red);
    }


    /** {@inheritDoc} */
    @Override
    public void parametersAdjusted ()
    {
        this.switchBanks (this.firstInstrument.hasDrumPads () ? this.firstInstrument.getDrumPadBank () : this.firstInstrument.getLayerBank ());

        super.parametersAdjusted ();
    }
}
