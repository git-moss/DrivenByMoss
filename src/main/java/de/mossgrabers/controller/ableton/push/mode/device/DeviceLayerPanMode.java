// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode.device;

import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.controller.ableton.push.parameterprovider.PushPanLayerOrDrumPadParameterProvider;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.AbstractGraphicDisplay;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.mode.Modes;

import java.util.Optional;


/**
 * Mode for editing the panorama of all device layers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceLayerPanMode extends DeviceLayerMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public DeviceLayerPanMode (final PushControlSurface surface, final IModel model)
    {
        super (Modes.NAME_LAYER_PANORAMA, surface, model);

        this.setParameterProvider (new PushPanLayerOrDrumPadParameterProvider (this.cursorDevice));
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.setTouchedKnob (index, isTouched);

        // Drum Pad Bank has size of 16, layers only 8
        final int offset = this.getDrumPadIndex ();
        final IChannel layer = this.bank.getItem (offset + index);
        if (!layer.doesExist ())
            return;

        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);
            layer.resetPan ();
        }

        layer.touchPan (isTouched);
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
                display.setCell (0, i, layer.doesExist () ? "Pan" : "").setCell (1, i, layer.getPanStr (8));
                if (layer.doesExist ())
                    display.setCell (2, i, layer.getPan (), Format.FORMAT_PAN);
            }
        }

        this.drawRow4 (display);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplayElements (final IGraphicDisplay display, final Optional<ILayer> l)
    {
        this.updateChannelDisplay (display, AbstractGraphicDisplay.GRID_ELEMENT_CHANNEL_PAN, false, true);
    }
}