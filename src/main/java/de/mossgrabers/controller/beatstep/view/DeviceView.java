// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.beatstep.view;

import de.mossgrabers.controller.beatstep.BeatstepConfiguration;
import de.mossgrabers.controller.beatstep.controller.BeatstepColors;
import de.mossgrabers.controller.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.view.AbstractView;


/**
 * The Device view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceView extends AbstractView<BeatstepControlSurface, BeatstepConfiguration> implements BeatstepView
{
    private TrackEditing extensions;
    private boolean      isLayer;


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public DeviceView (final BeatstepControlSurface surface, final IModel model)
    {
        super ("Device", surface, model);
        this.extensions = new TrackEditing (surface, model);
        this.isLayer = false;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnob (final int index, final int value)
    {
        final ICursorDevice cd = this.model.getCursorDevice ();
        if (index < 8)
        {
            this.extensions.onTrackKnob (index, value);
            return;
        }

        cd.changeParameter (index - 8, value);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        if (!this.model.hasSelectedDevice ())
            return;

        final ICursorDevice cd = this.model.getCursorDevice ();

        IChannel sel;
        int index;
        IChannel dl;
        int bank;
        int offset;
        switch (note - 36)
        {
            // Toggle device on/off
            case 0:
                cd.toggleEnabledState ();
                break;

            // Device Left
            case 1:
                if (this.isLayer)
                {
                    sel = cd.getSelectedLayer ();
                    index = sel == null || sel.getIndex () == 0 ? 0 : sel.getIndex () - 1;
                    cd.selectLayer (index);
                }
                else
                    cd.selectPrevious ();
                break;

            // Device Right
            case 2:
                if (this.isLayer)
                {
                    sel = cd.getSelectedLayer ();
                    index = sel == null ? 0 : sel.getIndex () + 1;
                    cd.selectLayer (index > 7 ? 7 : index);
                }
                else
                    cd.selectNext ();
                break;

            // Enter layer
            case 3:
                if (!cd.hasLayers ())
                    return;
                dl = cd.getSelectedLayerOrDrumPad ();
                if (this.isLayer)
                {
                    if (dl != null)
                    {
                        cd.enterLayerOrDrumPad (dl.getIndex ());
                        cd.selectFirstDeviceInLayerOrDrumPad (dl.getIndex ());
                    }
                }
                else if (dl == null)
                    cd.selectLayerOrDrumPad (0);

                this.isLayer = !this.isLayer;
                break;

            // Exit layer
            case 4:
                if (this.isLayer)
                    this.isLayer = false;
                else
                {
                    if (cd.isNested ())
                    {
                        cd.selectParent ();
                        cd.selectChannel ();
                        this.isLayer = true;
                    }
                }

                break;

            case 5:
                // Intentionally empty
                break;

            // Param bank down
            case 6:
                cd.setSelectedParameterPage (Math.max (cd.getSelectedParameterPage () - 8, 0));
                break;

            // Param bank page up
            case 7:
                cd.setSelectedParameterPage (Math.min (cd.getSelectedParameterPage () + 8, cd.getParameterPageNames ().length - 1));
                break;

            default:
                bank = note - 36 - 8;
                offset = cd.getSelectedParameterPage () / 8 * 8;
                cd.setSelectedParameterPage (offset + bank);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final ICursorDevice cd = this.model.getCursorDevice ();
        final int offset = cd.getSelectedParameterPage () / 8 * 8;
        final PadGrid padGrid = this.surface.getPadGrid ();
        for (int i = 0; i < 8; i++)
            padGrid.light (44 + i, offset + i == cd.getSelectedParameterPage () ? BeatstepColors.BEATSTEP_BUTTON_STATE_BLUE : BeatstepColors.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (36, cd.isEnabled () ? BeatstepColors.BEATSTEP_BUTTON_STATE_RED : BeatstepColors.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (37, BeatstepColors.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (38, BeatstepColors.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (39, BeatstepColors.BEATSTEP_BUTTON_STATE_RED);
        padGrid.light (40, BeatstepColors.BEATSTEP_BUTTON_STATE_RED);
        padGrid.light (41, BeatstepColors.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (42, BeatstepColors.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (43, BeatstepColors.BEATSTEP_BUTTON_STATE_BLUE);
    }
}