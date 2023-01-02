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
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.osc.IOpenSoundControlWriter;

import java.util.LinkedList;


/**
 * All global related commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SceneModule extends AbstractModule
{
    /**
     * Constructor.
     *
     * @param host The host
     * @param model The model
     * @param writer The writer
     */
    public SceneModule (final IHost host, final IModel model, final IOpenSoundControlWriter writer)
    {
        super (host, model, writer);
    }


    /** {@inheritDoc} */
    @Override
    public String [] getSupportedCommands ()
    {
        return new String []
        {
            "scene"
        };
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final String command, final LinkedList<String> path, final Object value) throws IllegalParameterException, UnknownCommandException, MissingCommandException
    {
        if (!"scene".equals (command))
            throw new UnknownCommandException (command);

        final String sceneCommand = getSubCommand (path);
        final ISceneBank sceneBank = this.model.getCurrentTrackBank ().getSceneBank ();
        switch (sceneCommand)
        {
            case "bank":
                final String subCommand2 = getSubCommand (path);
                switch (subCommand2)
                {
                    case "+":
                        if (isTrigger (value))
                            sceneBank.selectNextPage ();
                        break;
                    case "-":
                        if (isTrigger (value))
                            sceneBank.selectPreviousPage ();
                        break;
                    default:
                        throw new UnknownCommandException (subCommand2);
                }
                break;

            case "+":
                if (isTrigger (value))
                    sceneBank.scrollForwards ();
                break;

            case "-":
                if (isTrigger (value))
                    sceneBank.scrollBackwards ();
                break;

            case "create":
                if (isTrigger (value))
                    this.model.getProject ().createSceneFromPlayingLauncherClips ();
                break;

            case "add":
                if (isTrigger (value))
                    this.model.getProject ().createScene ();
                break;

            default:
                final int sceneIndex = Integer.parseInt (sceneCommand);
                final IScene scene = sceneBank.getItem (sceneIndex - 1);
                final String sceneCommand2 = getSubCommand (path);
                switch (sceneCommand2)
                {
                    case "launch":
                        scene.launch ();
                        break;

                    case TAG_DUPLICATE:
                        scene.duplicate ();
                        break;

                    case TAG_REMOVE:
                        scene.remove ();
                        break;

                    default:
                        throw new UnknownCommandException (sceneCommand2);
                }
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void flush (final boolean dump)
    {
        final ISceneBank sceneBank = this.model.getSceneBank ();
        for (int i = 0; i < sceneBank.getPageSize (); i++)
        {
            final IScene scene = sceneBank.getItem (i);
            final String sceneAddress = "/scene/" + (i + 1) + "/";
            this.writer.sendOSC (sceneAddress + TAG_EXISTS, scene.doesExist (), dump);
            this.writer.sendOSC (sceneAddress + TAG_NAME, scene.getName (), dump);
            this.writer.sendOSC (sceneAddress + TAG_SELECTED, scene.isSelected (), dump);

            ColorEx color = scene.getColor ();
            if (color == null)
                color = ColorEx.BLACK;
            this.writer.sendOSCColor (sceneAddress + TAG_COLOR, color.getRed (), color.getGreen (), color.getBlue (), dump);
        }
    }
}
