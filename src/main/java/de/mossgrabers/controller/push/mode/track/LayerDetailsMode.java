// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode.track;

import de.mossgrabers.controller.push.controller.PushColorManager;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.BaseMode;
import de.mossgrabers.controller.push.view.ColorView;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.bank.IChannelBank;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Mode for editing details of a layer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LayerDetailsMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public LayerDetailsMode (final PushControlSurface surface, final IModel model)
    {
        super ("Layer details", surface, model, model.getCursorDevice ().getLayerOrDrumPadBank ());

        model.getCursorDevice ().addHasDrumPadsObserver (hasDrumPads -> this.switchBanks (model.getCursorDevice ().getLayerOrDrumPadBank ()));
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        final IChannel channel = this.model.getCursorDevice ().getLayerOrDrumPadBank ().getSelectedItem ();
        if (channel == null)
            return;

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
                ((ColorView) viewManager.get (Views.COLOR)).setMode (ColorView.SelectMode.MODE_LAYER);
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
        final IChannelBank<?> bank = this.model.getCursorDevice ().getLayerOrDrumPadBank ();
        if (bank == null)
            return;

        switch (index)
        {
            case 6:
                if (bank instanceof IDrumPadBank)
                    ((IDrumPadBank) bank).clearMute ();
                break;
            case 7:
                if (bank instanceof IDrumPadBank)
                    ((IDrumPadBank) bank).clearSolo ();
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
        final IChannel deviceChain = this.model.getCursorDevice ().getLayerOrDrumPadBank ().getSelectedItem ();
        if (deviceChain == null)
            return super.getButtonColor (buttonID);

        int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
        {
            switch (index)
            {
                case 0:
                    return deviceChain.isActivated () ? this.isPush2 ? PushColorManager.PUSH2_COLOR_YELLOW_MD : PushColorManager.PUSH1_COLOR_YELLOW_MD : this.isPush2 ? PushColorManager.PUSH2_COLOR_YELLOW_LO : PushColorManager.PUSH1_COLOR_YELLOW_LO;
                case 2:
                    return deviceChain.isMute () ? this.isPush2 ? PushColorManager.PUSH2_COLOR_ORANGE_HI : PushColorManager.PUSH1_COLOR_ORANGE_HI : this.isPush2 ? PushColorManager.PUSH2_COLOR_ORANGE_LO : PushColorManager.PUSH1_COLOR_ORANGE_LO;
                case 3:
                    return deviceChain.isSolo () ? this.isPush2 ? PushColorManager.PUSH2_COLOR_ORANGE_HI : PushColorManager.PUSH1_COLOR_ORANGE_HI : this.isPush2 ? PushColorManager.PUSH2_COLOR_ORANGE_LO : PushColorManager.PUSH1_COLOR_ORANGE_LO;
                case 7:
                    return this.isPush2 ? PushColorManager.PUSH2_COLOR_GREEN_HI : PushColorManager.PUSH1_COLOR_GREEN_HI;
                default:
                    return this.isPush2 ? PushColorManager.PUSH2_COLOR_BLACK : PushColorManager.PUSH1_COLOR_BLACK;
            }
        }

        index = this.isButtonRow (1, buttonID);
        if (index >= 0)
        {
            if (index >= 6)
                return this.model.getColorManager ().getColorIndex (this.model.getCursorDevice ().getLayerOrDrumPadBank () instanceof IDrumPadBank ? AbstractMode.BUTTON_COLOR2_ON : AbstractFeatureGroup.BUTTON_COLOR_OFF);
            return this.isPush2 ? PushColorManager.PUSH2_COLOR_BLACK : PushColorManager.PUSH1_COLOR_BLACK;
        }

        return super.getButtonColor (buttonID);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final IChannel deviceChain = this.model.getCursorDevice ().getLayerOrDrumPadBank ().getSelectedItem ();
        if (deviceChain == null)
        {
            display.setRow (1, "                     Please selecta layer...                        ");
            return;
        }

        final String layerName = deviceChain.getName ();
        display.setBlock (0, 0, "Layer: " + layerName);
        if (layerName.length () > 10)
            display.setBlock (0, 1, layerName.substring (10));
        display.setCell (2, 0, "Active").setCell (3, 0, deviceChain.isActivated () ? "On" : "Off");
        display.setCell (2, 1, "");
        display.setCell (3, 1, "");
        display.setCell (2, 2, "Mute").setCell (3, 2, deviceChain.isMute () ? "On" : "Off");
        display.setCell (2, 3, "Solo").setCell (3, 3, deviceChain.isSolo () ? "On" : "Off");
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
        final IChannel deviceChain = this.model.getCursorDevice ().getLayerOrDrumPadBank ().getSelectedItem ();
        if (deviceChain == null)
        {
            display.setMessage (3, "Please select a layer...");
            return;
        }

        display.addOptionElement ("Layer: " + deviceChain.getName (), "", false, "", "Active", deviceChain.isActivated (), false);
        display.addEmptyElement ();
        display.addOptionElement ("", "", false, "", "Mute", deviceChain.isMute (), false);
        display.addOptionElement ("", "", false, "", "Solo", deviceChain.isSolo (), false);
        display.addEmptyElement ();
        display.addEmptyElement ();
        display.addOptionElement ("", "Clear Mute", false, "", "", false, false);
        display.addOptionElement ("", "Clear Solo", false, "", "Select Color", false, false);
    }
}