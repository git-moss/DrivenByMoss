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
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.data.ICursorTrack;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;
import de.mossgrabers.framework.osc.IOpenSoundControlWriter;

import java.util.LinkedList;
import java.util.Optional;


/**
 * All cursor clip related commands.
 *
 * @author Jürgen Moßgraber
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

        final String subCommand = getSubCommand (path);

        if ("stopall".equals (subCommand))
        {
            this.model.getTrackBank ().stop ();
            return;
        }

        // Cursor clip related commands

        final INoteClip cursorClip = this.model.getCursorClip ();
        switch (subCommand)
        {
            case "pinned":
                if (value == null)
                    cursorClip.togglePinned ();
                else
                    cursorClip.setPinned (isTrigger (value));
                return;

            case "quantize":
                if (cursorClip.doesExist ())
                    cursorClip.quantize (1);
                return;

            default:
                // Fall through
                break;
        }

        // Slot bank related commands

        final ICursorTrack cursorTrack = this.model.getCursorTrack ();
        if (!cursorTrack.doesExist ())
            return;

        if ("stop".equals (subCommand))
        {
            cursorTrack.stop ();
            return;
        }

        final ISlotBank slotBank = cursorTrack.getSlotBank ();
        switch (subCommand)
        {
            case "+":
                slotBank.selectNextItem ();
                return;

            case "-":
                slotBank.selectPreviousItem ();
                return;

            default:
                // Fall through
                break;
        }

        final Optional<ISlot> selectedSlotOptional = slotBank.getSelectedItem ();
        if (selectedSlotOptional.isEmpty ())
            return;

        final ISlot selectedSlot = selectedSlotOptional.get ();
        switch (subCommand)
        {
            case "launch":
                selectedSlot.launch (toInteger (value) > 0, false);
                return;

            case "launchAlt":
                selectedSlot.launch (toInteger (value) > 0, true);
                return;

            case "record":
                this.model.recordNoteClip (cursorTrack, selectedSlot);
                return;

            case "create":
                this.model.createNoteClip (cursorTrack, selectedSlot, toInteger (value), true);
                return;

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
