// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.mode;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.push.controller.DisplayMessage;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.controller.PushDisplay;


/**
 * Editing the default length of new clips.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FixedMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public FixedMode (final PushControlSurface surface, final Model model)
    {
        super (surface, model);
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        this.surface.getConfiguration ().setNewClipLength (index);
        this.surface.getModeManager ().restoreMode ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final ColorManager colorManager = this.model.getColorManager ();
        final Configuration configuration = this.surface.getConfiguration ();
        for (int i = 0; i < 8; i++)
            this.surface.updateButton (20 + i, colorManager.getColor (configuration.getNewClipLength () == i ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON));
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ();
        final int newClipLength = this.surface.getConfiguration ().getNewClipLength ();
        d.clear ().setBlock (1, 0, "New Clip Length:");
        for (int i = 0; i < 8; i++)
            d.setCell (3, i, (newClipLength == i ? PushDisplay.RIGHT_ARROW : "") + AbstractConfiguration.NEW_CLIP_LENGTH_VALUES[i]);
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final int newClipLength = this.surface.getConfiguration ().getNewClipLength ();
        final DisplayMessage message = ((PushDisplay) this.surface.getDisplay ()).createMessage ();
        for (int i = 0; i < 8; i++)
            message.addOptionElement ("", "", false, i == 0 ? "New Clip Length" : "", AbstractConfiguration.NEW_CLIP_LENGTH_VALUES[i], newClipLength == i, false);
        message.send ();
    }
}
