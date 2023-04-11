package de.mossgrabers.controller.novation.launchcontrol.mode.buttons;

import de.mossgrabers.controller.novation.launchcontrol.LaunchControlXLConfiguration;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLColorManager;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.WindCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Mode to select different transport commands.
 *
 * @author Jürgen Moßgraber
 */
public class XLTransportMode extends XLTemporaryButtonMode
{
    private final WindCommand<LaunchControlXLControlSurface, LaunchControlXLConfiguration> rwdCommand;
    private final WindCommand<LaunchControlXLControlSurface, LaunchControlXLConfiguration> fwdCommand;
    private final PlayCommand<LaunchControlXLControlSurface, LaunchControlXLConfiguration> playCommand;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public XLTransportMode (final LaunchControlXLControlSurface surface, final IModel model)
    {
        super ("Transport", surface, model);

        this.rwdCommand = new WindCommand<> (model, surface, false);
        this.fwdCommand = new WindCommand<> (model, surface, true);
        this.playCommand = new PlayCommand<> (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (row != 0)
            return;

        this.setHasBeenUsed ();

        final ITransport transport = this.model.getTransport ();

        switch (index)
        {
            case 0:
                this.playCommand.executeNormal (event);
                break;
            case 1:
                if (event == ButtonEvent.DOWN)
                    transport.startRecording ();
                break;
            case 2:
                this.rwdCommand.executeNormal (event);
                break;
            case 3:
                this.fwdCommand.executeNormal (event);
                break;
            case 4:
                if (event == ButtonEvent.DOWN)
                    transport.toggleLoop ();
                break;
            case 5:
                if (event == ButtonEvent.DOWN)
                    transport.toggleMetronome ();
                break;
            case 6:
                if (event == ButtonEvent.DOWN)
                    transport.toggleWriteArrangerAutomation ();
                break;
            case 7:
                if (event == ButtonEvent.DOWN)
                {
                    this.model.getProject ().clearMute ();
                    this.model.getProject ().clearSolo ();
                }
                break;
            default:
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final int index = buttonID.ordinal () - ButtonID.ROW2_1.ordinal ();
        if (index < 0 || index >= 8)
            return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;

        final ITransport transport = this.model.getTransport ();
        switch (index)
        {
            case 0:
                return transport.isPlaying () ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_GREEN : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_GREEN_LO;
            case 1:
                return transport.isRecording () ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_RED : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_RED_LO;
            case 2, 3:
                return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_AMBER_LO;
            case 4:
                return transport.isLoop () ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_YELLOW : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_YELLOW_LO;
            case 5:
                return transport.isMetronomeOn () ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_GREEN : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_GREEN_LO;
            case 6:
                return transport.isWritingArrangerAutomation () ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_RED : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_RED_LO;
            case 7:
                return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_AMBER_LO;
            default:
                return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;
        }
    }
}
