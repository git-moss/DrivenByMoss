// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.flexihandler;

import de.mossgrabers.controller.generic.GenericFlexiConfiguration;
import de.mossgrabers.controller.generic.controller.FlexiCommand;
import de.mossgrabers.controller.generic.controller.GenericFlexiControlSurface;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;

import java.util.ArrayList;
import java.util.List;


/**
 * The handler for layout commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ModesHandler extends AbstractHandler
{
    private static final List<Modes> MODE_IDS = new ArrayList<> ();
    static
    {
        MODE_IDS.add (Modes.TRACK);
        MODE_IDS.add (Modes.VOLUME);
        MODE_IDS.add (Modes.PAN);
        MODE_IDS.add (Modes.SEND1);
        MODE_IDS.add (Modes.SEND2);
        MODE_IDS.add (Modes.SEND3);
        MODE_IDS.add (Modes.SEND4);
        MODE_IDS.add (Modes.SEND5);
        MODE_IDS.add (Modes.SEND6);
        MODE_IDS.add (Modes.SEND7);
        MODE_IDS.add (Modes.SEND8);
        MODE_IDS.add (Modes.DEVICE_PARAMS);
    }

    private final ModeManager modeManager;
    private final IHost       host;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param configuration The configuration
     * @param relative2ValueChanger The relative value changer variant 2
     * @param relative3ValueChanger The relative value changer variant 3
     * @param host The host
     */
    public ModesHandler (final IModel model, final GenericFlexiControlSurface surface, final GenericFlexiConfiguration configuration, final IValueChanger relative2ValueChanger, final IValueChanger relative3ValueChanger, final IHost host)
    {
        super (model, surface, configuration, relative2ValueChanger, relative3ValueChanger);

        this.host = host;
        this.modeManager = this.surface.getModeManager ();
    }


    /** {@inheritDoc} */
    @Override
    public FlexiCommand [] getSupportedCommands ()
    {
        return new FlexiCommand []
        {
            FlexiCommand.MODES_KNOB1,
            FlexiCommand.MODES_KNOB2,
            FlexiCommand.MODES_KNOB3,
            FlexiCommand.MODES_KNOB4,
            FlexiCommand.MODES_KNOB5,
            FlexiCommand.MODES_KNOB6,
            FlexiCommand.MODES_KNOB7,
            FlexiCommand.MODES_KNOB8,
            FlexiCommand.MODES_BUTTON1,
            FlexiCommand.MODES_BUTTON2,
            FlexiCommand.MODES_BUTTON3,
            FlexiCommand.MODES_BUTTON4,
            FlexiCommand.MODES_BUTTON5,
            FlexiCommand.MODES_BUTTON6,
            FlexiCommand.MODES_BUTTON7,
            FlexiCommand.MODES_BUTTON8,
            FlexiCommand.MODES_NEXT_ITEM,
            FlexiCommand.MODES_PREV_ITEM,
            FlexiCommand.MODES_NEXT_PAGE,
            FlexiCommand.MODES_PREV_PAGE,
            FlexiCommand.MODES_SELECT_MODE_TRACK,
            FlexiCommand.MODES_SELECT_MODE_VOLUME,
            FlexiCommand.MODES_SELECT_MODE_PAN,
            FlexiCommand.MODES_SELECT_MODE_SEND1,
            FlexiCommand.MODES_SELECT_MODE_SEND2,
            FlexiCommand.MODES_SELECT_MODE_SEND3,
            FlexiCommand.MODES_SELECT_MODE_SEND4,
            FlexiCommand.MODES_SELECT_MODE_SEND5,
            FlexiCommand.MODES_SELECT_MODE_SEND6,
            FlexiCommand.MODES_SELECT_MODE_SEND7,
            FlexiCommand.MODES_SELECT_MODE_SEND8,
            FlexiCommand.MODES_SELECT_MODE_DEVICE,
            FlexiCommand.MODES_SELECT_MODE_NEXT,
            FlexiCommand.MODES_SELECT_MODE_PREV,
            FlexiCommand.MODES_BROWSE_PRESETS
        };
    }


    /** {@inheritDoc} */
    @Override
    public int getCommandValue (final FlexiCommand command)
    {
        final IMode mode = this.modeManager.getActive ();

        switch (command)
        {
            case MODES_KNOB1:
                return mode == null ? 0 : mode.getKnobValue (0);
            case MODES_KNOB2:
                return mode == null ? 0 : mode.getKnobValue (1);
            case MODES_KNOB3:
                return mode == null ? 0 : mode.getKnobValue (2);
            case MODES_KNOB4:
                return mode == null ? 0 : mode.getKnobValue (3);
            case MODES_KNOB5:
                return mode == null ? 0 : mode.getKnobValue (4);
            case MODES_KNOB6:
                return mode == null ? 0 : mode.getKnobValue (5);
            case MODES_KNOB7:
                return mode == null ? 0 : mode.getKnobValue (6);
            case MODES_KNOB8:
                return mode == null ? 0 : mode.getKnobValue (7);

            default:
                return -1;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void handle (final FlexiCommand command, final int knobMode, final int value)
    {
        final IMode mode = this.modeManager.getActive ();
        if (mode == null)
            return;

        final boolean isButtonPressed = this.isButtonPressed (knobMode, value);

        switch (command)
        {
            case MODES_KNOB1:
            case MODES_KNOB2:
            case MODES_KNOB3:
            case MODES_KNOB4:
            case MODES_KNOB5:
            case MODES_KNOB6:
            case MODES_KNOB7:
            case MODES_KNOB8:
                this.changeModeValue (knobMode, command.ordinal () - FlexiCommand.MODES_KNOB1.ordinal (), value);
                break;

            case MODES_BUTTON1:
            case MODES_BUTTON2:
            case MODES_BUTTON3:
            case MODES_BUTTON4:
            case MODES_BUTTON5:
            case MODES_BUTTON6:
            case MODES_BUTTON7:
            case MODES_BUTTON8:
                if (isButtonPressed)
                {
                    mode.selectItem (command.ordinal () - FlexiCommand.MODES_BUTTON1.ordinal ());
                    this.mvHelper.notifySelectedItem (mode);
                }
                break;

            case MODES_NEXT_ITEM:
                if (isButtonPressed)
                {
                    mode.selectNextItem ();
                    this.mvHelper.notifySelectedItem (mode);
                }
                break;
            case MODES_PREV_ITEM:
                if (isButtonPressed)
                {
                    mode.selectPreviousItem ();
                    this.mvHelper.notifySelectedItem (mode);
                }
                break;
            case MODES_NEXT_PAGE:
                if (isButtonPressed)
                {
                    mode.selectNextItemPage ();
                    this.mvHelper.notifySelectedItem (mode);
                }
                break;
            case MODES_PREV_PAGE:
                if (isButtonPressed)
                {
                    mode.selectPreviousItemPage ();
                    this.mvHelper.notifySelectedItem (mode);
                }
                break;
            case MODES_SELECT_MODE_TRACK:
                if (isButtonPressed)
                    this.surface.activateMode (Modes.TRACK);
                break;
            case MODES_SELECT_MODE_VOLUME:
                if (isButtonPressed)
                    this.surface.activateMode (Modes.VOLUME);
                break;
            case MODES_SELECT_MODE_PAN:
                if (isButtonPressed)
                    this.surface.activateMode (Modes.PAN);
                break;
            case MODES_SELECT_MODE_SEND1:
                if (isButtonPressed)
                    this.surface.activateMode (Modes.SEND1);
                break;
            case MODES_SELECT_MODE_SEND2:
                if (isButtonPressed)
                    this.surface.activateMode (Modes.SEND2);
                break;
            case MODES_SELECT_MODE_SEND3:
                if (isButtonPressed)
                    this.surface.activateMode (Modes.SEND3);
                break;
            case MODES_SELECT_MODE_SEND4:
                if (isButtonPressed)
                    this.surface.activateMode (Modes.SEND4);
                break;
            case MODES_SELECT_MODE_SEND5:
                if (isButtonPressed)
                    this.surface.activateMode (Modes.SEND5);
                break;
            case MODES_SELECT_MODE_SEND6:
                if (isButtonPressed)
                    this.surface.activateMode (Modes.SEND6);
                break;
            case MODES_SELECT_MODE_SEND7:
                if (isButtonPressed)
                    this.surface.activateMode (Modes.SEND7);
                break;
            case MODES_SELECT_MODE_SEND8:
                if (isButtonPressed)
                    this.surface.activateMode (Modes.SEND8);
                break;
            case MODES_SELECT_MODE_DEVICE:
                if (isButtonPressed)
                    this.surface.activateMode (Modes.DEVICE_PARAMS);
                break;
            case MODES_SELECT_MODE_NEXT:
                if (isButtonPressed)
                    this.selectNextMode ();
                break;
            case MODES_SELECT_MODE_PREV:
                if (isButtonPressed)
                    this.selectPreviousMode ();
                break;
            case MODES_BROWSE_PRESETS:
                if (isButtonPressed)
                {
                    this.model.getBrowser ().replace (this.model.getCursorDevice ());
                    this.host.scheduleTask ( () -> this.surface.activateMode (Modes.BROWSER), 500);
                }
                break;

            default:
                throw new FlexiHandlerException (command);
        }
    }


    private void changeModeValue (final int knobMode, final int knobIndex, final int value)
    {
        final IMode mode = this.modeManager.getActive ();
        if (isAbsolute (knobMode))
            mode.onKnobValue (knobIndex, value);
        else
        {
            final int knobValue = mode.getKnobValue (knobIndex);
            final int relativeSpeed = (int) Math.round (this.getRelativeSpeed (knobMode, value));
            mode.onKnobValue (knobIndex, knobValue == -1 ? relativeSpeed : (int) this.limit ((double) knobValue + relativeSpeed));
        }
    }


    private void selectPreviousMode ()
    {
        final ITrackBank trackBank = this.model.getTrackBank ();
        final Modes activeModeId = this.modeManager.getActiveID ();
        int index = MODE_IDS.indexOf (activeModeId);
        Modes newMode;
        int newModeID;
        // If a send mode is selected check if the according send exists
        do
        {
            index--;
            if (index < 0 || index >= MODE_IDS.size ())
                index = MODE_IDS.size () - 1;
            newMode = MODE_IDS.get (index);
            newModeID = newMode.ordinal ();
        } while (newModeID >= Modes.SEND1.ordinal () && newModeID <= Modes.SEND8.ordinal () && !trackBank.canEditSend (newModeID - Modes.SEND1.ordinal ()));

        this.surface.activateMode (newMode);
    }


    private void selectNextMode ()
    {
        final ITrackBank trackBank = this.model.getTrackBank ();
        final Modes activeModeId = this.modeManager.getActiveID ();
        int index = MODE_IDS.indexOf (activeModeId);
        Modes newMode;
        int newModeID;
        // If a send mode is selected check if the according send exists
        do
        {
            index++;
            if (index < 0 || index >= MODE_IDS.size ())
                index = 0;
            newMode = MODE_IDS.get (index);
            newModeID = newMode.ordinal ();
        } while (newModeID >= Modes.SEND1.ordinal () && newModeID <= Modes.SEND8.ordinal () && !trackBank.canEditSend (newModeID - Modes.SEND1.ordinal ()));

        this.surface.activateMode (newMode);
    }
}
