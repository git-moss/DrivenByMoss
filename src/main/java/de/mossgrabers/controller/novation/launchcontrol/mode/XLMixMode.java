package de.mossgrabers.controller.novation.launchcontrol.mode;

import de.mossgrabers.controller.novation.launchcontrol.LaunchControlXLConfiguration;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLColorManager;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.PanParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.SendParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.List;


/**
 * Mix mode for the LauchControl XL to control 2 sends and panorama.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class XLMixMode extends AbstractMode<LaunchControlXLControlSurface, LaunchControlXLConfiguration, ITrack>
{
    private final CombinedParameterProvider parameterProviderWithPan;
    private final CombinedParameterProvider parameterProviderWithDeviceParams;

    private boolean                   isDeviceActive = false;
    private boolean                   wasLong        = false;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public XLMixMode (final LaunchControlXLControlSurface surface, final IModel model, final List<ContinuousID> controls)
    {
        super ("Send A, B & Panorama", surface, model, true, model.getTrackBank (), controls);

        final SendParameterProvider sendParameterProvider1 = new SendParameterProvider (model, 0, 0);
        final SendParameterProvider sendParameterProvider2 = new SendParameterProvider (model, 1, 0);
        final PanParameterProvider panParameterProvider = new PanParameterProvider (model);
        final BankParameterProvider deviceParameterProvider = new BankParameterProvider (this.model.getCursorDevice ().getParameterBank ());

        this.parameterProviderWithPan = new CombinedParameterProvider (sendParameterProvider1, sendParameterProvider2, panParameterProvider);
        this.parameterProviderWithDeviceParams = new CombinedParameterProvider (sendParameterProvider1, sendParameterProvider2, deviceParameterProvider);
        this.setParameterProvider (this.parameterProviderWithPan);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (row != 0)
            return;

        final ModeManager modeManager = this.surface.getFaderModeManager ();

        if (event == ButtonEvent.DOWN)
        {
            modeManager.setTemporary (Modes.MASTER);
            this.wasLong = false;
            return;
        }

        if (event == ButtonEvent.LONG)
        {
            this.wasLong = true;
            return;
        }

        if (event == ButtonEvent.UP)
        {
            modeManager.restore ();

            if (this.wasLong)
                return;

            final ITrack track = this.model.getTrackBank ().getItem (index);
            if (track.doesExist ())
            {
                if (track.isSelected () && track.isGroup ())
                {
                    track.toggleGroupExpanded ();
                    return;
                }

                track.select ();
                this.mvHelper.notifySelectedTrack ();
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        final int row = index / 8;
        final int column = index % 8;
        final ITrack track = this.model.getTrackBank ().getItem (column);
        switch (row)
        {
            case 0:
                return track.getSendBank ().getItem (0).getValue ();
            case 1:
                return track.getSendBank ().getItem (1).getValue ();
            case 2:
                if (this.isDeviceActive)
                    return this.model.getCursorDevice ().getParameterBank ().getItem (column).getValue ();
                return track.getPan ();
            default:
                return 0;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final int index = buttonID.ordinal () - ButtonID.ROW1_1.ordinal ();
        if (index >= 0 && index < 8)
        {
            final ITrack track = this.model.getTrackBank ().getItem (index);
            if (track.doesExist ())
                return track.isSelected () ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_YELLOW : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_YELLOW_LO;
        }
        return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        final ITrackBank trackBank = this.model.getTrackBank ();
        for (int i = 0; i < 8; i++)
            trackBank.getItem (i).getSendBank ().selectPreviousPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        final ITrackBank trackBank = this.model.getTrackBank ();
        for (int i = 0; i < 8; i++)
            trackBank.getItem (i).getSendBank ().selectNextPage ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItem ()
    {
        final ITrackBank trackBank = this.model.getTrackBank ();
        boolean canScroll = false;
        for (int i = 0; i < 8; i++)
            canScroll |= trackBank.getItem (i).getSendBank ().canScrollPageBackwards ();
        return canScroll;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItem ()
    {
        final ITrackBank trackBank = this.model.getTrackBank ();
        boolean canScroll = false;
        for (int i = 0; i < 8; i++)
            canScroll |= trackBank.getItem (i).getSendBank ().canScrollPageForwards ();
        return canScroll;
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        if (this.isButtonCombination (ButtonID.DEVICE))
        {
            this.model.getCursorDevice ().selectPrevious ();
            this.mvHelper.notifySelectedDevice ();
            return;
        }

        super.selectPreviousItemPage ();
        this.mvHelper.notifySelectedTrack ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        if (this.isButtonCombination (ButtonID.DEVICE))
        {
            this.model.getCursorDevice ().selectNext ();
            this.mvHelper.notifySelectedDevice ();
            return;
        }

        super.selectNextItemPage ();
        this.mvHelper.notifySelectedTrack ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItemPage ()
    {
        if (this.surface.isPressed (ButtonID.DEVICE))
            return this.model.getCursorDevice ().canSelectPrevious ();

        return super.hasPreviousItemPage ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItemPage ()
    {
        if (this.surface.isPressed (ButtonID.DEVICE))
            return this.model.getCursorDevice ().canSelectNext ();

        return super.hasNextItemPage ();
    }


    /**
     * Toggle between panorama and device parameters control on 3rd row.
     */
    public void toggleDeviceActive ()
    {
        this.isDeviceActive = !this.isDeviceActive;

        this.setParameterProvider (this.isDeviceActive ? this.parameterProviderWithDeviceParams : this.parameterProviderWithPan);
        this.bindControls ();
    }


    /**
     * Are device parameters active?
     *
     * @return True if active otherwise panorama is active
     */
    public boolean isDeviceActive ()
    {
        return this.isDeviceActive;
    }
}
