// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc.module;

import de.mossgrabers.controller.osc.OSCConfiguration;
import de.mossgrabers.controller.osc.OSCControlSurface;
import de.mossgrabers.controller.osc.exception.IllegalParameterException;
import de.mossgrabers.controller.osc.exception.MissingCommandException;
import de.mossgrabers.controller.osc.exception.UnknownCommandException;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.clip.IClip;
import de.mossgrabers.framework.daw.constants.AutomationMode;
import de.mossgrabers.framework.daw.constants.LaunchQuantization;
import de.mossgrabers.framework.daw.constants.PostRecordingAction;
import de.mossgrabers.framework.osc.IOpenSoundControlWriter;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.LinkedList;
import java.util.Locale;


/**
 * All transport related commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TransportModule extends AbstractModule
{
    private static final String                                    TAG_LAUNCHER = "launcher";

    private final ITransport                                       transport;
    private final PlayCommand<OSCControlSurface, OSCConfiguration> playCommand;


    /**
     * Constructor.
     *
     * @param host The host
     * @param model The model
     * @param surface The surface
     * @param writer The writer
     */
    public TransportModule (final IHost host, final IModel model, final OSCControlSurface surface, final IOpenSoundControlWriter writer)
    {
        super (host, model, writer);

        this.transport = model.getTransport ();
        this.playCommand = new PlayCommand<> (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public String [] getSupportedCommands ()
    {
        return new String []
        {
            "play",
            "playbutton",
            "stop",
            "restart",
            "record",
            "overdub",
            "repeat",
            "punchIn",
            "punchOut",
            "click",
            "quantize",
            "tempo",
            "time",
            "position",
            "crossfade",
            "autowrite",
            "automationWriteMode",
            TAG_PREROLL,
            TAG_LAUNCHER
        };
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final String command, final LinkedList<String> path, final Object value) throws IllegalParameterException, UnknownCommandException, MissingCommandException
    {
        final boolean isTrigger = isTrigger (value);

        switch (command)
        {
            case "play":
                final boolean isPlaying = this.transport.isPlaying ();
                if (isTrigger && !isPlaying || !isTrigger && isPlaying)
                    this.transport.play ();
                break;

            case "playbutton":
                if (isTrigger)
                    this.playCommand.execute (ButtonEvent.DOWN, 127);
                break;

            case "stop":
                if (this.transport.isPlaying ())
                    this.transport.stop ();
                else
                    this.transport.stopAndRewind ();
                break;

            case "restart":
                if (isTrigger)
                    this.transport.restart ();
                break;

            case "record":
                this.transport.startRecording ();
                break;

            case "overdub":
                if (!path.isEmpty () && TAG_LAUNCHER.equals (path.get (0)))
                    this.transport.toggleLauncherOverdub ();
                else
                    this.transport.toggleOverdub ();
                break;

            case "repeat":
                if (value == null)
                    this.transport.toggleLoop ();
                else
                    this.transport.setLoop (isTrigger);
                break;

            case "punchIn":
                if (value == null)
                    this.transport.togglePunchIn ();
                else
                    this.transport.setPunchIn (isTrigger);
                break;

            case "punchOut":
                if (value == null)
                    this.transport.togglePunchOut ();
                else
                    this.transport.setPunchOut (isTrigger);
                break;

            case "click":
                if (path.isEmpty ())
                {
                    if (value == null)
                        this.transport.toggleMetronome ();
                    else
                        this.transport.setMetronome (isTrigger);
                    break;
                }
                final String subCommand = getSubCommand (path);
                switch (subCommand)
                {
                    case "volume":
                        this.transport.setMetronomeVolume (toInteger (value));
                        break;
                    case "ticks":
                        if (value == null)
                            this.transport.toggleMetronomeTicks ();
                        else
                            this.transport.setMetronomeTicks (isTrigger);
                        break;
                    case TAG_PREROLL:
                        if (isTrigger)
                            this.transport.togglePrerollMetronome ();
                        break;
                    default:
                        throw new UnknownCommandException (subCommand);
                }
                break;

            case "quantize":
                final IClip clip = this.getClip ();
                if (clip.doesExist ())
                    clip.quantize (1);
                break;

            case "tempo":
                final String tempoCommand = getSubCommand (path);
                switch (tempoCommand)
                {
                    case "raw":
                        this.transport.setTempo (toNumber (value));
                        break;
                    case "tap":
                        if (isTrigger)
                            this.transport.tapTempo ();
                        break;
                    case "+":
                        this.transport.setTempo (this.transport.getTempo () + toNumber (value, 1.0));
                        break;
                    case "-":
                        this.transport.setTempo (this.transport.getTempo () - toNumber (value, 1.0));
                        break;
                    default:
                        throw new UnknownCommandException (tempoCommand);
                }
                break;

            case "time":
                this.transport.setPosition (toNumber (value));
                break;

            case "position":
                if (path.isEmpty ())
                {
                    final double numValue = toNumber (value);
                    this.transport.changePosition (numValue >= 0, Math.abs (numValue) <= 1);
                    break;
                }
                final String positionCommand = path.get (0);
                switch (positionCommand)
                {
                    case "+":
                        this.transport.changePosition (true, true);
                        break;
                    case "-":
                        this.transport.changePosition (false, true);
                        break;
                    case "++":
                        this.transport.changePosition (true, false);
                        break;
                    case "--":
                        this.transport.changePosition (false, false);
                        break;
                    case "start":
                        this.transport.setPosition (0);
                        break;
                    default:
                        throw new UnknownCommandException (positionCommand);
                }
                break;

            case "crossfade":
                this.transport.setCrossfade (toInteger (value));
                break;

            case "autowrite":
                if (!path.isEmpty () && TAG_LAUNCHER.equals (path.get (0)))
                    this.transport.toggleWriteClipLauncherAutomation ();
                else
                    this.transport.toggleWriteArrangerAutomation ();
                break;

            case "automationWriteMode":
                if (value != null)
                    this.transport.setAutomationWriteMode (AutomationMode.valueOf (value.toString ().toUpperCase (Locale.US)));
                break;

            case TAG_PREROLL:
                this.transport.setPrerollMeasures (toInteger (value));
                break;

            case TAG_LAUNCHER:
                final String launcherCommand = path.get (0);
                switch (launcherCommand)
                {
                    case "postRecordingAction":
                        this.transport.setClipLauncherPostRecordingAction (PostRecordingAction.lookup (value.toString ()));
                        break;
                    case "postRecordingTimeOffset":
                        final double beats = Math.min (4000, Math.max (0, toNumber (value)));
                        this.transport.setClipLauncherPostRecordingTimeOffset (beats);
                        break;
                    case "defaultQuantization":
                        this.transport.setDefaultLaunchQuantization (LaunchQuantization.lookup (value.toString ()));
                        break;
                    default:
                        throw new UnknownCommandException (launcherCommand);
                }
                break;

            default:
                throw new UnknownCommandException (command);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void flush (final boolean dump)
    {
        this.writer.sendOSC ("/play", this.transport.isPlaying (), dump);
        this.writer.sendOSC ("/record", this.transport.isRecording (), dump);
        this.writer.sendOSC ("/overdub", this.transport.isArrangerOverdub (), dump);
        this.writer.sendOSC ("/overdub/launcher", this.transport.isLauncherOverdub (), dump);
        this.writer.sendOSC ("/repeat", this.transport.isLoop (), dump);
        this.writer.sendOSC ("/punchIn", this.transport.isPunchInEnabled (), dump);
        this.writer.sendOSC ("/punchOut", this.transport.isPunchOutEnabled (), dump);
        this.writer.sendOSC ("/click", this.transport.isMetronomeOn (), dump);
        this.writer.sendOSC ("/click/ticks", this.transport.isMetronomeTicksOn (), dump);
        this.writer.sendOSC ("/click/volume", this.transport.getMetronomeVolume (), dump);
        this.writer.sendOSC ("/click/volumeStr", this.transport.getMetronomeVolumeStr (), dump);
        this.writer.sendOSC ("/click/preroll", this.transport.isPrerollMetronomeEnabled (), dump);
        this.writer.sendOSC ("/preroll", this.transport.getPrerollMeasures (), dump);
        this.writer.sendOSC ("/tempo/raw", this.transport.getTempo (), dump);
        this.writer.sendOSC ("/crossfade", this.transport.getCrossfade (), dump);
        this.writer.sendOSC ("/autowrite", this.transport.isWritingArrangerAutomation (), dump);
        this.writer.sendOSC ("/autowrite/launcher", this.transport.isWritingClipLauncherAutomation (), dump);
        this.writer.sendOSC ("/automationWriteMode", this.transport.getAutomationWriteMode ().getIdentifier (), dump);
        this.writer.sendOSC ("/time/str", this.transport.getPositionText (), dump);
        this.writer.sendOSC ("/time/signature", this.transport.getNumerator () + " / " + this.transport.getDenominator (), dump);
        this.writer.sendOSC ("/beat/str", this.transport.getBeatText (), dump);
        this.writer.sendOSC ("/launcher/postRecordingAction", this.transport.getClipLauncherPostRecordingAction ().getIdentifier (), dump);
        this.writer.sendOSC ("/launcher/postRecordingTimeOffset", this.transport.getClipLauncherPostRecordingTimeOffset (), dump);
        this.writer.sendOSC ("/launcher/defaultQuantization", this.transport.getDefaultLaunchQuantization ().getValue (), dump);
    }
}
