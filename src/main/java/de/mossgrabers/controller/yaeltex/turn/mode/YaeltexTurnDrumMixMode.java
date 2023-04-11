package de.mossgrabers.controller.yaeltex.turn.mode;

import de.mossgrabers.controller.yaeltex.turn.YaeltexTurnConfiguration;
import de.mossgrabers.controller.yaeltex.turn.controller.YaeltexTurnColorManager;
import de.mossgrabers.controller.yaeltex.turn.controller.YaeltexTurnControlSurface;
import de.mossgrabers.controller.yaeltex.turn.view.DrumView;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.IDrumDevice;
import de.mossgrabers.framework.daw.data.IDrumPad;
import de.mossgrabers.framework.daw.data.IEqualizerDevice;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.MidiConstants;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.parameterprovider.device.PanLayerOrDrumPadParameterProvider;
import de.mossgrabers.framework.parameterprovider.device.SendLayerOrDrumPadParameterProvider;
import de.mossgrabers.framework.parameterprovider.device.VolumeLayerOrDrumPadParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;
import de.mossgrabers.framework.view.sequencer.AbstractDrumView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Drum machine mix mode for the Yaeltex Turn.
 *
 * @author Jürgen Moßgraber
 */
public class YaeltexTurnDrumMixMode extends AbstractParameterMode<YaeltexTurnControlSurface, YaeltexTurnConfiguration, ILayer>
{
    protected final IEqualizerDevice         eqDevice;
    protected final IDrumDevice              drumDevice;
    protected final List<IParameterProvider> providers       = new ArrayList<> ();
    protected final Scales                   scales;
    protected final YaeltexTurnConfiguration configuration;

    private boolean                          showEvenPeriods = true;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public YaeltexTurnDrumMixMode (final YaeltexTurnControlSurface surface, final IModel model, final List<ContinuousID> controls)
    {
        this ("Drum Machine Mixer", surface, model, controls);

        final IDrumDevice drum = model.getDrumDevice ();

        this.providers.add (0, new BankParameterProvider (drum.getParameterBank ()));
        this.providers.add (0, new SendLayerOrDrumPadParameterProvider (drum, 1));
        this.providers.add (0, new SendLayerOrDrumPadParameterProvider (drum, 0));
        this.providers.add (0, new PanLayerOrDrumPadParameterProvider (drum));
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
    protected YaeltexTurnDrumMixMode (final String name, final YaeltexTurnControlSurface surface, final IModel model, final List<ContinuousID> controls)
    {
        super (name, surface, model, true, model.getDrumDevice ().getDrumPadBank (), controls);

        this.configuration = this.surface.getConfiguration ();
        this.scales = model.getScales ();
        this.drumDevice = model.getDrumDevice ();
        this.eqDevice = (IEqualizerDevice) model.getSpecificDevice (DeviceID.EQ);

        this.providers.add (new FixedParameterProvider (this.eqDevice.getTypeParameters ()));
        this.providers.add (new FixedParameterProvider (this.eqDevice.getQParameters ()));
        this.providers.add (new FixedParameterProvider (this.eqDevice.getFrequencyParameters ()));
        this.providers.add (new FixedParameterProvider (this.eqDevice.getGainParameters ()));
        this.providers.add (new VolumeLayerOrDrumPadParameterProvider (this.drumDevice));

        this.drumDevice.addHasDrumPadsObserver (hasDrumPads -> this.parametersAdjusted ());
        this.getDrumPadBank ().addSelectionObserver (this::drumPadSelectionChanged);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.drumPadSelectionChanged (-1, true);

        super.onActivate ();
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        final int row = index / 8;
        final int column = index % 8;

        final IDrumPad drumPad = this.getDrumPad (column);
        switch (row)
        {
            case 0:
                return drumPad.getPan ();
            case 1:
                return drumPad.getSendBank ().getItem (0).getValue ();
            case 2:
                return drumPad.getSendBank ().getItem (1).getValue ();
            case 3:
                return this.getParameter (column).getValue ();
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
        final ColorEx color = row < 3 ? this.getDrumPad (column).getColor () : ColorEx.WHITE;
        return YaeltexTurnColorManager.getIndexFor (color);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event == ButtonEvent.LONG)
            return;

        if (this.surface.isShiftPressed ())
        {
            if (event == ButtonEvent.DOWN)
                this.onShiftButton (row, index);
            return;
        }

        final IDrumPad drumPad = this.getDrumPad (index);
        switch (row)
        {
            case 0:
                this.handleRow0 (index * 2, event == ButtonEvent.DOWN);
                break;

            case 1:
                this.handleRow0 (index * 2 + 1, event == ButtonEvent.DOWN);
                break;

            case 2:
                // Not used
                break;

            case 3:
                if (event != ButtonEvent.DOWN)
                    return;
                if (this.surface.isSelectPressed ())
                    this.drumDevice.getDrumPadBank ().clearSolo ();
                else
                    drumPad.toggleSolo ();
                break;

            case 4:
                if (event != ButtonEvent.DOWN)
                    return;
                if (this.surface.isSelectPressed ())
                    this.drumDevice.getDrumPadBank ().clearMute ();
                else
                    drumPad.toggleMute ();
                break;

            case 5:
                if (event == ButtonEvent.DOWN)
                    this.handleRow4 (index);
                break;

            default:
                // Not used
                break;
        }
    }


    /**
     * Overwrite to handle the 1st row buttons.
     *
     * @param index The index
     * @param isPressed True if pressed
     */
    protected void handleRow0 (final int index, final boolean isPressed)
    {
        if (index < 8)
        {
            final IMidiInput input = this.surface.getMidiInput ();
            input.sendRawMidiEvent (isPressed ? MidiConstants.CMD_NOTE_ON : MidiConstants.CMD_NOTE_OFF, this.scales.getDrumOffset () + index, isPressed ? 127 : 0);
            return;
        }

        if (!isPressed)
            return;

        switch (index)
        {
            case 10:
                // Toggle repeat
                this.configuration.toggleNoteRepeatActive ();
                break;

            case 11:
                // Toggle period resolution
                this.showEvenPeriods = !this.showEvenPeriods;
                break;

            // Select repeat periods
            case 12:
                this.setNoteRepeat (this.showEvenPeriods ? Resolution.RES_1_4 : Resolution.RES_1_4T);
                break;
            case 13:
                this.setNoteRepeat (this.showEvenPeriods ? Resolution.RES_1_8 : Resolution.RES_1_8T);
                break;
            case 14:
                this.setNoteRepeat (this.showEvenPeriods ? Resolution.RES_1_16 : Resolution.RES_1_16T);
                break;
            case 15:
                this.setNoteRepeat (this.showEvenPeriods ? Resolution.RES_1_32 : Resolution.RES_1_32T);
                break;

            default:
                // 8, 9 not used
                break;
        }
    }


    private void setNoteRepeat (final Resolution noteRepeat)
    {
        this.configuration.setNoteRepeatPeriod (noteRepeat);
        this.mvHelper.delayDisplay ( () -> "Repeat Period: " + noteRepeat.getName ());
    }


    /**
     * Overwrite to handle the 4th row buttons.
     *
     * @param index The index
     */
    protected void handleRow4 (final int index)
    {
        this.getDrumPad (index).select ();
        ((DrumView) this.surface.getViewManager ().get (Views.DRUM)).setSelectedPad (index);
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
            // Not used
            return;
        }

        if (row == 2 || row == 3)
        {
            final int paramPageIndex = 2 * index + row - 2;
            this.drumDevice.getParameterPageBank ().selectPage (paramPageIndex);
            this.mvHelper.notifyFirstDeviceAndParameterPage ();
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
                color = this.getFirstRowButtonColor ((buttonID.ordinal () - ButtonID.ROW1_1.ordinal ()) * 2);
                break;

            case ROW2_1, ROW2_2, ROW2_3, ROW2_4, ROW2_5, ROW2_6, ROW2_7, ROW2_8:
                color = this.getFirstRowButtonColor ((buttonID.ordinal () - ButtonID.ROW2_1.ordinal ()) * 2 + 1);
                break;

            case ROW3_1, ROW3_2, ROW3_3, ROW3_4, ROW3_5, ROW3_6, ROW3_7, ROW3_8:
                break;

            case ROW4_1, ROW4_2, ROW4_3, ROW4_4, ROW4_5, ROW4_6, ROW4_7, ROW4_8:
                color = this.getDrumPad (buttonID, ButtonID.ROW4_1).isSolo () ? YaeltexTurnColorManager.YELLOW : YaeltexTurnColorManager.BLACK;
                break;

            case ROW5_1, ROW5_2, ROW5_3, ROW5_4, ROW5_5, ROW5_6, ROW5_7, ROW5_8:
                color = this.getDrumPad (buttonID, ButtonID.ROW5_1).isMute () ? YaeltexTurnColorManager.ORANGE : YaeltexTurnColorManager.BLACK;
                break;

            case ROW6_1, ROW6_2, ROW6_3, ROW6_4, ROW6_5, ROW6_6, ROW6_7, ROW6_8:
                color = this.getFourthRowButtonColor (buttonID.ordinal () - ButtonID.ROW6_1.ordinal ());
                break;

            // Not used
            default:
                break;
        }

        return color;
    }


    /**
     * Get the 1st row color to set.
     *
     * @param index The index of the button
     * @return The color index
     */
    protected int getFirstRowButtonColor (final int index)
    {
        if (index < 8)
            return this.getFourthRowButtonColor (index);

        final Resolution noteRepeatPeriod = this.configuration.getNoteRepeatPeriod ();

        switch (index)
        {
            case 10:
                // Toggle repeat
                return this.configuration.isNoteRepeatActive () ? YaeltexTurnColorManager.GREEN : YaeltexTurnColorManager.DARK_GREEN;

            case 11:
                // Toggle period resolution
                return this.showEvenPeriods ? YaeltexTurnColorManager.WHITE : YaeltexTurnColorManager.PINK;

            case 12:
                if (this.showEvenPeriods)
                    return noteRepeatPeriod == Resolution.RES_1_4 ? YaeltexTurnColorManager.RED : YaeltexTurnColorManager.DARK_GRAY;
                return noteRepeatPeriod == Resolution.RES_1_4T ? YaeltexTurnColorManager.CYAN : YaeltexTurnColorManager.DARK_GRAY;

            case 13:
                if (this.showEvenPeriods)
                    return noteRepeatPeriod == Resolution.RES_1_8 ? YaeltexTurnColorManager.RED : YaeltexTurnColorManager.DARK_GRAY;
                return noteRepeatPeriod == Resolution.RES_1_8T ? YaeltexTurnColorManager.CYAN : YaeltexTurnColorManager.DARK_GRAY;

            case 14:
                if (this.showEvenPeriods)
                    return noteRepeatPeriod == Resolution.RES_1_16 ? YaeltexTurnColorManager.RED : YaeltexTurnColorManager.DARK_GRAY;
                return noteRepeatPeriod == Resolution.RES_1_16T ? YaeltexTurnColorManager.CYAN : YaeltexTurnColorManager.DARK_GRAY;

            case 15:
                if (this.showEvenPeriods)
                    return noteRepeatPeriod == Resolution.RES_1_32 ? YaeltexTurnColorManager.RED : YaeltexTurnColorManager.DARK_GRAY;
                return noteRepeatPeriod == Resolution.RES_1_32T ? YaeltexTurnColorManager.CYAN : YaeltexTurnColorManager.DARK_GRAY;

            default:
                // 8, 9 not used
                break;
        }

        return YaeltexTurnColorManager.BLACK;
    }


    /**
     * Get the 4th row color to set.
     *
     * @param index The index of the button
     * @return The color index
     */
    protected int getFourthRowButtonColor (final int index)
    {
        final String drumPadColor = this.getDrumPadColor (index, this.drumDevice.getDrumPadBank ());
        return this.colorManager.getColorIndex (drumPadColor);
    }


    private String getDrumPadColor (final int index, final IDrumPadBank drumPadBank)
    {
        // Selected?
        final int selectedPad = this.getDrumView ().getSelectedPad ();
        if (selectedPad == index)
            return AbstractDrumView.COLOR_PAD_SELECTED;

        // Exists and active?
        final IChannel drumPad = drumPadBank.getItem (index);
        if (!drumPad.doesExist () || !drumPad.isActivated ())
            return this.surface.getConfiguration ().isTurnOffEmptyDrumPads () ? AbstractDrumView.COLOR_PAD_OFF : AbstractDrumView.COLOR_PAD_NO_CONTENT;

        // Muted or soloed?
        if (drumPad.isMute () || drumPadBank.hasSoloedPads () && !drumPad.isSolo ())
            return AbstractDrumView.COLOR_PAD_MUTED;
        return DAWColor.getColorID (drumPad.getColor ());
    }


    /**
     * Get the button colors in combination with Shift.
     *
     * @param buttonID The button ID
     * @return The color index
     */
    protected int getButtonShiftColor (final ButtonID buttonID)
    {
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
            // Not used
            return YaeltexTurnColorManager.BLACK;
        }
        if (paramPageIndex != -1)
        {
            final IParameterPageBank parameterPageBank = this.drumDevice.getParameterPageBank ();
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
    private void drumPadSelectionChanged (final int index, final boolean isSelected)
    {
        if (!(isSelected && this.isActive))
            return;

        // Add an equalizer if not present
        if (!this.eqDevice.doesExist ())
        {
            final Optional<ILayer> selectedDrumPad = this.getDrumPadBank ().getSelectedItem ();
            if (selectedDrumPad.isPresent ())
                selectedDrumPad.get ().addEqualizerDevice ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        // Arrow Left

        this.getDrumView ().onOctaveDown (ButtonEvent.DOWN);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        // Arrow Right

        this.getDrumView ().onOctaveUp (ButtonEvent.DOWN);
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        // Arrow Down

        this.selectPreviousItem ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        // Arrow Up

        this.selectNextItem ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItem ()
    {
        // Arrow Left

        return this.getDrumView ().isOctaveDownButtonOn ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItem ()
    {
        // Arrow Right

        return this.getDrumView ().isOctaveUpButtonOn ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItemPage ()
    {
        // Arrow Down
        return this.hasPreviousItem ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItemPage ()
    {
        // Arrow Up
        return this.hasNextItem ();
    }


    private IDrumPad getDrumPad (final ButtonID buttonID, final ButtonID firstButtinInRow)
    {
        return this.getDrumPad (buttonID.ordinal () - firstButtinInRow.ordinal ());
    }


    private IDrumPad getDrumPad (final int index)
    {
        return this.getDrumPadBank ().getItem (index);
    }


    private IDrumPadBank getDrumPadBank ()
    {
        return this.drumDevice.getDrumPadBank ();
    }


    private IParameter getParameter (final int index)
    {
        return this.drumDevice.getParameterBank ().getItem (index);
    }


    protected DrumView getDrumView ()
    {
        return (DrumView) this.surface.getViewManager ().get (Views.DRUM);
    }
}
