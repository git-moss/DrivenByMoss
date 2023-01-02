// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
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
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.bank.ILayerBank;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.parameter.IParameter;

import java.util.Optional;


/**
 * The handler for layer commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LayerHandler extends AbstractHandler
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
    public LayerHandler (final IModel model, final GenericFlexiControlSurface surface, final GenericFlexiConfiguration configuration, final IValueChanger absoluteLowResValueChanger, final IValueChanger signedBitRelativeValueChanger, final IValueChanger offsetBinaryRelativeValueChanger)
    {
        super (model, surface, configuration, absoluteLowResValueChanger, signedBitRelativeValueChanger, offsetBinaryRelativeValueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public FlexiCommand [] getSupportedCommands ()
    {
        return new FlexiCommand []
        {
            FlexiCommand.LAYER_SELECT_PREVIOUS_BANK_PAGE,
            FlexiCommand.LAYER_SELECT_NEXT_BANK_PAGE,
            FlexiCommand.LAYER_SELECT_PREVIOUS_LAYER,
            FlexiCommand.LAYER_SELECT_NEXT_LAYER,
            FlexiCommand.LAYER_SCROLL_LAYERS,
            FlexiCommand.LAYER_1_SELECT,
            FlexiCommand.LAYER_2_SELECT,
            FlexiCommand.LAYER_3_SELECT,
            FlexiCommand.LAYER_4_SELECT,
            FlexiCommand.LAYER_5_SELECT,
            FlexiCommand.LAYER_6_SELECT,
            FlexiCommand.LAYER_7_SELECT,
            FlexiCommand.LAYER_8_SELECT,
            FlexiCommand.LAYER_1_TOGGLE_ACTIVE,
            FlexiCommand.LAYER_2_TOGGLE_ACTIVE,
            FlexiCommand.LAYER_3_TOGGLE_ACTIVE,
            FlexiCommand.LAYER_4_TOGGLE_ACTIVE,
            FlexiCommand.LAYER_5_TOGGLE_ACTIVE,
            FlexiCommand.LAYER_6_TOGGLE_ACTIVE,
            FlexiCommand.LAYER_7_TOGGLE_ACTIVE,
            FlexiCommand.LAYER_8_TOGGLE_ACTIVE,
            FlexiCommand.LAYER_1_SET_ACTIVE,
            FlexiCommand.LAYER_2_SET_ACTIVE,
            FlexiCommand.LAYER_3_SET_ACTIVE,
            FlexiCommand.LAYER_4_SET_ACTIVE,
            FlexiCommand.LAYER_5_SET_ACTIVE,
            FlexiCommand.LAYER_6_SET_ACTIVE,
            FlexiCommand.LAYER_7_SET_ACTIVE,
            FlexiCommand.LAYER_8_SET_ACTIVE,
            FlexiCommand.LAYER_SELECTED_TOGGLE_ACTIVE,
            FlexiCommand.LAYER_SELECTED_SET_ACTIVE,
            FlexiCommand.LAYER_1_SET_VOLUME,
            FlexiCommand.LAYER_2_SET_VOLUME,
            FlexiCommand.LAYER_3_SET_VOLUME,
            FlexiCommand.LAYER_4_SET_VOLUME,
            FlexiCommand.LAYER_5_SET_VOLUME,
            FlexiCommand.LAYER_6_SET_VOLUME,
            FlexiCommand.LAYER_7_SET_VOLUME,
            FlexiCommand.LAYER_8_SET_VOLUME,
            FlexiCommand.LAYER_SELECTED_SET_VOLUME_LAYER,
            FlexiCommand.LAYER_1_SET_PANORAMA,
            FlexiCommand.LAYER_2_SET_PANORAMA,
            FlexiCommand.LAYER_3_SET_PANORAMA,
            FlexiCommand.LAYER_4_SET_PANORAMA,
            FlexiCommand.LAYER_5_SET_PANORAMA,
            FlexiCommand.LAYER_6_SET_PANORAMA,
            FlexiCommand.LAYER_7_SET_PANORAMA,
            FlexiCommand.LAYER_8_SET_PANORAMA,
            FlexiCommand.LAYER_SELECTED_SET_PANORAMA,
            FlexiCommand.LAYER_1_TOGGLE_MUTE,
            FlexiCommand.LAYER_2_TOGGLE_MUTE,
            FlexiCommand.LAYER_3_TOGGLE_MUTE,
            FlexiCommand.LAYER_4_TOGGLE_MUTE,
            FlexiCommand.LAYER_5_TOGGLE_MUTE,
            FlexiCommand.LAYER_6_TOGGLE_MUTE,
            FlexiCommand.LAYER_7_TOGGLE_MUTE,
            FlexiCommand.LAYER_8_TOGGLE_MUTE,
            FlexiCommand.LAYER_1_SET_MUTE,
            FlexiCommand.LAYER_2_SET_MUTE,
            FlexiCommand.LAYER_3_SET_MUTE,
            FlexiCommand.LAYER_4_SET_MUTE,
            FlexiCommand.LAYER_5_SET_MUTE,
            FlexiCommand.LAYER_6_SET_MUTE,
            FlexiCommand.LAYER_7_SET_MUTE,
            FlexiCommand.LAYER_8_SET_MUTE,
            FlexiCommand.LAYER_SELECTED_TOGGLE_MUTE,
            FlexiCommand.LAYER_SELECTED_SET_MUTE,
            FlexiCommand.LAYER_1_TOGGLE_SOLO,
            FlexiCommand.LAYER_2_TOGGLE_SOLO,
            FlexiCommand.LAYER_3_TOGGLE_SOLO,
            FlexiCommand.LAYER_4_TOGGLE_SOLO,
            FlexiCommand.LAYER_5_TOGGLE_SOLO,
            FlexiCommand.LAYER_6_TOGGLE_SOLO,
            FlexiCommand.LAYER_7_TOGGLE_SOLO,
            FlexiCommand.LAYER_8_TOGGLE_SOLO,
            FlexiCommand.LAYER_1_SET_SOLO,
            FlexiCommand.LAYER_2_SET_SOLO,
            FlexiCommand.LAYER_3_SET_SOLO,
            FlexiCommand.LAYER_4_SET_SOLO,
            FlexiCommand.LAYER_5_SET_SOLO,
            FlexiCommand.LAYER_6_SET_SOLO,
            FlexiCommand.LAYER_7_SET_SOLO,
            FlexiCommand.LAYER_8_SET_SOLO,
            FlexiCommand.LAYER_SELECTED_TOGGLE_SOLO,
            FlexiCommand.LAYER_SELECTED_SET_SOLO,
            FlexiCommand.LAYER_1_SET_SEND_1,
            FlexiCommand.LAYER_2_SET_SEND_1,
            FlexiCommand.LAYER_3_SET_SEND_1,
            FlexiCommand.LAYER_4_SET_SEND_1,
            FlexiCommand.LAYER_5_SET_SEND_1,
            FlexiCommand.LAYER_6_SET_SEND_1,
            FlexiCommand.LAYER_7_SET_SEND_1,
            FlexiCommand.LAYER_8_SET_SEND_1,
            FlexiCommand.LAYER_1_SET_SEND_2,
            FlexiCommand.LAYER_2_SET_SEND_2,
            FlexiCommand.LAYER_3_SET_SEND_2,
            FlexiCommand.LAYER_4_SET_SEND_2,
            FlexiCommand.LAYER_5_SET_SEND_2,
            FlexiCommand.LAYER_6_SET_SEND_2,
            FlexiCommand.LAYER_7_SET_SEND_2,
            FlexiCommand.LAYER_8_SET_SEND_2,
            FlexiCommand.LAYER_1_SET_SEND_3,
            FlexiCommand.LAYER_2_SET_SEND_3,
            FlexiCommand.LAYER_3_SET_SEND_3,
            FlexiCommand.LAYER_4_SET_SEND_3,
            FlexiCommand.LAYER_5_SET_SEND_3,
            FlexiCommand.LAYER_6_SET_SEND_3,
            FlexiCommand.LAYER_7_SET_SEND_3,
            FlexiCommand.LAYER_8_SET_SEND_3,
            FlexiCommand.LAYER_1_SET_SEND_4,
            FlexiCommand.LAYER_2_SET_SEND_4,
            FlexiCommand.LAYER_3_SET_SEND_4,
            FlexiCommand.LAYER_4_SET_SEND_4,
            FlexiCommand.LAYER_5_SET_SEND_4,
            FlexiCommand.LAYER_6_SET_SEND_4,
            FlexiCommand.LAYER_7_SET_SEND_4,
            FlexiCommand.LAYER_8_SET_SEND_4,
            FlexiCommand.LAYER_1_SET_SEND_5,
            FlexiCommand.LAYER_2_SET_SEND_5,
            FlexiCommand.LAYER_3_SET_SEND_5,
            FlexiCommand.LAYER_4_SET_SEND_5,
            FlexiCommand.LAYER_5_SET_SEND_5,
            FlexiCommand.LAYER_6_SET_SEND_5,
            FlexiCommand.LAYER_7_SET_SEND_5,
            FlexiCommand.LAYER_8_SET_SEND_5,
            FlexiCommand.LAYER_1_SET_SEND_6,
            FlexiCommand.LAYER_2_SET_SEND_6,
            FlexiCommand.LAYER_3_SET_SEND_6,
            FlexiCommand.LAYER_4_SET_SEND_6,
            FlexiCommand.LAYER_5_SET_SEND_6,
            FlexiCommand.LAYER_6_SET_SEND_6,
            FlexiCommand.LAYER_7_SET_SEND_6,
            FlexiCommand.LAYER_8_SET_SEND_6,
            FlexiCommand.LAYER_1_SET_SEND_7,
            FlexiCommand.LAYER_2_SET_SEND_7,
            FlexiCommand.LAYER_3_SET_SEND_7,
            FlexiCommand.LAYER_4_SET_SEND_7,
            FlexiCommand.LAYER_5_SET_SEND_7,
            FlexiCommand.LAYER_6_SET_SEND_7,
            FlexiCommand.LAYER_7_SET_SEND_7,
            FlexiCommand.LAYER_8_SET_SEND_7,
            FlexiCommand.LAYER_1_SET_SEND_8,
            FlexiCommand.LAYER_2_SET_SEND_8,
            FlexiCommand.LAYER_3_SET_SEND_8,
            FlexiCommand.LAYER_4_SET_SEND_8,
            FlexiCommand.LAYER_5_SET_SEND_8,
            FlexiCommand.LAYER_6_SET_SEND_8,
            FlexiCommand.LAYER_7_SET_SEND_8,
            FlexiCommand.LAYER_8_SET_SEND_8,
            FlexiCommand.LAYER_SELECTED_SET_SEND_1,
            FlexiCommand.LAYER_SELECTED_SET_SEND_2,
            FlexiCommand.LAYER_SELECTED_SET_SEND_3,
            FlexiCommand.LAYER_SELECTED_SET_SEND_4,
            FlexiCommand.LAYER_SELECTED_SET_SEND_5,
            FlexiCommand.LAYER_SELECTED_SET_SEND_6,
            FlexiCommand.LAYER_SELECTED_SET_SEND_7,
            FlexiCommand.LAYER_SELECTED_SET_SEND_8
        };
    }


    /** {@inheritDoc} */
    @Override
    public int getCommandValue (final FlexiCommand command)
    {
        final ILayerBank layerBank = this.getLayerBank ();
        if (layerBank == null)
            return -1;
        final Optional<ILayer> selectedLayer = layerBank.getSelectedItem ();

        switch (command)
        {
            case LAYER_1_SELECT, LAYER_2_SELECT, LAYER_3_SELECT, LAYER_4_SELECT, LAYER_5_SELECT, LAYER_6_SELECT, LAYER_7_SELECT, LAYER_8_SELECT:
                return layerBank.getItem (command.ordinal () - FlexiCommand.LAYER_1_SELECT.ordinal ()).isSelected () ? 127 : 0;

            case LAYER_1_TOGGLE_ACTIVE, LAYER_2_TOGGLE_ACTIVE, LAYER_3_TOGGLE_ACTIVE, LAYER_4_TOGGLE_ACTIVE, LAYER_5_TOGGLE_ACTIVE, LAYER_6_TOGGLE_ACTIVE, LAYER_7_TOGGLE_ACTIVE, LAYER_8_TOGGLE_ACTIVE:
                return layerBank.getItem (command.ordinal () - FlexiCommand.LAYER_1_TOGGLE_ACTIVE.ordinal ()).isActivated () ? 127 : 0;
            case LAYER_1_SET_ACTIVE, LAYER_2_SET_ACTIVE, LAYER_3_SET_ACTIVE, LAYER_4_SET_ACTIVE, LAYER_5_SET_ACTIVE, LAYER_6_SET_ACTIVE, LAYER_7_SET_ACTIVE, LAYER_8_SET_ACTIVE:
                return layerBank.getItem (command.ordinal () - FlexiCommand.LAYER_1_SET_ACTIVE.ordinal ()).isActivated () ? 127 : 0;
            case LAYER_SELECTED_TOGGLE_ACTIVE, LAYER_SELECTED_SET_ACTIVE:
                return selectedLayer.isPresent () && selectedLayer.get ().isActivated () ? 127 : 0;

            case LAYER_1_SET_VOLUME, LAYER_2_SET_VOLUME, LAYER_3_SET_VOLUME, LAYER_4_SET_VOLUME, LAYER_5_SET_VOLUME, LAYER_6_SET_VOLUME, LAYER_7_SET_VOLUME, LAYER_8_SET_VOLUME:
                return layerBank.getItem (command.ordinal () - FlexiCommand.LAYER_1_SET_VOLUME.ordinal ()).getVolume ();
            case LAYER_SELECTED_SET_VOLUME_LAYER:
                return selectedLayer.isPresent () ? selectedLayer.get ().getVolume () : 0;

            case LAYER_1_SET_PANORAMA, LAYER_2_SET_PANORAMA, LAYER_3_SET_PANORAMA, LAYER_4_SET_PANORAMA, LAYER_5_SET_PANORAMA, LAYER_6_SET_PANORAMA, LAYER_7_SET_PANORAMA, LAYER_8_SET_PANORAMA:
                return layerBank.getItem (command.ordinal () - FlexiCommand.LAYER_1_SET_PANORAMA.ordinal ()).getPan ();
            case LAYER_SELECTED_SET_PANORAMA:
                return selectedLayer.isPresent () ? selectedLayer.get ().getPan () : 0;

            case LAYER_1_TOGGLE_MUTE, LAYER_2_TOGGLE_MUTE, LAYER_3_TOGGLE_MUTE, LAYER_4_TOGGLE_MUTE, LAYER_5_TOGGLE_MUTE, LAYER_6_TOGGLE_MUTE, LAYER_7_TOGGLE_MUTE, LAYER_8_TOGGLE_MUTE:
                return layerBank.getItem (command.ordinal () - FlexiCommand.LAYER_1_TOGGLE_MUTE.ordinal ()).isMute () ? 127 : 0;
            case LAYER_1_SET_MUTE, LAYER_2_SET_MUTE, LAYER_3_SET_MUTE, LAYER_4_SET_MUTE, LAYER_5_SET_MUTE, LAYER_6_SET_MUTE, LAYER_7_SET_MUTE, LAYER_8_SET_MUTE:
                return layerBank.getItem (command.ordinal () - FlexiCommand.LAYER_1_SET_MUTE.ordinal ()).isMute () ? 127 : 0;
            case LAYER_SELECTED_TOGGLE_MUTE, LAYER_SELECTED_SET_MUTE:
                return selectedLayer.isPresent () && selectedLayer.get ().isMute () ? 127 : 0;

            case LAYER_1_TOGGLE_SOLO, LAYER_2_TOGGLE_SOLO, LAYER_3_TOGGLE_SOLO, LAYER_4_TOGGLE_SOLO, LAYER_5_TOGGLE_SOLO, LAYER_6_TOGGLE_SOLO, LAYER_7_TOGGLE_SOLO, LAYER_8_TOGGLE_SOLO:
                return layerBank.getItem (command.ordinal () - FlexiCommand.LAYER_1_TOGGLE_SOLO.ordinal ()).isSolo () ? 127 : 0;
            case LAYER_1_SET_SOLO, LAYER_2_SET_SOLO, LAYER_3_SET_SOLO, LAYER_4_SET_SOLO, LAYER_5_SET_SOLO, LAYER_6_SET_SOLO, LAYER_7_SET_SOLO, LAYER_8_SET_SOLO:
                return layerBank.getItem (command.ordinal () - FlexiCommand.LAYER_1_SET_SOLO.ordinal ()).isSolo () ? 127 : 0;
            case LAYER_SELECTED_TOGGLE_SOLO, LAYER_SELECTED_SET_SOLO:
                return selectedLayer.isPresent () && selectedLayer.get ().isSolo () ? 127 : 0;

            case LAYER_1_SET_SEND_1, LAYER_2_SET_SEND_1, LAYER_3_SET_SEND_1, LAYER_4_SET_SEND_1, LAYER_5_SET_SEND_1, LAYER_6_SET_SEND_1, LAYER_7_SET_SEND_1, LAYER_8_SET_SEND_1:
                return this.getSendValue (command.ordinal () - FlexiCommand.LAYER_1_SET_SEND_1.ordinal (), 0);
            case LAYER_1_SET_SEND_2, LAYER_2_SET_SEND_2, LAYER_3_SET_SEND_2, LAYER_4_SET_SEND_2, LAYER_5_SET_SEND_2, LAYER_6_SET_SEND_2, LAYER_7_SET_SEND_2, LAYER_8_SET_SEND_2:
                return this.getSendValue (command.ordinal () - FlexiCommand.LAYER_1_SET_SEND_2.ordinal (), 1);
            case LAYER_1_SET_SEND_3, LAYER_2_SET_SEND_3, LAYER_3_SET_SEND_3, LAYER_4_SET_SEND_3, LAYER_5_SET_SEND_3, LAYER_6_SET_SEND_3, LAYER_7_SET_SEND_3, LAYER_8_SET_SEND_3:
                return this.getSendValue (command.ordinal () - FlexiCommand.LAYER_1_SET_SEND_3.ordinal (), 2);
            case LAYER_1_SET_SEND_4, LAYER_2_SET_SEND_4, LAYER_3_SET_SEND_4, LAYER_4_SET_SEND_4, LAYER_5_SET_SEND_4, LAYER_6_SET_SEND_4, LAYER_7_SET_SEND_4, LAYER_8_SET_SEND_4:
                return this.getSendValue (command.ordinal () - FlexiCommand.LAYER_1_SET_SEND_4.ordinal (), 3);
            case LAYER_1_SET_SEND_5, LAYER_2_SET_SEND_5, LAYER_3_SET_SEND_5, LAYER_4_SET_SEND_5, LAYER_5_SET_SEND_5, LAYER_6_SET_SEND_5, LAYER_7_SET_SEND_5, LAYER_8_SET_SEND_5:
                return this.getSendValue (command.ordinal () - FlexiCommand.LAYER_1_SET_SEND_5.ordinal (), 4);
            case LAYER_1_SET_SEND_6, LAYER_2_SET_SEND_6, LAYER_3_SET_SEND_6, LAYER_4_SET_SEND_6, LAYER_5_SET_SEND_6, LAYER_6_SET_SEND_6, LAYER_7_SET_SEND_6, LAYER_8_SET_SEND_6:
                return this.getSendValue (command.ordinal () - FlexiCommand.LAYER_1_SET_SEND_6.ordinal (), 5);
            case LAYER_1_SET_SEND_7, LAYER_2_SET_SEND_7, LAYER_3_SET_SEND_7, LAYER_4_SET_SEND_7, LAYER_5_SET_SEND_7, LAYER_6_SET_SEND_7, LAYER_7_SET_SEND_7, LAYER_8_SET_SEND_7:
                return this.getSendValue (command.ordinal () - FlexiCommand.LAYER_1_SET_SEND_7.ordinal (), 6);
            case LAYER_1_SET_SEND_8, LAYER_2_SET_SEND_8, LAYER_3_SET_SEND_8, LAYER_4_SET_SEND_8, LAYER_5_SET_SEND_8, LAYER_6_SET_SEND_8, LAYER_7_SET_SEND_8, LAYER_8_SET_SEND_8:
                return this.getSendValue (command.ordinal () - FlexiCommand.LAYER_1_SET_SEND_8.ordinal (), 7);
            case LAYER_SELECTED_SET_SEND_1, LAYER_SELECTED_SET_SEND_2, LAYER_SELECTED_SET_SEND_3, LAYER_SELECTED_SET_SEND_4, LAYER_SELECTED_SET_SEND_5, LAYER_SELECTED_SET_SEND_6, LAYER_SELECTED_SET_SEND_7, LAYER_SELECTED_SET_SEND_8:
                return this.getSendValue (-1, command.ordinal () - FlexiCommand.LAYER_SELECTED_SET_SEND_1.ordinal ());

            default:
                return -1;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void handle (final FlexiCommand command, final KnobMode knobMode, final MidiValue value)
    {
        final ILayerBank layerBank = this.getLayerBank ();
        if (layerBank == null)
            return;
        final Optional<ILayer> selectedLayer = layerBank.getSelectedItem ();

        final boolean isButtonPressed = this.isButtonPressed (knobMode, value);

        switch (command)
        {
            // Layer: Select Previous Bank Page
            case LAYER_SELECT_PREVIOUS_BANK_PAGE:
                if (isButtonPressed)
                    this.scrollLayerLeft (true);
                break;
            // Layer: Select Next Bank Page
            case LAYER_SELECT_NEXT_BANK_PAGE:
                if (isButtonPressed)
                    this.scrollLayerRight (true);
                break;
            // Layer: Select Previous Layer
            case LAYER_SELECT_PREVIOUS_LAYER:
                if (isButtonPressed)
                    this.scrollLayerLeft (false);
                break;
            // Layer: Select Next Layer
            case LAYER_SELECT_NEXT_LAYER:
                if (isButtonPressed)
                    this.scrollLayerRight (false);
                break;

            case LAYER_SCROLL_LAYERS:
                this.scrollLayer (knobMode, value);
                break;

            // Layer 1-8: Select
            case LAYER_1_SELECT, LAYER_2_SELECT, LAYER_3_SELECT, LAYER_4_SELECT, LAYER_5_SELECT, LAYER_6_SELECT, LAYER_7_SELECT, LAYER_8_SELECT:
                if (isButtonPressed)
                {
                    layerBank.getItem (command.ordinal () - FlexiCommand.LAYER_1_SELECT.ordinal ()).select ();
                    this.mvHelper.notifySelectedLayer ();
                }
                break;

            // Layer 1-8: Toggle Active
            case LAYER_1_TOGGLE_ACTIVE, LAYER_2_TOGGLE_ACTIVE, LAYER_3_TOGGLE_ACTIVE, LAYER_4_TOGGLE_ACTIVE, LAYER_5_TOGGLE_ACTIVE, LAYER_6_TOGGLE_ACTIVE, LAYER_7_TOGGLE_ACTIVE, LAYER_8_TOGGLE_ACTIVE:
                if (isButtonPressed)
                    layerBank.getItem (command.ordinal () - FlexiCommand.LAYER_1_TOGGLE_ACTIVE.ordinal ()).toggleIsActivated ();
                break;
            // Layer 1-8: Set Active
            case LAYER_1_SET_ACTIVE, LAYER_2_SET_ACTIVE, LAYER_3_SET_ACTIVE, LAYER_4_SET_ACTIVE, LAYER_5_SET_ACTIVE, LAYER_6_SET_ACTIVE, LAYER_7_SET_ACTIVE, LAYER_8_SET_ACTIVE:
                if (isButtonPressed)
                    layerBank.getItem (command.ordinal () - FlexiCommand.LAYER_1_SET_ACTIVE.ordinal ()).setIsActivated (value.isPositive ());
                break;
            case LAYER_SELECTED_TOGGLE_ACTIVE:
                if (isButtonPressed && selectedLayer.isPresent ())
                    selectedLayer.get ().toggleIsActivated ();
                break;
            case LAYER_SELECTED_SET_ACTIVE:
                if (isButtonPressed && selectedLayer.isPresent ())
                    selectedLayer.get ().setIsActivated (value.isPositive ());
                break;

            // Layer 1-8: Set Volume
            case LAYER_1_SET_VOLUME, LAYER_2_SET_VOLUME, LAYER_3_SET_VOLUME, LAYER_4_SET_VOLUME, LAYER_5_SET_VOLUME, LAYER_6_SET_VOLUME, LAYER_7_SET_VOLUME, LAYER_8_SET_VOLUME:
                this.changeLayerVolume (knobMode, command.ordinal () - FlexiCommand.LAYER_1_SET_VOLUME.ordinal (), value);
                break;
            // Layer Selected: Set Volume Layer
            case LAYER_SELECTED_SET_VOLUME_LAYER:
                this.changeLayerVolume (knobMode, -1, value);
                break;

            // Layer 1-8: Set Panorama
            case LAYER_1_SET_PANORAMA, LAYER_2_SET_PANORAMA, LAYER_3_SET_PANORAMA, LAYER_4_SET_PANORAMA, LAYER_5_SET_PANORAMA, LAYER_6_SET_PANORAMA, LAYER_7_SET_PANORAMA, LAYER_8_SET_PANORAMA:
                this.changeLayerPanorama (knobMode, command.ordinal () - FlexiCommand.LAYER_1_SET_PANORAMA.ordinal (), value);
                break;
            // Layer Selected: Set Panorama
            case LAYER_SELECTED_SET_PANORAMA:
                this.changeLayerPanorama (knobMode, -1, value);
                break;

            // Layer 1-8: Toggle Mute
            case LAYER_1_TOGGLE_MUTE, LAYER_2_TOGGLE_MUTE, LAYER_3_TOGGLE_MUTE, LAYER_4_TOGGLE_MUTE, LAYER_5_TOGGLE_MUTE, LAYER_6_TOGGLE_MUTE, LAYER_7_TOGGLE_MUTE, LAYER_8_TOGGLE_MUTE:
                if (isButtonPressed)
                    layerBank.getItem (command.ordinal () - FlexiCommand.LAYER_1_TOGGLE_MUTE.ordinal ()).toggleMute ();
                break;
            // Layer 1-8: Set Mute
            case LAYER_1_SET_MUTE, LAYER_2_SET_MUTE, LAYER_3_SET_MUTE, LAYER_4_SET_MUTE, LAYER_5_SET_MUTE, LAYER_6_SET_MUTE, LAYER_7_SET_MUTE, LAYER_8_SET_MUTE:
                if (isButtonPressed)
                    layerBank.getItem (command.ordinal () - FlexiCommand.LAYER_1_SET_MUTE.ordinal ()).setMute (value.isPositive ());
                break;
            // Layer Selected: Toggle Mute
            case LAYER_SELECTED_TOGGLE_MUTE:
                if (isButtonPressed && selectedLayer.isPresent ())
                    selectedLayer.get ().toggleMute ();
                break;
            // Layer Selected: Set Mute
            case LAYER_SELECTED_SET_MUTE:
                if (isButtonPressed && selectedLayer.isPresent ())
                    selectedLayer.get ().setMute (value.isPositive ());
                break;

            // Layer 1-8: Toggle Solo
            case LAYER_1_TOGGLE_SOLO, LAYER_2_TOGGLE_SOLO, LAYER_3_TOGGLE_SOLO, LAYER_4_TOGGLE_SOLO, LAYER_5_TOGGLE_SOLO, LAYER_6_TOGGLE_SOLO, LAYER_7_TOGGLE_SOLO, LAYER_8_TOGGLE_SOLO:
                if (isButtonPressed)
                    layerBank.getItem (command.ordinal () - FlexiCommand.LAYER_1_TOGGLE_SOLO.ordinal ()).toggleSolo ();
                break;
            // Layer 1-8: Set Solo
            case LAYER_1_SET_SOLO, LAYER_2_SET_SOLO, LAYER_3_SET_SOLO, LAYER_4_SET_SOLO, LAYER_5_SET_SOLO, LAYER_6_SET_SOLO, LAYER_7_SET_SOLO, LAYER_8_SET_SOLO:
                if (isButtonPressed)
                    layerBank.getItem (command.ordinal () - FlexiCommand.LAYER_1_SET_SOLO.ordinal ()).setSolo (value.isPositive ());
                break;
            // Layer Selected: Toggle Solo
            case LAYER_SELECTED_TOGGLE_SOLO:
                if (isButtonPressed && selectedLayer.isPresent ())
                    selectedLayer.get ().toggleSolo ();
                break;
            // Layer Selected: Set Solo
            case LAYER_SELECTED_SET_SOLO:
                if (isButtonPressed && selectedLayer.isPresent ())
                    selectedLayer.get ().setSolo (value.isPositive ());
                break;

            // Layer 1-8: Set Send 1
            case LAYER_1_SET_SEND_1, LAYER_2_SET_SEND_1, LAYER_3_SET_SEND_1, LAYER_4_SET_SEND_1, LAYER_5_SET_SEND_1, LAYER_6_SET_SEND_1, LAYER_7_SET_SEND_1, LAYER_8_SET_SEND_1:
                this.changeSendVolume (command.ordinal () - FlexiCommand.LAYER_1_SET_SEND_1.ordinal (), 0, knobMode, value);
                break;
            // Layer 1-8: Set Send 2
            case LAYER_1_SET_SEND_2, LAYER_2_SET_SEND_2, LAYER_3_SET_SEND_2, LAYER_4_SET_SEND_2, LAYER_5_SET_SEND_2, LAYER_6_SET_SEND_2, LAYER_7_SET_SEND_2, LAYER_8_SET_SEND_2:
                this.changeSendVolume (command.ordinal () - FlexiCommand.LAYER_1_SET_SEND_2.ordinal (), 1, knobMode, value);
                break;
            // Layer 1-8: Set Send 3
            case LAYER_1_SET_SEND_3, LAYER_2_SET_SEND_3, LAYER_3_SET_SEND_3, LAYER_4_SET_SEND_3, LAYER_5_SET_SEND_3, LAYER_6_SET_SEND_3, LAYER_7_SET_SEND_3, LAYER_8_SET_SEND_3:
                this.changeSendVolume (command.ordinal () - FlexiCommand.LAYER_1_SET_SEND_3.ordinal (), 2, knobMode, value);
                break;
            // Layer 1-8: Set Send 4
            case LAYER_1_SET_SEND_4, LAYER_2_SET_SEND_4, LAYER_3_SET_SEND_4, LAYER_4_SET_SEND_4, LAYER_5_SET_SEND_4, LAYER_6_SET_SEND_4, LAYER_7_SET_SEND_4, LAYER_8_SET_SEND_4:
                this.changeSendVolume (command.ordinal () - FlexiCommand.LAYER_1_SET_SEND_4.ordinal (), 3, knobMode, value);
                break;
            // Layer 1: Set Send 5
            case LAYER_1_SET_SEND_5, LAYER_2_SET_SEND_5, LAYER_3_SET_SEND_5, LAYER_4_SET_SEND_5, LAYER_5_SET_SEND_5, LAYER_6_SET_SEND_5, LAYER_7_SET_SEND_5, LAYER_8_SET_SEND_5:
                this.changeSendVolume (command.ordinal () - FlexiCommand.LAYER_1_SET_SEND_5.ordinal (), 4, knobMode, value);
                break;
            // Layer 1: Set Send 6
            case LAYER_1_SET_SEND_6, LAYER_2_SET_SEND_6, LAYER_3_SET_SEND_6, LAYER_4_SET_SEND_6, LAYER_5_SET_SEND_6, LAYER_6_SET_SEND_6, LAYER_7_SET_SEND_6, LAYER_8_SET_SEND_6:
                this.changeSendVolume (command.ordinal () - FlexiCommand.LAYER_1_SET_SEND_6.ordinal (), 5, knobMode, value);
                break;
            // Layer 1-8: Set Send 7
            case LAYER_1_SET_SEND_7, LAYER_2_SET_SEND_7, LAYER_3_SET_SEND_7, LAYER_4_SET_SEND_7, LAYER_5_SET_SEND_7, LAYER_6_SET_SEND_7, LAYER_7_SET_SEND_7, LAYER_8_SET_SEND_7:
                this.changeSendVolume (command.ordinal () - FlexiCommand.LAYER_1_SET_SEND_7.ordinal (), 6, knobMode, value);
                break;
            // Layer 1-8: Set Send 8
            case LAYER_1_SET_SEND_8, LAYER_2_SET_SEND_8, LAYER_3_SET_SEND_8, LAYER_4_SET_SEND_8, LAYER_5_SET_SEND_8, LAYER_6_SET_SEND_8, LAYER_7_SET_SEND_8, LAYER_8_SET_SEND_8:
                this.changeSendVolume (command.ordinal () - FlexiCommand.LAYER_1_SET_SEND_8.ordinal (), 7, knobMode, value);
                break;
            // Layer Selected: Set Send 1-8
            case LAYER_SELECTED_SET_SEND_1, LAYER_SELECTED_SET_SEND_2, LAYER_SELECTED_SET_SEND_3, LAYER_SELECTED_SET_SEND_4, LAYER_SELECTED_SET_SEND_5, LAYER_SELECTED_SET_SEND_6, LAYER_SELECTED_SET_SEND_7, LAYER_SELECTED_SET_SEND_8:
                this.changeSendVolume (-1, command.ordinal () - FlexiCommand.LAYER_SELECTED_SET_SEND_1.ordinal (), knobMode, value);
                break;

            default:
                throw new FlexiHandlerException (command);
        }
    }


    private int getSendValue (final int layerIndex, final int sendIndex)
    {
        final Optional<ILayer> layer = this.getLayer (layerIndex);
        if (layer.isEmpty ())
            return 0;

        final ISendBank sendBank = layer.get ().getSendBank ();
        if (sendIndex >= sendBank.getPageSize ())
            return 0;

        final ISend send = sendBank.getItem (sendIndex);
        if (send == null)
            return 0;

        return send.getValue ();
    }


    private Optional<ILayer> getLayer (final int layerIndex)
    {
        final ILayerBank layerBank = this.getLayerBank ();
        if (layerBank == null)
            return Optional.empty ();
        if (layerIndex < 0)
            return layerBank.getSelectedItem ();
        final ILayer item = layerBank.getItem (layerIndex);
        return item.doesExist () ? Optional.of (item) : Optional.empty ();
    }


    private void changeLayerVolume (final KnobMode knobMode, final int layerIndex, final MidiValue value)
    {
        final Optional<ILayer> layer = this.getLayer (layerIndex);
        if (layer.isEmpty ())
            return;
        final int val = value.getValue ();
        final IParameter volumeParameter = layer.get ().getVolumeParameter ();
        if (isAbsolute (knobMode))
            volumeParameter.setValue (this.getAbsoluteValueChanger (value), val);
        else
            volumeParameter.changeValue (this.getRelativeValueChanger (knobMode), val);
    }


    private void changeLayerPanorama (final KnobMode knobMode, final int layerIndex, final MidiValue value)
    {
        final Optional<ILayer> layer = this.getLayer (layerIndex);
        if (layer.isEmpty ())
            return;
        final int val = value.getValue ();
        final IParameter panParameter = layer.get ().getPanParameter ();
        if (isAbsolute (knobMode))
            panParameter.setValue (this.getAbsoluteValueChanger (value), val);
        else
            panParameter.changeValue (this.getRelativeValueChanger (knobMode), val);
    }


    private void changeSendVolume (final int layerIndex, final int sendIndex, final KnobMode knobMode, final MidiValue value)
    {
        final Optional<ILayer> layer = this.getLayer (layerIndex);
        if (layer.isEmpty ())
            return;

        final ISendBank sendBank = layer.get ().getSendBank ();
        if (sendIndex >= sendBank.getPageSize ())
            return;

        final ISend send = sendBank.getItem (sendIndex);
        if (send == null)
            return;

        final int val = value.getValue ();
        if (isAbsolute (knobMode))
            send.setValue (this.getAbsoluteValueChanger (value), val);
        else
            send.changeValue (this.getRelativeValueChanger (knobMode), val);
    }


    private void scrollLayer (final KnobMode knobMode, final MidiValue value)
    {
        if (isAbsolute (knobMode) || !this.increaseKnobMovement ())
            return;

        if (this.isIncrease (knobMode, value))
            this.scrollLayerRight (false);
        else
            this.scrollLayerLeft (false);
    }


    private void scrollLayerLeft (final boolean switchBank)
    {
        final ILayerBank layerBank = this.getLayerBank ();
        if (layerBank == null)
            return;
        final Optional<ILayer> sel = layerBank.getSelectedItem ();
        final int index = sel.isEmpty () ? 0 : sel.get ().getIndex () - 1;
        if (index == -1 || switchBank)
        {
            layerBank.selectPreviousPage ();
            return;
        }
        layerBank.getItem (index).select ();
    }


    private void scrollLayerRight (final boolean switchBank)
    {
        final ILayerBank layerBank = this.getLayerBank ();
        if (layerBank == null)
            return;
        final Optional<ILayer> sel = layerBank.getSelectedItem ();
        final int index = sel.isEmpty () ? 0 : sel.get ().getIndex () + 1;
        if (index == 8 || switchBank)
        {
            layerBank.selectNextPage ();
            return;
        }
        layerBank.getItem (index).select ();
    }


    private ILayerBank getLayerBank ()
    {
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        return cursorDevice.hasLayers () ? cursorDevice.getLayerBank () : null;
    }
}
