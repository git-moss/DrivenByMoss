// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc.module;

import de.mossgrabers.controller.osc.exception.IllegalParameterException;
import de.mossgrabers.controller.osc.exception.MissingCommandException;
import de.mossgrabers.controller.osc.exception.UnknownCommandException;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IProject;
import de.mossgrabers.framework.osc.IOpenSoundControlWriter;

import java.util.LinkedList;


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
                if (value == null)
                    this.model.getApplication ().toggleEngineActive ();
                else
                    this.model.getApplication ().setEngineActive (isTrigger (value));
                break;

            case "save":
                project.save ();
                break;

            default:
                throw new UnknownCommandException (subCommand);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void flush (final boolean dump)
    {
        this.writer.sendOSC ("/project/name", this.model.getProject ().getName (), dump);
        this.writer.sendOSC ("/project/engine", this.model.getApplication ().isEngineActive (), dump);
    }
}
