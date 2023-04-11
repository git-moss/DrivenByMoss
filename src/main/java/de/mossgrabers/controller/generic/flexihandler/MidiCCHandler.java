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
import de.mossgrabers.framework.daw.midi.MidiConstants;


/**
 * The handler for MIDI CC commands.
 *
 * @author Jürgen Moßgraber
 */
public class MidiCCHandler extends AbstractHandler
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
    public MidiCCHandler (final IModel model, final GenericFlexiControlSurface surface, final GenericFlexiConfiguration configuration, final IValueChanger absoluteLowResValueChanger, final IValueChanger signedBitRelativeValueChanger, final IValueChanger offsetBinaryRelativeValueChanger)
    {
        super (model, surface, configuration, absoluteLowResValueChanger, signedBitRelativeValueChanger, offsetBinaryRelativeValueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public FlexiCommand [] getSupportedCommands ()
    {
        return new FlexiCommand []
        {
            FlexiCommand.MIDI_CC_0,
            FlexiCommand.MIDI_CC_1,
            FlexiCommand.MIDI_CC_2,
            FlexiCommand.MIDI_CC_3,
            FlexiCommand.MIDI_CC_4,
            FlexiCommand.MIDI_CC_5,
            FlexiCommand.MIDI_CC_6,
            FlexiCommand.MIDI_CC_7,
            FlexiCommand.MIDI_CC_8,
            FlexiCommand.MIDI_CC_9,
            FlexiCommand.MIDI_CC_10,
            FlexiCommand.MIDI_CC_11,
            FlexiCommand.MIDI_CC_12,
            FlexiCommand.MIDI_CC_13,
            FlexiCommand.MIDI_CC_14,
            FlexiCommand.MIDI_CC_15,
            FlexiCommand.MIDI_CC_16,
            FlexiCommand.MIDI_CC_17,
            FlexiCommand.MIDI_CC_18,
            FlexiCommand.MIDI_CC_19,
            FlexiCommand.MIDI_CC_20,
            FlexiCommand.MIDI_CC_21,
            FlexiCommand.MIDI_CC_22,
            FlexiCommand.MIDI_CC_23,
            FlexiCommand.MIDI_CC_24,
            FlexiCommand.MIDI_CC_25,
            FlexiCommand.MIDI_CC_26,
            FlexiCommand.MIDI_CC_27,
            FlexiCommand.MIDI_CC_28,
            FlexiCommand.MIDI_CC_29,
            FlexiCommand.MIDI_CC_30,
            FlexiCommand.MIDI_CC_31,
            FlexiCommand.MIDI_CC_32,
            FlexiCommand.MIDI_CC_33,
            FlexiCommand.MIDI_CC_34,
            FlexiCommand.MIDI_CC_35,
            FlexiCommand.MIDI_CC_36,
            FlexiCommand.MIDI_CC_37,
            FlexiCommand.MIDI_CC_38,
            FlexiCommand.MIDI_CC_39,
            FlexiCommand.MIDI_CC_40,
            FlexiCommand.MIDI_CC_41,
            FlexiCommand.MIDI_CC_42,
            FlexiCommand.MIDI_CC_43,
            FlexiCommand.MIDI_CC_44,
            FlexiCommand.MIDI_CC_45,
            FlexiCommand.MIDI_CC_46,
            FlexiCommand.MIDI_CC_47,
            FlexiCommand.MIDI_CC_48,
            FlexiCommand.MIDI_CC_49,
            FlexiCommand.MIDI_CC_50,
            FlexiCommand.MIDI_CC_51,
            FlexiCommand.MIDI_CC_52,
            FlexiCommand.MIDI_CC_53,
            FlexiCommand.MIDI_CC_54,
            FlexiCommand.MIDI_CC_55,
            FlexiCommand.MIDI_CC_56,
            FlexiCommand.MIDI_CC_57,
            FlexiCommand.MIDI_CC_58,
            FlexiCommand.MIDI_CC_59,
            FlexiCommand.MIDI_CC_60,
            FlexiCommand.MIDI_CC_61,
            FlexiCommand.MIDI_CC_62,
            FlexiCommand.MIDI_CC_63,
            FlexiCommand.MIDI_CC_64,
            FlexiCommand.MIDI_CC_65,
            FlexiCommand.MIDI_CC_66,
            FlexiCommand.MIDI_CC_67,
            FlexiCommand.MIDI_CC_68,
            FlexiCommand.MIDI_CC_69,
            FlexiCommand.MIDI_CC_70,
            FlexiCommand.MIDI_CC_71,
            FlexiCommand.MIDI_CC_72,
            FlexiCommand.MIDI_CC_73,
            FlexiCommand.MIDI_CC_74,
            FlexiCommand.MIDI_CC_75,
            FlexiCommand.MIDI_CC_76,
            FlexiCommand.MIDI_CC_77,
            FlexiCommand.MIDI_CC_78,
            FlexiCommand.MIDI_CC_79,
            FlexiCommand.MIDI_CC_80,
            FlexiCommand.MIDI_CC_81,
            FlexiCommand.MIDI_CC_82,
            FlexiCommand.MIDI_CC_83,
            FlexiCommand.MIDI_CC_84,
            FlexiCommand.MIDI_CC_85,
            FlexiCommand.MIDI_CC_86,
            FlexiCommand.MIDI_CC_87,
            FlexiCommand.MIDI_CC_88,
            FlexiCommand.MIDI_CC_89,
            FlexiCommand.MIDI_CC_90,
            FlexiCommand.MIDI_CC_91,
            FlexiCommand.MIDI_CC_92,
            FlexiCommand.MIDI_CC_93,
            FlexiCommand.MIDI_CC_94,
            FlexiCommand.MIDI_CC_95,
            FlexiCommand.MIDI_CC_96,
            FlexiCommand.MIDI_CC_97,
            FlexiCommand.MIDI_CC_98,
            FlexiCommand.MIDI_CC_99,
            FlexiCommand.MIDI_CC_100,
            FlexiCommand.MIDI_CC_101,
            FlexiCommand.MIDI_CC_102,
            FlexiCommand.MIDI_CC_103,
            FlexiCommand.MIDI_CC_104,
            FlexiCommand.MIDI_CC_105,
            FlexiCommand.MIDI_CC_106,
            FlexiCommand.MIDI_CC_107,
            FlexiCommand.MIDI_CC_108,
            FlexiCommand.MIDI_CC_109,
            FlexiCommand.MIDI_CC_110,
            FlexiCommand.MIDI_CC_111,
            FlexiCommand.MIDI_CC_112,
            FlexiCommand.MIDI_CC_113,
            FlexiCommand.MIDI_CC_114,
            FlexiCommand.MIDI_CC_115,
            FlexiCommand.MIDI_CC_116,
            FlexiCommand.MIDI_CC_117,
            FlexiCommand.MIDI_CC_118,
            FlexiCommand.MIDI_CC_119,
            FlexiCommand.MIDI_CC_120,
            FlexiCommand.MIDI_CC_121,
            FlexiCommand.MIDI_CC_122,
            FlexiCommand.MIDI_CC_123,
            FlexiCommand.MIDI_CC_124,
            FlexiCommand.MIDI_CC_125,
            FlexiCommand.MIDI_CC_126,
            FlexiCommand.MIDI_CC_127
        };
    }


    /** {@inheritDoc} */
    @Override
    public int getCommandValue (final FlexiCommand command)
    {
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public void handle (final FlexiCommand command, final KnobMode knobMode, final MidiValue value)
    {
        switch (command)
        {
            case MIDI_CC_0:
            case MIDI_CC_1:
            case MIDI_CC_2:
            case MIDI_CC_3:
            case MIDI_CC_4:
            case MIDI_CC_5:
            case MIDI_CC_6:
            case MIDI_CC_7:
            case MIDI_CC_8:
            case MIDI_CC_9:
            case MIDI_CC_10:
            case MIDI_CC_11:
            case MIDI_CC_12:
            case MIDI_CC_13:
            case MIDI_CC_14:
            case MIDI_CC_15:
            case MIDI_CC_16:
            case MIDI_CC_17:
            case MIDI_CC_18:
            case MIDI_CC_19:
            case MIDI_CC_20:
            case MIDI_CC_21:
            case MIDI_CC_22:
            case MIDI_CC_23:
            case MIDI_CC_24:
            case MIDI_CC_25:
            case MIDI_CC_26:
            case MIDI_CC_27:
            case MIDI_CC_28:
            case MIDI_CC_29:
            case MIDI_CC_30:
            case MIDI_CC_31:
            case MIDI_CC_32:
            case MIDI_CC_33:
            case MIDI_CC_34:
            case MIDI_CC_35:
            case MIDI_CC_36:
            case MIDI_CC_37:
            case MIDI_CC_38:
            case MIDI_CC_39:
            case MIDI_CC_40:
            case MIDI_CC_41:
            case MIDI_CC_42:
            case MIDI_CC_43:
            case MIDI_CC_44:
            case MIDI_CC_45:
            case MIDI_CC_46:
            case MIDI_CC_47:
            case MIDI_CC_48:
            case MIDI_CC_49:
            case MIDI_CC_50:
            case MIDI_CC_51:
            case MIDI_CC_52:
            case MIDI_CC_53:
            case MIDI_CC_54:
            case MIDI_CC_55:
            case MIDI_CC_56:
            case MIDI_CC_57:
            case MIDI_CC_58:
            case MIDI_CC_59:
            case MIDI_CC_60:
            case MIDI_CC_61:
            case MIDI_CC_62:
            case MIDI_CC_63:
            case MIDI_CC_64:
            case MIDI_CC_65:
            case MIDI_CC_66:
            case MIDI_CC_67:
            case MIDI_CC_68:
            case MIDI_CC_69:
            case MIDI_CC_70:
            case MIDI_CC_71:
            case MIDI_CC_72:
            case MIDI_CC_73:
            case MIDI_CC_74:
            case MIDI_CC_75:
            case MIDI_CC_76:
            case MIDI_CC_77:
            case MIDI_CC_78:
            case MIDI_CC_79:
            case MIDI_CC_80:
            case MIDI_CC_81:
            case MIDI_CC_82:
            case MIDI_CC_83:
            case MIDI_CC_84:
            case MIDI_CC_85:
            case MIDI_CC_86:
            case MIDI_CC_87:
            case MIDI_CC_88:
            case MIDI_CC_89:
            case MIDI_CC_90:
            case MIDI_CC_91:
            case MIDI_CC_92:
            case MIDI_CC_93:
            case MIDI_CC_94:
            case MIDI_CC_95:
            case MIDI_CC_96:
            case MIDI_CC_97:
            case MIDI_CC_98:
            case MIDI_CC_99:
            case MIDI_CC_100:
            case MIDI_CC_101:
            case MIDI_CC_102:
            case MIDI_CC_103:
            case MIDI_CC_104:
            case MIDI_CC_105:
            case MIDI_CC_106:
            case MIDI_CC_107:
            case MIDI_CC_108:
            case MIDI_CC_109:
            case MIDI_CC_110:
            case MIDI_CC_111:
            case MIDI_CC_112:
            case MIDI_CC_113:
            case MIDI_CC_114:
            case MIDI_CC_115:
            case MIDI_CC_116:
            case MIDI_CC_117:
            case MIDI_CC_118:
            case MIDI_CC_119:
            case MIDI_CC_120:
            case MIDI_CC_121:
            case MIDI_CC_122:
            case MIDI_CC_123:
            case MIDI_CC_124:
            case MIDI_CC_125:
            case MIDI_CC_126:
            case MIDI_CC_127:
                int val = value.getValue ();
                if (value.isHighRes ())
                    val = val % 128;
                this.surface.getMidiInput ().sendRawMidiEvent (MidiConstants.CMD_CC, command.ordinal () - FlexiCommand.MIDI_CC_0.ordinal (), val);
                break;

            default:
                throw new FlexiHandlerException (command);
        }
    }
}
