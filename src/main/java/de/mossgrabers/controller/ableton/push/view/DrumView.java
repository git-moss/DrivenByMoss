// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.view;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IDrumDevice;
import de.mossgrabers.framework.daw.data.IDrumPad;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.featuregroup.IExpressionView;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;
import de.mossgrabers.framework.view.sequencer.AbstractDrumView;


/**
 * The Drum view.
 *
 * @author Jürgen Moßgraber
 */
public class DrumView extends AbstractDrumView<PushControlSurface, PushConfiguration> implements IExpressionView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumView (final PushControlSurface surface, final IModel model)
    {
        super (Views.NAME_DRUM, surface, model, 4, 4, true);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNoteLongPress (final int note)
    {
        if (!this.isActive ())
            return;

        final int index = note - DRUM_START_KEY;
        this.surface.getButton (ButtonID.get (ButtonID.PAD1, index)).setConsumed ();

        final int y = index / GRID_COLUMNS;

        // Sequencer steps?
        if (y < this.playRows)
            return;

        final int x = index % GRID_COLUMNS;
        final int stepX = GRID_COLUMNS * (this.allRows - 1 - y) + x;
        final int stepY = this.scales.getDrumOffset () + this.getSelectedPad ();

        final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), stepX, stepY);
        this.editNote (this.getClip (), notePosition, false);
    }


    /** {@inheritDoc} */
    @Override
    public synchronized void handleSelectButton (final int playedPad)
    {
        // Do we have drum pads?
        final IDrumDevice primary = this.model.getDrumDevice ();
        if (!primary.hasDrumPads ())
            return;
        final ICursorDevice cd = this.model.getCursorDevice ();
        final boolean isNested = cd.isNested ();
        if (isNested)
        {
            // We have to move up to compare the main drum devices
            cd.selectParent ();
        }

        // Can only scroll to the channel if the cursor device is the primary device
        if (primary.getPosition () != cd.getPosition ())
            return;

        // Align the primary and cursor device drum bank view
        final IDrumPadBank drumPadBank = primary.getDrumPadBank ();
        final int scrollPos = drumPadBank.getScrollPosition ();
        final IDrumPadBank cdDrumPadBank = cd.getDrumPadBank ();
        final int pageSize = cdDrumPadBank.getPageSize ();
        final int adjustedPage = playedPad / pageSize * pageSize;
        cdDrumPadBank.scrollTo (scrollPos + adjustedPage, false);

        // Do not reselect
        final IDrumPad drumPad = drumPadBank.getItem (playedPad);
        if (drumPad.isSelected ())
        {
            // If the instrument of the pad was selected for editing, try to select it again
            if (isNested)
            {
                final IDrumPad selectedItem = cdDrumPadBank.getItem (playedPad % pageSize);
                if (selectedItem != null)
                    selectedItem.enter ();
            }
            return;
        }

        // Only activate layer mode if not one of the layer modes is already active and browser is
        // off
        final ModeManager modeManager = this.surface.getModeManager ();
        if (!Modes.isLayerMode (modeManager.getActiveID ()) && !this.model.getBrowser ().isActive ())
            modeManager.setActive (Modes.DEVICE_LAYER);

        drumPad.select ();

        this.updateNoteMapping ();
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        if (ButtonID.isSceneButton (buttonID) && this.surface.isPressed (ButtonID.REPEAT))
            return NoteRepeatSceneHelper.getButtonColorID (this.surface, buttonID);
        return super.getButtonColorID (buttonID);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN)
            return;

        final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        if (this.surface.isPressed (ButtonID.REPEAT))
        {
            NoteRepeatSceneHelper.handleNoteRepeatSelection (this.surface, 7 - index);
            return;
        }

        super.onButton (buttonID, event, velocity);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final NotePosition notePosition, final int velocity)
    {
        final boolean isSelectPressed = this.surface.isSelectPressed ();

        if (this.surface.isShiftPressed ())
        {
            if (velocity > 0)
                this.handleSequencerAreaRepeatOperator (clip, notePosition, velocity, !isSelectPressed);
            return true;
        }

        if (isSelectPressed)
        {
            this.editNote (clip, notePosition, true);
            return true;
        }

        return super.handleSequencerAreaButtonCombinations (clip, notePosition, velocity);
    }
}