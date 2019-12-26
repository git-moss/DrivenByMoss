// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc.module;

import de.mossgrabers.controller.osc.OSCConfiguration;
import de.mossgrabers.controller.osc.exception.IllegalParameterException;
import de.mossgrabers.controller.osc.exception.MissingCommandException;
import de.mossgrabers.controller.osc.exception.UnknownCommandException;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.ISlotBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.empty.EmptyTrack;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.osc.IOpenSoundControlWriter;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * All track related commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackModule extends AbstractModule
{
    private static final Pattern   RGB_COLOR_PATTERN = Pattern.compile ("(rgb|RGB)\\((\\d+(\\.\\d+)?),(\\d+(\\.\\d+)?),(\\d+(\\.\\d+)?)\\)");

    private final OSCConfiguration configuration;


    /**
     * Constructor.
     *
     * @param host The host
     * @param model The model
     * @param writer The writer
     * @param configuration The configuration
     */
    public TrackModule (final IHost host, final IModel model, final IOpenSoundControlWriter writer, final OSCConfiguration configuration)
    {
        super (host, model, writer);

        this.configuration = configuration;
    }


    /** {@inheritDoc} */
    @Override
    public String [] getSupportedCommands ()
    {
        return new String []
        {
            "track",
            "master"
        };
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final String command, final LinkedList<String> path, final Object value) throws IllegalParameterException, UnknownCommandException, MissingCommandException
    {
        switch (command)
        {
            case "track":
                final String subCommand = getSubCommand (path);
                try
                {
                    final int trackNo = Integer.parseInt (subCommand) - 1;
                    parseTrackValue (this.model.getCurrentTrackBank ().getItem (trackNo), path, value);
                }
                catch (final NumberFormatException ex)
                {
                    this.parseTrackCommands (subCommand, path, value);
                }
                break;

            case "master":
                parseTrackValue (this.model.getMasterTrack (), path, value);
                break;

            default:
                throw new UnknownCommandException (command);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void flush (final boolean dump)
    {
        final ITrackBank trackBank = this.model.getCurrentTrackBank ();
        for (int i = 0; i < trackBank.getPageSize (); i++)
            this.flushTrack (this.writer, "/track/" + (i + 1) + "/", trackBank.getItem (i), dump);
        this.flushTrack (this.writer, "/master/", this.model.getMasterTrack (), dump);
        final ITrack selectedTrack = trackBank.getSelectedItem ();
        this.flushTrack (this.writer, "/track/selected/", selectedTrack == null ? EmptyTrack.INSTANCE : selectedTrack, dump);
        this.writer.sendOSC ("/track/toggleBank", this.model.isEffectTrackBankActive () ? 1 : 0, dump);
    }


    /**
     * Flush all data of a track.
     *
     * @param writer Where to send the messages to
     * @param trackAddress The start address for the track
     * @param track The track
     * @param dump Forces a flush if true otherwise only changed values are flushed
     */
    private void flushTrack (final IOpenSoundControlWriter writer, final String trackAddress, final ITrack track, final boolean dump)
    {
        writer.sendOSC (trackAddress + "exists", track.doesExist (), dump);
        final ChannelType type = track.getType ();
        writer.sendOSC (trackAddress + "type", type == null ? null : type.name ().toLowerCase (), dump);
        writer.sendOSC (trackAddress + "activated", track.isActivated (), dump);
        writer.sendOSC (trackAddress + "selected", track.isSelected (), dump);
        writer.sendOSC (trackAddress + "isGroup", track.isGroup (), dump);
        writer.sendOSC (trackAddress + "name", track.getName (), dump);
        writer.sendOSC (trackAddress + "volumeStr", track.getVolumeStr (), dump);
        writer.sendOSC (trackAddress + "volume", track.getVolume (), dump);
        writer.sendOSC (trackAddress + "panStr", track.getPanStr (), dump);
        writer.sendOSC (trackAddress + "pan", track.getPan (), dump);
        writer.sendOSC (trackAddress + "mute", track.isMute (), dump);
        writer.sendOSC (trackAddress + "solo", track.isSolo (), dump);
        writer.sendOSC (trackAddress + "recarm", track.isRecArm (), dump);
        writer.sendOSC (trackAddress + "monitor", track.isMonitor (), dump);
        writer.sendOSC (trackAddress + "autoMonitor", track.isAutoMonitor (), dump);
        writer.sendOSC (trackAddress + "canHoldNotes", track.canHoldNotes (), dump);
        writer.sendOSC (trackAddress + "canHoldAudioData", track.canHoldAudioData (), dump);
        writer.sendOSC (trackAddress + "position", track.getPosition (), dump);

        final ISendBank sendBank = track.getSendBank ();
        for (int i = 0; i < sendBank.getPageSize (); i++)
            this.flushParameterData (writer, trackAddress + "send/" + (i + 1) + "/", sendBank.getItem (i), dump);

        final ISlotBank slotBank = track.getSlotBank ();
        for (int i = 0; i < slotBank.getPageSize (); i++)
        {
            final ISlot slot = slotBank.getItem (i);
            final String clipAddress = trackAddress + "clip/" + (i + 1) + "/";
            writer.sendOSC (clipAddress + "name", slot.getName (), dump);
            writer.sendOSC (clipAddress + "isSelected", slot.isSelected (), dump);
            writer.sendOSC (clipAddress + "hasContent", slot.hasContent (), dump);
            writer.sendOSC (clipAddress + "isPlaying", slot.isPlaying (), dump);
            writer.sendOSC (clipAddress + "isRecording", slot.isRecording (), dump);
            writer.sendOSC (clipAddress + "isPlayingQueued", slot.isPlayingQueued (), dump);
            writer.sendOSC (clipAddress + "isRecordingQueued", slot.isRecordingQueued (), dump);
            writer.sendOSC (clipAddress + "isStopQueued", slot.isStopQueued (), dump);

            final ColorEx color = slot.getColor ();
            writer.sendOSCColor (clipAddress + "color", color.getRed (), color.getGreen (), color.getBlue (), dump);
        }

        final ColorEx color = track.getColor ();
        writer.sendOSCColor (trackAddress + "color", color.getRed (), color.getGreen (), color.getBlue (), dump);

        final String crossfadeMode = track.getCrossfadeMode ();
        writer.sendOSC (trackAddress + "crossfadeMode/A", "A".equals (crossfadeMode), dump);
        writer.sendOSC (trackAddress + "crossfadeMode/B", "B".equals (crossfadeMode), dump);
        writer.sendOSC (trackAddress + "crossfadeMode/AB", "AB".equals (crossfadeMode), dump);

        writer.sendOSC (trackAddress + "vu", this.configuration.isEnableVUMeters () ? track.getVu () : 0, dump);
    }


    private void parseTrackCommands (final String command, final LinkedList<String> path, final Object value) throws UnknownCommandException, MissingCommandException, IllegalParameterException
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        switch (command)
        {
            case "indicate":
            {
                final String subCommand = getSubCommand (path);
                final boolean isTrue = isTrigger (value);
                switch (subCommand)
                {
                    case "volume":
                        for (int i = 0; i < tb.getPageSize (); i++)
                            tb.getItem (i).setVolumeIndication (isTrue);
                        break;
                    case "pan":
                        for (int i = 0; i < tb.getPageSize (); i++)
                            tb.getItem (i).setPanIndication (isTrue);
                        break;
                    case "send":
                        if (this.model.isEffectTrackBankActive ())
                            return;
                        final int sendIndex = Integer.parseInt (path.get (0)) - 1;
                        for (int i = 0; i < tb.getPageSize (); i++)
                            tb.getItem (i).getSendBank ().getItem (sendIndex).setIndication (isTrue);
                        break;
                    default:
                        throw new UnknownCommandException (subCommand);
                }
                break;
            }

            case "bank":
                final String subCommand = getSubCommand (path);
                switch (subCommand)
                {
                    case "page":
                        final String subCommand2 = getSubCommand (path);
                        if ("+".equals (subCommand2))
                            tb.selectNextPage ();
                        else // "-"
                            tb.selectPreviousPage ();
                        break;
                    case "+":
                        tb.scrollForwards ();
                        break;
                    case "-":
                        tb.scrollBackwards ();
                        break;
                    default:
                        throw new UnknownCommandException (subCommand);
                }
                break;

            case "+":
            {
                final ITrack sel = tb.getSelectedItem ();
                final int index = sel == null ? 0 : sel.getIndex () + 1;
                if (index == tb.getPageSize ())
                {
                    tb.selectNextPage ();
                    return;
                }
                tb.getItem (index).select ();
                break;
            }

            case "-":
            {
                final ITrack sel = tb.getSelectedItem ();
                final int index = sel == null ? 0 : sel.getIndex () - 1;
                if (index == -1)
                {
                    tb.selectPreviousPage ();
                    return;
                }
                tb.getItem (index).select ();
                break;
            }

            case "add":
                final String subCommand2 = getSubCommand (path);
                final IApplication application = this.model.getApplication ();
                switch (subCommand2)
                {
                    case "audio":
                        application.addAudioTrack ();
                        break;
                    case "effect":
                        application.addEffectTrack ();
                        break;
                    case "instrument":
                        application.addInstrumentTrack ();
                        break;
                    default:
                        throw new UnknownCommandException (subCommand2);
                }
                break;

            case "stop":
                this.model.getCurrentTrackBank ().stop ();
                break;

            case "vu":
                this.configuration.setVUMetersEnabled (isTrigger (value));
                break;

            case "toggleBank":
            {
                if (this.model.getEffectTrackBank () == null)
                    return;
                this.model.toggleCurrentTrackBank ();
                final ITrackBank tbNew = this.model.getCurrentTrackBank ();
                // Make sure a track is selected
                final ITrackBank tbOther = this.model.isEffectTrackBankActive () ? this.model.getTrackBank () : this.model.getEffectTrackBank ();
                final ITrack selectedTrack = tbNew.getSelectedItem ();
                if (selectedTrack == null)
                    tbNew.getItem (0).select ();
                // Move the indication to the other bank
                for (int i = 0; i < tbNew.getPageSize (); i++)
                {
                    final ITrack otherTrack = tbOther.getItem (i);
                    otherTrack.setVolumeIndication (false);
                    otherTrack.setPanIndication (false);
                    final ITrack track = tbNew.getItem (i);
                    track.setVolumeIndication (true);
                    track.setPanIndication (true);
                }
                break;
            }

            case "parent":
            {
                tb.selectParent ();
                break;
            }

            case "select":
            case "selected":
                final ITrack selectedTrack = tb.getSelectedItem ();
                if (selectedTrack != null)
                    parseTrackValue (selectedTrack, path, value);
                break;

            default:
                throw new UnknownCommandException (command);
        }
    }


    private static void parseTrackValue (final ITrack track, final LinkedList<String> path, final Object value) throws IllegalParameterException, MissingCommandException, UnknownCommandException
    {
        final String command = getSubCommand (path);
        switch (command)
        {
            case "activated":
                track.setIsActivated (isTrigger (value));
                break;

            case "crossfadeMode":
                track.setCrossfadeMode (getSubCommand (path));
                break;

            case "select":
            case "selected":
                if (isTrigger (value))
                    track.select ();
                break;

            case "volume":
                if (path.isEmpty ())
                    track.setVolume (toInteger (value));
                else if ("indicate".equals (path.get (0)))
                    track.setVolumeIndication (isTrigger (value));
                else if ("reset".equals (path.get (0)))
                    track.resetVolume ();
                else if ("touched".equals (path.get (0)))
                    track.touchVolume (isTrigger (value));
                break;

            case "pan":
                if (path.isEmpty ())
                    track.setPan (toInteger (value));
                else if ("indicate".equals (path.get (0)))
                    track.setPanIndication (isTrigger (value));
                else if ("reset".equals (path.get (0)))
                    track.resetPan ();
                else if ("touched".equals (path.get (0)))
                    track.touchPan (isTrigger (value));
                break;

            case "mute":
                if (value == null)
                    track.toggleMute ();
                else
                    track.setMute (isTrigger (value));
                break;

            case "solo":
                if (value == null)
                    track.toggleSolo ();
                else
                    track.setSolo (isTrigger (value));
                break;

            case "recarm":
                if (value == null)
                    track.toggleRecArm ();
                else
                    track.setRecArm (isTrigger (value));
                break;

            case "monitor":
                if (value == null)
                    track.toggleMonitor ();
                else
                    track.setMonitor (isTrigger (value));
                break;

            case "autoMonitor":
                if (value == null)
                    track.toggleAutoMonitor ();
                else
                    track.setAutoMonitor (isTrigger (value));
                break;

            case "send":
                final int sendNo = Integer.parseInt (path.removeFirst ()) - 1;
                parseSendValue (track, sendNo, path, value);
                break;

            case "clip":
                parseClipValue (track, path, value);
                break;

            case "enter":
                track.enter ();
                break;

            case "color":
                final Matcher matcher = RGB_COLOR_PATTERN.matcher (toString (value));
                if (!matcher.matches ())
                    return;
                final int count = matcher.groupCount ();
                if (count == 7)
                    track.setColor (new ColorEx (Double.parseDouble (matcher.group (2)) / 255.0, Double.parseDouble (matcher.group (4)) / 255.0, Double.parseDouble (matcher.group (6)) / 255.0));
                break;

            default:
                throw new UnknownCommandException (command);
        }
    }


    private static void parseClipValue (final ITrack track, final LinkedList<String> path, final Object value) throws UnknownCommandException, MissingCommandException
    {
        final String command = getSubCommand (path);

        try
        {
            final int clipNo = Integer.parseInt (command) - 1;
            final String clipCommand = getSubCommand (path);
            final ISlot slot = track.getSlotBank ().getItem (clipNo);
            switch (clipCommand)
            {
                case "select":
                case "selected":
                    slot.select ();
                    break;
                case "launch":
                    slot.launch ();
                    break;
                case "record":
                    slot.record ();
                    break;
                case "remove":
                    slot.remove ();
                    break;
                case "color":
                    final Matcher matcher = RGB_COLOR_PATTERN.matcher (value.toString ());
                    if (!matcher.matches ())
                        return;
                    final int count = matcher.groupCount ();
                    if (count != 7)
                        return;
                    slot.setColor (new ColorEx (Double.parseDouble (matcher.group (2)) / 255.0, Double.parseDouble (matcher.group (4)) / 255.0, Double.parseDouble (matcher.group (6)) / 255.0));
                    break;
                default:
                    throw new UnknownCommandException (clipCommand);
            }
        }
        catch (final NumberFormatException ex)
        {
            switch (command)
            {
                case "stop":
                    track.stop ();
                    break;
                case "returntoarrangement":
                    track.returnToArrangement ();
                    break;
                default:
                    throw new UnknownCommandException (command);
            }
        }
    }


    private static void parseSendValue (final ITrack track, final int sendIndex, final LinkedList<String> path, final Object value) throws UnknownCommandException, MissingCommandException, IllegalParameterException
    {
        final String command = getSubCommand (path);
        switch (command)
        {
            case "volume":
                final ISend send = track.getSendBank ().getItem (sendIndex);
                if (send != null)
                {
                    if (path.isEmpty ())
                        send.setValue (toInteger (value));
                    else if ("indicate".equals (path.get (0)))
                        send.setIndication (isTrigger (value));
                    else if ("touched".equals (path.get (0)))
                        send.touchValue (isTrigger (value));
                }
                break;

            default:
                throw new UnknownCommandException (command);
        }
    }
}
