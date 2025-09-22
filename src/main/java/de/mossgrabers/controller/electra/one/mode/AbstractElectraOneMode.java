// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.mode;

import java.util.Optional;

import de.mossgrabers.controller.electra.one.ElectraOneConfiguration;
import de.mossgrabers.controller.electra.one.controller.ElectraOneColorManager;
import de.mossgrabers.controller.electra.one.controller.ElectraOneControlSurface;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.mode.track.DefaultTrackMode;


/**
 * Abstract base mode for all Electra One modes.
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractElectraOneMode extends DefaultTrackMode<ElectraOneControlSurface, ElectraOneConfiguration>
{
    protected final PlayCommand<ElectraOneControlSurface, ElectraOneConfiguration> playCommand;
    protected final PageCache                                                      pageCache;


    /**
     * Constructor.
     *
     * @param pageIndex The index of the preset page where the mode is located
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     */
    protected AbstractElectraOneMode (final int pageIndex, final String name, final ElectraOneControlSurface surface, final IModel model)
    {
        super (name, surface, model, false, ElectraOneControlSurface.KNOB_IDS);

        this.playCommand = new PlayCommand<> (model, surface);
        this.pageCache = new PageCache (pageIndex, surface);
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
        if (index < 0 || index >= 36)
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
