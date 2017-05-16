// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.mode.device;

import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.daw.CursorDeviceProxy;
import de.mossgrabers.framework.daw.EffectTrackBankProxy;
import de.mossgrabers.framework.daw.data.ChannelData;
import de.mossgrabers.framework.daw.data.SendData;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.DisplayMessage;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.mode.Modes;


/**
 * Mode for editing a all sends of a device layer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceLayerModeSend extends DeviceLayerMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public DeviceLayerModeSend (final PushControlSurface surface, final Model model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        final CursorDeviceProxy cd = this.model.getCursorDevice ();

        // Drum Pad Bank has size of 16, layers only 8
        final int offset = getDrumPadIndex (cd);
        final ChannelData layer = cd.getLayerOrDrumPad (offset + index);
        if (layer.doesExist ())
            cd.changeLayerOrDrumPadSend (index, this.getCurrentSendIndex (), value);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;

        final CursorDeviceProxy cd = this.model.getCursorDevice ();

        // Drum Pad Bank has size of 16, layers only 8
        final int offset = getDrumPadIndex (cd);
        final ChannelData layer = cd.getLayerOrDrumPad (offset + index);
        if (!layer.doesExist ())
            return;

        final int sendIndex = this.getCurrentSendIndex ();

        if (isTouched)
        {
            if (this.surface.isDeletePressed ())
            {
                this.surface.setButtonConsumed (this.surface.getDeleteButtonId ());
                cd.resetLayerSend (index, sendIndex);
                return;
            }

            final EffectTrackBankProxy fxTrackBank = this.model.getEffectTrackBank ();
            final String name = fxTrackBank == null ? layer.getSends ()[sendIndex].getName () : fxTrackBank.getTrack (sendIndex).getName ();
            if (!name.isEmpty ())
                this.surface.getDisplay ().notify ("Send " + name + ": " + layer.getSends ()[sendIndex].getDisplayedValue ());
        }

        cd.touchLayerOrDrumPadSend (index, sendIndex, isTouched);
        this.checkStopAutomationOnKnobRelease (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ();
        final CursorDeviceProxy cd = this.model.getCursorDevice ();
        // Drum Pad Bank has size of 16, layers only 8
        final int offset = getDrumPadIndex (cd);
        final int sendIndex = this.getCurrentSendIndex ();

        for (int i = 0; i < 8; i++)
        {
            final ChannelData layer = cd.getLayerOrDrumPad (offset + i);
            final boolean exists = layer.doesExist ();
            d.setCell (0, i, exists ? layer.getSends ()[sendIndex].getName () : "").setCell (1, i, layer.getSends ()[sendIndex].getDisplayedValue ());
            if (exists)
                d.setCell (2, i, layer.getSends ()[sendIndex].getValue (), Format.FORMAT_VALUE);
            else
                d.clearCell (2, i);
        }
        d.done (0).done (1).done (2);

        this.drawRow4 (d, cd);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplayElements (final DisplayMessage message, final CursorDeviceProxy cd, final ChannelData l)
    {
        final int sendIndex = this.getCurrentSendIndex ();
        final EffectTrackBankProxy fxTrackBank = this.model.getEffectTrackBank ();

        this.updateMenu ();

        final PushConfiguration config = this.surface.getConfiguration ();

        // Drum Pad Bank has size of 16, layers only 8
        final int offset = getDrumPadIndex (cd);
        final int sendOffset = config.isSendsAreToggled () ? 4 : 0;
        for (int i = 0; i < 8; i++)
        {
            final ChannelData layer = cd.getLayerOrDrumPad (offset + i);

            message.addByte (DisplayMessage.GRID_ELEMENT_CHANNEL_SENDS);

            // The menu item
            if (config.isMuteLongPressed () || config.isMuteSoloLocked () && config.isMuteState ())
            {
                message.addString (layer.doesExist () ? "Mute" : "");
                message.addBoolean (layer.isMute ());
            }
            else if (config.isSoloLongPressed () || config.isMuteSoloLocked () && config.isSoloState ())
            {
                message.addString (layer.doesExist () ? "Solo" : "");
                message.addBoolean (layer.isSolo ());
            }
            else
            {
                message.addString (this.menu[i]);
                message.addBoolean (i > 3 && i - 4 + sendOffset == sendIndex);
            }

            // Channel info
            message.addString (layer.getName ());
            message.addString ("layer");
            message.addColor (cd.getLayerOrDrumPad (offset + i).getColor ());
            message.addByte (layer.isSelected () ? 1 : 0);

            for (int j = 0; j < 4; j++)
            {
                final int sendPos = sendOffset + j;
                final SendData send = layer.getSends ()[sendPos];
                message.addString (fxTrackBank == null ? send.getName () : fxTrackBank.getTrack (sendPos).getName ());
                message.addString (send.doesExist () && sendIndex == sendPos && this.isKnobTouched[i] ? send.getDisplayedValue () : "");
                message.addInteger (send.doesExist () ? send.getValue () : 0);
                message.addInteger (send.doesExist () ? send.getModulatedValue () : 0);
                message.addByte (sendIndex == sendPos ? 1 : 0);
            }

            // Signal Track mode off
            message.addBoolean (false);
        }

        message.send ();
    }


    private int getCurrentSendIndex ()
    {
        return this.surface.getModeManager ().getActiveModeId ().intValue () - Modes.MODE_DEVICE_LAYER_SEND1.intValue ();
    }
}