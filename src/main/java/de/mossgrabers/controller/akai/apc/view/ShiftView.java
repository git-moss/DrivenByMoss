// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apc.view;

import de.mossgrabers.controller.akai.apc.APCConfiguration;
import de.mossgrabers.controller.akai.apc.command.trigger.APCBrowserCommand;
import de.mossgrabers.controller.akai.apc.controller.APCColorManager;
import de.mossgrabers.controller.akai.apc.controller.APCControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractShiftView;
import de.mossgrabers.framework.view.Views;


/**
 * Provides additional features/functions on the grid.
 *
 * @author Jürgen Moßgraber
 */
public class ShiftView extends AbstractShiftView<APCControlSurface, APCConfiguration>
{
    private static final Views []   VIEW_IDS  =
    {
        Views.SESSION,
        Views.PLAY,
        Views.DRUM,
        Views.SEQUENCER,
        Views.RAINDROPS
    };

    private static final int []     TRANSLATE =
    {
        0,
        2,
        4,
        6,
        1,
        3,
        5,
        -1,
        -1,
        10,
        8,
        -1,
        11,
        9,
        7,
        -1
    };

    private final APCBrowserCommand browserCommand;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ShiftView (final APCControlSurface surface, final IModel model)
    {
        super ("Shift", surface, model);

        this.browserCommand = new APCBrowserCommand (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();
        for (int i = 7; i < 64; i++)
            padGrid.light (36 + i, APCColorManager.APC_COLOR_BLACK);

        // Add tracks
        for (int i = 0; i < 3; i++)
            padGrid.light (36 + 32 + i, APCColorManager.COLOR_KEY_WHITE);
        padGrid.light (36 + 35, APCColorManager.COLOR_KEY_SELECTED);
        padGrid.light (36 + 36, APCColorManager.COLOR_KEY_SELECTED);
        padGrid.light (36 + 38, APCColorManager.COLOR_KEY_BLACK);
        padGrid.light (36 + 39, APCColorManager.COLOR_KEY_BLACK);

        // Draw the keyboard
        final int scaleOffset = this.model.getScales ().getScaleOffsetIndex ();
        // 0'C', 1'G', 2'D', 3'A', 4'E', 5'B', 6'F', 7'Bb', 8'Eb', 9'Ab', 10'Db', 11'Gb'
        padGrid.light (36, scaleOffset == 0 ? APCColorManager.COLOR_KEY_SELECTED : APCColorManager.COLOR_KEY_WHITE);
        padGrid.light (36 + 1, scaleOffset == 2 ? APCColorManager.COLOR_KEY_SELECTED : APCColorManager.COLOR_KEY_WHITE);
        padGrid.light (36 + 2, scaleOffset == 4 ? APCColorManager.COLOR_KEY_SELECTED : APCColorManager.COLOR_KEY_WHITE);
        padGrid.light (36 + 3, scaleOffset == 6 ? APCColorManager.COLOR_KEY_SELECTED : APCColorManager.COLOR_KEY_WHITE);
        padGrid.light (36 + 4, scaleOffset == 1 ? APCColorManager.COLOR_KEY_SELECTED : APCColorManager.COLOR_KEY_WHITE);
        padGrid.light (36 + 5, scaleOffset == 3 ? APCColorManager.COLOR_KEY_SELECTED : APCColorManager.COLOR_KEY_WHITE);
        padGrid.light (36 + 6, scaleOffset == 5 ? APCColorManager.COLOR_KEY_SELECTED : APCColorManager.COLOR_KEY_WHITE);
        padGrid.light (36 + 9, scaleOffset == 10 ? APCColorManager.COLOR_KEY_SELECTED : APCColorManager.COLOR_KEY_BLACK);
        padGrid.light (36 + 10, scaleOffset == 8 ? APCColorManager.COLOR_KEY_SELECTED : APCColorManager.COLOR_KEY_BLACK);
        padGrid.light (36 + 12, scaleOffset == 11 ? APCColorManager.COLOR_KEY_SELECTED : APCColorManager.COLOR_KEY_BLACK);
        padGrid.light (36 + 13, scaleOffset == 9 ? APCColorManager.COLOR_KEY_SELECTED : APCColorManager.COLOR_KEY_BLACK);
        padGrid.light (36 + 14, scaleOffset == 7 ? APCColorManager.COLOR_KEY_SELECTED : APCColorManager.COLOR_KEY_BLACK);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        this.setWasUsed ();

        final int index = note - 36;
        if (index > 15)
        {
            switch (index)
            {
                case 32:
                    this.model.getTrackBank ().addChannel (ChannelType.INSTRUMENT);
                    break;
                case 33:
                    this.model.getTrackBank ().addChannel (ChannelType.AUDIO);
                    break;
                case 34:
                    this.model.getApplication ().addEffectTrack ();
                    break;
                case 35, 36:
                    this.browserCommand.startBrowser (true, index == 35);
                    break;
                case 38:
                    this.model.getApplication ().undo ();
                    break;
                case 39:
                    this.model.getApplication ().redo ();
                    break;
                default:
                    // Not used
                    break;
            }
            return;
        }

        // Scale Base note selection
        final int pos = TRANSLATE[index];
        if (pos == -1)
            return;
        this.model.getScales ().setScaleOffsetByIndex (pos);
        this.surface.getConfiguration ().setScaleBase (Scales.BASES.get (pos));
        this.surface.getDisplay ().notify (Scales.BASES.get (pos));
        this.surface.getViewManager ().getActive ().updateNoteMapping ();
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN)
            return;

        final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();

        final ViewManager viewManager = this.surface.getViewManager ();
        if (viewManager.getActiveIDIgnoreTemporary () == Views.SESSION && VIEW_IDS[index] == Views.SESSION)
            ((SessionView) viewManager.get (Views.SESSION)).toggleBirdsEyeView ();
        else
            viewManager.setActive (VIEW_IDS[index]);
        this.surface.getDisplay ().notify (viewManager.get (VIEW_IDS[index]).getName ());

        if (Views.SESSION.equals (VIEW_IDS[index]))
            return;

        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (cursorTrack.doesExist ())
            viewManager.setPreferredView (cursorTrack.getPosition (), VIEW_IDS[index]);
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        final Views previousViewId = this.surface.getViewManager ().getActiveIDIgnoreTemporary ();
        if (buttonID == ButtonID.SCENE1)
            return Views.SESSION.equals (previousViewId) ? APCColorManager.COLOR_VIEW_SELECTED : APCColorManager.COLOR_VIEW_UNSELECTED;
        if (buttonID == ButtonID.SCENE2)
            return Views.PLAY.equals (previousViewId) ? APCColorManager.COLOR_VIEW_SELECTED : APCColorManager.COLOR_VIEW_UNSELECTED;
        if (buttonID == ButtonID.SCENE3)
            return Views.DRUM.equals (previousViewId) ? APCColorManager.COLOR_VIEW_SELECTED : APCColorManager.COLOR_VIEW_UNSELECTED;
        if (buttonID == ButtonID.SCENE4)
            return Views.SEQUENCER.equals (previousViewId) ? APCColorManager.COLOR_VIEW_SELECTED : APCColorManager.COLOR_VIEW_UNSELECTED;
        if (buttonID == ButtonID.SCENE5)
            return Views.RAINDROPS.equals (previousViewId) ? APCColorManager.COLOR_VIEW_SELECTED : APCColorManager.COLOR_VIEW_UNSELECTED;
        return ColorManager.BUTTON_STATE_OFF;
    }
}