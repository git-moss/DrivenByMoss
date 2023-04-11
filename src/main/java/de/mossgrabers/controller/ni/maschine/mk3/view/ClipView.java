// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.view;

import de.mossgrabers.controller.ni.maschine.core.MaschineColorManager;
import de.mossgrabers.controller.ni.maschine.mk3.MaschineConfiguration;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Optional;


/**
 * The Clip (session) view.
 *
 * @author Jürgen Moßgraber
 */
public class ClipView extends BaseView
{
    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public ClipView (final MaschineControlSurface surface, final IModel model)
    {
        super ("Clip", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    protected void executeFunction (final int padIndex, final ButtonEvent buttonEvent)
    {
        final boolean isDown = buttonEvent == ButtonEvent.DOWN;

        final Optional<ITrack> track = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (track.isEmpty ())
            return;
        final ISlot slot = track.get ().getSlotBank ().getItem (padIndex);
        final MaschineConfiguration configuration = this.surface.getConfiguration ();

        if (isDown)
        {
            if (this.isButtonCombination (ButtonID.DUPLICATE))
            {
                if (track.get ().doesExist ())
                    slot.duplicate ();
                return;
            }

            // Stop clip
            if (this.isButtonCombination (ButtonID.CLIP))
            {
                track.get ().stop ();
                return;
            }

            // Browse for clips
            if (this.isButtonCombination (ButtonID.BROWSE))
            {
                if (track.get ().doesExist ())
                    this.model.getBrowser ().replace (slot);
                return;
            }

            // Delete selected clip
            if (this.isButtonCombination (ButtonID.DELETE))
            {
                slot.remove ();
                return;
            }

            if (configuration.isSelectClipOnLaunch ())
                slot.select ();
        }

        if (!track.get ().isRecArm () || slot.hasContent ())
        {
            slot.launch (isDown, this.surface.isSelectPressed ());
            return;
        }

        if (isDown)
        {
            switch (configuration.getActionForRecArmedPad ())
            {
                case 0:
                    this.model.recordNoteClip (track.get (), slot);
                    break;

                case 1:
                    final int lengthInBeats = configuration.getNewClipLenghthInBeats (this.model.getTransport ().getQuartersPerMeasure ());
                    this.model.createNoteClip (track.get (), slot, lengthInBeats, true);
                    break;

                case 2:
                default:
                    // Do nothing
                    break;
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();

        final Optional<ITrack> selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (selectedTrack.isEmpty ())
            return;
        final ISlotBank slotBank = selectedTrack.get ().getSlotBank ();
        for (int i = 0; i < 16; i++)
        {
            final ISlot item = slotBank.getItem (i);
            final int x = i % 4;
            final int y = 3 - i / 4;
            if (item.doesExist ())
            {
                if (item.isRecordingQueued ())
                    padGrid.lightEx (x, y, MaschineColorManager.COLOR_RED_LO);
                else if (item.isRecording ())
                    padGrid.lightEx (x, y, MaschineColorManager.COLOR_RED);
                else if (item.isPlayingQueued ())
                    padGrid.lightEx (x, y, MaschineColorManager.COLOR_GREEN_LO);
                else if (item.isPlaying ())
                    padGrid.lightEx (x, y, MaschineColorManager.COLOR_GREEN);
                else if (item.isStopQueued ())
                    padGrid.lightEx (x, y, MaschineColorManager.COLOR_GREEN_LO);
                else
                    padGrid.lightEx (x, y, DAWColor.getColorID (item.getColor ()));
            }
            else
                padGrid.lightEx (x, y, AbstractFeatureGroup.BUTTON_COLOR_OFF);
        }
    }
}