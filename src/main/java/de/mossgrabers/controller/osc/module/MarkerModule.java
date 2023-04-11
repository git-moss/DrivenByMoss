// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc.module;

import de.mossgrabers.controller.osc.exception.IllegalParameterException;
import de.mossgrabers.controller.osc.exception.MissingCommandException;
import de.mossgrabers.controller.osc.exception.UnknownCommandException;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IMarker;
import de.mossgrabers.framework.daw.data.bank.IMarkerBank;
import de.mossgrabers.framework.osc.IOpenSoundControlWriter;

import java.util.LinkedList;


/**
 * All marker related commands.
 *
 * @author Jürgen Moßgraber
 */
public class MarkerModule extends AbstractModule
{
    /**
     * Constructor.
     *
     * @param host The host
     * @param model The model
     * @param writer The writer
     */
    public MarkerModule (final IHost host, final IModel model, final IOpenSoundControlWriter writer)
    {
        super (host, model, writer);
    }


    /** {@inheritDoc} */
    @Override
    public String [] getSupportedCommands ()
    {
        return new String []
        {
            "marker"
        };
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final String command, final LinkedList<String> path, final Object value) throws IllegalParameterException, UnknownCommandException, MissingCommandException
    {
        if (!"marker".equals (command))
            throw new UnknownCommandException (command);

        final String subCommand = getSubCommand (path);
        try
        {
            final int markerNo = Integer.parseInt (subCommand) - 1;
            final String subCommand2 = getSubCommand (path);
            if ("launch".equals (subCommand2))
                this.model.getMarkerBank ().getItem (markerNo).launch (true);
            else
                throw new UnknownCommandException (subCommand2);
        }
        catch (final NumberFormatException ex)
        {
            final IMarkerBank markerBank = this.model.getMarkerBank ();
            if ("bank".equals (subCommand))
            {
                final String subCommand2 = getSubCommand (path);
                switch (subCommand2)
                {
                    case "+":
                        markerBank.selectNextPage ();
                        break;
                    case "-":
                        markerBank.selectPreviousPage ();
                        break;
                    default:
                        throw new UnknownCommandException (subCommand2);
                }
            }
            else
                throw new UnknownCommandException (subCommand);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void flush (final boolean dump)
    {
        final IMarkerBank markerBank = this.model.getMarkerBank ();
        for (int i = 0; i < markerBank.getPageSize (); i++)
        {
            final String markerAddress = "/marker/" + (i + 1) + "/";
            final IMarker marker = markerBank.getItem (i);
            this.writer.sendOSC (markerAddress + "exists", marker.doesExist (), dump);
            this.writer.sendOSC (markerAddress + TAG_NAME, marker.getName (), dump);
            final ColorEx color = marker.getColor ();
            this.writer.sendOSCColor (markerAddress + "color", color.getRed (), color.getGreen (), color.getBlue (), dump);
        }
    }
}