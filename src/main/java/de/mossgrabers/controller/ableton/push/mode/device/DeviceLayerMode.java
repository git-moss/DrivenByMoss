// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode.device;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.Push1Display;
import de.mossgrabers.controller.ableton.push.controller.PushColorManager;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.controller.ableton.push.mode.BaseMode;
import de.mossgrabers.controller.ableton.push.parameterprovider.PushSelectedLayerOrDrumPadParameterProvider;
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
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.bank.ILayerBank;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.graphics.canvas.utils.SendData;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.Pair;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Mode for editing a device layer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceLayerMode extends BaseMode<ILayer>
{
    protected final List<Pair<String, Boolean>> menu = new ArrayList<> ();
    protected final ICursorDevice               cursorDevice;
    protected final PushConfiguration           configuration;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public DeviceLayerMode (final PushControlSurface surface, final IModel model)
    {
        this (Modes.NAME_LAYER, surface, model);

        this.setParameterProvider (new PushSelectedLayerOrDrumPadParameterProvider (this.cursorDevice, this.configuration));
    }


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     */
    DeviceLayerMode (final String name, final PushControlSurface surface, final IModel model)
    {
        super (name, surface, model, model.getCursorDevice ().getLayerBank ());

        this.configuration = this.surface.getConfiguration ();
        this.cursorDevice = this.model.getCursorDevice ();
        this.cursorDevice.addHasDrumPadsObserver (hasDrumPads -> this.switchBanks (this.cursorDevice.hasDrumPads () ? this.cursorDevice.getDrumPadBank () : this.cursorDevice.getLayerBank ()));

        for (int i = 0; i < 8; i++)
            this.menu.add (new Pair<> (" ", Boolean.FALSE));
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        final Optional<ILayer> channelOpt = this.bank.getSelectedItem ();
        if (channelOpt.isEmpty ())
            return;

        final ILayer channel = channelOpt.get ();

        this.setTouchedKnob (index, isTouched);

        final ISendBank sendBank = channel.getSendBank ();

        if (isTouched && this.surface.isDeletePressed ())
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
                    if (!this.isPush2 || index >= 4)
                        sendBank.getItem (this.getSendIndex (index)).resetValue ();
                    break;
            }
            return;
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
                if (!this.isPush2 || index >= 4)
                    sendBank.getItem (this.getSendIndex (index)).touchValue (isTouched);
                break;
        }

        this.checkStopAutomationOnKnobRelease (isTouched);
    }


    private int getSendIndex (final int index)
    {
        return this.isPush2 ? index - 4 : index - 2;
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            return;

        if (event == ButtonEvent.UP)
        {
            if (!this.cursorDevice.doesExist ())
                return;

            final int offset = this.getDrumPadIndex ();
            final ILayer layer = this.bank.getItem (offset + index);
            if (!layer.doesExist ())
                return;

            final int layerIndex = layer.getIndex ();
            if (!layer.isSelected ())
            {
                this.bank.getItem (layerIndex).select ();
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
        if (!this.cursorDevice.doesExist ())
        {
            this.surface.getButton (ButtonID.TRACK).trigger (ButtonEvent.DOWN);
            return;
        }

        this.setMode (Modes.DEVICE_PARAMS);
        this.cursorDevice.selectChannel ();
        final ModeManager modeManager = this.surface.getModeManager ();
        ((DeviceParamsMode) modeManager.get (Modes.DEVICE_PARAMS)).setShowDevices (true);
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        if (!this.isPush2 || this.configuration.isMuteLongPressed () || this.configuration.isSoloLongPressed () || this.configuration.isMuteSoloLocked ())
        {
            final int offset = this.getDrumPadIndex ();
            final ILayer layer = this.bank.getItem (offset + index);
            if (this.configuration.isMuteState ())
                layer.toggleMute ();
            else
                layer.toggleSolo ();
            return;
        }

        final ModeManager modeManager = this.surface.getModeManager ();
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
                final boolean isShift = this.surface.isShiftPressed ();
                final int offset = this.getDrumPadIndex ();
                for (int i = 0; i < this.bank.getPageSize (); i++)
                {
                    final ILayer layer = this.bank.getItem (offset + i);
                    final ISendBank sendBank = layer.getSendBank ();
                    if (isShift)
                    {
                        if (sendBank.canScrollPageBackwards ())
                            sendBank.selectPreviousPage ();
                        else
                            sendBank.scrollTo (sendBank.getItemCount () / 4 * 4);
                    }
                    else
                    {
                        if (sendBank.canScrollPageForwards ())
                            sendBank.selectNextPage ();
                        else
                            sendBank.scrollTo (0);
                    }
                }

                break;

            case 7:
                if (this.surface.isShiftPressed ())
                    this.handleSendEffect (3);
                else
                    this.moveUp ();
                break;

            default:
                this.handleSendEffect (index - 4);
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
        final ISendBank sendBank = this.bank.getItem (0).getSendBank ();
        if (!sendBank.getItem (sendIndex).doesExist ())
            return;
        final Modes si = Modes.get (Modes.DEVICE_LAYER_SEND1, sendIndex);
        final ModeManager modeManager = this.surface.getModeManager ();
        this.setMode (modeManager.isActive (si) ? Modes.DEVICE_LAYER : si);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        if (!this.cursorDevice.doesExist ())
        {
            display.setBlock (1, 0, "           Select").setBlock (1, 1, "a device or press").setBlock (1, 2, "'Add Effect'...  ").allDone ();
            return;
        }

        if (!this.cursorDevice.hasLayers ())
            display.setBlock (1, 1, "    This device  ").setBlock (1, 2, "does not have layers.");
        else if (!this.bank.hasExistingItems ())
            display.setBlock (1, 1, "    Please create").setBlock (1, 2, this.cursorDevice.hasDrumPads () ? "a Drum Pad..." : "a Device Layer...");
        else
        {
            final Optional<ILayer> layer = this.bank.getSelectedItem ();
            if (layer.isPresent ())
            {
                final ILayer l = layer.get ();

                display.setCell (0, 0, "Volume").setCell (1, 0, l.getVolumeStr (8)).setCell (2, 0, this.configuration.isEnableVUMeters () ? l.getVu () : l.getVolume (), Format.FORMAT_VALUE);
                display.setCell (0, 1, "Pan").setCell (1, 1, l.getPanStr (8)).setCell (2, 1, l.getPan (), Format.FORMAT_PAN);

                final ISendBank sendBank = l.getSendBank ();
                for (int i = 0; i < 6; i++)
                {
                    final int pos = 2 + i;
                    final ISend send = sendBank.getItem (i);
                    display.setCell (0, pos, send.getName ()).setCell (1, pos, send.getDisplayedValue (8)).setCell (2, pos, send.getValue (), Format.FORMAT_VALUE);
                }
            }
        }

        this.drawRow4 (display);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        if (!this.cursorDevice.doesExist ())
        {
            for (int i = 0; i < 8; i++)
                display.addOptionElement (i == 2 ? "Please select a device or press 'Add Device'..." : "", i == 7 ? "Up" : "", true, "", "", false, true);
            return;
        }

        if (this.checkLayerExistance (display))
            this.updateDisplayElements (display, this.bank.getSelectedItem ());
    }


    /**
     * Check if the cursor device has layers and at least one. Otherwise a message is displayed
     *
     * @param display The display where to show the message
     * @return True if layers exist
     */
    protected boolean checkLayerExistance (final IGraphicDisplay display)
    {
        if (!this.cursorDevice.hasLayers ())
        {
            for (int i = 0; i < 8; i++)
                display.addOptionElement (i == 3 ? "This device does not have layers." : "", i == 7 ? "Up" : "", true, "", "", false, true);
            return false;
        }

        if (this.bank.hasExistingItems ())
            return true;

        for (int i = 0; i < 8; i++)
        {
            final String label;
            if (i == 3)
                label = "Please create a " + (this.cursorDevice.hasDrumPads () ? "Drum Pad..." : "Device Layer...");
            else
                label = "";
            display.addOptionElement (label, i == 7 ? "Up" : "", true, "", "", false, true);
        }
        return false;
    }


    /**
     * Update all 8 elements.
     *
     * @param display The display
     * @param l The channel data
     */
    protected void updateDisplayElements (final IGraphicDisplay display, final Optional<ILayer> l)
    {
        // Drum Pad Bank has size of 16, layers only 8
        final int offset = this.getDrumPadIndex ();

        // Get the index at which to draw the Sends element
        int sendsIndex = l.isEmpty () ? -1 : l.get ().getIndex () - offset + 1;
        if (sendsIndex == 8)
            sendsIndex = 6;

        this.updateMenuItems (-1);

        final PushConfiguration config = this.surface.getConfiguration ();

        for (int i = 0; i < 8; i++)
        {
            final IChannel layer = this.bank.getItem (offset + i);

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
                display.addChannelElement (topMenu, isTopMenuOn, bottomMenu, ChannelType.LAYER, bottomMenuColor, isBottomMenuOn, valueChanger.toDisplayValue (layer.getVolume ()), valueChanger.toDisplayValue (layer.getModulatedVolume ()), this.isKnobTouched (0) ? layer.getVolumeStr (8) : "", valueChanger.toDisplayValue (layer.getPan ()), valueChanger.toDisplayValue (layer.getModulatedPan ()), this.isKnobTouched (1) ? layer.getPanStr (8) : "", vuL, vuR, layer.isMute (), layer.isSolo (), false, layer.isActivated (), 0, false);
            }
            else if (sendsIndex == i && l.isPresent ())
            {
                final ISendBank sendBank = l.get ().getSendBank ();
                final SendData [] sendData = new SendData [4];
                for (int j = 0; j < 4; j++)
                {
                    final ISend send = sendBank.getItem (j);
                    final boolean doesExist = send.doesExist ();
                    sendData[j] = new SendData (send.getName (), doesExist && this.isKnobTouched (4 + j) ? send.getDisplayedValue () : "", doesExist ? send.getValue () : 0, doesExist ? send.getModulatedValue () : 0, true);
                }
                display.addSendsElement (topMenu, isTopMenuOn, layer.doesExist () ? layer.getName () : "", ChannelType.LAYER, this.bank.getItem (offset + i).getColor (), layer.isSelected (), sendData, true, l.get ().isActivated (), layer.isActivated ());
            }
            else
                display.addChannelSelectorElement (topMenu, isTopMenuOn, bottomMenu, ChannelType.LAYER, bottomMenuColor, isBottomMenuOn, layer.isActivated ());
        }
    }


    // Called from sub-classes
    protected void updateChannelDisplay (final IGraphicDisplay display, final int selectedMenu, final boolean isVolume, final boolean isPan)
    {
        this.updateMenuItems (selectedMenu);

        // Drum Pad Bank has size of 16, layers only 8
        final int offset = this.getDrumPadIndex ();

        final IValueChanger valueChanger = this.model.getValueChanger ();
        for (int i = 0; i < 8; i++)
        {
            final IChannel layer = this.bank.getItem (offset + i);
            final Pair<String, Boolean> pair = this.menu.get (i);
            final String topMenu = pair.getKey ();
            final boolean isTopMenuOn = pair.getValue ().booleanValue ();
            final boolean enableVUMeters = this.configuration.isEnableVUMeters ();
            final int vuR = valueChanger.toDisplayValue (enableVUMeters ? layer.getVuRight () : 0);
            final int vuL = valueChanger.toDisplayValue (enableVUMeters ? layer.getVuLeft () : 0);
            display.addChannelElement (selectedMenu, topMenu, isTopMenuOn, layer.doesExist () ? layer.getName () : "", ChannelType.LAYER, layer.getColor (), layer.isSelected (), valueChanger.toDisplayValue (layer.getVolume ()), valueChanger.toDisplayValue (layer.getModulatedVolume ()), isVolume && this.isKnobTouched (i) ? layer.getVolumeStr (8) : "", valueChanger.toDisplayValue (layer.getPan ()), valueChanger.toDisplayValue (layer.getModulatedPan ()), isPan && this.isKnobTouched (i) ? layer.getPanStr () : "", vuL, vuR, layer.isMute (), layer.isSolo (), false, layer.isActivated (), 0, false);
        }
    }


    protected void updateMenuItems (final int selectedMenu)
    {
        if (this.configuration.isMuteLongPressed () || this.configuration.isMuteSoloLocked () && this.configuration.isMuteState ())
            this.updateMuteMenu ();
        else if (this.configuration.isSoloLongPressed () || this.configuration.isMuteSoloLocked () && this.configuration.isSoloState ())
            this.updateSoloMenu ();
        else
            this.updateLayerMenu (selectedMenu);
    }


    protected void updateSoloMenu ()
    {
        for (int i = 0; i < 8; i++)
        {
            final IChannel layer = this.bank.getItem (i);
            this.menu.get (i).set (layer.doesExist () ? "Solo" : "", Boolean.valueOf (layer.isSolo ()));
        }
    }


    protected void updateMuteMenu ()
    {
        for (int i = 0; i < 8; i++)
        {
            final IChannel layer = this.bank.getItem (i);
            this.menu.get (i).set (layer.doesExist () ? "Mute" : "", Boolean.valueOf (layer.isMute ()));
        }
    }


    protected void updateLayerMenu (final int selectedMenu)
    {
        this.menu.get (0).set ("Volume", Boolean.valueOf (selectedMenu - 1 == 0));
        this.menu.get (1).set ("Pan", Boolean.valueOf (selectedMenu - 1 == 1));
        this.menu.get (2).set (" ", Boolean.FALSE);

        final ILayerBank layerBank = (ILayerBank) this.bank;
        final int start = Math.max (0, layerBank.getItem (0).getSendBank ().getItem (0).getPosition ()) + 1;
        this.menu.get (3).set (String.format ("Sends %d-%d", Integer.valueOf (start), Integer.valueOf (start + 3)), Boolean.FALSE);

        for (int i = 0; i < 4; i++)
        {
            final String sendName = StringUtils.optimizeName (layerBank.getEditSendName (i), 12);
            this.menu.get (4 + i).set (sendName.isEmpty () ? " " : sendName, Boolean.valueOf (4 + i == selectedMenu - 1));
        }

        if (!this.surface.isShiftPressed () && !this.isKnobTouched (7))
            this.menu.get (7).set ("Up", Boolean.TRUE);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final ICursorDevice cd = this.model.getCursorDevice ();
        if (cd == null || !cd.hasLayers ())
            return super.getButtonColor (buttonID);

        // Drum Pad Bank has size of 16, layers only 8
        final int offset = this.getDrumPadIndex ();

        int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
        {
            final IChannel dl = this.bank.getItem (offset + buttonID.ordinal () - ButtonID.ROW1_1.ordinal ());
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
            final boolean muteState = this.configuration.isMuteState ();
            final IChannel layer = this.bank.getItem (offset + index);
            if (this.isPush2)
            {
                if (this.configuration.isMuteLongPressed () || this.configuration.isSoloLongPressed () || this.configuration.isMuteSoloLocked ())
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
                    case 4, 5, 6, 7:
                        return modeManager.isActive (Modes.get (Modes.DEVICE_LAYER_SEND1, index - 4)) ? PushColorManager.PUSH2_COLOR2_WHITE : PushColorManager.PUSH2_COLOR_BLACK;
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
     */
    protected void drawRow4 (final ITextDisplay display)
    {
        // Drum Pad Bank has size of 16, layers only 8
        final int offset = this.getDrumPadIndex ();
        for (int i = 0; i < 8; i++)
        {
            final IChannel layer = this.bank.getItem (offset + i);
            final String n = StringUtils.shortenAndFixASCII (layer.getName (), layer.isSelected () ? 7 : 8);
            display.setCell (3, i, layer.isSelected () ? Push1Display.SELECT_ARROW + n : n);
        }
    }


    protected int getDrumPadIndex ()
    {
        if (this.cursorDevice.hasDrumPads ())
        {
            final Optional<ILayer> selectedDrumPad = this.bank.getSelectedItem ();
            if (selectedDrumPad.isPresent () && selectedDrumPad.get ().getIndex () > 7)
                return 8;
        }
        return 0;
    }
}