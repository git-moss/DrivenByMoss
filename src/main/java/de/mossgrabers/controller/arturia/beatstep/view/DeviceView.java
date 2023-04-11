// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.arturia.beatstep.view;

import de.mossgrabers.controller.arturia.beatstep.BeatstepConfiguration;
import de.mossgrabers.controller.arturia.beatstep.controller.BeatstepColorManager;
import de.mossgrabers.controller.arturia.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.bank.IChannelBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.featuregroup.AbstractView;

import java.util.Optional;


/**
 * The Device view.
 *
 * @author Jürgen Moßgraber
 */
public class DeviceView extends AbstractView<BeatstepControlSurface, BeatstepConfiguration> implements BeatstepView
{
    private final TrackEditing extensions;
    private boolean            isLayer;


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
    public void onKnob (final int index, final int value, final boolean isTurnedRight)
    {
        final ICursorDevice cd = this.model.getCursorDevice ();
        if (index < 8)
        {
            this.extensions.onTrackKnob (index, value, isTurnedRight);
            return;
        }

        cd.getParameterBank ().getItem (index - 8).changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0 || !this.model.hasSelectedDevice ())
            return;

        final ICursorDevice cd = this.model.getCursorDevice ();
        final IChannelBank<ILayer> bank = cd.getLayerBank ();
        final Optional<ILayer> sel = bank.getSelectedItem ();

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
                    int index = 0;
                    if (sel.isPresent ())
                    {
                        final int idx = sel.get ().getIndex ();
                        index = idx - 1;
                    }
                    if (index >= 0)
                        bank.getItem (index).select ();
                }
                else
                    cd.selectPrevious ();
                break;

            // Device Right
            case 2:
                if (this.isLayer)
                {
                    final int index = sel.isEmpty () ? 0 : sel.get ().getIndex () + 1;
                    bank.getItem (index > 7 ? 7 : index).select ();
                }
                else
                    cd.selectNext ();
                break;

            // Enter layer
            case 3:
                if (!cd.hasLayers ())
                    return;
                if (this.isLayer)
                {
                    if (sel.isPresent ())
                        sel.get ().enter ();
                }
                else if (sel.isEmpty ())
                    bank.getItem (0).select ();

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
                cd.getParameterBank ().scrollBackwards ();
                break;

            // Param bank page up
            case 7:
                cd.getParameterBank ().scrollForwards ();
                break;

            default:
                cd.getParameterPageBank ().selectPage (note - 36 - 8);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final ICursorDevice cd = this.model.getCursorDevice ();
        final IParameterPageBank parameterPageBank = cd.getParameterPageBank ();
        final int selectedItemIndex = parameterPageBank.getSelectedItemIndex ();
        final IPadGrid padGrid = this.surface.getPadGrid ();
        for (int i = 0; i < parameterPageBank.getPageSize (); i++)
            padGrid.light (44 + i, i == selectedItemIndex ? BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE : BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (36, cd.isEnabled () ? BeatstepColorManager.BEATSTEP_BUTTON_STATE_RED : BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (37, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (38, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (39, BeatstepColorManager.BEATSTEP_BUTTON_STATE_RED);
        padGrid.light (40, BeatstepColorManager.BEATSTEP_BUTTON_STATE_RED);
        padGrid.light (41, BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (42, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (43, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
    }
}