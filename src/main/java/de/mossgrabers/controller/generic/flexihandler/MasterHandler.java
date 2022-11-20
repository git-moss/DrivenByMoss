// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.flexihandler;

import de.mossgrabers.controller.generic.GenericFlexiConfiguration;
import de.mossgrabers.controller.generic.controller.FlexiCommand;
import de.mossgrabers.controller.generic.controller.GenericFlexiControlSurface;
import de.mossgrabers.controller.generic.flexihandler.utils.FlexiHandlerException;
import de.mossgrabers.controller.generic.flexihandler.utils.KnobMode;
import de.mossgrabers.controller.generic.flexihandler.utils.MidiValue;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.parameter.IParameter;


/**
 * The handler for master channel commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MasterHandler extends AbstractHandler
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param configuration The configuration
     * @param absoluteLowResValueChanger The default absolute value changer in low res mode
     * @param signedBitRelativeValueChanger The signed bit relative value changer
     * @param offsetBinaryRelativeValueChanger The offset binary relative value changer
     */
    public MasterHandler (final IModel model, final GenericFlexiControlSurface surface, final GenericFlexiConfiguration configuration, final IValueChanger absoluteLowResValueChanger, final IValueChanger signedBitRelativeValueChanger, final IValueChanger offsetBinaryRelativeValueChanger)
    {
        super (model, surface, configuration, absoluteLowResValueChanger, signedBitRelativeValueChanger, offsetBinaryRelativeValueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public FlexiCommand [] getSupportedCommands ()
    {
        return new FlexiCommand []
        {
            FlexiCommand.MASTER_SET_VOLUME,
            FlexiCommand.MASTER_SET_PANORAMA,
            FlexiCommand.MASTER_TOGGLE_MUTE,
            FlexiCommand.MASTER_SET_MUTE,
            FlexiCommand.MASTER_TOGGLE_SOLO,
            FlexiCommand.MASTER_SET_SOLO,
            FlexiCommand.MASTER_TOGGLE_ARM,
            FlexiCommand.MASTER_SET_ARM,
            FlexiCommand.MASTER_CROSSFADER,
            FlexiCommand.MASTER_SELECT
        };
    }


    /** {@inheritDoc} */
    @Override
    public int getCommandValue (final FlexiCommand command)
    {
        final IMasterTrack masterTrack = this.model.getMasterTrack ();

        switch (command)
        {
            case MASTER_SET_VOLUME:
                return masterTrack.getVolume ();

            case MASTER_SET_PANORAMA:
                return masterTrack.getPan ();

            case MASTER_TOGGLE_MUTE, MASTER_SET_MUTE:
                return masterTrack.isMute () ? 127 : 0;

            case MASTER_TOGGLE_SOLO, MASTER_SET_SOLO:
                return masterTrack.isSolo () ? 127 : 0;

            case MASTER_TOGGLE_ARM, MASTER_SET_ARM:
                return masterTrack.isRecArm () ? 127 : 0;

            case MASTER_CROSSFADER:
                return this.model.getTransport ().getCrossfade ();

            case MASTER_SELECT:
                return masterTrack.isSelected () ? 127 : 0;

            default:
                return -1;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void handle (final FlexiCommand command, final KnobMode knobMode, final MidiValue value)
    {
        final boolean isButtonPressed = this.isButtonPressed (knobMode, value);

        switch (command)
        {
            // Master: Set Volume
            case MASTER_SET_VOLUME:
                this.changeMasterVolume (knobMode, value);
                break;

            // Master: Set Panorama
            case MASTER_SET_PANORAMA:
                this.changeMasterPanorama (knobMode, value);
                break;

            // Master: Toggle Mute
            case MASTER_TOGGLE_MUTE:
                if (isButtonPressed)
                    this.model.getMasterTrack ().toggleMute ();
                break;

            // Master: Set Mute
            case MASTER_SET_MUTE:
                if (isButtonPressed)
                    this.model.getMasterTrack ().setMute (value.isPositive ());
                break;

            // Master: Toggle Solo
            case MASTER_TOGGLE_SOLO:
                if (isButtonPressed)
                    this.model.getMasterTrack ().toggleSolo ();
                break;

            // Master: Set Solo
            case MASTER_SET_SOLO:
                if (isButtonPressed)
                    this.model.getMasterTrack ().setSolo (value.isPositive ());
                break;
            // Master: Toggle Arm
            case MASTER_TOGGLE_ARM:
                if (isButtonPressed)
                    this.model.getMasterTrack ().toggleRecArm ();
                break;

            // Master: Set Arm
            case MASTER_SET_ARM:
                if (isButtonPressed)
                    this.model.getMasterTrack ().setRecArm (value.isPositive ());
                break;

            // Master: Cross-fader
            case MASTER_CROSSFADER:
                this.changeMasterCrossfader (knobMode, value);
                break;

            // Master: Select
            case MASTER_SELECT:
                if (isButtonPressed)
                {
                    this.model.getMasterTrack ().select ();
                    this.mvHelper.notifySelectedTrack ();
                }
                break;

            default:
                throw new FlexiHandlerException (command);
        }
    }


    private void changeMasterVolume (final KnobMode knobMode, final MidiValue value)
    {
        final ITrack track = this.model.getMasterTrack ();
        final int val = value.getValue ();
        final IParameter volumeParameter = track.getVolumeParameter ();
        if (isAbsolute (knobMode))
            volumeParameter.setValue (this.getAbsoluteValueChanger (value), val);
        else
            volumeParameter.changeValue (this.getRelativeValueChanger (knobMode), val);
    }


    private void changeMasterPanorama (final KnobMode knobMode, final MidiValue value)
    {
        final ITrack track = this.model.getMasterTrack ();
        final int val = value.getValue ();
        final IParameter panParameter = track.getPanParameter ();
        if (isAbsolute (knobMode))
            panParameter.setValue (this.getAbsoluteValueChanger (value), val);
        else
            panParameter.changeValue (this.getRelativeValueChanger (knobMode), val);
    }


    private void changeMasterCrossfader (final KnobMode knobMode, final MidiValue value)
    {
        final ITransport transport = this.model.getTransport ();
        final int val = value.getValue ();
        final IParameter crossfadeParameter = transport.getCrossfadeParameter ();
        if (isAbsolute (knobMode))
            crossfadeParameter.setValue (this.getAbsoluteValueChanger (value), val);
        else
            crossfadeParameter.changeValue (this.getRelativeValueChanger (knobMode), val);
    }
}
