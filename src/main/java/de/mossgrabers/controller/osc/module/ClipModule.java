// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc.module;

import de.mossgrabers.controller.osc.exception.IllegalParameterException;
import de.mossgrabers.controller.osc.exception.MissingCommandException;
import de.mossgrabers.controller.osc.exception.UnknownCommandException;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.osc.IOpenSoundControlWriter;

import java.util.LinkedList;


/**
 * All cursor clip related commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ClipModule extends AbstractModule
{
    /**
     * Constructor.
     *
     * @param host The host
     * @param model The model
     * @param writer The writer
     */
    public ClipModule (final IHost host, final IModel model, final IOpenSoundControlWriter writer)
    {
        super (host, model, writer);
    }


    /** {@inheritDoc} */
    @Override
    public String [] getSupportedCommands ()
    {
        return new String []
        {
            "clip"
        };
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final String command, final LinkedList<String> path, final Object value) throws IllegalParameterException, UnknownCommandException, MissingCommandException
    {
        if (!"clip".equals (command))
            throw new UnknownCommandException (command);

        final INoteClip cursorClip = this.model.getCursorClip ();

        final String subCommand = getSubCommand (path);
        switch (subCommand)
        {
            case "pinned":
                if (value == null)
                    cursorClip.togglePinned ();
                else
                    cursorClip.setPinned (isTrigger (value));
                break;

            case "+":
                this.model.getCursorTrack ().getSlotBank ().selectNextItem ();
                break;

            case "-":
                this.model.getCursorTrack ().getSlotBank ().selectPreviousItem ();
                break;

            case "launch":
                final ISlot selectedSlot = this.model.getSelectedSlot ();
                if (selectedSlot != null)
                    selectedSlot.launch ();
                break;

            case "stop":
                this.model.getCursorTrack ().stop ();
                break;

            case "record":
                final ISlot selectedSlot2 = this.model.getSelectedSlot ();
                if (selectedSlot2 != null)
                    selectedSlot2.record ();
                break;

            case "quantize":
                if (cursorClip.doesExist ())
                    cursorClip.quantize (1);
                break;

            default:
                throw new UnknownCommandException (command);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void flush (final boolean dump)
    {
        final INoteClip cursorClip = this.model.getCursorClip ();

        this.writer.sendOSC ("/clip/exists", cursorClip.doesExist (), dump);
        this.writer.sendOSC ("/clip/pinned", cursorClip.isPinned (), dump);

        ColorEx color = cursorClip.getColor ();
        if (color == null)
            color = ColorEx.BLACK;
        this.writer.sendOSCColor ("/clip/color", color.getRed (), color.getGreen (), color.getBlue (), dump);
    }
}
