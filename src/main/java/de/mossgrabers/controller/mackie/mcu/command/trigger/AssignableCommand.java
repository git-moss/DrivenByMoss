// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.command.trigger;

import de.mossgrabers.controller.mackie.mcu.MCUConfiguration;
import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.command.trigger.FootswitchCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for assignable functions.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class AssignableCommand extends FootswitchCommand<MCUControlSurface, MCUConfiguration>
{
    private final ModeSwitcher   switcher;
    private final MCUFlipCommand flipCommand;


    /**
     * Constructor.
     *
     * @param index The index of the assignable button
     * @param model The model
     * @param surface The surface
     */
    public AssignableCommand (final int index, final IModel model, final MCUControlSurface surface)
    {
        super (model, surface, index);

        this.switcher = new ModeSwitcher (surface);
        this.flipCommand = new MCUFlipCommand (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        final MCUConfiguration configuration = this.surface.getConfiguration ();

        switch (this.getSetting ())
        {
            case MCUConfiguration.FOOTSWITCH_PREV_MODE:
                if (event == ButtonEvent.DOWN)
                    this.switcher.scrollDown ();
                break;

            case MCUConfiguration.FOOTSWITCH_NEXT_MODE:
                if (event == ButtonEvent.DOWN)
                    this.switcher.scrollUp ();
                break;

            case MCUConfiguration.FOOTSWITCH_SHOW_MARKER_MODE:
                if (event != ButtonEvent.DOWN)
                    return;
                final ModeManager modeManager = this.surface.getModeManager ();
                if (modeManager.isActive (Modes.MARKERS))
                    modeManager.restore ();
                else
                    modeManager.setActive (Modes.MARKERS);
                final IMode mode = modeManager.getActive ();
                if (mode != null)
                    this.surface.getDisplay ().notify (mode.getName ());
                break;

            case MCUConfiguration.FOOTSWITCH_USE_FADERS_LIKE_EDIT_KNOBS:
                this.flipCommand.executeNormal (event);
                break;

            case MCUConfiguration.FOOTSWITCH_TOGGLE_MOTOR_FADERS_ON_OFF:
                if (event != ButtonEvent.DOWN)
                    return;
                configuration.toggleMotorFaders ();
                this.mvHelper.delayDisplay ( () -> "Motor Faders: " + (configuration.hasMotorFaders () ? "On" : "Off"));
                break;

            case MCUConfiguration.FOOTSWITCH_ACTION:
                if (event != ButtonEvent.DOWN)
                    return;
                final String assignableActionID = configuration.getAssignableAction (this.index);
                if (assignableActionID != null)
                    this.model.getApplication ().invokeAction (assignableActionID);
                break;

            default:
                super.execute (event, velocity);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    protected int getSetting ()
    {
        return this.surface.getConfiguration ().getAssignable (this.index);
    }


    /**
     * Test if the assignable is active.
     *
     * @return True if active
     */
    public boolean isActive ()
    {
        switch (this.getSetting ())
        {
            case AbstractConfiguration.FOOTSWITCH_TOGGLE_PLAY:
                return this.model.getTransport ().isPlaying ();

            case AbstractConfiguration.FOOTSWITCH_TOGGLE_RECORD:
                return this.model.getTransport ().isRecording ();

            case AbstractConfiguration.FOOTSWITCH_TOGGLE_CLIP_OVERDUB:
                return this.model.getTransport ().isLauncherOverdub ();

            case AbstractConfiguration.FOOTSWITCH_PANEL_LAYOUT_ARRANGE:
                return IApplication.PANEL_LAYOUT_ARRANGE.equals (this.model.getApplication ().getPanelLayout ());

            case AbstractConfiguration.FOOTSWITCH_PANEL_LAYOUT_MIX:
                return IApplication.PANEL_LAYOUT_MIX.equals (this.model.getApplication ().getPanelLayout ());

            case AbstractConfiguration.FOOTSWITCH_PANEL_LAYOUT_EDIT:
                return IApplication.PANEL_LAYOUT_EDIT.equals (this.model.getApplication ().getPanelLayout ());

            case MCUConfiguration.FOOTSWITCH_SHOW_MARKER_MODE:
                return this.surface.getModeManager ().isActive (Modes.MARKERS);

            case MCUConfiguration.FOOTSWITCH_USE_FADERS_LIKE_EDIT_KNOBS:
                return this.surface.getConfiguration ().useFadersAsKnobs ();

            case MCUConfiguration.FOOTSWITCH_TOGGLE_MOTOR_FADERS_ON_OFF:
                return this.surface.getConfiguration ().hasMotorFaders ();

            case AbstractConfiguration.FOOTSWITCH_UNDO:
            case AbstractConfiguration.FOOTSWITCH_TAP_TEMPO:
            case AbstractConfiguration.FOOTSWITCH_NEW_BUTTON:
            case AbstractConfiguration.FOOTSWITCH_CLIP_BASED_LOOPER:
            case AbstractConfiguration.FOOTSWITCH_STOP_ALL_CLIPS:
            case AbstractConfiguration.FOOTSWITCH_ADD_INSTRUMENT_TRACK:
            case AbstractConfiguration.FOOTSWITCH_ADD_AUDIO_TRACK:
            case AbstractConfiguration.FOOTSWITCH_ADD_EFFECT_TRACK:
            case AbstractConfiguration.FOOTSWITCH_QUANTIZE:
            case MCUConfiguration.FOOTSWITCH_PREV_MODE:
            case MCUConfiguration.FOOTSWITCH_NEXT_MODE:
            default:
                return false;
        }
    }
}
