// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc.module;

import de.mossgrabers.controller.osc.exception.IllegalParameterException;
import de.mossgrabers.controller.osc.exception.MissingCommandException;
import de.mossgrabers.controller.osc.exception.UnknownCommandException;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.osc.IOpenSoundControlWriter;
import de.mossgrabers.framework.parameter.IParameter;

import java.util.LinkedList;


/**
 * All user related commands.
 *
 * @author Jürgen Moßgraber
 */
public class UserModule extends AbstractModule
{
    /**
     * Constructor.
     *
     * @param host The host
     * @param model The model
     * @param writer The writer
     */
    public UserModule (final IHost host, final IModel model, final IOpenSoundControlWriter writer)
    {
        super (host, model, writer);
    }


    /** {@inheritDoc} */
    @Override
    public String [] getSupportedCommands ()
    {
        return new String []
        {
            "user"
        };
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final String command, final LinkedList<String> path, final Object value) throws IllegalParameterException, UnknownCommandException, MissingCommandException
    {
        if ("user".equals (command))
            this.parseUserValue (path, value);
        else
            throw new UnknownCommandException (command);
    }


    /** {@inheritDoc} */
    @Override
    public void flush (final boolean dump)
    {
        final String userAddress = "/user/";

        final IParameterBank parameterBank = this.model.getProject ().getParameterBank ();
        for (int i = 0; i < parameterBank.getPageSize (); i++)
        {
            final int oneplus = i + 1;
            this.flushParameterData (this.writer, userAddress + oneplus + "/", parameterBank.getItem (i), dump);
        }

        final int numPages = parameterBank.getItemCount () / parameterBank.getPageSize ();
        final int selectedPage = parameterBank.getScrollPosition () / parameterBank.getPageSize ();
        for (int i = 0; i < numPages; i++)
        {
            final int oneplus = i + 1;
            this.writer.sendOSC (userAddress + "page/" + oneplus + "/", "Page " + (i + 1), dump);
            this.writer.sendOSC (userAddress + "page/" + oneplus + "/selected", selectedPage == i, dump);
        }
        this.writer.sendOSC (userAddress + "page/selected/name", "Page " + (selectedPage + 1), dump);
    }


    private void parseUserValue (final LinkedList<String> path, final Object value) throws UnknownCommandException, MissingCommandException, IllegalParameterException
    {
        final IParameterBank parameterBank = this.model.getProject ().getParameterBank ();
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

                case "page":
                    final String pageCommand = getSubCommand (path);
                    if ("select".equals (pageCommand) || "selected".equals (pageCommand))
                    {
                        this.selectPage (parameterBank, toInteger (value) - 1);
                    }
                    else
                    {
                        try
                        {
                            final int index = Integer.parseInt (pageCommand) - 1;
                            this.selectPage (parameterBank, index);
                        }
                        catch (final NumberFormatException ex2)
                        {
                            throw new UnknownCommandException (pageCommand);
                        }
                    }
                    break;

                default:
                    throw new UnknownCommandException (subCommand);
            }
        }
    }


    private void selectPage (final IParameterBank parameterBank, final int index)
    {
        parameterBank.scrollTo (index * parameterBank.getPageSize ());
        // We need to do a manual flush since the manual paging does not
        // trigger a flush
        this.flush (false);
        this.writer.flush (false);
    }


    private static void parseFXParamValue (final IParameter parameter, final LinkedList<String> path, final Object value) throws UnknownCommandException, MissingCommandException, IllegalParameterException
    {
        final String command = getSubCommand (path);
        switch (command)
        {
            case "value":
                parameter.setValue (toInteger (value));
                break;

            case "indicate":
                parameter.setIndication (isTrigger (value));
                break;

            case "reset":
                parameter.resetValue ();
                break;

            case "touched":
                parameter.touchValue (isTrigger (value));
                break;

            default:
                throw new UnknownCommandException (command);
        }
    }
}
