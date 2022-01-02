// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode.device;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.controller.ableton.push.parameterprovider.PushSendLayerOrDrumPadParameterProvider;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.graphics.canvas.utils.SendData;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.Pair;

import java.util.Optional;


/**
 * Mode for editing a all sends of a device layer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceLayerModeSend extends DeviceLayerMode
{
    private final int sendIndex;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param sendIndex The index of the send
     */
    public DeviceLayerModeSend (final PushControlSurface surface, final IModel model, final int sendIndex)
    {
        super (Modes.NAME_LAYER_SENDS, surface, model);

        this.sendIndex = sendIndex;

        this.setParameterProvider (new PushSendLayerOrDrumPadParameterProvider (this.cursorDevice, sendIndex));
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;

        // Drum Pad Bank has size of 16, layers only 8
        final int offset = this.getDrumPadIndex ();
        final IChannel layer = this.bank.getItem (offset + index);
        if (!layer.doesExist ())
            return;

        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);
            layer.getSendBank ().getItem (this.sendIndex).resetValue ();
        }

        layer.getSendBank ().getItem (this.sendIndex).touchValue (isTouched);
        this.checkStopAutomationOnKnobRelease (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        if (!this.cursorDevice.hasLayers ())
            display.setBlock (1, 1, "    This device  ").setBlock (1, 2, "does not have layers.");
        else if (!this.bank.hasExistingItems ())
            display.setBlock (1, 1, "    Please create").setBlock (1, 2, this.cursorDevice.hasDrumPads () ? "a Drum Pad..." : "a Device Layer...");
        else
        {
            // Drum Pad Bank has size of 16, layers only 8
            final int offset = this.getDrumPadIndex ();

            for (int i = 0; i < 8; i++)
            {
                final IChannel layer = this.bank.getItem (offset + i);
                final boolean exists = layer.doesExist ();
                final ISend send = layer.getSendBank ().getItem (this.sendIndex);
                display.setCell (0, i, exists ? send.getName () : "").setCell (1, i, send.getDisplayedValue (8));
                if (exists)
                    display.setCell (2, i, send.getValue (), Format.FORMAT_VALUE);
            }
        }

        this.drawRow4 (display);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplayElements (final IGraphicDisplay display, final Optional<ILayer> l)
    {
        this.updateMenuItems (5 + this.sendIndex % 4);

        final PushConfiguration config = this.surface.getConfiguration ();

        // Drum Pad Bank has size of 16, layers only 8
        final int offset = this.getDrumPadIndex ();
        final int sendOffset = config.isSendsAreToggled () ? 4 : 0;
        for (int i = 0; i < 8; i++)
        {
            final IChannel layer = this.bank.getItem (offset + i);

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
                sendData[j] = new SendData (send.getName (), exists && this.sendIndex == sendPos && this.isKnobTouched[i] ? send.getDisplayedValue () : "", exists ? send.getValue () : 0, exists ? send.getModulatedValue () : 0, this.sendIndex == sendPos);
            }

            display.addSendsElement (topMenu, isTopMenuOn, layer.doesExist () ? layer.getName () : "", ChannelType.LAYER, this.bank.getItem (offset + i).getColor (), layer.isSelected (), sendData, false, layer.isActivated (), layer.isActivated ());
        }
    }
}