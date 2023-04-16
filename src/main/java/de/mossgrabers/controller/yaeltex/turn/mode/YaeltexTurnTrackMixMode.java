package de.mossgrabers.controller.yaeltex.turn.mode;

import de.mossgrabers.controller.yaeltex.turn.YaeltexTurnConfiguration;
import de.mossgrabers.controller.yaeltex.turn.controller.YaeltexTurnColorManager;
import de.mossgrabers.controller.yaeltex.turn.controller.YaeltexTurnControlSurface;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IDevice;
import de.mossgrabers.framework.daw.data.IEqualizerDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.PanParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.SendParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.VolumeParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Track mix mode for the Yaeltex Turn.
 *
 * @author Jürgen Moßgraber
 */
public class YaeltexTurnTrackMixMode extends AbstractParameterMode<YaeltexTurnControlSurface, YaeltexTurnConfiguration, ITrack>
{
    private final IEqualizerDevice           eqDevice;

    protected final List<IParameterProvider> providers = new ArrayList<> ();


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public YaeltexTurnTrackMixMode (final YaeltexTurnControlSurface surface, final IModel model, final List<ContinuousID> controls)
    {
        this ("Track Mixer", surface, model, controls);

        this.providers.add (0, new BankParameterProvider (this.model.getCursorDevice ().getParameterBank ()));
        this.providers.add (0, new SendParameterProvider (model, 1, 0));
        this.providers.add (0, new SendParameterProvider (model, 0, 0));
        this.providers.add (0, new PanParameterProvider (model));
        this.setParameterProvider (new CombinedParameterProvider (this.providers));
    }


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     * @param controls The IDs of the knobs or faders to control this mode
     */
    protected YaeltexTurnTrackMixMode (final String name, final YaeltexTurnControlSurface surface, final IModel model, final List<ContinuousID> controls)
    {
        super (name, surface, model, true, model.getTrackBank (), controls);

        this.eqDevice = (IEqualizerDevice) model.getSpecificDevice (DeviceID.EQ);
        this.providers.add (new FixedParameterProvider (this.eqDevice.getTypeParameters ()));
        this.providers.add (new FixedParameterProvider (this.eqDevice.getQParameters ()));
        this.providers.add (new FixedParameterProvider (this.eqDevice.getFrequencyParameters ()));
        this.providers.add (new FixedParameterProvider (this.eqDevice.getGainParameters ()));
        this.providers.add (new VolumeParameterProvider (model));

        this.model.getTrackBank ().addSelectionObserver (this::trackSelectionChanged);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.trackSelectionChanged (-1, true);

        super.onActivate ();
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
                return track.getPan ();
            case 1:
                return track.getSendBank ().getItem (0).getValue ();
            case 2:
                return track.getSendBank ().getItem (1).getValue ();
            case 3:
                return this.model.getCursorDevice ().getParameterBank ().getItem (column).getValue ();
            default:
                return 0;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobColor (final int index)
    {
        final int row = index / 8;
        final int column = index % 8;
        final ColorEx color = row < 3 ? this.model.getTrackBank ().getItem (column).getColor () : ColorEx.WHITE;
        return YaeltexTurnColorManager.getIndexFor (color);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        if (this.surface.isShiftPressed ())
        {
            this.onShiftButton (row, index);
            return;
        }

        final ITrack track = this.model.getCurrentTrackBank ().getItem (index);
        switch (row)
        {
            case 0:
                final IParameter crossfadeParameter = track.getCrossfadeParameter ();
                final double value = this.model.getValueChanger ().toNormalizedValue (crossfadeParameter.getValue ()) + 0.5;
                crossfadeParameter.setNormalizedValue (value > 1.1 ? 0 : value);
                break;

            case 1:
                if (this.surface.isSelectPressed ())
                    track.returnToArrangement ();
                else
                    track.stop (this.surface.isShiftPressed ());
                break;

            case 2:
                track.toggleRecArm ();
                break;

            case 3:
                if (this.surface.isSelectPressed ())
                    this.model.getProject ().clearSolo ();
                else
                    track.toggleSolo ();
                break;

            case 4:
                if (this.surface.isSelectPressed ())
                    this.model.getProject ().clearMute ();
                else
                    track.toggleMute ();
                break;

            case 5:
                track.selectOrExpandGroup ();
                break;

            default:
                // Not used
                break;
        }
    }


    /**
     * Handle button combinations with Shift.
     *
     * @param row The button row
     * @param index The button index
     */
    protected void onShiftButton (final int row, final int index)
    {
        if (row == 0 || row == 1)
        {
            final int deviceIndex = 2 * index + row;
            final IDevice device = this.model.getCursorDevice ().getDeviceBank ().getItem (deviceIndex);
            if (device.doesExist ())
            {
                device.select ();
                this.mvHelper.notifySelectedDevice ();
            }
            return;
        }

        if (row == 2 || row == 3)
        {
            final int paramPageIndex = 2 * index + row - 2;
            this.model.getCursorDevice ().getParameterPageBank ().selectPage (paramPageIndex);
            this.mvHelper.notifySelectedDeviceAndParameterPage ();
            return;
        }

        if (row == 5)
        {
            this.surface.getDisplay ().notify (AbstractConfiguration.getNewClipLengthValue (index));
            this.surface.getConfiguration ().setNewClipLength (index);
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        if (this.surface.isShiftPressed ())
            return this.getButtonShiftColor (buttonID);

        int color = YaeltexTurnColorManager.BLACK;

        switch (buttonID)
        {
            case ROW1_1, ROW1_2, ROW1_3, ROW1_4, ROW1_5, ROW1_6, ROW1_7, ROW1_8:
                final ITrack track = this.getTrack (buttonID, ButtonID.ROW1_1);
                final String crossfadeMode = track.getCrossfadeParameter ().getDisplayedValue ();
                if (track.doesExist () && !"AB".equals (crossfadeMode))
                    color = "B".equals (crossfadeMode) ? YaeltexTurnColorManager.BLUE : YaeltexTurnColorManager.CYAN;
                break;

            case ROW2_1, ROW2_2, ROW2_3, ROW2_4, ROW2_5, ROW2_6, ROW2_7, ROW2_8:
                if (this.getTrack (buttonID, ButtonID.ROW2_1).doesExist ())
                    color = this.surface.isPressed (buttonID) ? YaeltexTurnColorManager.WHITE : YaeltexTurnColorManager.DARK_GRAY;
                break;

            case ROW3_1, ROW3_2, ROW3_3, ROW3_4, ROW3_5, ROW3_6, ROW3_7, ROW3_8:
                color = this.getTrack (buttonID, ButtonID.ROW3_1).isRecArm () ? YaeltexTurnColorManager.RED : YaeltexTurnColorManager.BLACK;
                break;

            case ROW4_1, ROW4_2, ROW4_3, ROW4_4, ROW4_5, ROW4_6, ROW4_7, ROW4_8:
                color = this.getTrack (buttonID, ButtonID.ROW4_1).isSolo () ? YaeltexTurnColorManager.YELLOW : YaeltexTurnColorManager.BLACK;
                break;

            case ROW5_1, ROW5_2, ROW5_3, ROW5_4, ROW5_5, ROW5_6, ROW5_7, ROW5_8:
                color = this.getTrack (buttonID, ButtonID.ROW5_1).isMute () ? YaeltexTurnColorManager.ORANGE : YaeltexTurnColorManager.BLACK;
                break;

            case ROW6_1, ROW6_2, ROW6_3, ROW6_4, ROW6_5, ROW6_6, ROW6_7, ROW6_8:
                color = this.getTrack (buttonID, ButtonID.ROW6_1).isSelected () ? YaeltexTurnColorManager.YELLOW : YaeltexTurnColorManager.WHITE;
                break;

            // Not used
            default:
                break;
        }

        return color;
    }


    /**
     * Get the button colors in combination with Shift.
     *
     * @param buttonID The button ID
     * @return The color index
     */
    protected int getButtonShiftColor (final ButtonID buttonID)
    {
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();

        int deviceIndex = -1;
        int paramPageIndex = -1;
        switch (buttonID)
        {
            case ROW1_1, ROW1_2, ROW1_3, ROW1_4, ROW1_5, ROW1_6, ROW1_7, ROW1_8:
                deviceIndex = 2 * (buttonID.ordinal () - ButtonID.ROW1_1.ordinal ());
                break;
            case ROW2_1, ROW2_2, ROW2_3, ROW2_4, ROW2_5, ROW2_6, ROW2_7, ROW2_8:
                deviceIndex = 2 * (buttonID.ordinal () - ButtonID.ROW2_1.ordinal ()) + 1;
                break;
            case ROW3_1, ROW3_2, ROW3_3, ROW3_4, ROW3_5, ROW3_6, ROW3_7, ROW3_8:
                paramPageIndex = 2 * (buttonID.ordinal () - ButtonID.ROW3_1.ordinal ());
                break;
            case ROW4_1, ROW4_2, ROW4_3, ROW4_4, ROW4_5, ROW4_6, ROW4_7, ROW4_8:
                paramPageIndex = 2 * (buttonID.ordinal () - ButtonID.ROW4_1.ordinal ()) + 1;
                break;
            case ROW6_1, ROW6_2, ROW6_3, ROW6_4, ROW6_5, ROW6_6, ROW6_7, ROW6_8:
                final int index = buttonID.ordinal () - ButtonID.ROW6_1.ordinal ();
                return this.surface.getConfiguration ().getNewClipLength () == index ? YaeltexTurnColorManager.BLUE : YaeltexTurnColorManager.BLACK;

            default:
                break;
        }
        if (deviceIndex != -1)
        {
            final IDevice device = cursorDevice.getDeviceBank ().getItem (deviceIndex);
            if (!device.doesExist ())
                return 0;
            return deviceIndex == cursorDevice.getIndex () ? YaeltexTurnColorManager.CYAN : YaeltexTurnColorManager.WHITE;
        }
        if (paramPageIndex != -1)
        {
            final IParameterPageBank parameterPageBank = cursorDevice.getParameterPageBank ();
            final String paramPage = parameterPageBank.getItem (paramPageIndex);
            if (paramPage == null || paramPage.isBlank ())
                return 0;
            return paramPageIndex == parameterPageBank.getSelectedItemIndex () ? YaeltexTurnColorManager.BLUE : YaeltexTurnColorManager.WHITE;
        }
        return 0;
    }


    /**
     * Add a equalizer device to a track which does not already contain one.
     *
     * @param index The index of the selected or de-selected track
     * @param isSelected Is the track selected or de-selected?
     */
    private void trackSelectionChanged (final int index, final boolean isSelected)
    {
        if (!(isSelected && this.isActive))
            return;

        // Add an equalizer if not present
        if (!this.eqDevice.doesExist ())
        {
            final Optional<ITrack> selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
            if (selectedTrack.isPresent ())
                selectedTrack.get ().addEqualizerDevice ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        // Arrow Left

        this.bank.selectPreviousPage ();
        this.mvHelper.notifyTrackRange ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        // Arrow Right

        this.bank.selectNextPage ();
        this.mvHelper.notifyTrackRange ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        // Arrow Down

        if (this.surface.isShiftPressed ())
        {
            // Scroll all send banks
            final ITrackBank trackBank = this.model.getTrackBank ();
            for (int i = 0; i < 8; i++)
                trackBank.getItem (i).getSendBank ().selectPreviousPage ();
            this.mvHelper.notifySelectedSends (trackBank.getItem (0).getSendBank ());
            return;
        }

        this.model.getSceneBank ().selectPreviousPage ();
        this.mvHelper.notifyScenePage ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        // Arrow Up

        if (this.surface.isShiftPressed ())
        {
            // Scroll all send banks
            final ITrackBank trackBank = this.model.getTrackBank ();
            for (int i = 0; i < 8; i++)
                trackBank.getItem (i).getSendBank ().selectNextPage ();
            this.mvHelper.notifySelectedSends (trackBank.getItem (0).getSendBank ());
            return;
        }

        this.model.getSceneBank ().selectNextPage ();
        this.mvHelper.notifyScenePage ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItem ()
    {
        // Arrow Left

        return this.bank.canScrollPageBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItem ()
    {
        // Arrow Right

        return this.bank.canScrollPageForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItemPage ()
    {
        // Arrow Down
        if (this.surface.isShiftPressed ())
        {
            final ITrackBank trackBank = this.model.getTrackBank ();
            boolean canScroll = false;
            for (int i = 0; i < 8; i++)
                canScroll |= trackBank.getItem (i).getSendBank ().canScrollPageBackwards ();
            return canScroll;
        }

        return this.model.getSceneBank ().canScrollPageBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItemPage ()
    {
        // Arrow Up
        if (this.surface.isShiftPressed ())
        {
            final ITrackBank trackBank = this.model.getTrackBank ();
            boolean canScroll = false;
            for (int i = 0; i < 8; i++)
                canScroll |= trackBank.getItem (i).getSendBank ().canScrollPageForwards ();
            return canScroll;
        }

        return this.model.getSceneBank ().canScrollPageForwards ();
    }


    private ITrack getTrack (final ButtonID buttonID, final ButtonID firstButtinInRow)
    {
        return this.model.getCurrentTrackBank ().getItem (buttonID.ordinal () - firstButtinInRow.ordinal ());
    }
}
