// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
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
import de.mossgrabers.framework.daw.data.ICursorTrack;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.osc.IOpenSoundControlWriter;
import de.mossgrabers.framework.parameter.IParameter;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Optional;


/**
 * All track related commands.
 *
 * @author Jürgen Moßgraber
 */
public class TrackModule extends AbstractModule
{
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
                    this.parseTrackValue (this.model.getCurrentTrackBank ().getItem (trackNo), path, value);
                }
                catch (final NumberFormatException ex)
                {
                    this.parseTrackCommands (subCommand, path, value);
                }
                break;

            case "master":
                this.parseTrackValue (this.model.getMasterTrack (), path, value);
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
        final ICursorTrack cursorTrack = this.model.getCursorTrack ();
        this.flushTrack (this.writer, "/track/selected/", cursorTrack, dump);
        this.writer.sendOSC ("/track/toggleBank", this.model.isEffectTrackBankActive () ? 1 : 0, dump);
        this.writer.sendOSC ("/track/hasParent", trackBank.hasParent (), dump);

        // Flush track parameters
        final String paramAddress = "/track/param/";
        final IParameterBank parameterBank = this.model.getCursorTrack ().getParameterBank ();
        for (int i = 0; i < parameterBank.getPageSize (); i++)
        {
            final int oneplus = i + 1;
            this.flushParameterData (this.writer, paramAddress + oneplus + "/", parameterBank.getItem (i), dump);
        }

        final IParameterPageBank parameterPageBank = parameterBank.getPageBank ();
        final int selectedParameterPage = parameterPageBank.getSelectedItemIndex ();
        for (int i = 0; i < parameterPageBank.getPageSize (); i++)
        {
            final int oneplus = i + 1;
            final String pageName = parameterPageBank.getItem (i);
            final String pageAddress = "/track/page/" + oneplus + "/";
            this.writer.sendOSC (pageAddress + TAG_EXISTS, !pageName.isBlank (), dump);
            this.writer.sendOSC (pageAddress, pageName, dump);
            this.writer.sendOSC (pageAddress + TAG_NAME, pageName, dump);
            this.writer.sendOSC (pageAddress + TAG_SELECTED, selectedParameterPage == i, dump);
        }
        final Optional<String> selectedItem = parameterPageBank.getSelectedItem ();
        this.writer.sendOSC ("/track/page/selected/" + TAG_NAME, selectedItem.isPresent () ? selectedItem.get () : "", dump);
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
        writer.sendOSC (trackAddress + TAG_EXISTS, track.doesExist (), dump);
        final ChannelType type = track.getType ();
        writer.sendOSC (trackAddress + "type", type == null ? null : type.name ().toLowerCase (Locale.US), dump);
        writer.sendOSC (trackAddress + TAG_ACTIVATED, track.isActivated (), dump);
        writer.sendOSC (trackAddress + TAG_SELECTED, track.isSelected (), dump);
        writer.sendOSC (trackAddress + "isGroup", track.isGroup (), dump);
        writer.sendOSC (trackAddress + TAG_NAME, track.getName (), dump);
        writer.sendOSC (trackAddress + "volumeStr", track.getVolumeStr (), dump);
        writer.sendOSC (trackAddress + TAG_VOLUME, track.getVolume (), dump);
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

        if (track instanceof final ICursorTrack cursorTrack)
            writer.sendOSC (trackAddress + "pinned", cursorTrack.isPinned (), dump);

        final ISendBank sendBank = track.getSendBank ();
        for (int i = 0; i < sendBank.getPageSize (); i++)
            this.flushParameterData (writer, trackAddress + "send/" + (i + 1) + "/", sendBank.getItem (i), dump);

        final ISlotBank slotBank = track.getSlotBank ();
        for (int i = 0; i < slotBank.getPageSize (); i++)
        {
            final ISlot slot = slotBank.getItem (i);
            final String clipAddress = trackAddress + "clip/" + (i + 1) + "/";
            writer.sendOSC (clipAddress + TAG_NAME, slot.getName (), dump);
            writer.sendOSC (clipAddress + "isSelected", slot.isSelected (), dump);
            writer.sendOSC (clipAddress + "hasContent", slot.hasContent (), dump);
            writer.sendOSC (clipAddress + "isPlaying", slot.isPlaying (), dump);
            writer.sendOSC (clipAddress + "isRecording", slot.isRecording (), dump);
            writer.sendOSC (clipAddress + "isPlayingQueued", slot.isPlayingQueued (), dump);
            writer.sendOSC (clipAddress + "isRecordingQueued", slot.isRecordingQueued (), dump);
            writer.sendOSC (clipAddress + "isStopQueued", slot.isStopQueued (), dump);

            final ColorEx color = slot.getColor ();
            writer.sendOSCColor (clipAddress + TAG_COLOR, color.getRed (), color.getGreen (), color.getBlue (), dump);
        }

        final ColorEx color = track.getColor ();
        writer.sendOSCColor (trackAddress + TAG_COLOR, color.getRed (), color.getGreen (), color.getBlue (), dump);

        final String crossfadeMode = track.getCrossfadeParameter ().getDisplayedValue ();
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
            case TAG_INDICATE:
                final String indicateCommand = getSubCommand (path);
                final boolean isTrue = isTrigger (value);
                switch (indicateCommand)
                {
                    case TAG_VOLUME:
                        for (int i = 0; i < tb.getPageSize (); i++)
                            tb.getItem (i).setVolumeIndication (isTrue);
                        break;
                    case "pan":
                        for (int i = 0; i < tb.getPageSize (); i++)
                            tb.getItem (i).setPanIndication (isTrue);
                        break;
                    case "send":
                        final int sendIndex = Integer.parseInt (path.get (0)) - 1;
                        for (int i = 0; i < tb.getPageSize (); i++)
                            tb.getItem (i).getSendBank ().getItem (sendIndex).setIndication (isTrue);
                        break;
                    default:
                        throw new UnknownCommandException (indicateCommand);
                }
                break;

            case "bank":
                final String bankCommand = getSubCommand (path);
                switch (bankCommand)
                {
                    case TAG_PAGE:
                        if ("+".equals (getSubCommand (path)))
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
                        throw new UnknownCommandException (bankCommand);
                }
                break;

            case "+":
                final Optional<ITrack> selTrack1 = tb.getSelectedItem ();
                final int index1 = selTrack1.isEmpty () ? 0 : selTrack1.get ().getIndex () + 1;
                if (index1 == tb.getPageSize ())
                {
                    tb.selectNextPage ();
                    return;
                }
                tb.getItem (index1).select ();
                break;

            case "-":
                final Optional<ITrack> selTrack2 = tb.getSelectedItem ();
                final int index2 = selTrack2.isEmpty () ? 0 : selTrack2.get ().getIndex () - 1;
                if (index2 == -1)
                {
                    tb.selectPreviousPage ();
                    return;
                }
                tb.getItem (index2).select ();
                break;

            case "add":
                final String subCommand2 = getSubCommand (path);
                final IApplication application = this.model.getApplication ();
                switch (subCommand2)
                {
                    case "audio":
                        this.model.getTrackBank ().addChannel (ChannelType.AUDIO);
                        break;
                    case "effect":
                        application.addEffectTrack ();
                        break;
                    case "instrument":
                        this.model.getTrackBank ().addChannel (ChannelType.INSTRUMENT);
                        break;
                    default:
                        throw new UnknownCommandException (subCommand2);
                }
                break;

            case "stop", "stopAlt":
                this.model.getCurrentTrackBank ().stop ("stopAlt".equals (command));
                break;

            case "vu":
                this.configuration.setVUMetersEnabled (isTrigger (value));
                break;

            case "toggleBank":
                if (this.model.getEffectTrackBank () == null)
                    return;
                this.model.toggleCurrentTrackBank ();
                final ITrackBank tbNew = this.model.getCurrentTrackBank ();
                // Make sure a track is selected
                final ITrackBank tbOther = this.model.isEffectTrackBankActive () ? this.model.getTrackBank () : this.model.getEffectTrackBank ();
                final Optional<ITrack> selectedTrack = tbNew.getSelectedItem ();
                if (selectedTrack.isEmpty ())
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

            case "parent":
                if (!this.configuration.isTrackNavigationFlat ())
                    tb.selectParent ();
                break;

            case TAG_SELECT, TAG_SELECTED:
                final ITrack cursorTrack = this.model.getCursorTrack ();
                if (cursorTrack.doesExist ())
                    this.parseTrackValue (cursorTrack, path, value);
                break;

            case "param":
                this.parseParamValue (path, value);
                break;

            case "page":
                this.parsePageValue (path, value);
                break;

            default:
                throw new UnknownCommandException (command);
        }
    }


    private void parseTrackValue (final ITrack track, final LinkedList<String> path, final Object value) throws IllegalParameterException, MissingCommandException, UnknownCommandException
    {
        final String command = getSubCommand (path);
        switch (command)
        {
            case TAG_NAME:
                if (value != null)
                    track.setName (value.toString ());
                break;

            case TAG_ACTIVATED:
                track.setIsActivated (isTrigger (value));
                break;

            case "crossfadeMode":
                switch (getSubCommand (path))
                {
                    case "A":
                        track.getCrossfadeParameter ().setNormalizedValue (0);
                        break;
                    case "B":
                        track.getCrossfadeParameter ().setNormalizedValue (1);
                        break;
                    default:
                        track.getCrossfadeParameter ().setNormalizedValue (0.5);
                        break;
                }
                break;

            case TAG_SELECT, TAG_SELECTED:
                if (isTrigger (value))
                    track.selectOrExpandGroup ();
                break;

            case TAG_DUPLICATE:
                track.duplicate ();
                break;

            case TAG_REMOVE:
                track.remove ();
                break;

            case TAG_VOLUME:
                if (path.isEmpty ())
                    track.setVolume (toInteger (value));
                else if (TAG_INDICATE.equals (path.get (0)))
                    track.setVolumeIndication (isTrigger (value));
                else if ("reset".equals (path.get (0)))
                    track.resetVolume ();
                else if (TAG_TOUCHED.equals (path.get (0)))
                    track.touchVolume (isTrigger (value));
                break;

            case "pan":
                if (path.isEmpty ())
                    track.setPan (toInteger (value));
                else if (TAG_INDICATE.equals (path.get (0)))
                    track.setPanIndication (isTrigger (value));
                else if ("reset".equals (path.get (0)))
                    track.resetPan ();
                else if (TAG_TOUCHED.equals (path.get (0)))
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
                this.parseClipValue (track, path, value);
                break;

            case "enter":
                if (isTrigger (value) && track.isGroup ())
                {
                    if (this.configuration.isTrackNavigationFlat ())
                        track.toggleGroupExpanded ();
                    else
                    {
                        track.setGroupExpanded (true);
                        track.enter ();
                    }
                }
                break;

            case TAG_COLOR:
                final Optional<ColorEx> color = matchColor (toString (value));
                if (color.isPresent ())
                    track.setColor (color.get ());
                break;

            case "pinned":
                if (track instanceof final ICursorTrack cursorTrack)
                {
                    if (value == null)
                        cursorTrack.togglePinned ();
                    else
                        cursorTrack.setPinned (isTrigger (value));
                }
                break;

            default:
                throw new UnknownCommandException (command);
        }
    }


    private void parseClipValue (final ITrack track, final LinkedList<String> path, final Object value) throws UnknownCommandException, MissingCommandException, IllegalParameterException
    {
        final String command = getSubCommand (path);

        try
        {
            final int clipNo = Integer.parseInt (command) - 1;
            final String clipCommand = getSubCommand (path);
            final ISlot slot = track.getSlotBank ().getItem (clipNo);
            switch (clipCommand)
            {
                case TAG_SELECT, TAG_SELECTED:
                    slot.select ();
                    break;
                case "launch":
                    slot.launch (toInteger (value) > 0, false);
                    break;
                case "launchAlt":
                    slot.launch (toInteger (value) > 0, true);
                    return;
                case "record":
                    this.model.recordNoteClip (track, slot);
                    break;
                case "create":
                    this.model.createNoteClip (track, slot, toInteger (value), true);
                    break;
                case TAG_DUPLICATE:
                    slot.duplicate ();
                    break;
                case TAG_REMOVE:
                    slot.remove ();
                    break;
                case TAG_COLOR:
                    final Optional<ColorEx> color = matchColor (toString (value));
                    if (color.isPresent ())
                        slot.setColor (color.get ());
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
                    track.stop (false);
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
        final ISend send = track.getSendBank ().getItem (sendIndex);
        if (send == null)
            return;

        final String command = getSubCommand (path);
        switch (command)
        {
            case TAG_ACTIVATED:
                if (isTrigger (value))
                    send.toggleEnabled ();
                break;
            case TAG_INDICATE:
                send.setIndication (isTrigger (value));
                break;
            case TAG_TOUCHED:
                send.touchValue (isTrigger (value));
                break;
            case TAG_VOLUME:
                send.setValue (toInteger (value));
                break;
            default:
                throw new UnknownCommandException (command);
        }
    }


    private void parseParamValue (final LinkedList<String> path, final Object value) throws UnknownCommandException, MissingCommandException, IllegalParameterException
    {
        final IParameterBank parameterBank = this.model.getCursorTrack ().getParameterBank ();
        final String subCommand = getSubCommand (path);
        try
        {
            final int paramNo = Integer.parseInt (subCommand) - 1;
            parseFXParamValue (parameterBank.getItem (paramNo), path, value);
        }
        catch (final NumberFormatException ex)
        {
            switch (subCommand)
            {
                case "+":
                    if (isTrigger (value))
                        parameterBank.selectNextPage ();
                    break;

                case "-":
                    if (isTrigger (value))
                        parameterBank.selectPreviousPage ();
                    break;

                default:
                    throw new UnknownCommandException (subCommand);
            }
        }
    }


    private void parsePageValue (final LinkedList<String> path, final Object value) throws UnknownCommandException, MissingCommandException, IllegalParameterException
    {
        final IParameterBank parameterBank = this.model.getCursorTrack ().getParameterBank ();
        final IParameterPageBank parameterPageBank = parameterBank.getPageBank ();
        final String subCommand = getSubCommand (path);
        if ("select".equals (subCommand) || "selected".equals (subCommand))
        {
            parameterPageBank.selectPage (toInteger (value) - 1);
        }
        else
        {
            try
            {
                final int index = Integer.parseInt (subCommand) - 1;
                parameterPageBank.selectPage (index);
            }
            catch (final NumberFormatException ex2)
            {
                throw new UnknownCommandException (subCommand);
            }
        }
    }


    private static void parseFXParamValue (final IParameter param, final LinkedList<String> path, final Object value) throws MissingCommandException, IllegalParameterException, UnknownCommandException
    {
        final String command = getSubCommand (path);
        switch (command)
        {
            case "value":
                param.setValue (toInteger (value));
                break;

            case TAG_INDICATE:
                param.setIndication (isTrigger (value));
                break;

            case "reset":
                param.resetValue ();
                break;

            case TAG_TOUCHED:
                param.touchValue (isTrigger (value));
                break;

            default:
                throw new UnknownCommandException (command);
        }
    }
}
