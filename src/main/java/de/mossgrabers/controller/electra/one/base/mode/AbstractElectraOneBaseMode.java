// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.base.mode;

import java.util.List;
import java.util.Optional;

import de.mossgrabers.controller.electra.one.base.AbstractElectraOneConfiguration;
import de.mossgrabers.controller.electra.one.base.controller.AbstractElectraOneControlSurface;
import de.mossgrabers.controller.electra.one.base.controller.ElectraOneColorManager;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.mode.track.DefaultTrackMode;


/**
 * Abstract base mode for all Electra One modes.
 *
 * @param <S> The specific type of the control surface
 * @param <C> The specific type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractElectraOneBaseMode<S extends AbstractElectraOneControlSurface<C>, C extends AbstractElectraOneConfiguration> extends DefaultTrackMode<S, C>
{
    protected final PageCache<S, C> pageCache;


    /**
     * Constructor.
     *
     * @param pageIndex The index of the preset page where the mode is located
     * @param name The name of the mode
     * @param numRows The number of rows to cache
     * @param numCols The number of columns to cache
     * @param surface The control surface
     * @param model The model
     * @param controls The IDs of the knobs or faders to control this mode
     * @param groupOffset The ID of the first group
     */
    protected AbstractElectraOneBaseMode (final int pageIndex, final String name, final S surface, final IModel model, final int numRows, final int numCols, final List<ContinuousID> controls, final int groupOffset)
    {
        super (name, surface, model, false, controls);

        this.pageCache = new PageCache<> (pageIndex, numRows, numCols, surface, groupOffset);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.pageCache.reset ();

        super.onActivate ();
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        return ElectraOneColorManager.COLOR_BUTTON_STATE_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.setTouchedKnob (index, isTouched);
        this.getParameterProvider ().get (index).touchValue (isTouched);
    }


    /**
     * De-/activate editing mode for knobs.
     *
     * @param controlID The ID of the knob control on the page
     * @param isTouched True if touched
     */
    public void setEditing (final int controlID, final boolean isTouched)
    {
        final int index = this.pageCache.getIndex (controlID);
        if (index < 0 || index >= this.pageCache.getPageSize ())
        {
            this.surface.getHost ().error ("Control ID out of bounds: " + controlID);
            return;
        }
        this.onKnobTouch (index, isTouched);
    }


    /**
     * Get the name of the active device.
     *
     * @return The name or an empty optional
     */
    public Optional<String> getActiveDeviceName ()
    {
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        return cursorDevice.doesExist () ? Optional.of (cursorDevice.getName ()) : Optional.empty ();
    }
}
