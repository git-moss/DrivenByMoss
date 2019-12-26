// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode.device;

import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.AbstractGraphicDisplay;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;


/**
 * Mode for editing the panorama of all device layers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceLayerModePan extends DeviceLayerMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public DeviceLayerModePan (final PushControlSurface surface, final IModel model)
    {
        super ("Layer Panorama", surface, model);
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
            layer.changePan (value);
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
        final ICursorDevice cd = this.model.getCursorDevice ();
        // Drum Pad Bank has size of 16, layers only 8
        final int offset = getDrumPadIndex (cd);

        final IChannelBank<?> bank = cd.getLayerOrDrumPadBank ();
        for (int i = 0; i < 8; i++)
        {
            final IChannel layer = bank.getItem (offset + i);
            display.setCell (0, i, layer.doesExist () ? "Pan" : "").setCell (1, i, layer.getPanStr (8));
            if (layer.doesExist ())
                display.setCell (2, i, layer.getPan (), Format.FORMAT_VALUE);
        }

        this.drawRow4 (display, cd);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplayElements (final IGraphicDisplay display, final ICursorDevice cd, final IChannel l)
    {
        this.updateChannelDisplay (display, cd, AbstractGraphicDisplay.GRID_ELEMENT_CHANNEL_PAN, false, true);
    }
}