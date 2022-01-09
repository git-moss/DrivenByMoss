// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc.module;

import de.mossgrabers.controller.osc.OSCConfiguration;
import de.mossgrabers.controller.osc.exception.IllegalParameterException;
import de.mossgrabers.controller.osc.exception.MissingCommandException;
import de.mossgrabers.controller.osc.exception.UnknownCommandException;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.osc.IOpenSoundControlWriter;

import java.util.LinkedList;


/**
 * All action related commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ActionModule extends AbstractModule
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
    public ActionModule (final IHost host, final IModel model, final IOpenSoundControlWriter writer, final OSCConfiguration configuration)
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
            "action",
            "actionID"
        };
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final String command, final LinkedList<String> path, final Object value) throws IllegalParameterException, UnknownCommandException, MissingCommandException
    {
        switch (command)
        {
            case "action":
                final String subCommand = getSubCommand (path);                
                try
                {
                    final int actionNo = Math.min (7, Math.max (0, Integer.parseInt (subCommand) - 1));
                    final String assignableActionID = this.configuration.getAssignableAction (actionNo);
                    if (assignableActionID != null)
                        this.model.getApplication ().invokeAction (assignableActionID);
                }
                catch (final NumberFormatException ex)
                {
                    throw new UnknownCommandException (subCommand);
                }
                break;
            
            case "actionID":
                this.model.getApplication ().invokeAction( value.toString() );
                break;
                
            default:
                throw new UnknownCommandException (command);
        }
    }
}
