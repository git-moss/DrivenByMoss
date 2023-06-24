// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc.module;

import de.mossgrabers.controller.osc.exception.IllegalParameterException;
import de.mossgrabers.controller.osc.exception.MissingCommandException;
import de.mossgrabers.controller.osc.exception.UnknownCommandException;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IProject;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.osc.IOpenSoundControlWriter;
import de.mossgrabers.framework.parameter.IParameter;

import java.util.LinkedList;
import java.util.Optional;


/**
 * All project related commands.
 *
 * @author Jürgen Moßgraber
 */
public class ProjectModule extends AbstractModule
{
    /**
     * Constructor.
     *
     * @param host The host
     * @param model The model
     * @param writer The writer
     */
    public ProjectModule (final IHost host, final IModel model, final IOpenSoundControlWriter writer)
    {
        super (host, model, writer);
    }


    /** {@inheritDoc} */
    @Override
    public String [] getSupportedCommands ()
    {
        return new String []
        {
            "project"
        };
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final String command, final LinkedList<String> path, final Object value) throws IllegalParameterException, UnknownCommandException, MissingCommandException
    {
        if (!"project".equals (command))
            throw new UnknownCommandException (command);

        final String subCommand = getSubCommand (path);
        final IProject project = this.model.getProject ();
        switch (subCommand)
        {
            case "+":
                project.next ();
                break;

            case "-":
                project.previous ();
                break;

            case "engine":
                final IApplication application = this.model.getApplication ();
                if (value == null)
                    application.toggleEngineActive ();
                else
                    application.setEngineActive (isTrigger (value));
                break;

            case "save":
                project.save ();
                break;

            case "param":
                this.parseParamValue (path, value);
                break;

            case "page":
                this.parsePageValue (path, value);
                break;

            default:
                throw new UnknownCommandException (subCommand);
        }
    }


    private void parseParamValue (final LinkedList<String> path, final Object value) throws UnknownCommandException, MissingCommandException, IllegalParameterException
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

                default:
                    throw new UnknownCommandException (subCommand);
            }
        }
    }


    private void parsePageValue (final LinkedList<String> path, final Object value) throws UnknownCommandException, MissingCommandException, IllegalParameterException
    {
        final IParameterBank parameterBank = this.model.getProject ().getParameterBank ();
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


    /** {@inheritDoc} */
    @Override
    public void flush (final boolean dump)
    {
        this.writer.sendOSC ("/project/name", this.model.getProject ().getName (), dump);
        this.writer.sendOSC ("/project/engine", this.model.getApplication ().isEngineActive (), dump);

        final String paramAddress = "/project/param/";
        final IParameterBank parameterBank = this.model.getProject ().getParameterBank ();
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
            final String pageAddress = "/project/page/" + oneplus + "/";
            this.writer.sendOSC (pageAddress + TAG_EXISTS, !pageName.isBlank (), dump);
            this.writer.sendOSC (pageAddress, pageName, dump);
            this.writer.sendOSC (pageAddress + TAG_NAME, pageName, dump);
            this.writer.sendOSC (pageAddress + TAG_SELECTED, selectedParameterPage == i, dump);
        }
        final Optional<String> selectedItem = parameterPageBank.getSelectedItem ();
        this.writer.sendOSC ("/project/page/selected/" + TAG_NAME, selectedItem.isPresent () ? selectedItem.get () : "", dump);
    }
}
