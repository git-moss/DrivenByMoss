// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode.device;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.graphics.display.DisplayModel;
import de.mossgrabers.framework.graphics.grid.SendData;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.Pair;


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
    public DeviceLayerModeSend (final PushControlSurface surface, final IModel model)
    {
        super ("Layer Send", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final ICursorDevice cd = this.model.getCursorDevice ();

        // Drum Pad Bank has size of 16, layers only 8
        final int offset = getDrumPadIndex (cd);
        final IChannel layer = cd.getLayerOrDrumPadBank ().getItem (offset + index);
        if (layer.doesExist ())
            layer.getSendBank ().getItem (this.getCurrentSendIndex ()).changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;

        final ICursorDevice cd = this.model.getCursorDevice ();

        // Drum Pad Bank has size of 16, layers only 8
        final int offset = getDrumPadIndex (cd);
        final IChannel layer = cd.getLayerOrDrumPadBank ().getItem (offset + index);
        if (!layer.doesExist ())
            return;

        final int sendIndex = this.getCurrentSendIndex ();

        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (this.surface.getDeleteTriggerId ());
            layer.getSendBank ().getItem (sendIndex).resetValue ();
        }

        layer.getSendBank ().getItem (sendIndex).touchValue (isTouched);
        this.checkStopAutomationOnKnobRelease (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ();
        final ICursorDevice cd = this.model.getCursorDevice ();
        // Drum Pad Bank has size of 16, layers only 8
        final int offset = getDrumPadIndex (cd);
        final int sendIndex = this.getCurrentSendIndex ();

        final IChannelBank<?> bank = cd.getLayerOrDrumPadBank ();
        for (int i = 0; i < 8; i++)
        {
            final IChannel layer = bank.getItem (offset + i);
            final boolean exists = layer.doesExist ();
            final ISend send = layer.getSendBank ().getItem (sendIndex);
            d.setCell (0, i, exists ? send.getName () : "").setCell (1, i, send.getDisplayedValue (8));
            if (exists)
                d.setCell (2, i, send.getValue (), Format.FORMAT_VALUE);
            else
                d.clearCell (2, i);
        }
        d.done (0).done (1).done (2);

        this.drawRow4 (d, cd);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplayElements (final DisplayModel message, final ICursorDevice cd, final IChannel l)
    {
        final int sendIndex = this.getCurrentSendIndex ();

        this.updateMenuItems (5 + sendIndex % 4);

        final PushConfiguration config = this.surface.getConfiguration ();

        // Drum Pad Bank has size of 16, layers only 8
        final int offset = getDrumPadIndex (cd);
        final int sendOffset = config.isSendsAreToggled () ? 4 : 0;
        final IChannelBank<?> bank = cd.getLayerOrDrumPadBank ();
        for (int i = 0; i < 8; i++)
        {
            final IChannel layer = bank.getItem (offset + i);

            final Pair<String, Boolean> pair = this.menu.get (i);
            final String topMenu = pair.getKey ();
            final boolean isTopMenuOn = pair.getValue ().booleanValue ();

            // Channel info
            final SendData [] sendData = new SendData [4];
            for (int j = 0; j < 4; j++)
            {
                final int sendPos = sendOffset + j;
                final ISend send = layer.getSendBank ().getItem (sendPos);
                final boolean exists = send.doesExist ();
                sendData[j] = new SendData (send.getName (), exists && sendIndex == sendPos && this.isKnobTouched[i] ? send.getDisplayedValue () : "", exists ? send.getValue () : 0, exists ? send.getModulatedValue () : 0, sendIndex == sendPos);
            }

            message.addSendsElement (topMenu, isTopMenuOn, layer.doesExist () ? layer.getName () : "", ChannelType.LAYER, bank.getItem (offset + i).getColor (), layer.isSelected (), sendData, false, layer.isActivated (), layer.isActivated ());
        }
    }


    private int getCurrentSendIndex ()
    {
        return this.surface.getModeManager ().getActiveOrTempModeId ().ordinal () - Modes.MODE_DEVICE_LAYER_SEND1.ordinal ();
    }
}