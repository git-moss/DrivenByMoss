// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode.device;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.controller.display.DisplayModel;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;


/**
 * Mode for editing the volume of all device layers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceLayerModeVolume extends DeviceLayerMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public DeviceLayerModeVolume (final PushControlSurface surface, final IModel model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        final ICursorDevice cd = this.model.getCursorDevice ();

        // Drum Pad Bank has size of 16, layers only 8
        final int offset = getDrumPadIndex (cd);
        final IChannel layer = cd.getLayerOrDrumPad (offset + index);
        if (layer.doesExist ())
            cd.changeLayerOrDrumPadVolume (offset + index, value);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;

        final ICursorDevice cd = this.model.getCursorDevice ();

        // Drum Pad Bank has size of 16, layers only 8
        final int offset = getDrumPadIndex (cd);
        final IChannel layer = cd.getLayerOrDrumPad (offset + index);
        if (!layer.doesExist ())
            return;

        if (isTouched)
        {
            if (this.surface.isDeletePressed ())
            {
                this.surface.setButtonConsumed (this.surface.getDeleteButtonId ());
                cd.resetLayerOrDrumPadVolume (offset + index);
                return;
            }

            this.surface.getDisplay ().notify ("Volume: " + layer.getVolumeStr ());
        }

        cd.touchLayerOrDrumPadVolume (layer.getIndex (), isTouched);
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

        final PushConfiguration config = this.surface.getConfiguration ();
        for (int i = 0; i < 8; i++)
        {
            final IChannel layer = cd.getLayerOrDrumPad (offset + i);
            d.setCell (0, i, layer.doesExist () ? "Volume" : "").setCell (1, i, layer.getVolumeStr (8));
            if (layer.doesExist ())
                d.setCell (2, i, config.isEnableVUMeters () ? layer.getVu () : layer.getVolume (), Format.FORMAT_VALUE);
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
        this.updateChannelDisplay (message, cd, DisplayModel.GRID_ELEMENT_CHANNEL_VOLUME, true, false);
    }
}