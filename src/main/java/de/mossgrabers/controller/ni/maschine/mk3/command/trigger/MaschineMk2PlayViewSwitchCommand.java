// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.command.trigger;

import de.mossgrabers.controller.ni.maschine.mk3.MaschineConfiguration;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.controller.ni.maschine.mk3.view.PlayView;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.controller.display.IDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Command for switching between Drum, Keyboard and Chords view as well as selecting the
 * configuration modes with Shift.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineMk2PlayViewSwitchCommand extends AbstractTriggerCommand<MaschineControlSurface, MaschineConfiguration>
{
    private final PadModeCommand  drumViewCommand;
    private final KeyboardCommand playViewCommand;
    private final ViewManager     viewManager;
    private final PlayView        playView;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public MaschineMk2PlayViewSwitchCommand (final IModel model, final MaschineControlSurface surface)
    {
        super (model, surface);

        this.drumViewCommand = new PadModeCommand (null, model, surface);
        this.playViewCommand = new KeyboardCommand (model, surface);
        this.viewManager = this.surface.getViewManager ();
        this.playView = (PlayView) this.viewManager.get (Views.PLAY);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        switch (this.viewManager.getActiveID ())
        {
            case Views.DRUM:
                this.playViewCommand.executeNormal (event);
                break;

            case Views.PLAY:
                if (this.playView.isChordMode ())
                    this.drumViewCommand.executeNormal (event);
                this.playView.toggleChordMode ();
                break;

            default:
                this.drumViewCommand.executeNormal (event);
                break;
        }

        final IDisplay display = this.surface.getDisplay ();
        if (this.viewManager.getActiveID () == Views.DRUM)
            display.notify ("Drum");
        else if (this.playView.isChordMode ())
            display.notify ("Chord");
        else
            display.notify ("Play");
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        final Views activeIDIgnoreTemporary = this.viewManager.getActiveIDIgnoreTemporary ();
        switch (activeIDIgnoreTemporary)
        {
            case Views.DRUM:
                if (modeManager.isActive (Modes.PLAY_OPTIONS))
                    modeManager.restore ();
                else
                    modeManager.setActive (Modes.PLAY_OPTIONS);
                break;

            case Views.PLAY:
                if (modeManager.isActive (Modes.SCALES))
                    modeManager.restore ();
                else
                    modeManager.setTemporary (Modes.SCALES);
                break;

            default:
                // No function in other modes
                break;
        }
    }
}
