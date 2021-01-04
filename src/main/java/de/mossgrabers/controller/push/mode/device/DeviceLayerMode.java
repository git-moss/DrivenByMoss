// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode.device;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.Push1Display;
import de.mossgrabers.controller.push.controller.PushColorManager;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.BaseMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IChannelBank;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.graphics.canvas.utils.SendData;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.observer.IParametersAdjustObserver;
import de.mossgrabers.framework.observer.IValueObserver;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.Pair;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Mode for editing a device layer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceLayerMode extends BaseMode implements IParameterProvider, IValueObserver<Boolean>
{
    protected final List<Pair<String, Boolean>>  menu      = new ArrayList<> ();
    private final Set<IParametersAdjustObserver> observers = new HashSet<> ();


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     */
    public DeviceLayerMode (final String name, final PushControlSurface surface, final IModel model)
    {
        super (name, surface, model, model.getCursorDevice ().getLayerOrDrumPadBank ());

        this.setParameters (this);

        for (int i = 0; i < 8; i++)
            this.menu.add (new Pair<> (" ", Boolean.FALSE));
    }


    /** {@inheritDoc} */
    @Override
    public int size ()
    {
        return 8;
    }


    /** {@inheritDoc} */
    @Override
    public IParameter get (final int index)
    {
        final ICursorDevice cd = this.model.getCursorDevice ();
        if (!cd.hasLayers ())
            return EmptyParameter.INSTANCE;
        final IChannel channel = cd.getLayerOrDrumPadBank ().getSelectedItem ();
        if (channel == null)
            return EmptyParameter.INSTANCE;

        switch (index)
        {
            case 0:
                return channel.getVolumeParameter ();

            case 1:
                return channel.getPanParameter ();

            default:
                if (this.isPush2 && index < 4)
                    return EmptyParameter.INSTANCE;

                final int sendIndex = index - (this.isPush2 ? this.surface.getConfiguration ().isSendsAreToggled () ? 0 : 4 : 2);
                return channel.getSendBank ().getItem (sendIndex);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void addParametersObserver (final IParametersAdjustObserver observer)
    {
        this.observers.add (observer);

        this.model.getCursorDevice ().addHasDrumPadsObserver (this);

        // Also update straight away to current state
        this.update (null);
    }


    /** {@inheritDoc} */
    @Override
    public void removeParametersObserver (final IParametersAdjustObserver observer)
    {
        this.observers.remove (observer);

        this.model.getCursorDevice ().removeHasDrumPadsObserver (this);
    }


    /** {@inheritDoc} */
    @Override
    public void notifyParametersObservers ()
    {
        this.observers.forEach (IParametersAdjustObserver::parametersAdjusted);
    }


    /**
     * Callback from drum bank monitor. Update the bank.
     */
    @Override
    public void update (final Boolean hasDrumPads)
    {
        this.switchBanks (this.model.getCursorDevice ().getLayerOrDrumPadBank ());
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        final ICursorDevice cd = this.model.getCursorDevice ();
        final IChannel channel = cd.getLayerOrDrumPadBank ().getSelectedItem ();
        if (channel == null)
            return;

        this.isKnobTouched[index] = isTouched;

        if (isTouched)
        {
            final ISendBank sendBank = channel.getSendBank ();
            if (this.surface.isDeletePressed ())
            {
                this.surface.setTriggerConsumed (ButtonID.DELETE);
                switch (index)
                {
                    case 0:
                        channel.resetVolume ();
                        break;
                    case 1:
                        channel.resetPan ();
                        break;
                    default:
                        if (this.isPush2 && index < 4)
                            break;
                        final int sendIndex = index - (this.isPush2 ? this.surface.getConfiguration ().isSendsAreToggled () ? 0 : 4 : 2);
                        sendBank.getItem (sendIndex).resetValue ();
                        break;
                }
                return;
            }
        }

        switch (index)
        {
            case 0:
                channel.touchVolume (isTouched);
                break;
            case 1:
                channel.touchPan (isTouched);
                break;
            default:
                if (this.isPush2 && index < 4)
                    break;
                final int sendIndex = index - (this.isPush2 ? this.surface.getConfiguration ().isSendsAreToggled () ? 0 : 4 : 2);
                channel.getSendBank ().getItem (sendIndex).touchValue (isTouched);
                break;
        }

        this.checkStopAutomationOnKnobRelease (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            return;

        if (event == ButtonEvent.UP)
        {
            final ICursorDevice cd = this.model.getCursorDevice ();
            if (!cd.doesExist ())
                return;

            final int offset = getDrumPadIndex (cd);
            final IChannelBank<?> bank = cd.getLayerOrDrumPadBank ();
            final ILayer layer = (ILayer) bank.getItem (offset + index);
            if (!layer.doesExist ())
                return;

            final int layerIndex = layer.getIndex ();
            if (!layer.isSelected ())
            {
                bank.getItem (layerIndex).select ();
                return;
            }

            // Only select if it exists otherwise the parent device is selected which is confusing
            // to the user
            if (!layer.hasDevices ())
                return;
            layer.enter ();
            final ModeManager modeManager = this.surface.getModeManager ();
            this.setMode (Modes.DEVICE_PARAMS);
            ((DeviceParamsMode) modeManager.get (Modes.DEVICE_PARAMS)).setShowDevices (true);
            return;
        }

        // LONG press
        this.surface.setTriggerConsumed (ButtonID.get (ButtonID.ROW1_1, index));
        this.moveUp ();
    }


    /**
     * Move up the hierarchy.
     */
    protected void moveUp ()
    {
        // There is no device on the track move upwards to the track view
        final ICursorDevice cd = this.model.getCursorDevice ();
        if (!cd.doesExist ())
        {
            this.surface.getButton (ButtonID.TRACK).trigger (ButtonEvent.DOWN);
            return;
        }

        this.setMode (Modes.DEVICE_PARAMS);
        cd.selectChannel ();
        final ModeManager modeManager = this.surface.getModeManager ();
        ((DeviceParamsMode) modeManager.get (Modes.DEVICE_PARAMS)).setShowDevices (true);
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final PushConfiguration config = this.surface.getConfiguration ();
        if (!this.isPush2 || config.isMuteLongPressed () || config.isSoloLongPressed () || config.isMuteSoloLocked ())
        {
            final ICursorDevice cd = this.model.getCursorDevice ();
            final int offset = getDrumPadIndex (cd);
            if (config.isMuteState ())
                cd.getLayerOrDrumPadBank ().getItem (offset + index).toggleMute ();
            else
                cd.getLayerOrDrumPadBank ().getItem (offset + index).toggleSolo ();
            return;
        }

        final ModeManager modeManager = this.surface.getModeManager ();
        ITrackBank fxTrackBank;
        switch (index)
        {
            case 0:
                if (modeManager.isActive (Modes.DEVICE_LAYER_VOLUME))
                    this.setMode (Modes.DEVICE_LAYER);
                else
                    this.setMode (Modes.DEVICE_LAYER_VOLUME);
                break;

            case 1:
                if (modeManager.isActive (Modes.DEVICE_LAYER_PAN))
                    this.setMode (Modes.DEVICE_LAYER);
                else
                    this.setMode (Modes.DEVICE_LAYER_PAN);
                break;

            case 2:
                // Not used
                break;

            case 3:
                if (this.model.isEffectTrackBankActive ())
                    return;
                // Check if there are more than 4 FX channels
                if (!config.isSendsAreToggled ())
                {
                    fxTrackBank = this.model.getEffectTrackBank ();
                    if (fxTrackBank == null || !fxTrackBank.getItem (4).doesExist ())
                        return;
                }
                config.setSendsAreToggled (!config.isSendsAreToggled ());
                this.bindControls ();

                if (!modeManager.isActive (Modes.DEVICE_LAYER))
                    this.setMode (Modes.get (Modes.DEVICE_LAYER_SEND1, config.isSendsAreToggled () ? 4 : 0));
                break;

            case 7:
                if (this.surface.isShiftPressed ())
                    this.handleSendEffect (config.isSendsAreToggled () ? 7 : 3);
                else
                    this.moveUp ();
                break;

            default:
                this.handleSendEffect (index - (config.isSendsAreToggled () ? 0 : 4));
                break;
        }
    }


    private void setMode (final Modes layerMode)
    {
        this.surface.getModeManager ().setActive (layerMode);
        if (Modes.isLayerMode (layerMode))
            this.surface.getConfiguration ().setLayerMixMode (layerMode);
    }


    /**
     * Handle the selection of a send effect.
     *
     * @param sendIndex The index of the send
     */
    protected void handleSendEffect (final int sendIndex)
    {
        if (this.model.isEffectTrackBankActive ())
            return;
        final ITrackBank fxTrackBank = this.model.getEffectTrackBank ();
        if (fxTrackBank == null || !fxTrackBank.getItem (sendIndex).doesExist ())
            return;
        final Modes si = Modes.get (Modes.DEVICE_LAYER_SEND1, sendIndex);
        final ModeManager modeManager = this.surface.getModeManager ();
        this.setMode (modeManager.isActive (si) ? Modes.DEVICE_LAYER : si);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final ICursorDevice cd = this.model.getCursorDevice ();
        if (!cd.doesExist ())
        {
            display.setBlock (1, 0, "           Select").setBlock (1, 1, "a device or press").setBlock (1, 2, "'Add Effect'...  ").allDone ();
            return;
        }

        if (!cd.hasLayers ())
            display.setBlock (1, 1, "    This device  ").setBlock (1, 2, "does not have layers.");
        else if (cd.getLayerBank ().hasZeroLayers ())
            display.setBlock (1, 1, "    Please create").setBlock (1, 2, cd.hasDrumPads () ? "a Drum Pad..." : "a Device Layer...");
        else
        {
            final IChannel l = cd.getLayerOrDrumPadBank ().getSelectedItem ();
            if (l != null)
            {
                display.setCell (0, 0, "Volume").setCell (1, 0, l.getVolumeStr (8)).setCell (2, 0, this.surface.getConfiguration ().isEnableVUMeters () ? l.getVu () : l.getVolume (), Format.FORMAT_VALUE);
                display.setCell (0, 1, "Pan").setCell (1, 1, l.getPanStr (8)).setCell (2, 1, l.getPan (), Format.FORMAT_PAN);

                final ITrackBank fxTrackBank = this.model.getEffectTrackBank ();
                if (fxTrackBank == null)
                {
                    for (int i = 0; i < 6; i++)
                    {
                        final int pos = 2 + i;
                        final ISend send = l.getSendBank ().getItem (i);
                        display.setCell (0, pos, send.getName ()).setCell (1, pos, send.getDisplayedValue (8)).setCell (2, pos, send.getValue (), Format.FORMAT_VALUE);
                    }
                }
                else
                {
                    final boolean isFX = this.model.isEffectTrackBankActive ();
                    for (int i = 0; i < 6; i++)
                    {
                        final ITrack fxTrack = fxTrackBank.getItem (i);
                        final boolean isEmpty = isFX || !fxTrack.doesExist ();
                        final int pos = 2 + i;
                        if (!isEmpty)
                        {
                            final ISend send = l.getSendBank ().getItem (i);
                            display.setCell (0, pos, fxTrack.getName ()).setCell (1, pos, send.getDisplayedValue (8));
                            display.setCell (2, pos, send.getValue (), Format.FORMAT_VALUE);
                        }
                    }
                }
            }
        }

        this.drawRow4 (display, cd);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final ICursorDevice cd = this.model.getCursorDevice ();
        if (!cd.doesExist ())
        {
            for (int i = 0; i < 8; i++)
                display.addOptionElement (i == 2 ? "Please select a device or press 'Add Device'..." : "", i == 7 ? "Up" : "", true, "", "", false, true);
            return;
        }

        if (checkLayerExistance (display, cd))
            this.updateDisplayElements (display, cd, cd.getLayerOrDrumPadBank ().getSelectedItem ());
    }


    /**
     * Check if the cursor device has layers and at least one. Otherwise a message is displayed
     *
     * @param display The display where to show the message
     * @param cd The cursor device
     * @return True if layers exist
     */
    protected static boolean checkLayerExistance (final IGraphicDisplay display, final ICursorDevice cd)
    {
        if (!cd.hasLayers ())
        {
            for (int i = 0; i < 8; i++)
                display.addOptionElement (i == 3 ? "This device does not have layers." : "", i == 7 ? "Up" : "", true, "", "", false, true);
            return false;
        }

        if (cd.getLayerBank ().hasZeroLayers ())
        {
            for (int i = 0; i < 8; i++)
                display.addOptionElement (i == 3 ? "Please create a " + (cd.hasDrumPads () ? "Drum Pad..." : "Device Layer...") : "", i == 7 ? "Up" : "", true, "", "", false, true);
            return false;
        }

        return true;
    }


    /**
     * Update all 8 elements.
     *
     * @param display The display
     * @param cd The cursor device
     * @param l The channel data
     */
    protected void updateDisplayElements (final IGraphicDisplay display, final ICursorDevice cd, final IChannel l)
    {
        // Drum Pad Bank has size of 16, layers only 8
        final int offset = getDrumPadIndex (cd);

        // Get the index at which to draw the Sends element
        int sendsIndex = l == null ? -1 : l.getIndex () - offset + 1;
        if (sendsIndex == 8)
            sendsIndex = 6;

        this.updateMenuItems (-1);

        final PushConfiguration config = this.surface.getConfiguration ();

        final IChannelBank<?> bank = cd.getLayerOrDrumPadBank ();
        for (int i = 0; i < 8; i++)
        {
            final IChannel layer = bank.getItem (offset + i);

            final Pair<String, Boolean> pair = this.menu.get (i);
            final String topMenu = pair.getKey ();
            final boolean isTopMenuOn = pair.getValue ().booleanValue ();

            // Channel info
            final String bottomMenu = layer.doesExist () ? layer.getName (12) : "";
            final ColorEx bottomMenuColor = layer.getColor ();
            final boolean isBottomMenuOn = layer.isSelected ();

            if (layer.isSelected ())
            {
                final IValueChanger valueChanger = this.model.getValueChanger ();
                final boolean enableVUMeters = config.isEnableVUMeters ();
                final int vuR = valueChanger.toDisplayValue (enableVUMeters ? layer.getVuRight () : 0);
                final int vuL = valueChanger.toDisplayValue (enableVUMeters ? layer.getVuLeft () : 0);
                display.addChannelElement (topMenu, isTopMenuOn, bottomMenu, ChannelType.LAYER, bottomMenuColor, isBottomMenuOn, valueChanger.toDisplayValue (layer.getVolume ()), valueChanger.toDisplayValue (layer.getModulatedVolume ()), this.isKnobTouched[0] ? layer.getVolumeStr (8) : "", valueChanger.toDisplayValue (layer.getPan ()), valueChanger.toDisplayValue (layer.getModulatedPan ()), this.isKnobTouched[1] ? layer.getPanStr (8) : "", vuL, vuR, layer.isMute (), layer.isSolo (), false, layer.isActivated (), 0);
            }
            else if (sendsIndex == i && l != null)
            {
                final ITrackBank fxTrackBank = this.model.getEffectTrackBank ();
                final SendData [] sendData = new SendData [4];
                for (int j = 0; j < 4; j++)
                {
                    final int sendOffset = config.isSendsAreToggled () ? 4 : 0;
                    final int sendPos = sendOffset + j;
                    final ISend send = l.getSendBank ().getItem (sendPos);
                    final boolean doesExist = send.doesExist ();
                    sendData[j] = new SendData (fxTrackBank == null ? send.getName () : fxTrackBank.getItem (sendPos).getName (), doesExist && this.isKnobTouched[4 + j] ? send.getDisplayedValue () : "", doesExist ? send.getValue () : 0, doesExist ? send.getModulatedValue () : 0, true);
                }
                display.addSendsElement (topMenu, isTopMenuOn, layer.doesExist () ? layer.getName () : "", ChannelType.LAYER, bank.getItem (offset + i).getColor (), layer.isSelected (), sendData, true, l.isActivated (), layer.isActivated ());
            }
            else
                display.addChannelSelectorElement (topMenu, isTopMenuOn, bottomMenu, ChannelType.LAYER, bottomMenuColor, isBottomMenuOn, layer.isActivated ());
        }
    }


    // Called from sub-classes
    protected void updateChannelDisplay (final IGraphicDisplay display, final ICursorDevice cd, final int selectedMenu, final boolean isVolume, final boolean isPan)
    {
        this.updateMenuItems (selectedMenu);

        final PushConfiguration config = this.surface.getConfiguration ();

        // Drum Pad Bank has size of 16, layers only 8
        final int offset = getDrumPadIndex (cd);

        final IValueChanger valueChanger = this.model.getValueChanger ();
        final IChannelBank<?> bank = cd.getLayerOrDrumPadBank ();
        for (int i = 0; i < 8; i++)
        {
            final IChannel layer = bank.getItem (offset + i);
            final Pair<String, Boolean> pair = this.menu.get (i);
            final String topMenu = pair.getKey ();
            final boolean isTopMenuOn = pair.getValue ().booleanValue ();
            final boolean enableVUMeters = config.isEnableVUMeters ();
            final int vuR = valueChanger.toDisplayValue (enableVUMeters ? layer.getVuRight () : 0);
            final int vuL = valueChanger.toDisplayValue (enableVUMeters ? layer.getVuLeft () : 0);
            display.addChannelElement (selectedMenu, topMenu, isTopMenuOn, layer.doesExist () ? layer.getName () : "", ChannelType.LAYER, layer.getColor (), layer.isSelected (), valueChanger.toDisplayValue (layer.getVolume ()), valueChanger.toDisplayValue (layer.getModulatedVolume ()), isVolume && this.isKnobTouched[i] ? layer.getVolumeStr (8) : "", valueChanger.toDisplayValue (layer.getPan ()), valueChanger.toDisplayValue (layer.getModulatedPan ()), isPan && this.isKnobTouched[i] ? layer.getPanStr () : "", vuL, vuR, layer.isMute (), layer.isSolo (), false, layer.isActivated (), 0);
        }
    }


    protected void updateMenuItems (final int selectedMenu)
    {
        final PushConfiguration config = this.surface.getConfiguration ();
        if (config.isMuteLongPressed () || config.isMuteSoloLocked () && config.isMuteState ())
            this.updateMuteMenu ();
        else if (config.isSoloLongPressed () || config.isMuteSoloLocked () && config.isSoloState ())
            this.updateSoloMenu ();
        else
            this.updateLayerMenu (selectedMenu);
    }


    protected void updateSoloMenu ()
    {
        final IChannelBank<?> bank = this.model.getCursorDevice ().getLayerOrDrumPadBank ();
        for (int i = 0; i < 8; i++)
        {
            final IChannel layer = bank.getItem (i);
            this.menu.get (i).set (layer.doesExist () ? "Solo" : "", Boolean.valueOf (layer.isSolo ()));
        }
    }


    protected void updateMuteMenu ()
    {
        final IChannelBank<?> bank = this.model.getCursorDevice ().getLayerOrDrumPadBank ();
        for (int i = 0; i < 8; i++)
        {
            final IChannel layer = bank.getItem (i);
            this.menu.get (i).set (layer.doesExist () ? "Mute" : "", Boolean.valueOf (layer.isMute ()));
        }
    }


    protected void updateLayerMenu (final int selectedMenu)
    {
        final PushConfiguration config = this.surface.getConfiguration ();

        this.menu.get (0).set ("Volume", Boolean.valueOf (selectedMenu - 1 == 0));
        this.menu.get (1).set ("Pan", Boolean.valueOf (selectedMenu - 1 == 1));
        this.menu.get (2).set (" ", Boolean.FALSE);

        if (this.model.isEffectTrackBankActive ())
        {
            // No sends for FX tracks
            for (int i = 3; i < 7; i++)
                this.menu.get (i).set (" ", Boolean.FALSE);
            return;
        }

        final boolean sendsAreToggled = config.isSendsAreToggled ();

        this.menu.get (3).set (sendsAreToggled ? "Sends 5-8" : "Sends 1-4", Boolean.valueOf (sendsAreToggled));

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final int sendOffset = sendsAreToggled ? 4 : 0;
        final boolean isShiftPressed = this.surface.isShiftPressed ();
        for (int i = 0; i < (isShiftPressed ? 4 : 3); i++)
        {
            final String sendName = tb.getEditSendName (sendOffset + i);
            this.menu.get (4 + i).set (sendName.isEmpty () ? " " : sendName, Boolean.valueOf (4 + i == selectedMenu - 1));
        }

        if (!isShiftPressed)
            this.menu.get (7).set ("Up", Boolean.TRUE);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final ICursorDevice cd = this.model.getCursorDevice ();
        if (cd == null || !cd.hasLayers ())
            return super.getButtonColor (buttonID);

        final IChannelBank<?> bank = cd.getLayerOrDrumPadBank ();
        // Drum Pad Bank has size of 16, layers only 8
        final int offset = getDrumPadIndex (cd);

        int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
        {
            final IChannel dl = bank.getItem (offset + buttonID.ordinal () - ButtonID.ROW1_1.ordinal ());
            if (dl.doesExist () && dl.isActivated ())
            {
                if (dl.isSelected ())
                    return this.isPush2 ? PushColorManager.PUSH2_COLOR_ORANGE_HI : PushColorManager.PUSH1_COLOR_ORANGE_HI;
                return this.isPush2 ? PushColorManager.PUSH2_COLOR_YELLOW_LO : PushColorManager.PUSH1_COLOR_YELLOW_LO;
            }
            return super.getButtonColor (buttonID);
        }

        index = this.isButtonRow (1, buttonID);
        if (index >= 0)
        {
            final PushConfiguration config = this.surface.getConfiguration ();
            final boolean muteState = config.isMuteState ();
            final IChannel layer = bank.getItem (offset + index);
            if (this.isPush2)
            {
                if (config.isMuteLongPressed () || config.isSoloLongPressed () || config.isMuteSoloLocked ())
                {
                    if (layer.doesExist ())
                    {
                        if (muteState)
                        {
                            if (layer.isMute ())
                                return PushColorManager.PUSH2_COLOR2_AMBER_LO;
                        }
                        else if (layer.isSolo ())
                            return PushColorManager.PUSH2_COLOR2_YELLOW_HI;
                    }
                    return PushColorManager.PUSH2_COLOR_BLACK;
                }

                final ModeManager modeManager = this.surface.getModeManager ();
                switch (index)
                {
                    case 0:
                        return modeManager.isActive (Modes.DEVICE_LAYER_VOLUME) ? PushColorManager.PUSH2_COLOR2_WHITE : PushColorManager.PUSH2_COLOR_BLACK;
                    case 1:
                        return modeManager.isActive (Modes.DEVICE_LAYER_PAN) ? PushColorManager.PUSH2_COLOR2_WHITE : PushColorManager.PUSH2_COLOR_BLACK;
                    case 4:
                        return modeManager.isActive (config.isSendsAreToggled () ? Modes.DEVICE_LAYER_SEND5 : Modes.DEVICE_LAYER_SEND1) ? PushColorManager.PUSH2_COLOR2_WHITE : PushColorManager.PUSH2_COLOR_BLACK;
                    case 5:
                        return modeManager.isActive (config.isSendsAreToggled () ? Modes.DEVICE_LAYER_SEND6 : Modes.DEVICE_LAYER_SEND2) ? PushColorManager.PUSH2_COLOR2_WHITE : PushColorManager.PUSH2_COLOR_BLACK;
                    case 6:
                        return modeManager.isActive (config.isSendsAreToggled () ? Modes.DEVICE_LAYER_SEND7 : Modes.DEVICE_LAYER_SEND3) ? PushColorManager.PUSH2_COLOR2_WHITE : PushColorManager.PUSH2_COLOR_BLACK;
                    case 7:
                        return modeManager.isActive (config.isSendsAreToggled () ? Modes.DEVICE_LAYER_SEND8 : Modes.DEVICE_LAYER_SEND4) ? PushColorManager.PUSH2_COLOR2_WHITE : PushColorManager.PUSH2_COLOR_BLACK;
                    default:
                        return PushColorManager.PUSH2_COLOR_BLACK;
                }
            }

            if (!cd.hasLayers ())
                return index == 7 ? PushColorManager.PUSH1_COLOR2_WHITE : super.getButtonColor (buttonID);

            if (layer.doesExist ())
            {
                if (muteState)
                    return layer.isMute () ? PushColorManager.PUSH1_COLOR_BLACK : PushColorManager.PUSH1_COLOR2_YELLOW_HI;
                return layer.isSolo () ? PushColorManager.PUSH1_COLOR2_BLUE_HI : PushColorManager.PUSH1_COLOR2_GREY_LO;
            }
        }

        return super.getButtonColor (buttonID);
    }


    /**
     * Draw the fourth row.
     *
     * @param display The display
     * @param cd The cursor device
     */
    protected void drawRow4 (final ITextDisplay display, final ICursorDevice cd)
    {
        // Drum Pad Bank has size of 16, layers only 8
        final int offset = getDrumPadIndex (cd);
        final IChannelBank<?> bank = cd.getLayerOrDrumPadBank ();
        for (int i = 0; i < 8; i++)
        {
            final IChannel layer = bank.getItem (offset + i);
            final String n = StringUtils.shortenAndFixASCII (layer.getName (), layer.isSelected () ? 7 : 8);
            display.setCell (3, i, layer.isSelected () ? Push1Display.SELECT_ARROW + n : n);
        }
    }


    protected static int getDrumPadIndex (final ICursorDevice cd)
    {
        if (cd.hasDrumPads ())
        {
            final IChannel selectedDrumPad = cd.getLayerOrDrumPadBank ().getSelectedItem ();
            if (selectedDrumPad != null && selectedDrumPad.getIndex () > 7)
                return 8;
        }
        return 0;
    }
}