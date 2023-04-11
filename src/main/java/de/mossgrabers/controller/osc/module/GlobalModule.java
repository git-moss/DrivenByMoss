// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc.module;

import de.mossgrabers.controller.osc.exception.IllegalParameterException;
import de.mossgrabers.controller.osc.exception.MissingCommandException;
import de.mossgrabers.controller.osc.exception.UnknownCommandException;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.osc.IOpenSoundControlWriter;

import java.util.LinkedList;


/**
 * All global related commands.
 *
 * @author Jürgen Moßgraber
 */
public class GlobalModule extends AbstractModule
{
    /**
     * Constructor.
     *
     * @param host The host
     * @param model The model
     * @param writer The writer
     */
    public GlobalModule (final IHost host, final IModel model, final IOpenSoundControlWriter writer)
    {
        super (host, model, writer);
    }


    /** {@inheritDoc} */
    @Override
    public String [] getSupportedCommands ()
    {
        return new String []
        {
            "undo",
            "redo"
        };
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final String command, final LinkedList<String> path, final Object value) throws IllegalParameterException, UnknownCommandException, MissingCommandException
    {
        switch (command)
        {
            case "undo":
                this.model.getApplication ().undo ();
                break;

            case "redo":
                this.model.getApplication ().redo ();
                break;

            default:
                throw new UnknownCommandException (command);
        }
    }
}
