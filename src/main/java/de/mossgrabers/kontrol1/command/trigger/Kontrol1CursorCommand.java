// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.kontrol1.command.trigger;

import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.kontrol1.Kontrol1Configuration;
import de.mossgrabers.kontrol1.controller.Kontrol1ControlSurface;
import de.mossgrabers.kontrol1.mode.Modes;
import de.mossgrabers.kontrol1.mode.device.ParamsMode;


/**
 * Command for cursor arrow keys.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol1CursorCommand extends de.mossgrabers.framework.command.trigger.CursorCommand<Kontrol1ControlSurface, Kontrol1Configuration>
{
    /**
     * Constructor.
     *
     * @param direction The direction of the kontrol1ed cursor arrow
     * @param model The model
     * @param surface The surface
     */
    public Kontrol1CursorCommand (final Direction direction, final IModel model, final Kontrol1ControlSurface surface)
    {
        super (direction, model, surface);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateArrowStates ()
    {
        final IChannelBank tb = this.model.getCurrentTrackBank ();
        this.canScrollUp = tb.canScrollScenesUp ();
        this.canScrollDown = tb.canScrollScenesDown ();

        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActiveMode (Modes.MODE_PARAMS))
        {
            final ParamsMode mode = (ParamsMode) modeManager.getActiveMode ();
            this.canScrollLeft = mode.canSelectPreviousPage ();
            this.canScrollRight = mode.canSelectNextPage ();
            return;
        }

        if (modeManager.isActiveMode (Modes.MODE_BROWSER))
        {
            final IBrowser browser = this.model.getBrowser ();
            final int index = browser.getSelectedContentTypeIndex ();
            this.canScrollLeft = index > 0;
            this.canScrollRight = index < browser.getContentTypeNames ().length - 1;
            return;
        }

        final ITrack sel = tb.getSelectedTrack ();
        final int selIndex = sel != null ? sel.getIndex () : -1;
        this.canScrollLeft = selIndex > 0 || tb.canScrollTracksUp ();
        this.canScrollRight = selIndex >= 0 && selIndex < 7 && tb.getTrack (selIndex + 1).doesExist () || tb.canScrollTracksDown ();
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollLeft ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActiveMode (Modes.MODE_PARAMS))
        {
            final ParamsMode paramsMode = (ParamsMode) modeManager.getActiveMode ();
            if (this.surface.isShiftPressed ())
                paramsMode.selectPreviousPageBank ();
            else
                paramsMode.selectPreviousPage ();
            return;
        }

        if (modeManager.isActiveMode (Modes.MODE_BROWSER))
        {
            this.model.getBrowser ().previousContentType ();
            return;
        }

        if (modeManager.isActiveMode (Modes.MODE_SCALE))
        {
            this.model.getScales ().prevScale ();
            this.updateScalePreferences ();
            return;
        }

        this.scrollTracksLeft ();
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollRight ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActiveMode (Modes.MODE_PARAMS))
        {
            final ParamsMode activeMode = (ParamsMode) modeManager.getActiveMode ();
            if (this.surface.isShiftPressed ())
                activeMode.selectNextPageBank ();
            else
                activeMode.selectNextPage ();
            return;
        }

        if (modeManager.isActiveMode (Modes.MODE_BROWSER))
        {
            this.model.getBrowser ().nextContentType ();
            return;
        }

        if (modeManager.isActiveMode (Modes.MODE_SCALE))
        {
            this.model.getScales ().nextScale ();
            this.updateScalePreferences ();
            return;
        }

        this.scrollTracksRight ();
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollUp ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActiveMode (Modes.MODE_PARAMS))
        {
            if (this.surface.isShiftPressed ())
                this.model.getCursorDevice ().selectNextBank ();
            else
                this.model.getCursorDevice ().selectNext ();
            return;
        }

        if (modeManager.isActiveMode (Modes.MODE_BROWSER))
            return;

        if (modeManager.isActiveMode (Modes.MODE_SCALE))
        {
            final Scales scales = this.model.getScales ();
            scales.setScaleOffset (scales.getScaleOffset () + 1);
            this.updateScalePreferences ();
            return;
        }

        this.scrollTrackBankRight (null, 8);
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollDown ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActiveMode (Modes.MODE_PARAMS))
        {
            if (this.surface.isShiftPressed ())
                this.model.getCursorDevice ().selectPreviousBank ();
            else
                this.model.getCursorDevice ().selectPrevious ();
            return;
        }

        if (modeManager.isActiveMode (Modes.MODE_BROWSER))
            return;

        if (modeManager.isActiveMode (Modes.MODE_SCALE))
        {
            final Scales scales = this.model.getScales ();
            scales.setScaleOffset (scales.getScaleOffset () - 1);
            this.updateScalePreferences ();
            return;
        }

        this.scrollTrackBankLeft (null, 8);
    }


    private void updateScalePreferences ()
    {
        final Kontrol1Configuration config = this.surface.getConfiguration ();
        final Scales scales = this.model.getScales ();
        config.setScale (scales.getScale ().getName ());
        config.setScaleBase (Scales.BASES[scales.getScaleOffset ()]);
        config.setScaleInKey (!scales.isChromatic ());
    }
}
