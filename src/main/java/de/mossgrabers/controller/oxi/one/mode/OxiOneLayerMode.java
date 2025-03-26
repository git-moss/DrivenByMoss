// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.mode;

import java.util.Optional;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IDrumDevice;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.bank.ILayerBank;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.graphics.canvas.component.IComponent;
import de.mossgrabers.framework.graphics.canvas.component.simple.TitleChannelsComponent;
import de.mossgrabers.framework.graphics.canvas.component.simple.TitleValueMenuComponent;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameterprovider.device.SelectedLayerOrDrumPadParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FourKnobProvider;


/**
 * The layer mode. The knobs control the volume, the panning and the sends of the selected layer
 * channel.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneLayerMode extends AbstractParameterMode<OxiOneControlSurface, OxiOneConfiguration, ILayer> implements IOxiModeDisplay
{
    private static final String [] MENU              =
    {
        "Vol",
        "Pan",
        "S1",
        "S2"
    };

    private static final String [] SHIFTED_MENU      =
    {
        "S3",
        "S4",
        "S5",
        "S6"
    };

    private static final Modes []  MODES             =
    {
        Modes.VOLUME,
        Modes.PAN,
        Modes.SEND1,
        Modes.SEND2
    };

    private static final Modes []  ALT_MODES         =
    {
        Modes.SEND3,
        Modes.SEND4,
        Modes.SEND5,
        Modes.SEND6
    };

    private Modes                  selectedParameter = Modes.VOLUME;
    private boolean                isMixerMode       = false;
    protected final ICursorDevice  cursorDevice;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public OxiOneLayerMode (final OxiOneControlSurface surface, final IModel model)
    {
        super (Modes.NAME_LAYER, surface, model, true, model.getCursorDevice ().getLayerBank ());

        this.isAlternativeFunction = () -> false;
        this.cursorDevice = this.model.getCursorDevice ();

        this.setControls (ContinuousID.createSequentialList (ContinuousID.KNOB1, 4));
        this.setParameterProvider (new FourKnobProvider<> (surface, new SelectedLayerOrDrumPadParameterProvider (this.getDrumDevice ()), ButtonID.SHIFT));
        this.cursorDevice.addHasDrumPadsObserver (hasDrumPads -> this.switchBanks (this.cursorDevice.hasDrumPads () ? this.cursorDevice.getDrumPadBank () : this.cursorDevice.getLayerBank ()));
    }


    /** {@inheritDoc} */
    @Override
    public void toggleDisplay ()
    {
        this.isMixerMode = !this.isMixerMode;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        this.updateMode ();

        final IGraphicDisplay display = this.surface.getGraphicsDisplay ();
        display.addElement (this.isMixerMode ? this.drawMixerMode () : this.drawLayerMode ());
        display.send ();
    }


    private IComponent drawLayerMode ()
    {
        String desc = "Select";
        String label = "a drum pad";
        int value = -1;
        int vuLeft = -1;
        int vuRight = -1;
        boolean isPan = false;

        final Optional<ILayer> channelOptional = this.getDrumDevice ().getLayerBank ().getSelectedItem ();
        if (channelOptional.isPresent ())
        {
            final IChannel channel = channelOptional.get ();
            vuLeft = channel.getVuLeft ();
            vuRight = channel.getVuRight ();

            desc = channel.getPosition () + 1 + ": " + channel.getName (9);

            final ISendBank sendBank = channel.getSendBank ();

            switch (this.selectedParameter)
            {
                case VOLUME:
                    label = "Vol: " + channel.getVolumeStr ();
                    value = channel.getVolume ();
                    break;

                case PAN:
                    label = "Pan: " + channel.getPanStr ();
                    value = channel.getPan ();
                    isPan = true;
                    break;

                case SEND1:
                case SEND2:
                case SEND3:
                case SEND4:
                case SEND5:
                case SEND6:
                    final int sendIndex = this.selectedParameter.ordinal () - Modes.SEND1.ordinal ();
                    label = getSendLabel (sendBank, sendIndex);
                    value = getSendValue (sendBank, sendIndex);
                    break;

                default:
                    label = "Select a track";
                    break;
            }
        }

        return new TitleValueMenuComponent (desc, label, this.surface.isShiftPressed () ? SHIFTED_MENU : MENU, value, vuLeft, vuRight, isPan);
    }


    private IComponent drawMixerMode ()
    {
        final ISpecificDevice cd = this.model.getDrumDevice ();
        final ILayerBank layerBank = cd.getLayerBank ();
        final Optional<ILayer> channelOptional = layerBank.getSelectedItem ();
        String label = "None";
        if (channelOptional.isPresent ())
        {
            final IChannel channel = channelOptional.get ();
            label = channel.getPosition () + 1 + ": " + channel.getName (9);
        }

        final int size = layerBank.getPageSize ();
        final boolean [] selected = new boolean [size];
        final int [] values = new int [size];

        final boolean isMode = this.isAnyKnobTouched () || !this.model.getTransport ().isPlaying ();
        if (isMode)
        {
            switch (this.selectedParameter)
            {
                case VOLUME:
                    for (int i = 0; i < size; i++)
                    {
                        final IChannel channel = layerBank.getItem (i);
                        selected[i] = channel.isSelected ();
                        values[i] = channel.getVolume ();
                    }
                    break;

                case PAN:
                    for (int i = 0; i < size; i++)
                    {
                        final IChannel channel = layerBank.getItem (i);
                        selected[i] = channel.isSelected ();
                        values[i] = channel.getPan ();
                    }
                    break;

                case SEND1:
                case SEND2:
                case SEND3:
                case SEND4:
                case SEND5:
                case SEND6:
                    final int sendIndex = this.selectedParameter.ordinal () - Modes.SEND1.ordinal ();
                    for (int i = 0; i < size; i++)
                    {
                        final IChannel channel = layerBank.getItem (i);
                        selected[i] = channel.isSelected ();

                        final ISendBank sendBank = channel.getSendBank ();
                        if (sendBank != null)
                        {
                            final ISend send = sendBank.getItem (sendIndex);
                            if (send.doesExist ())
                                values[i] = send.getValue ();
                        }
                    }
                    break;

                default:
                    // Not used
                    break;
            }
        }
        else
        {
            for (int i = 0; i < size; i++)
            {
                final IChannel channel = layerBank.getItem (i);
                selected[i] = channel.isSelected ();
                values[i] = channel.getVu ();
            }
        }

        return new TitleChannelsComponent (label, selected, values, this.selectedParameter == Modes.PAN && isMode);
    }


    /**
     * Ensure that the correct mode is still active in case the ALT key was toggled.
     */
    protected void updateMode ()
    {
        int index = -1;
        final Modes [] ms = this.surface.isPressed (ButtonID.SHIFT) ? MODES : ALT_MODES;
        for (int i = 0; i < ms.length; i++)
        {
            if (ms[i] == this.selectedParameter)
            {
                index = i;
                break;
            }
        }
        if (index >= 0)
            this.selectedParameter = this.surface.isPressed (ButtonID.SHIFT) ? ALT_MODES[index] : MODES[index];
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.selectedParameter = this.surface.isPressed (ButtonID.SHIFT) ? ALT_MODES[index] : MODES[index];

        this.setTouchedKnob (index, isTouched);

        final Optional<ILayer> channelOptional = this.getDrumDevice ().getLayerBank ().getSelectedItem ();
        if (!channelOptional.isPresent ())
            return;

        final IChannel channel = channelOptional.get ();

        switch (this.selectedParameter)
        {
            case VOLUME:
                if (isTouched && this.surface.isDeletePressed ())
                    channel.resetVolume ();
                channel.touchVolume (isTouched);
                break;

            case PAN:
                if (isTouched && this.surface.isDeletePressed ())
                    channel.resetPan ();
                channel.touchPan (isTouched);
                break;

            case SEND1, SEND2, SEND3, SEND4, SEND5, SEND6:
                final int sendIndex = this.selectedParameter.ordinal () - Modes.SEND1.ordinal ();
                final ISend item = channel.getSendBank ().getItem (sendIndex);
                if (isTouched && this.surface.isDeletePressed ())
                    item.resetValue ();
                item.touchValue (isTouched);
                break;

            default:
                // No more
                break;
        }
    }


    private static String getSendLabel (final ISendBank sendBank, final int index)
    {
        final int pos = index + 1;

        if (sendBank == null)
            return "No FX " + pos;

        final ISend send = sendBank.getItem (index);
        if (!send.doesExist ())
            return "No FX " + pos;

        return send.getName (4) + ": " + send.getDisplayedValue ();
    }


    private static int getSendValue (final ISendBank sendBank, final int index)
    {
        if (sendBank == null)
            return 0;
        final ISend send = sendBank.getItem (index);
        return send.doesExist () ? send.getValue () : 0;
    }


    private IDrumDevice getDrumDevice ()
    {
        return this.model.getDrumDevice (16);
    }
}
