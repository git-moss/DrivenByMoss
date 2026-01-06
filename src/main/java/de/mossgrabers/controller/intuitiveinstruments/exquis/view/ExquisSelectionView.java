// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.intuitiveinstruments.exquis.view;

import de.mossgrabers.controller.intuitiveinstruments.exquis.ExquisConfiguration;
import de.mossgrabers.controller.intuitiveinstruments.exquis.controller.ExquisColorManager;
import de.mossgrabers.controller.intuitiveinstruments.exquis.controller.ExquisControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IDeviceBank;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.scale.Scales;


/**
 * The selection view to select tracks, devices and parameter pages.
 *
 * @author Jürgen Moßgraber
 */
public class ExquisSelectionView extends AbstractView<ExquisControlSurface, ExquisConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ExquisSelectionView (final ExquisControlSurface surface, final IModel model)
    {
        super ("Selection", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.surface.configureDeveloperMode (ExquisControlSurface.DEV_MODE_FULL);

        super.onActivate ();
        this.surface.forceFlush ();
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();
        final ITrackBank trackBank = this.model.getTrackBank ();
        final IParameterBank parameterBank = this.getParameterBank ();
        final IParameterPageBank pageBank = parameterBank.getPageBank ();

        // Parameter page selection
        final int selectedItemIndex = pageBank.getSelectedItemIndex ();
        for (int i = 0; i < pageBank.getPageSize (); i++)
        {
            final String pageName = pageBank.getItem (i);
            String colorID = IPadGrid.GRID_OFF;
            if (pageName != null && !pageName.isBlank ())
                colorID = DAWColor.getColorID (selectedItemIndex == i ? ColorEx.WHITE : ColorEx.GREEN);
            padGrid.light (36 + i, colorID);
        }

        // Device selection
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final IDeviceBank deviceBank = cursorDevice.getDeviceBank ();
        final boolean showCursorDevice = !this.surface.getModeManager ().isActive (Modes.PROJECT);
        for (int i = 0; i < deviceBank.getPageSize (); i++)
        {
            final IDevice device = deviceBank.getItem (i);
            String colorID = IPadGrid.GRID_OFF;
            if (showCursorDevice && device.doesExist ())
                colorID = DAWColor.getColorID (i == cursorDevice.getIndex () ? ColorEx.WHITE : ColorEx.PINK);
            padGrid.light (36 + 22 + i, colorID);
        }

        // Unused
        for (int i = 44; i < 55; i++)
            padGrid.light (36 + i, IPadGrid.GRID_OFF);

        // Track selection and navigation
        padGrid.light (91, trackBank.canScrollPageBackwards () ? ExquisColorManager.DARK_GREY : ExquisColorManager.BLACK);
        padGrid.light (96, trackBank.canScrollPageForwards () ? ExquisColorManager.DARK_GREY : ExquisColorManager.BLACK);
        for (int i = 0; i < 4; i++)
        {
            final ITrack track = trackBank.getItem (i);
            padGrid.light (92 + i, track.doesExist () ? DAWColor.getColorID (track.getColor ()) : ColorManager.BUTTON_STATE_OFF, track.isSelected () ? ColorManager.BUTTON_STATE_ON : null, true);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final ITrackBank trackBank = this.model.getTrackBank ();

        switch (note)
        {
            // The parameter pages
            case 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57:
                final IParameterBank parameterBank = this.getParameterBank ();
                final IParameterPageBank pageBank = parameterBank.getPageBank ();
                final String pageName = pageBank.getItem (note - 36);
                if (pageName != null)
                {
                    pageBank.selectPage (note - 36);
                    this.mvHelper.notifySelectedParameterPage (parameterBank, "");
                }
                break;

            // The device pages
            case 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79:
                final IDeviceBank deviceBank = this.model.getCursorDevice ().getDeviceBank ();
                final boolean showCursorDevice = !this.surface.getModeManager ().isActive (Modes.PROJECT);
                if (showCursorDevice)
                {
                    deviceBank.getItem (note - 58).select ();
                    this.mvHelper.notifySelectedDevice ();
                }
                break;

            // The top row
            case 91:
                trackBank.selectPreviousPage ();
                this.mvHelper.notifySelectedTrack ();
                break;
            case 92, 93, 94, 95:
                trackBank.getItem (note - 92).select ();
                this.mvHelper.notifySelectedTrack ();
                break;
            case 96:
                trackBank.selectNextPage ();
                this.mvHelper.notifySelectedTrack ();
                break;
        }
    }


    private IParameterBank getParameterBank ()
    {
        switch (this.surface.getModeManager ().getActiveID ())
        {
            case Modes.PROJECT:
                return this.model.getProject ().getParameterBank ();

            case Modes.TRACK_DETAILS:
                return this.model.getCursorTrack ().getParameterBank ();

            default:
                return this.model.getCursorDevice ().getParameterBank ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        return this.colorManager.getColorIndex (IPadGrid.GRID_OFF);
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.surface.scheduleTask ( () -> this.delayedUpdateNoteMapping (Scales.getEmptyMatrix ()), 100);
    }
}
