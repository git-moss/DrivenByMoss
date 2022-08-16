package de.mossgrabers.controller.novation.launchcontrol.mode;

import de.mossgrabers.controller.novation.launchcontrol.LaunchControlXLConfiguration;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.WindCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Mode to select different transport commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class XLTransportMode extends AbstractMode<LaunchControlXLControlSurface, LaunchControlXLConfiguration, ITrack>
{
    private boolean                                                                  transportHasBeenSelected = false;

    private WindCommand<LaunchControlXLControlSurface, LaunchControlXLConfiguration> rwdCommand;
    private WindCommand<LaunchControlXLControlSurface, LaunchControlXLConfiguration> fwdCommand;
    private PlayCommand<LaunchControlXLControlSurface, LaunchControlXLConfiguration> playCommand;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public XLTransportMode (final LaunchControlXLControlSurface surface, final IModel model)
    {
        super ("Select Device Page", surface, model);

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

        this.transportHasBeenSelected = true;

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
    public void onActivate ()
    {
        super.onActivate ();

        this.transportHasBeenSelected = false;
    }


    /**
     * Has a transport function been executed during the last active phase of the mode?
     *
     * @return True if a page has been selected
     */
    public boolean hasTransportBeenSelected ()
    {
        return this.transportHasBeenSelected;
    }
}
