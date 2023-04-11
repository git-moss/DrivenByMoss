// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.sl.mode;

import de.mossgrabers.controller.novation.sl.SLConfiguration;
import de.mossgrabers.controller.novation.sl.controller.SLControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Frame selection mode.
 *
 * @author Jürgen Moßgraber
 */
public class FrameMode extends AbstractParameterMode<SLControlSurface, SLConfiguration, IItem>
{
    private static final String [] COMMANDS =
    {
        "Arrange ",
        "  Mix   ",
        "  Edit  ",
        "NoteEdit",
        "Automate",
        " Device ",
        "  Mixer ",
        "   Full "
    };


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public FrameMode (final SLControlSurface surface, final IModel model)
    {
        super ("Frame", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        switch (index)
        {
            case 0:
                this.model.getApplication ().setPanelLayout ("ARRANGE");
                break;

            case 1:
                this.model.getApplication ().setPanelLayout ("MIX");
                break;

            case 2:
                this.model.getApplication ().setPanelLayout ("EDIT");
                break;

            case 3:
                this.model.getApplication ().toggleNoteEditor ();
                break;

            case 4:
                this.model.getApplication ().toggleAutomationEditor ();
                break;

            case 5:
                this.model.getApplication ().toggleDevices ();
                break;

            case 6:
                this.model.getApplication ().toggleMixer ();
                break;

            case 7:
                this.model.getApplication ().toggleFullScreen ();
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ().clearRow (0).clearRow (1);
        d.setBlock (0, 0, "Layouts:").setCell (0, 3, "Panels:");
        for (int i = 0; i < COMMANDS.length; i++)
            d.setCell (1, i, COMMANDS[i]);
        d.done (0).done (1);
    }
}
