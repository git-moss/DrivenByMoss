// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc.module;

import de.mossgrabers.controller.osc.exception.IllegalParameterException;
import de.mossgrabers.controller.osc.exception.MissingCommandException;
import de.mossgrabers.controller.osc.exception.UnknownCommandException;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IBrowserColumn;
import de.mossgrabers.framework.daw.data.IBrowserColumnItem;
import de.mossgrabers.framework.osc.IOpenSoundControlWriter;

import java.util.LinkedList;


/**
 * All browser related commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BrowserModule extends AbstractModule
{
    /**
     * Constructor.
     *
     * @param host The host
     * @param model The model
     * @param writer The writer
     */
    public BrowserModule (final IHost host, final IModel model, final IOpenSoundControlWriter writer)
    {
        super (host, model, writer);
    }


    /** {@inheritDoc} */
    @Override
    public String [] getSupportedCommands ()
    {
        return new String []
        {
            "browser"
        };
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final String command, final LinkedList<String> path, final Object value) throws IllegalParameterException, UnknownCommandException, MissingCommandException
    {
        switch (command)
        {
            case "browser":
                final String subCommand = getSubCommand (path);
                final IBrowser browser = this.model.getBrowser ();
                switch (subCommand)
                {
                    case "preset":
                        browser.replace (this.model.getCursorDevice ());
                        break;

                    case "tab":
                        if (!browser.isActive ())
                            return;
                        final String subCmd = getSubCommand (path);
                        if ("+".equals (subCmd))
                            browser.nextContentType ();
                        else if ("-".equals (subCmd))
                            browser.previousContentType ();
                        break;

                    case "device":
                        final String insertLocation = path.isEmpty () ? null : path.removeFirst ();
                        if (insertLocation == null || "after".equals (insertLocation))
                            browser.insertAfterCursorDevice ();
                        else
                            browser.insertBeforeCursorDevice ();
                        break;

                    case "commit":
                        browser.stopBrowsing (true);
                        break;

                    case "cancel":
                        browser.stopBrowsing (false);
                        break;

                    case "filter":
                        final String indexCmd = getSubCommand (path);
                        int column = Integer.parseInt (indexCmd);
                        if (column < 1 || column > 6)
                            return;
                        column = column - 1;
                        if (!browser.isActive ())
                            return;

                        final String cmd = getSubCommand (path);
                        if ("+".equals (cmd))
                            browser.selectNextFilterItem (column);
                        else if ("-".equals (cmd))
                            browser.selectPreviousFilterItem (column);
                        else if ("reset".equals (cmd))
                            browser.getFilterColumn (column).resetFilter ();
                        break;

                    case "result":
                        if (!browser.isActive ())
                            return;
                        final String direction = path.isEmpty () ? "+" : path.removeFirst ();
                        if ("+".equals (direction))
                            browser.selectNextResult ();
                        else
                            browser.selectPreviousResult ();
                        break;

                    default:
                        throw new UnknownCommandException (command);
                }
                break;

            default:
                throw new UnknownCommandException (command);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void flush (final boolean dump)
    {
        final String browserAddress = "/browser/";
        final IBrowser browser = this.model.getBrowser ();

        final boolean isActive = browser.isActive ();
        this.writer.sendOSC (browserAddress + "isActive", isActive, dump);

        if (!isActive)
            return;

        this.writer.sendOSC (browserAddress + "tab", browser.getSelectedContentType (), dump);

        IBrowserColumn column;
        // Filter Columns
        for (int i = 0; i < browser.getFilterColumnCount (); i++)
        {
            final String filterAddress = browserAddress + "filter/" + (i + 1) + "/";
            column = browser.getFilterColumn (i);
            this.writer.sendOSC (filterAddress + "exists", column.doesExist (), dump);
            this.writer.sendOSC (filterAddress + "name", column.getName (), dump);
            this.writer.sendOSC (filterAddress + "wildcard", column.getWildcard (), dump);
            final IBrowserColumnItem [] items = column.getItems ();
            for (int j = 0; j < items.length; j++)
            {
                this.writer.sendOSC (filterAddress + "item/" + (j + 1) + "/exists", items[j].doesExist (), dump);
                this.writer.sendOSC (filterAddress + "item/" + (j + 1) + "/name", items[j].getName (), dump);
                this.writer.sendOSC (filterAddress + "item/" + (j + 1) + "/hits", items[j].getHitCount (), dump);
                this.writer.sendOSC (filterAddress + "item/" + (j + 1) + "/isSelected", items[j].isSelected (), dump);
            }
        }

        // Presets
        final String presetAddress = browserAddress + "result/";
        final IBrowserColumnItem [] items = browser.getResultColumnItems ();
        for (int i = 0; i < items.length; i++)
        {
            this.writer.sendOSC (presetAddress + (i + 1) + "/exists", items[i].doesExist (), dump);
            this.writer.sendOSC (presetAddress + (i + 1) + "/name", items[i].getName (), dump);
            this.writer.sendOSC (presetAddress + (i + 1) + "/hits", items[i].getHitCount (), dump);
            this.writer.sendOSC (presetAddress + (i + 1) + "/isSelected", items[i].isSelected (), dump);
        }
    }
}
