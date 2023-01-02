// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc.protocol;

import de.mossgrabers.controller.osc.OSCControlSurface;
import de.mossgrabers.controller.osc.exception.IllegalParameterException;
import de.mossgrabers.controller.osc.exception.MissingCommandException;
import de.mossgrabers.controller.osc.exception.UnknownCommandException;
import de.mossgrabers.controller.osc.module.IModule;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.osc.AbstractOpenSoundControlParser;
import de.mossgrabers.framework.osc.IOpenSoundControlConfiguration;
import de.mossgrabers.framework.osc.IOpenSoundControlMessage;
import de.mossgrabers.framework.osc.IOpenSoundControlWriter;
import de.mossgrabers.framework.utils.KeyManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 * Parser for OSC messages.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OSCParser extends AbstractOpenSoundControlParser
{
    private final OSCControlSurface    surface;
    private final Map<String, IModule> modules = new HashMap<> ();


    /**
     * Constructor.
     *
     * @param host The host
     * @param surface The control surface
     * @param model The model
     * @param configuration The configuration
     * @param writer The OSC writer
     * @param midiInput The MIDI input
     * @param keyManager The key manager
     */
    public OSCParser (final IHost host, final OSCControlSurface surface, final IModel model, final IOpenSoundControlConfiguration configuration, final IOpenSoundControlWriter writer, final IMidiInput midiInput, final KeyManager keyManager)
    {
        super (host, model, midiInput, configuration, writer);

        this.surface = surface;

        this.model.getCurrentTrackBank ().setIndication (true);
        this.surface.setKeyTranslationTable (model.getScales ().getNoteMatrix ());
    }


    /** {@inheritDoc} */
    @Override
    public void handle (final IOpenSoundControlMessage message)
    {
        this.logMessage (message);

        final LinkedList<String> oscParts = parseAddress (message);
        if (oscParts.isEmpty ())
            return;

        final String command = oscParts.removeFirst ();
        if ("refresh".equals (command))
        {
            this.writer.flush (true);
            return;
        }

        final Object [] values = message.getValues ();
        try
        {
            final IModule module = this.modules.get (command);
            if (module == null)
                throw new UnknownCommandException (command);
            if (values != null && values.length > 1)
                module.execute (command, oscParts, values);
            else
                module.execute (command, oscParts, values == null || values.length == 0 ? null : values[0]);
        }
        catch (final IllegalParameterException ex)
        {
            this.host.println ("Illegal parameter: " + message.getAddress () + " " + ex.getMessage ());
        }
        catch (final UnknownCommandException ex)
        {
            this.host.println ("Unknown OSC command: " + message.getAddress () + " " + ex.getMessage ());
        }
        catch (final MissingCommandException ex)
        {
            this.host.println ("Missing command: " + message.getAddress ());
        }
    }


    /**
     * Parses the OSC message into seprate parts.
     *
     * @param message The message
     * @return The split parts
     */
    private static LinkedList<String> parseAddress (final IOpenSoundControlMessage message)
    {
        final LinkedList<String> oscParts = new LinkedList<> ();
        Collections.addAll (oscParts, message.getAddress ().split ("/"));

        // Remove first empty element
        oscParts.removeFirst ();
        return oscParts;
    }


    /**
     * Register a command module.
     *
     * @param module The module to register
     */
    public void registerModule (final IModule module)
    {
        Arrays.asList (module.getSupportedCommands ()).forEach (command -> this.modules.put (command, module));
    }
}