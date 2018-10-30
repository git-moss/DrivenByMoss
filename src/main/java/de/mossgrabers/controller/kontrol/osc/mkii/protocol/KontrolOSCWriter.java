// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.osc.mkii.protocol;

import de.mossgrabers.controller.kontrol.osc.mkii.KontrolOSCConfiguration;
import de.mossgrabers.controller.kontrol.osc.mkii.TrackType;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.EmptyTrackData;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.osc.AbstractOpenSoundControlWriter;
import de.mossgrabers.framework.osc.IOpenSoundControlServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Writes the changed DAW stati as OSC messages.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class KontrolOSCWriter extends AbstractOpenSoundControlWriter
{
    private static final List<Object> DOUBLE_TRUE = new ArrayList<> ();
    static
    {
        Collections.addAll (DOUBLE_TRUE, Integer.valueOf (1), Integer.valueOf (1));
    }

    private final boolean is16;
    private final String  daw;


    /**
     * Constructor.
     *
     * @param host The host
     * @param model The model
     * @param oscServer The OSC server to write to
     * @param is16 If true use 1.6 protocol otherwise 1.5
     * @param configuration The configuration
     */
    public KontrolOSCWriter (final IHost host, final IModel model, final IOpenSoundControlServer oscServer, final boolean is16, final KontrolOSCConfiguration configuration)
    {
        super (host, model, oscServer, configuration);

        this.is16 = is16;
        this.daw = is16 ? "/dawctrl/" : "/live/";
    }


    /** {@inheritDoc} */
    @Override
    public void flush (final boolean dump)
    {
        if (!this.isConnected ())
            return;

        final ITransport trans = this.model.getTransport ();
        final ITrackBank tb = this.model.getTrackBank ();
        final ITrackBank tbe = this.model.getEffectTrackBank ();
        final IMasterTrack masterTrack = this.model.getMasterTrack ();
        final ISceneBank sceneBank = this.model.getSceneBank ();

        //
        // Transport
        //

        // 1.x
        this.sendOSC (this.daw + "loop", trans.isLoop (), dump);
        this.sendOSC (this.daw + "undo_redo", DOUBLE_TRUE, dump);

        if (this.is16)
        {
            // 1.6
            this.sendOSC (this.daw + "play", trans.isPlaying (), dump);
            this.sendOSC (this.daw + "record", trans.isRecording (), dump);
            this.sendOSC (this.daw + "session_record", trans.isLauncherOverdub (), dump);
            this.sendOSC (this.daw + "metronome", trans.isMetronomeOn (), dump);
        }
        else
        {
            // 1.5
            this.sendOSC (this.daw + "play", trans.isPlaying () ? 2 : 1, dump);
            this.sendOSC (this.daw + "record", trans.isRecording () ? 2 : 1, dump);
            this.sendOSC (this.daw + "session_record", trans.isLauncherOverdub () ? 2 : 1, dump);
            this.sendOSC (this.daw + "metronome", Integer.valueOf (trans.isMetronomeOn () ? 2 : 1), dump);
        }

        // 1.5
        this.sendOSC (this.daw + "session_automation_record", trans.isWritingClipLauncherAutomation (), dump);
        this.sendOSC (this.daw + "tempo", trans.getTempo (), dump);

        // 1.6
        if (this.is16)
            this.sendOSC (this.daw + "automation", trans.isWritingArrangerAutomation (), dump);

        //
        // Master-/Track(-commands)
        //

        // 1.x
        final List<Object> params = new ArrayList<> ();
        // The three arguments will report the total number of tracks (excluding the master track
        // and return tracks), the number of scenes and the number of return tracks.
        final int trackCount = tb.getItemCount ();
        final int sendTrackCount = tbe == null ? 0 : tbe.getItemCount ();
        Collections.addAll (params, Integer.valueOf (trackCount), Integer.valueOf (sceneBank.getItemCount ()), Integer.valueOf (sendTrackCount));
        this.sendOSC (this.daw + "size", params, dump);

        // 1.x
        ITrack selTrack = tb.getSelectedItem ();
        if (selTrack != null)
        {
            final int trackType = TrackType.toTrackType (selTrack.getType ());
            final List<Object> ps = new ArrayList<> ();
            Collections.addAll (ps, Integer.valueOf (trackType), Integer.valueOf (selTrack.getIndex ()), "");
            if (this.is16)
            {
                // 1.6
                this.sendOSC (this.daw + "track/view", ps, dump);
            }
            else
            {
                // 1.5
                this.sendOSC (this.daw + "track", ps, dump);
            }
        }
        if (tbe != null)
        {
            selTrack = tbe.getSelectedItem ();
            if (selTrack != null)
            {
                final int trackType = TrackType.toTrackType (selTrack.getType ());
                final List<Object> ps = new ArrayList<> ();
                if (this.is16)
                {
                    // 1.6
                    Collections.addAll (ps, Integer.valueOf (trackType), Integer.valueOf (selTrack.getIndex ()), "");
                    this.sendOSC (this.daw + "track/view", ps, dump);
                }
                else
                {
                    // 1.5
                    Collections.addAll (ps, Integer.valueOf (trackType), Integer.valueOf (selTrack.getIndex ()), "");
                    this.sendOSC (this.daw + "track", ps, dump);
                }
            }
        }
        if (masterTrack.isSelected ())
        {
            final List<Object> ps = new ArrayList<> ();
            if (this.is16)
            {
                // 1.6
                Collections.addAll (ps, Integer.valueOf (TrackType.MASTER), Integer.valueOf (0), "");
                this.sendOSC (this.daw + "track/view", ps, dump);
            }
            else
            {
                // 1.5
                Collections.addAll (ps, Integer.valueOf (TrackType.MASTER), Integer.valueOf (0), "");
                this.sendOSC (this.daw + "track", ps, dump);
            }
        }

        this.sendTrackBank (this.is16, tb, trackCount, dump);
        if (tbe != null)
            this.sendTrackBank (this.is16, tbe, sendTrackCount, dump);
        this.sendTrack (this.is16, 0, masterTrack, dump);

        if (this.is16)
        {
            // 1.6
            this.sendOSC (this.daw + "track/device/instance-name", this.getKompleteInstance (), dump);
        }
        else
        {
            // 1.5
            this.sendOSC ("/track/device/instance-name", this.getKompleteInstance (), dump);
        }

        // Send all collected messages
        this.flush ();
    }


    private void sendTrackBank (final boolean is16, final ITrackBank bank, final int trackCount, final boolean dump)
    {
        final int amount = Math.min (trackCount, bank.getPageSize ());
        for (int i = 0; i < amount; i++)
            this.sendTrack (is16, i, bank.getItem (i), dump);
    }


    private void sendTrack (final boolean is16, final int trackIndex, final ITrack track, final boolean dump)
    {
        final int trackType = TrackType.toTrackType (track.getType ());
        final IValueChanger valueChanger = this.model.getValueChanger ();

        final double normalizedVolume = valueChanger.toNormalizedValue (track.getVolume ());

        if (is16)
        {
            // 1.6
            this.sendTrackOSC (this.daw + "track/volume", createTrackValueParameter (trackType, trackIndex, Float.valueOf ((float) normalizedVolume)), dump);
            this.sendTrackOSC (this.daw + "track/pan", createTrackValueParameter (trackType, trackIndex, Float.valueOf ((float) (valueChanger.toNormalizedValue (track.getPan ()) * 2.0 - 1.0))), dump);
            this.sendTrackVuOSC (this.daw + "track/meter", createTrackValueParameter (trackType, trackIndex, Integer.valueOf (0), Float.valueOf ((float) valueChanger.toNormalizedValue (track.getVuLeft ()))), dump);
            this.sendTrackVuOSC (this.daw + "track/meter", createTrackValueParameter (trackType, trackIndex, Integer.valueOf (1), Float.valueOf ((float) valueChanger.toNormalizedValue (track.getVuRight ()))), dump);
            this.sendTrackOSC (this.daw + "track/arm", createTrackValueParameter (trackType, trackIndex, Integer.valueOf (track.isRecArm () ? 1 : 0)), dump);
            this.sendTrackOSC (this.daw + "track/mute", createTrackValueParameter (trackType, trackIndex, Integer.valueOf (track.isMute () ? 1 : 0)), dump);
            this.sendTrackOSC (this.daw + "track/solo", createTrackValueParameter (trackType, trackIndex, Integer.valueOf (track.isSolo () ? 1 : 0)), dump);
        }
        else
        {
            // 1.5
            this.sendTrackOSC (this.daw + "volume", createTrackValueParameter (trackType, trackIndex, Float.valueOf ((float) normalizedVolume)), dump);
            this.sendTrackOSC (this.daw + "pan", createTrackValueParameter (trackType, trackIndex, Float.valueOf ((float) (valueChanger.toNormalizedValue (track.getPan ()) * 2.0 - 1.0))), dump);
            // type and index are switched with these 2 messages...
            this.sendTrackVuOSC (this.daw + "meter", createTrackValueParameter (trackIndex, trackType, Integer.valueOf (0), Float.valueOf ((float) valueChanger.toNormalizedValue (track.getVuLeft ()))), dump);
            this.sendTrackVuOSC (this.daw + "meter", createTrackValueParameter (trackIndex, trackType, Integer.valueOf (1), Float.valueOf ((float) valueChanger.toNormalizedValue (track.getVuRight ()))), dump);
            this.sendTrackOSC (this.daw + "arm", createTrackValueParameter (trackType, trackIndex, Integer.valueOf (track.isRecArm () ? 1 : 0)), dump);
            this.sendTrackOSC (this.daw + "mute", createTrackValueParameter (trackType, trackIndex, Integer.valueOf (track.isMute () ? 1 : 0)), dump);
            this.sendTrackOSC (this.daw + "solo", createTrackValueParameter (trackType, trackIndex, Integer.valueOf (track.isSolo () ? 1 : 0)), dump);
        }

        // Track info needs to be used to update the track name but must only be sent in this case
        final String cacheAddress = new StringBuilder ("/trackname/").append (trackType).append ("/").append (trackIndex).toString ();
        final String name = track.getName ();
        if (!compareValues (this.oldValues.get (cacheAddress), name))
        {
            this.oldValues.put (cacheAddress, name);
            this.sendTrackInfo (trackType, trackIndex);
        }
    }


    /**
     * Send the shutdown message to the host.
     */
    public void shutdown ()
    {
        try
        {
            this.oscServer.sendMessage (this.host.createOSCMessage (this.daw + "shutdown", Collections.emptyList ()));
        }
        catch (final IOException ex)
        {
            this.model.getHost ().error ("Could not send shutdown message.", ex);
        }
    }


    /**
     * Send all info for a track.
     *
     * @param trackType The type, see TrackType
     * @param trackIndex The index of the track, must be less than the size of the configured track
     *            bank
     */
    public void sendTrackInfo (final int trackType, final int trackIndex)
    {
        if (trackIndex >= 0)
            this.fastSendOSC (this.daw + "track/info", this.fillTrackInfoParameters (trackIndex, this.getTrack (trackType, trackIndex)));
    }


    /**
     * Get the track for the given type and index, if any.
     *
     * @param trackType The type, see TrackType
     * @param trackIndex The index of the track, must be less than the size of the configured track
     *            bank
     * @return The track or null if it does not exist
     */
    public ITrack getTrack (final int trackType, final int trackIndex)
    {
        switch (trackType)
        {
            case TrackType.MASTER:
                return this.model.getMasterTrack ();

            case TrackType.EMPTY:
                // Ignore the empty track
                break;

            case TrackType.RETURN_BUS:
                final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
                if (effectTrackBank != null)
                {
                    final int numTracks = effectTrackBank.getPageSize ();
                    if (trackIndex >= numTracks)
                        this.model.getHost ().error ("Track is outside of supported number of tracks (" + numTracks + "): " + trackIndex);
                    else
                        return effectTrackBank.getItem (trackIndex);
                }
                break;

            default:
                final ITrackBank trackBank = this.model.getTrackBank ();
                if (trackBank != null)
                {
                    final int numTracks = trackBank.getPageSize ();
                    if (trackIndex >= numTracks)
                        this.model.getHost ().error ("Track is outside of supported number of tracks (" + numTracks + "): " + trackIndex);
                    else
                        return trackBank.getItem (trackIndex);
                }
                break;
        }
        return EmptyTrackData.INSTANCE;
    }


    private void sendTrackVuOSC (final String address, final List<Object> values, final boolean dump)
    {
        final List<Object> testValues = new ArrayList<> (values);
        final String cacheAddress = new StringBuilder (address).append ("/").append (testValues.remove (0)).append ("/").append (testValues.remove (0)).append ("/").append (testValues.remove (0)).toString ();
        this.sendOSC (cacheAddress, address, testValues, values, dump);
    }


    private void sendTrackOSC (final String address, final List<Object> values, final boolean dump)
    {
        final List<Object> testValues = new ArrayList<> (values);
        final String cacheAddress = new StringBuilder (address).append ("/").append (testValues.remove (0)).append ("/").append (testValues.remove (0)).toString ();
        this.sendOSC (cacheAddress, address, testValues, values, dump);
    }


    /**
     * Get the name of an Komplete Kontrol instance on the current track, or an empty string
     * otherwise. A track contains a Komplete Kontrol instance if: There is an instance of a plugin
     * whose name starts with Komplete Kontrol and the first parameter label exposed by the plugin
     * is NIKBxx, where xx is a number between 00 and 99 If the conditions are satisfied.
     *
     * @return The instance name, which is the actual label of the first parameter (e.g. NIKB01). An
     *         empty string if none is present
     */
    private String getKompleteInstance ()
    {
        final ICursorDevice instrumentDevice = this.model.getPrimaryDevice ();
        if (instrumentDevice.doesExist () && instrumentDevice.getName ().startsWith ("Komplete Kontrol"))
        {
            // "NIKBxx";
            final String kompleteID = instrumentDevice.getParameterBank ().getItem (0).getName ();
            return kompleteID;
        }
        return "";
    }


    private List<Object> fillTrackInfoParameters (final int trackIndex, final ITrack track)
    {
        final IValueChanger valueChanger = this.model.getValueChanger ();
        final List<Object> parameters = new ArrayList<> ();
        parameters.add (Integer.valueOf (TrackType.toTrackType (track.getType ())));
        parameters.add (Integer.valueOf (trackIndex));
        parameters.add (track.getName ());
        parameters.add (Integer.valueOf (1)); // TODO Spec missing: How to convert? track.getColor
                                              // ()
        parameters.add (Integer.valueOf (track.isRecArm () ? 1 : 0));
        parameters.add (Integer.valueOf (track.isSolo () ? 1 : 0));
        parameters.add (Integer.valueOf (track.isMute () ? 1 : 0));
        parameters.add (Float.valueOf ((float) valueChanger.toNormalizedValue (track.getVolume ())));
        parameters.add (Float.valueOf ((float) (valueChanger.toNormalizedValue (track.getPan ()) * 2.0 - 1.0)));
        return parameters;
    }


    /**
     * Creates a parameter list with track type and index.
     *
     * @param trackType The track type
     * @param trackIndex The track index
     * @param values Other values
     * @return The full parameter list
     */
    private static List<Object> createTrackValueParameter (final int trackType, final int trackIndex, final Object... values)
    {
        final List<Object> parameters = new ArrayList<> ();
        parameters.add (Integer.valueOf (trackType));
        parameters.add (Integer.valueOf (trackIndex));
        Collections.addAll (parameters, values);
        return parameters;
    }


    /** {@inheritDoc} */
    @Override
    protected boolean isHeartbeatMessage (final String address)
    {
        return address.contains ("pong");
    }
}
