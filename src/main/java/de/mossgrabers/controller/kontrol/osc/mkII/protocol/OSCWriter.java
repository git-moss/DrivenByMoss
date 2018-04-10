// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.osc.mkII.protocol;

import de.mossgrabers.controller.kontrol.osc.mkII.KontrolOSCConfiguration;
import de.mossgrabers.controller.kontrol.osc.mkII.TrackType;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.EmptyTrackData;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.StringUtils;

import com.bitwig.extension.api.opensoundcontrol.OscConnection;
import com.bitwig.extension.api.opensoundcontrol.OscInvalidArgumentTypeException;
import com.bitwig.extension.api.opensoundcontrol.OscModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Writes the changed DAW stati as OSC messages.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OSCWriter
{
    private static final List<Object> DOUBLE_TRUE = new ArrayList<> ();
    static
    {
        Collections.addAll (DOUBLE_TRUE, Integer.valueOf (1), Integer.valueOf (1));
    }

    private static final ITrack     EMPTY_TRACK = new EmptyTrackData ();

    private OSCModel                model;
    private Map<String, Object>     oldValues   = new HashMap<> ();
    private List<OscMessageData>    messages    = new ArrayList<> ();
    private KontrolOSCConfiguration configuration;
    private OscConnection           udpServer;
    private boolean                 logEnabled;

    private final String            daw;
    private final boolean           is16;


    /**
     * Constructor.
     *
     * @param is16 If true use 1.6 protocol otherwise 1.5
     * @param logEnabled Enable message logging
     * @param model The model
     * @param configuration The configuration
     * @param oscModule The UDP server to send to
     */
    public OSCWriter (final boolean is16, final boolean logEnabled, final OSCModel model, final KontrolOSCConfiguration configuration, final OscModule oscModule)
    {
        this.is16 = is16;

        this.logEnabled = logEnabled;
        this.model = model;
        this.configuration = configuration;
        this.daw = is16 ? "/dawctrl/" : "/live/";

        this.udpServer = oscModule.connectToUdpServer (this.configuration.getSendHost (), this.configuration.getSendPort (), oscModule.createAddressSpace ());
    }


    /**
     * Flush out all values.
     *
     * @param dump Forces a flush if true otherwise only changed values are flushed
     */
    public void sendFrequentProperties (final boolean dump)
    {
        if (this.udpServer == null)
            return;

        final ITransport trans = this.model.getTransport ();
        final ITrackBank tb = this.model.getTrackBank ();
        final IChannelBank tbe = this.model.getEffectTrackBank ();
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
        final int trackCount = tb.getTrackCount ();
        final List<Object> params = new ArrayList<> ();
        Collections.addAll (params, Integer.valueOf (trackCount), Integer.valueOf (sceneBank.getSceneCount ()), Integer.valueOf (tbe == null ? 0 : tbe.getTrackCount ()));
        this.sendOSC (this.daw + "size", params, dump);

        // 1.x
        ITrack selTrack = tb.getSelectedTrack ();
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
            selTrack = tbe.getSelectedTrack ();
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
            this.sendTrackBank (this.is16, tbe, trackCount, dump);
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


    private void sendTrackBank (final boolean is16, final IChannelBank bank, final int trackCount, final boolean dump)
    {
        for (int i = 0; i < Math.min (trackCount, bank.getNumTracks ()); i++)
            this.sendTrack (is16, i, bank.getTrack (i), dump);
    }


    private void sendTrack (final boolean is16, final int trackIndex, final ITrack track, final boolean dump)
    {
        final int trackType = TrackType.toTrackType (track.getType ());
        final IValueChanger valueChanger = this.model.getValueChanger ();

        if (is16)
        {
            // 1.6
            this.sendTrackOSC (this.daw + "track/volume", createTrackValueParameter (trackType, trackIndex, Float.valueOf ((float) valueChanger.toNormalizedValue (track.getVolume ()))), dump);
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
            this.sendTrackOSC (this.daw + "volume", createTrackValueParameter (trackType, trackIndex, Float.valueOf ((float) valueChanger.toNormalizedValue (track.getVolume ()))), dump);
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
     * Flush out all collected messages.
     */
    public void flush ()
    {
        synchronized (this.messages)
        {
            try
            {
                int pos = 0;
                this.udpServer.startBundle ();
                for (final OscMessageData message: this.messages)
                {
                    this.logMessage (message, this.logEnabled, false);

                    final String address = message.getAddress ();
                    final Object [] values = message.getValues ();
                    this.udpServer.sendMessage (address, values);
                    pos++;
                    if (pos > 1000)
                    {
                        pos = 0;
                        this.udpServer.endBundle ();
                        this.udpServer.startBundle ();
                    }
                }
                this.udpServer.endBundle ();
            }
            catch (final IOException ex)
            {
                this.model.getHost ().error ("Could not send UDP message.", ex);
            }

            this.messages.clear ();
        }
    }


    /**
     * Sends the message and calls flush.
     *
     * @param address The OSC address
     * @param numbers Integer parameters
     */
    public void fastSendOSC (final String address, final int [] numbers)
    {
        final List<Object> params = new ArrayList<> ();
        for (final int number: numbers)
            params.add (Integer.valueOf (number));
        this.fastSendOSC (address, params);
    }


    /**
     * Sends the message and calls flush.
     *
     * @param address The OSC address
     */
    public void fastSendOSC (final String address)
    {
        this.fastSendOSC (address, Collections.emptyList ());
    }


    /**
     * Send the shutdown message to the host.
     */
    public void shutdown ()
    {
        try
        {
            this.udpServer.sendMessage (this.daw + "shutdown");
        }
        catch (final OscInvalidArgumentTypeException | IOException ex)
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
                final IChannelBank effectTrackBank = this.model.getEffectTrackBank ();
                if (effectTrackBank != null)
                {
                    final int numTracks = effectTrackBank.getNumTracks ();
                    if (trackIndex >= numTracks)
                        this.model.getHost ().error ("Track is outside of supported number of tracks (" + numTracks + "): " + trackIndex);
                    else
                        return effectTrackBank.getTrack (trackIndex);
                }
                break;

            default:
                final ITrackBank trackBank = this.model.getTrackBank ();
                if (trackBank != null)
                {
                    final int numTracks = trackBank.getNumTracks ();
                    if (trackIndex >= numTracks)
                        this.model.getHost ().error ("Track is outside of supported number of tracks (" + numTracks + "): " + trackIndex);
                    else
                        return trackBank.getTrack (trackIndex);
                }
                break;
        }
        return EMPTY_TRACK;
    }


    private void sendTrackVuOSC (final String address, final List<Object> values, final boolean dump)
    {
        final List<Object> testValues = new ArrayList<> (values);
        final String cacheAddress = new StringBuilder (address).append ("/").append (testValues.remove (0)).append ("/").append (testValues.remove (0)).append ("/").append (testValues.remove (0)).toString ();

        if (!dump && compareValues (this.oldValues.get (cacheAddress), testValues))
            return;
        this.oldValues.put (cacheAddress, testValues);

        synchronized (this.messages)
        {
            this.messages.add (new OscMessageData (address, values));
        }
    }


    private void sendTrackOSC (final String address, final List<Object> values, final boolean dump)
    {
        final List<Object> testValues = new ArrayList<> (values);
        final String cacheAddress = new StringBuilder (address).append ("/").append (testValues.remove (0)).append ("/").append (testValues.remove (0)).toString ();

        if (!dump && compareValues (this.oldValues.get (cacheAddress), testValues))
            return;
        this.oldValues.put (cacheAddress, testValues);

        synchronized (this.messages)
        {
            this.messages.add (new OscMessageData (address, values));
        }
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
            // TODO The parameter seems to be only empty (also does not work with Live
            // Therefore, lets send always the first one
            return "NIKB00";
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
        parameters.add (Integer.valueOf (1)); // TODO How to convert? track.getColor ()
        parameters.add (Integer.valueOf (track.isRecArm () ? 1 : 0));
        parameters.add (Integer.valueOf (track.isSolo () ? 1 : 0));
        parameters.add (Integer.valueOf (track.isMute () ? 1 : 0));
        parameters.add (Float.valueOf ((float) valueChanger.toNormalizedValue (track.getVolume ())));
        parameters.add (Float.valueOf ((float) (valueChanger.toNormalizedValue (track.getPan ()) * 2.0 - 1.0)));
        return parameters;
    }


    private static List<Object> createTrackValueParameter (final int trackType, final int trackIndex, final Object... values)
    {
        final List<Object> parameters = new ArrayList<> ();
        parameters.add (Integer.valueOf (trackType));
        parameters.add (Integer.valueOf (trackIndex));
        Collections.addAll (parameters, values);
        return parameters;
    }


    private void fastSendOSC (final String address, final List<Object> parameters)
    {
        this.sendOSC (address, parameters, true);
        this.flush ();
    }


    private void sendOSC (final String address, final boolean value, final boolean dump)
    {
        this.sendOSC (address, Boolean.valueOf (value), dump);
    }


    private void sendOSC (final String address, final double value, final boolean dump)
    {
        // Using float here since Double seems to be always received as 0 in Max.
        this.sendOSC (address, Float.valueOf ((float) value), dump);
    }


    private void sendOSC (final String address, final int value, final boolean dump)
    {
        this.sendOSC (address, Integer.valueOf (value), dump);
    }


    private void sendOSC (final String address, final String value, final boolean dump)
    {
        this.sendOSC (address, (Object) StringUtils.fixASCII (value), dump);
    }


    private void sendOSC (final String address, final Object value, final boolean dump)
    {
        if (!dump && compareValues (this.oldValues.get (address), value))
            return;

        this.oldValues.put (address, value);

        synchronized (this.messages)
        {
            this.messages.add (new OscMessageData (address, convertToList (value)));
        }
    }


    /**
     * Convert the value to a list in case it is not already one. Also converts Boolean to Integer.
     *
     * @param value The value to convert
     * @return The converted value
     */
    @SuppressWarnings("unchecked")
    private static List<Object> convertToList (final Object value)
    {
        if (value instanceof List)
            return List.class.cast (value);
        if (value instanceof Boolean)
            return Collections.singletonList (Integer.valueOf (((Boolean) value).booleanValue () ? 1 : 0));
        return Collections.singletonList (value);
    }


    /**
     * Compares two values. Additionally checks for list values.
     *
     * @param value1 The first value
     * @param value2 The second value
     * @return True if equal
     */
    private static boolean compareValues (final Object value1, final Object value2)
    {
        if (value1 == null)
            return value2 == null;

        if (value1 instanceof List && value2 instanceof List)
        {
            final List<?> l1 = List.class.cast (value1);
            final List<?> l2 = List.class.cast (value2);
            final int size1 = l1.size ();
            final int size2 = l2.size ();
            if (size1 != size2)
                return false;
            for (int i = 0; i < size1; i++)
            {
                if (!l1.get (i).equals (l2.get (i)))
                    return false;
            }
            return true;
        }

        return value1.equals (value2);
    }


    private void logMessage (final OscMessageData message, final boolean log, final boolean logPong)
    {
        if (!log)
            return;
        final String address = message.getAddress ();
        if (logPong || !address.contains ("pong"))
            this.model.getHost ().println ("Sending: " + address + " [ " + formatValues (message) + " ]");
    }


    private static String formatValues (final OscMessageData message)
    {
        final Object [] values = message.getValues ();
        final StringBuilder sb = new StringBuilder ();
        for (int i = 0; i < values.length; i++)
        {
            if (i > 0)
                sb.append (", ");
            sb.append (values[i]);
        }
        return sb.toString ();
    }
}
