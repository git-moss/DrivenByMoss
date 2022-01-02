// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode.device;

import de.mossgrabers.controller.ableton.push.controller.PushColorManager;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.controller.ableton.push.mode.BaseMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.ColorSelectMode;
import de.mossgrabers.framework.view.ColorView;
import de.mossgrabers.framework.view.Views;

import java.util.Optional;


/**
 * Mode for editing details of a layer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceLayerDetailsMode extends BaseMode<ILayer>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public DeviceLayerDetailsMode (final PushControlSurface surface, final IModel model)
    {
        super ("Layer details", surface, model, model.getCursorDevice ().getLayerBank ());
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        final Optional<ILayer> channelOpt = this.bank.getSelectedItem ();
        if (channelOpt.isEmpty ())
            return;

        final IChannel channel = channelOpt.get ();

        switch (index)
        {
            case 0:
                channel.toggleIsActivated ();
                break;
            case 2:
                channel.toggleMute ();
                break;
            case 3:
                channel.toggleSolo ();
                break;
            case 7:
                final ViewManager viewManager = this.surface.getViewManager ();
                ((ColorView<?, ?>) viewManager.get (Views.COLOR)).setMode (ColorSelectMode.MODE_LAYER);
                viewManager.setActive (Views.COLOR);
                break;
            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        switch (index)
        {
            case 6:
                if (this.bank instanceof final IDrumPadBank drumPadBank)
                    drumPadBank.clearMute ();
                break;
            case 7:
                if (this.bank instanceof final IDrumPadBank drumPadBank)
                    drumPadBank.clearSolo ();
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
        final Optional<ILayer> channelOpt = this.bank.getSelectedItem ();
        if (channelOpt.isEmpty ())
            return super.getButtonColor (buttonID);

        int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
        {
            final IChannel channel = channelOpt.get ();
            switch (index)
            {
                case 0:
                    return this.colorManager.getColorIndex (channel.isActivated () ? PushColorManager.PUSH_YELLOW_MD : PushColorManager.PUSH_YELLOW_LO);
                case 2:
                    return this.colorManager.getColorIndex (channel.isMute () ? PushColorManager.PUSH_ORANGE_HI : PushColorManager.PUSH_ORANGE_LO);
                case 3:
                    return this.colorManager.getColorIndex (channel.isSolo () ? PushColorManager.PUSH_ORANGE_HI : PushColorManager.PUSH_ORANGE_LO);
                case 7:
                    return this.colorManager.getColorIndex (PushColorManager.PUSH_GREEN_HI);
                default:
                    return this.colorManager.getColorIndex (PushColorManager.PUSH_BLACK);
            }
        }

        index = this.isButtonRow (1, buttonID);
        if (index >= 0)
        {
            if (index >= 6)
                return this.model.getColorManager ().getColorIndex (this.bank instanceof IDrumPadBank ? AbstractMode.BUTTON_COLOR2_ON : AbstractFeatureGroup.BUTTON_COLOR_OFF);
            return this.isPush2 ? PushColorManager.PUSH2_COLOR_BLACK : PushColorManager.PUSH1_COLOR_BLACK;
        }

        return super.getButtonColor (buttonID);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final Optional<ILayer> channelOpt = this.bank.getSelectedItem ();
        if (channelOpt.isEmpty ())
        {
            display.setRow (1, "                     Please selecta layer...                        ");
            return;
        }

        final IChannel channel = channelOpt.get ();

        final String layerName = channel.getName ();
        display.setBlock (0, 0, "Layer: " + layerName);
        if (layerName.length () > 10)
            display.setBlock (0, 1, layerName.substring (10));
        display.setCell (2, 0, "Active").setCell (3, 0, channel.isActivated () ? "On" : "Off");
        display.setCell (2, 1, "");
        display.setCell (3, 1, "");
        display.setCell (2, 2, "Mute").setCell (3, 2, channel.isMute () ? "On" : "Off");
        display.setCell (2, 3, "Solo").setCell (3, 3, channel.isSolo () ? "On" : "Off");
        display.setCell (2, 4, "");
        display.setCell (3, 4, "");
        display.setCell (2, 5, "");
        display.setCell (3, 5, "");
        display.setCell (0, 6, "Clr Mute");
        display.setCell (0, 7, "Clr Solo");
        display.setCell (2, 7, "Select");
        display.setCell (3, 7, "Color");
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final Optional<ILayer> channelOpt = this.bank.getSelectedItem ();
        if (channelOpt.isEmpty ())
        {
            display.setMessage (3, "Please select a layer...");
            return;
        }

        final IChannel channel = channelOpt.get ();

        display.addOptionElement ("Layer: " + channel.getName (), "", false, "", "Active", channel.isActivated (), false);
        display.addEmptyElement ();
        display.addOptionElement ("", "", false, "", "Mute", channel.isMute (), false);
        display.addOptionElement ("", "", false, "", "Solo", channel.isSolo (), false);
        display.addEmptyElement ();
        display.addEmptyElement ();
        display.addOptionElement ("", "Clear Mute", false, "", "", false, false);
        display.addOptionElement ("", "Clear Solo", false, "", "Select Color", false, false);
    }
}