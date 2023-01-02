// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.view;

import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Edit remote parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceView extends AbstractFaderView
{
    private final ICursorDevice cursorDevice;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DeviceView (final LaunchpadControlSurface surface, final IModel model)
    {
        super ("Device", surface, model);

        this.cursorDevice = this.model.getCursorDevice ();
    }


    /** {@inheritDoc} */
    @Override
    public void setupFader (final int index)
    {
        this.surface.setupFader (index, LaunchpadColorManager.DAW_INDICATOR_COLORS.get (index).intValue (), false);

        // Prevent issue with catch mode by initializing fader value at setup
        this.onValueKnob (index, this.getFaderValue (index));
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        // Set immediately to prevent issue with relative scaling mode
        this.cursorDevice.getParameterBank ().getItem (index).setValueImmediatly (value);
    }


    /** {@inheritDoc} */
    @Override
    protected int getFaderValue (final int index)
    {
        return this.cursorDevice.getParameterBank ().getItem (index).getValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IParameterBank parameterBank = this.cursorDevice.getParameterBank ();
        for (int i = 0; i < 8; i++)
            this.surface.setFaderValue (i, parameterBank.getItem (i).getValue ());
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN || !this.cursorDevice.doesExist ())
            return;

        switch (buttonID)
        {
            case SCENE1:
                this.cursorDevice.toggleEnabledState ();
                break;

            case SCENE3:
                this.cursorDevice.toggleParameterPageSectionVisible ();
                break;

            case SCENE4:
                this.cursorDevice.toggleExpanded ();
                break;

            case SCENE6:
                this.cursorDevice.toggleWindowOpen ();
                break;

            case SCENE8:
                final boolean isPinned = !this.cursorDevice.isPinned ();
                this.cursorDevice.setPinned (isPinned);
                this.model.getCursorTrack ().setPinned (isPinned);
                this.mvHelper.delayDisplay ( () -> this.cursorDevice.getName () + ": " + (this.cursorDevice.isPinned () ? "Pinned" : "Not pinned"));
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        switch (buttonID)
        {
            case SCENE1:
                return this.cursorDevice.isEnabled () ? LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;

            case SCENE3:
                if (!this.model.getHost ().supports (Capability.HAS_PARAMETER_PAGE_SECTION))
                    return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
                return this.cursorDevice.isParameterPageSectionVisible () ? LaunchpadColorManager.LAUNCHPAD_COLOR_PINK_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_PINK_LO;

            case SCENE4:
                return this.cursorDevice.isExpanded () ? LaunchpadColorManager.LAUNCHPAD_COLOR_MAGENTA_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_MAGENTA_LO;

            case SCENE6:
                return this.cursorDevice.isWindowOpen () ? LaunchpadColorManager.LAUNCHPAD_COLOR_AMBER_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_AMBER_LO;

            case SCENE8:
                if (!this.model.getHost ().supports (Capability.HAS_PINNING))
                    return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
                return this.cursorDevice.isPinned () ? LaunchpadColorManager.LAUNCHPAD_COLOR_TURQUOISE_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_TURQUOISE_LO;

            default:
                // Not used
                return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
        }
    }
}
