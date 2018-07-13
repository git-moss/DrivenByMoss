// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode.track;

import de.mossgrabers.controller.push.controller.PushColors;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.controller.display.DisplayModel;
import de.mossgrabers.controller.push.mode.BaseMode;
import de.mossgrabers.controller.push.view.ColorView;
import de.mossgrabers.controller.push.view.Views;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.ViewManager;


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
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        final IChannel deviceChain = this.model.getCursorDevice ().getSelectedLayerOrDrumPad ();
        if (deviceChain == null)
            return;

        switch (index)
        {
            case 0:
                this.model.getCursorDevice ().toggleLayerOrDrumPadIsActivated (deviceChain.getIndex ());
                break;
            case 2:
                this.model.getCursorDevice ().toggleLayerOrDrumPadMute (deviceChain.getIndex ());
                break;
            case 3:
                this.model.getCursorDevice ().toggleLayerOrDrumPadSolo (deviceChain.getIndex ());
                break;
            case 7:
                final ViewManager viewManager = this.surface.getViewManager ();
                ((ColorView) viewManager.getView (Views.VIEW_COLOR)).setMode (ColorView.SelectMode.MODE_LAYER);
                viewManager.setActiveView (Views.VIEW_COLOR);
                break;
            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final IChannel deviceChain = this.model.getCursorDevice ().getSelectedLayerOrDrumPad ();
        if (deviceChain == null)
        {
            this.disableFirstRow ();
            return;
        }

        final int off = this.isPush2 ? PushColors.PUSH2_COLOR_BLACK : PushColors.PUSH1_COLOR_BLACK;
        this.surface.updateButton (20, deviceChain.isActivated () ? this.isPush2 ? PushColors.PUSH2_COLOR_YELLOW_MD : PushColors.PUSH1_COLOR_YELLOW_MD : this.isPush2 ? PushColors.PUSH2_COLOR_YELLOW_LO : PushColors.PUSH1_COLOR_YELLOW_LO);
        this.surface.updateButton (21, off);
        this.surface.updateButton (22, deviceChain.isMute () ? this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_HI : PushColors.PUSH1_COLOR_ORANGE_HI : this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_LO : PushColors.PUSH1_COLOR_ORANGE_LO);
        this.surface.updateButton (23, deviceChain.isSolo () ? this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_HI : PushColors.PUSH1_COLOR_ORANGE_HI : this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_LO : PushColors.PUSH1_COLOR_ORANGE_LO);
        this.surface.updateButton (24, off);
        this.surface.updateButton (25, off);
        this.surface.updateButton (26, off);
        this.surface.updateButton (27, this.isPush2 ? PushColors.PUSH2_COLOR_GREEN_HI : PushColors.PUSH1_COLOR_GREEN_HI);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ();
        final IChannel deviceChain = this.model.getCursorDevice ().getSelectedLayerOrDrumPad ();
        if (deviceChain == null)
            d.setRow (1, "                     Please selecta layer...                        ").clearRow (0).clearRow (2).done (0).done (2);
        else
        {
            d.clearRow (0).clearRow (1).setBlock (0, 0, "Layer: " + deviceChain.getName ());
            d.setCell (2, 0, "Active").setCell (3, 0, deviceChain.isActivated () ? "On" : "Off");
            d.setCell (2, 1, "");
            d.setCell (3, 1, "");
            d.setCell (2, 2, "Mute").setCell (3, 2, deviceChain.isMute () ? "On" : "Off");
            d.setCell (2, 3, "Solo").setCell (3, 3, deviceChain.isSolo () ? "On" : "Off");
            d.setCell (2, 4, "");
            d.setCell (3, 4, "");
            d.setCell (2, 5, "");
            d.setCell (3, 5, "");
            d.clearCell (2, 6).clearCell (3, 6);
            d.setCell (2, 7, "Select").setCell (3, 7, "Color").done (0).done (1).done (2).done (3);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final DisplayModel message = this.surface.getDisplay ().getModel ();
        final IChannel deviceChain = this.model.getCursorDevice ().getSelectedLayerOrDrumPad ();
        if (deviceChain == null)
            message.setMessage (3, "Please select a layer...");
        else
        {
            message.addOptionElement ("Layer: " + deviceChain.getName (), "", false, "", "Active", deviceChain.isActivated (), false);
            message.addEmptyElement ();
            message.addOptionElement ("", "", false, "", "Mute", deviceChain.isMute (), false);
            message.addOptionElement ("", "", false, "", "Solo", deviceChain.isSolo (), false);
            message.addEmptyElement ();
            message.addEmptyElement ();
            message.addEmptyElement ();
            message.addOptionElement ("", "", false, "", "Select Color", false, false);
        }
        message.send ();
    }
}