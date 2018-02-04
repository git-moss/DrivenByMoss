// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.mode.device;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.controller.ValueChanger;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.daw.CursorDeviceProxy;
import de.mossgrabers.framework.daw.EffectTrackBankProxy;
import de.mossgrabers.framework.daw.data.ChannelData;
import de.mossgrabers.framework.daw.data.SendData;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.DisplayMessage;
import de.mossgrabers.push.controller.PushColors;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.controller.PushDisplay;
import de.mossgrabers.push.mode.BaseMode;
import de.mossgrabers.push.mode.Modes;


/**
 * Mode for editing a device layer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceLayerMode extends BaseMode
{
    protected String [] menu =
    {
        "Volume",
        "Pan",
        "",
        "Sends 1-4",
        "Send 1",
        "Send 2",
        "Send 3",
        "Up"
    };


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public DeviceLayerMode (final PushControlSurface surface, final Model model)
    {
        super (surface, model);
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        final CursorDeviceProxy cd = this.model.getCursorDevice ();
        final ChannelData selectedDeviceLayer = cd.getSelectedLayerOrDrumPad ();
        if (selectedDeviceLayer == null)
            return;
        switch (index)
        {
            case 0:
                cd.changeLayerOrDrumPadVolume (selectedDeviceLayer.getIndex (), value);
                break;
            case 1:
                cd.changeLayerOrDrumPadPan (selectedDeviceLayer.getIndex (), value);
                break;
            default:
                if (this.isPush2 && index < 4)
                    break;
                final int sendIndex = index - (this.isPush2 ? this.surface.getConfiguration ().isSendsAreToggled () ? 0 : 4 : 2);
                cd.changeLayerOrDrumPadSend (selectedDeviceLayer.getIndex (), sendIndex, value);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (final int index, final boolean isTouched)
    {
        final CursorDeviceProxy cd = this.model.getCursorDevice ();
        final ChannelData l = cd.getSelectedLayerOrDrumPad ();
        if (l == null)
            return;

        this.isKnobTouched[index] = isTouched;

        if (isTouched)
        {
            if (this.surface.isDeletePressed ())
            {
                this.surface.setButtonConsumed (this.surface.getDeleteButtonId ());
                switch (index)
                {
                    case 0:
                        cd.resetLayerOrDrumPadVolume (l.getIndex ());
                        break;
                    case 1:
                        cd.resetLayerOrDrumPadPan (l.getIndex ());
                        break;
                    default:
                        if (this.isPush2 && index < 4)
                            break;
                        final int sendIndex = index - (this.isPush2 ? this.surface.getConfiguration ().isSendsAreToggled () ? 0 : 4 : 2);
                        cd.resetLayerSend (l.getIndex (), sendIndex);
                        break;
                }
                return;
            }

            switch (index)
            {
                case 0:
                    this.surface.getDisplay ().notify ("Volume: " + l.getVolumeStr ());
                    break;
                case 1:
                    this.surface.getDisplay ().notify ("Pan: " + l.getPanStr ());
                    break;
                default:
                    if (this.isPush2 && index < 4)
                        break;
                    final int sendIndex = index - (this.isPush2 ? this.surface.getConfiguration ().isSendsAreToggled () ? 0 : 4 : 2);
                    final EffectTrackBankProxy fxTrackBank = this.model.getEffectTrackBank ();
                    final String name = fxTrackBank == null ? l.getSends ()[sendIndex].getName () : fxTrackBank.getTrack (sendIndex).getName ();
                    if (!name.isEmpty ())
                        this.surface.getDisplay ().notify ("Send " + name + ": " + l.getSends ()[sendIndex].getDisplayedValue ());
                    break;
            }
        }

        switch (index)
        {
            case 0:
                cd.touchLayerOrDrumPadVolume (l.getIndex (), isTouched);
                break;
            case 1:
                cd.touchLayerOrDrumPadPan (l.getIndex (), isTouched);
                break;
            default:
                if (this.isPush2 && index < 4)
                    break;
                final int sendIndex = index - (this.isPush2 ? this.surface.getConfiguration ().isSendsAreToggled () ? 0 : 4 : 2);
                cd.touchLayerOrDrumPadSend (l.getIndex (), sendIndex, isTouched);
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
            final CursorDeviceProxy cd = this.model.getCursorDevice ();
            if (!cd.hasSelectedDevice ())
                return;

            final int offset = getDrumPadIndex (cd);
            final ChannelData layer = cd.getLayerOrDrumPad (offset + index);
            if (!layer.doesExist ())
                return;

            final int layerIndex = layer.getIndex ();
            if (!layer.isSelected ())
            {
                cd.selectLayerOrDrumPad (layerIndex);
                return;
            }

            cd.enterLayerOrDrumPad (layer.getIndex ());
            cd.selectFirstDeviceInLayerOrDrumPad (layer.getIndex ());
            final ModeManager modeManager = this.surface.getModeManager ();
            modeManager.setActiveMode (Modes.MODE_DEVICE_PARAMS);
            ((DeviceParamsMode) modeManager.getMode (Modes.MODE_DEVICE_PARAMS)).setShowDevices (true);
            return;
        }

        // LONG press
        this.surface.setButtonConsumed (PushControlSurface.PUSH_BUTTON_ROW1_1 + index);
        this.moveUp ();
    }


    /**
     * Move up the hierarchy.
     */
    protected void moveUp ()
    {
        // There is no device on the track move upwards to the track view
        final CursorDeviceProxy cd = this.model.getCursorDevice ();
        if (!cd.hasSelectedDevice ())
        {
            this.surface.getViewManager ().getActiveView ().executeTriggerCommand (Commands.COMMAND_TRACK, ButtonEvent.DOWN);
            return;
        }

        final ModeManager modeManager = this.surface.getModeManager ();
        modeManager.setActiveMode (Modes.MODE_DEVICE_PARAMS);
        cd.selectChannel ();
        ((DeviceParamsMode) modeManager.getMode (Modes.MODE_DEVICE_PARAMS)).setShowDevices (true);
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
            final CursorDeviceProxy cd = this.model.getCursorDevice ();
            final int offset = getDrumPadIndex (cd);
            if (config.isMuteState ())
                cd.toggleLayerOrDrumPadMute (offset + index);
            else
                cd.toggleLayerOrDrumPadSolo (offset + index);
            return;
        }

        final ModeManager modeManager = this.surface.getModeManager ();
        EffectTrackBankProxy fxTrackBank;
        switch (index)
        {
            case 0:
                if (modeManager.isActiveMode (Modes.MODE_DEVICE_LAYER_VOLUME))
                    modeManager.setActiveMode (Modes.MODE_DEVICE_LAYER);
                else
                    modeManager.setActiveMode (Modes.MODE_DEVICE_LAYER_VOLUME);
                break;

            case 1:
                if (modeManager.isActiveMode (Modes.MODE_DEVICE_LAYER_PAN))
                    modeManager.setActiveMode (Modes.MODE_DEVICE_LAYER);
                else
                    modeManager.setActiveMode (Modes.MODE_DEVICE_LAYER_PAN);
                break;

            case 2:
                // Not used
                break;

            case 3:
                if (!this.model.isEffectTrackBankActive ())
                {
                    // Check if there are more than 4 FX channels
                    if (!config.isSendsAreToggled ())
                    {
                        fxTrackBank = this.model.getEffectTrackBank ();
                        if (!fxTrackBank.getTrack (4).doesExist ())
                            return;
                    }
                    config.setSendsAreToggled (!config.isSendsAreToggled ());

                    if (!modeManager.isActiveMode (Modes.MODE_DEVICE_LAYER))
                        modeManager.setActiveMode (Integer.valueOf (Modes.MODE_DEVICE_LAYER_SEND1.intValue () + (config.isSendsAreToggled () ? 4 : 0)));
                }
                break;

            case 7:
                this.moveUp ();
                break;

            default:
                if (!this.model.isEffectTrackBankActive ())
                {
                    final int sendOffset = config.isSendsAreToggled () ? 0 : 4;
                    final int sendIndex = index - sendOffset;
                    fxTrackBank = this.model.getEffectTrackBank ();
                    if (fxTrackBank.getTrack (sendIndex).doesExist ())
                    {
                        final Integer si = Integer.valueOf (Modes.MODE_DEVICE_LAYER_SEND1.intValue () + sendIndex);
                        if (modeManager.isActiveMode (si))
                            modeManager.setActiveMode (Modes.MODE_DEVICE_LAYER);
                        else
                            modeManager.setActiveMode (si);
                    }
                }
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ().clear ();
        final CursorDeviceProxy cd = this.model.getCursorDevice ();
        if (!cd.hasSelectedDevice ())
        {
            d.setBlock (1, 0, "           Select").setBlock (1, 1, "a device or press").setBlock (1, 2, "'Add Effect'...  ").allDone ();
            return;
        }

        final boolean noLayers = cd.hasLayers () && cd.hasZeroLayers ();
        if (noLayers)
        {
            d.setBlock (1, 1, "    Please create").setBlock (1, 2, cd.hasDrumPads () ? "a Drum Pad..." : "a Device Layer...");
        }
        else
        {
            final ChannelData l = cd.getSelectedLayerOrDrumPad ();
            if (l != null)
            {
                d.setCell (0, 0, "Volume").setCell (1, 0, l.getVolumeStr (8)).setCell (2, 0, this.surface.getConfiguration ().isEnableVUMeters () ? l.getVu () : l.getVolume (), Format.FORMAT_VALUE);
                d.setCell (0, 1, "Pan").setCell (1, 1, l.getPanStr (8)).setCell (2, 1, l.getPan (), Format.FORMAT_PAN);

                final EffectTrackBankProxy fxTrackBank = this.model.getEffectTrackBank ();
                if (fxTrackBank == null)
                {
                    for (int i = 0; i < 6; i++)
                    {
                        final int pos = 2 + i;
                        final SendData sendData = l.getSends ()[i];
                        d.setCell (0, pos, sendData.getName ()).setCell (1, pos, sendData.getDisplayedValue (8)).setCell (2, pos, sendData.getValue (), Format.FORMAT_VALUE);
                    }
                }
                else
                {
                    final boolean isFX = this.model.isEffectTrackBankActive ();
                    for (int i = 0; i < 6; i++)
                    {
                        final TrackData fxTrack = fxTrackBank.getTrack (i);
                        final boolean isEmpty = isFX || !fxTrack.doesExist ();
                        final int pos = 2 + i;
                        if (isEmpty)
                        {
                            d.clearCell (0, pos);
                            d.clearCell (2, pos);
                        }
                        else
                        {
                            d.setCell (0, pos, fxTrack.getName ()).setCell (1, pos, l.getSends ()[i].getDisplayedValue (8));
                            d.setCell (2, pos, l.getSends ()[i].getValue (), Format.FORMAT_VALUE);
                        }
                    }
                }
            }
        }

        this.drawRow4 (d, cd);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final CursorDeviceProxy cd = this.model.getCursorDevice ();
        final DisplayMessage message = ((PushDisplay) this.surface.getDisplay ()).createMessage ();
        if (!cd.hasSelectedDevice ())
        {
            for (int i = 0; i < 8; i++)
                message.addOptionElement (i == 2 ? "Please select a device or press 'Add Device'..." : "", i == 7 ? "Up" : "", true, "", "", false, true);
            message.send ();
            return;
        }

        final boolean noLayers = cd.hasLayers () && cd.hasZeroLayers ();
        if (noLayers)
        {
            for (int i = 0; i < 8; i++)
                message.addOptionElement (i == 3 ? "Please create a " + (cd.hasDrumPads () ? "Drum Pad..." : "Device Layer...") : "", i == 7 ? "Up" : "", true, "", "", false, true);
            message.send ();
            return;
        }

        this.updateMenu ();
        this.updateDisplayElements (message, cd, cd.getSelectedLayerOrDrumPad ());
    }


    /**
     * Update all 8 elements.
     *
     * @param message The display message
     * @param cd The cursor device
     * @param l The channel data
     */
    protected void updateDisplayElements (final DisplayMessage message, final CursorDeviceProxy cd, final ChannelData l)
    {
        // Drum Pad Bank has size of 16, layers only 8
        final int offset = getDrumPadIndex (cd);

        // Get the index at which to draw the Sends element
        int sendsIndex = l == null ? -1 : l.getIndex () - offset + 1;
        if (sendsIndex == 8)
            sendsIndex = 6;

        final PushConfiguration config = this.surface.getConfiguration ();

        for (int i = 0; i < 8; i++)
        {
            final ChannelData layer = cd.getLayerOrDrumPad (offset + i);

            // The menu
            String topMenu;
            boolean topMenuSelected;
            if (config.isMuteLongPressed () || config.isMuteSoloLocked () && config.isMuteState ())
            {
                topMenu = layer.doesExist () ? "Mute" : "";
                topMenuSelected = layer.isMute ();
            }
            else if (config.isSoloLongPressed () || config.isMuteSoloLocked () && config.isSoloState ())
            {
                topMenu = layer.doesExist () ? "Solo" : "";
                topMenuSelected = layer.isSolo ();
            }
            else
            {
                topMenu = this.menu[i];
                topMenuSelected = i == 7;
            }

            // Channel info
            final String bottomMenu = layer.doesExist () ? layer.getName () : "";
            final String bottomMenuIcon = "layer";
            final double [] bottomMenuColor = layer.getColor ();
            final boolean isBottomMenuOn = layer.isSelected ();

            if (layer.isSelected ())
            {
                final ValueChanger valueChanger = this.model.getValueChanger ();
                message.addChannelElement (topMenu, topMenuSelected, bottomMenu, bottomMenuIcon, bottomMenuColor, isBottomMenuOn, valueChanger.toDisplayValue (layer.getVolume ()), valueChanger.toDisplayValue (layer.getModulatedVolume ()), this.isKnobTouched[0] ? layer.getVolumeStr (8) : "", valueChanger.toDisplayValue (layer.getPan ()), valueChanger.toDisplayValue (layer.getModulatedPan ()), this.isKnobTouched[1] ? layer.getPanStr (8) : "", valueChanger.toDisplayValue (config.isEnableVUMeters () ? layer.getVu () : 0), layer.isMute (), layer.isSolo (), false, 0);
            }
            else if (sendsIndex == i && l != null)
            {
                final EffectTrackBankProxy fxTrackBank = this.model.getEffectTrackBank ();
                final String [] sendName = new String [4];
                final String [] valueStr = new String [4];
                final int [] value = new int [4];
                final int [] modulatedValue = new int [4];
                final boolean [] selected = new boolean [4];
                for (int j = 0; j < 4; j++)
                {
                    final int sendOffset = config.isSendsAreToggled () ? 4 : 0;
                    final int sendPos = sendOffset + j;
                    final SendData send = l.getSends ()[sendPos];
                    sendName[j] = fxTrackBank == null ? send.getName () : fxTrackBank.getTrack (sendPos).getName ();
                    final boolean doesExist = send.doesExist ();
                    valueStr[j] = doesExist && this.isKnobTouched[4 + j] ? send.getDisplayedValue () : "";
                    value[j] = doesExist ? send.getValue () : 0;
                    modulatedValue[j] = doesExist ? send.getModulatedValue () : 0;
                    selected[j] = true;
                }
                message.addSendsElement (topMenu, topMenuSelected, layer.doesExist () ? layer.getName () : "", "layer", cd.getLayerOrDrumPad (offset + i).getColor (), layer.isSelected (), sendName, valueStr, value, modulatedValue, selected, true);
            }
            else
                message.addChannelSelectorElement (topMenu, topMenuSelected, bottomMenu, bottomMenuIcon, bottomMenuColor, isBottomMenuOn);
        }

        message.send ();
    }


    // Called from sub-classes
    protected void updateChannelDisplay (final DisplayMessage message, final CursorDeviceProxy cd, final int selectedMenu, final boolean isVolume, final boolean isPan)
    {
        this.updateMenu ();

        final PushConfiguration config = this.surface.getConfiguration ();

        // Drum Pad Bank has size of 16, layers only 8
        final int offset = getDrumPadIndex (cd);

        final ValueChanger valueChanger = this.model.getValueChanger ();
        for (int i = 0; i < 8; i++)
        {
            final ChannelData layer = cd.getLayerOrDrumPad (offset + i);

            // The menu item
            String topMenu;
            boolean isTopMenuOn;
            if (config.isMuteLongPressed () || config.isMuteSoloLocked () && config.isMuteState ())
            {
                topMenu = layer.doesExist () ? "Mute" : "";
                isTopMenuOn = layer.isMute ();
            }
            else if (config.isSoloLongPressed () || config.isMuteSoloLocked () && config.isSoloState ())
            {
                topMenu = layer.doesExist () ? "Solo" : "";
                isTopMenuOn = layer.isSolo ();
            }
            else
            {
                topMenu = this.menu[i];
                isTopMenuOn = i == 7 || i == selectedMenu - 1;
            }
            message.addChannelElement (selectedMenu, topMenu, isTopMenuOn, layer.doesExist () ? layer.getName () : "", "layer", cd.getLayerOrDrumPad (offset + i).getColor (), layer.isSelected (), valueChanger.toDisplayValue (layer.getVolume ()), valueChanger.toDisplayValue (layer.getModulatedVolume ()), isVolume && this.isKnobTouched[i] ? layer.getVolumeStr (8) : "", valueChanger.toDisplayValue (layer.getPan ()), valueChanger.toDisplayValue (layer.getModulatedPan ()), isPan && this.isKnobTouched[i] ? layer.getPanStr () : "", valueChanger.toDisplayValue (config.isEnableVUMeters () ? layer.getVu () : 0), layer.isMute (), layer.isSolo (), false, 0);
        }

        message.send ();
    }


    protected void updateMenu ()
    {
        final EffectTrackBankProxy fxTrackBank = this.model.getEffectTrackBank ();
        final PushConfiguration config = this.surface.getConfiguration ();
        final int sendOffset = config.isSendsAreToggled () ? 4 : 0;
        for (int i = 0; i < 3; i++)
            this.menu[4 + i] = fxTrackBank.getTrack (sendOffset + i).getName ();
        this.menu[3] = config.isSendsAreToggled () ? "Sends 5-8" : "Sends 1-4";
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final CursorDeviceProxy cd = this.model.getCursorDevice ();
        if (cd == null || !cd.hasLayers ())
        {
            this.disableFirstRow ();
            return;
        }

        final int offset = getDrumPadIndex (cd);
        for (int i = 0; i < 8; i++)
        {
            final ChannelData dl = cd.getLayerOrDrumPad (offset + i);
            this.surface.updateButton (20 + i, dl.doesExist () && dl.isActivated () ? dl.isSelected () ? this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_HI : PushColors.PUSH1_COLOR_ORANGE_HI : this.isPush2 ? PushColors.PUSH2_COLOR_YELLOW_LO : PushColors.PUSH1_COLOR_YELLOW_LO : this.isPush2 ? PushColors.PUSH2_COLOR_BLACK : PushColors.PUSH1_COLOR_BLACK);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateSecondRow ()
    {
        final CursorDeviceProxy cd = this.model.getCursorDevice ();

        final PushConfiguration config = this.surface.getConfiguration ();
        final boolean muteState = config.isMuteState ();
        if (this.isPush2)
        {
            if (config.isMuteLongPressed () || config.isSoloLongPressed () || config.isMuteSoloLocked ())
            {
                // Drum Pad Bank has size of 16, layers only 8
                final int offset = getDrumPadIndex (cd);

                for (int i = 0; i < 8; i++)
                {
                    final ChannelData layer = cd.getLayerOrDrumPad (offset + i);

                    int color = PushColors.PUSH2_COLOR_BLACK;
                    if (layer.doesExist ())
                    {
                        if (muteState)
                        {
                            if (layer.isMute ())
                                color = PushColors.PUSH2_COLOR2_AMBER_LO;
                        }
                        else if (layer.isSolo ())
                            color = PushColors.PUSH2_COLOR2_YELLOW_HI;
                    }

                    this.surface.updateButton (102 + i, color);
                }
                return;
            }

            final ModeManager modeManager = this.surface.getModeManager ();
            this.surface.updateButton (102, modeManager.isActiveMode (Modes.MODE_DEVICE_LAYER_VOLUME) ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH2_COLOR_BLACK);
            this.surface.updateButton (103, modeManager.isActiveMode (Modes.MODE_DEVICE_LAYER_PAN) ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH2_COLOR_BLACK);
            this.surface.updateButton (104, PushColors.PUSH2_COLOR_BLACK);
            this.surface.updateButton (105, PushColors.PUSH2_COLOR_BLACK);
            this.surface.updateButton (106, modeManager.isActiveMode (config.isSendsAreToggled () ? Modes.MODE_DEVICE_LAYER_SEND5 : Modes.MODE_DEVICE_LAYER_SEND1) ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH2_COLOR_BLACK);
            this.surface.updateButton (107, modeManager.isActiveMode (config.isSendsAreToggled () ? Modes.MODE_DEVICE_LAYER_SEND6 : Modes.MODE_DEVICE_LAYER_SEND2) ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH2_COLOR_BLACK);
            this.surface.updateButton (108, modeManager.isActiveMode (config.isSendsAreToggled () ? Modes.MODE_DEVICE_LAYER_SEND7 : Modes.MODE_DEVICE_LAYER_SEND3) ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH2_COLOR_BLACK);
            this.surface.updateButton (109, modeManager.isActiveMode (config.isSendsAreToggled () ? Modes.MODE_DEVICE_LAYER_SEND8 : Modes.MODE_DEVICE_LAYER_SEND4) ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH2_COLOR_BLACK);
            return;
        }

        if (cd == null || !cd.hasLayers ())
        {
            this.disableSecondRow ();
            this.surface.updateButton (109, this.isPush2 ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH1_COLOR2_WHITE);
            return;
        }

        final int offset = getDrumPadIndex (cd);
        for (int i = 0; i < 8; i++)
        {
            final ChannelData dl = cd.getLayerOrDrumPad (offset + i);
            int color = PushColors.PUSH1_COLOR_BLACK;
            if (dl.doesExist ())
            {
                if (muteState)
                {
                    if (!dl.isMute ())
                        color = PushColors.PUSH1_COLOR2_YELLOW_HI;
                }
                else
                    color = dl.isSolo () ? PushColors.PUSH1_COLOR2_BLUE_HI : PushColors.PUSH1_COLOR2_GREY_LO;
            }
            this.surface.updateButton (102 + i, color);
        }
    }


    /**
     * Draw the fourth row.
     *
     * @param d The display
     * @param cd The cursor device
     */
    protected void drawRow4 (final Display d, final CursorDeviceProxy cd)
    {
        // Drum Pad Bank has size of 16, layers only 8
        final int offset = getDrumPadIndex (cd);
        for (int i = 0; i < 8; i++)
        {
            final ChannelData layer = cd.getLayerOrDrumPad (offset + i);
            final String n = this.optimizeName (layer.getName (), layer.isSelected () ? 7 : 8);
            d.setCell (3, i, layer.isSelected () ? PushDisplay.RIGHT_ARROW + n : n);
        }
        d.allDone ();
    }


    protected static int getDrumPadIndex (final CursorDeviceProxy cd)
    {
        if (cd.hasDrumPads ())
        {
            final ChannelData selectedDrumPad = cd.getSelectedDrumPad ();
            if (selectedDrumPad != null && selectedDrumPad.getIndex () > 7)
                return 8;
        }
        return 0;
    }
}