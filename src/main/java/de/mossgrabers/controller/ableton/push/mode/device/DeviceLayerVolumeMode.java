// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode.device;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.controller.ableton.push.parameterprovider.PushVolumeLayerOrDrumPadParameterProvider;
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
 * Mode for editing the volume of all device layers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceLayerVolumeMode extends DeviceLayerMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public DeviceLayerVolumeMode (final PushControlSurface surface, final IModel model)
    {
        super (Modes.NAME_LAYER_VOLUME, surface, model);

        this.setParameterProvider (new PushVolumeLayerOrDrumPadParameterProvider (this.cursorDevice));
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
            layer.resetVolume ();
        }

        layer.touchVolume (isTouched);
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

            final PushConfiguration config = this.surface.getConfiguration ();
            for (int i = 0; i < 8; i++)
            {
                final IChannel layer = this.bank.getItem (offset + i);
                display.setCell (0, i, layer.doesExist () ? "Volume" : "").setCell (1, i, layer.getVolumeStr (8));
                if (layer.doesExist ())
                    display.setCell (2, i, config.isEnableVUMeters () ? layer.getVu () : layer.getVolume (), Format.FORMAT_VALUE);
            }
        }

        this.drawRow4 (display);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplayElements (final IGraphicDisplay message, final Optional<ILayer> l)
    {
        this.updateChannelDisplay (message, AbstractGraphicDisplay.GRID_ELEMENT_CHANNEL_VOLUME, true, false);
    }
}