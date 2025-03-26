// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.melbourne.rotocontrol.mode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.mossgrabers.controller.melbourne.rotocontrol.RotoControlConfiguration;
import de.mossgrabers.controller.melbourne.rotocontrol.controller.RotoControlControlSurface;
import de.mossgrabers.controller.melbourne.rotocontrol.controller.RotoControlMessage;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IParameterList;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * The device parameter mode. The knobs control the value of the parameter on the parameter page.
 * device. Updates all relevant parameters for the mode on the ROTO CONTROl.
 *
 * @author Jürgen Moßgraber
 */
public class RotoControlDeviceParameterMode extends AbstractParameterMode<RotoControlControlSurface, RotoControlConfiguration, IParameter> implements RotoControlMode
{
    private final RotoControlDisplay           rotoDisplay;
    private boolean                            learnMode;
    private int                                lastMappedParameterIndex = -1;
    private final ReplaceableParameterProvider parameterProvider;
    private final ICursorDevice                cursorDevice;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public RotoControlDeviceParameterMode (final RotoControlControlSurface surface, final IModel model)
    {
        super ("Parameters", surface, model, true);

        this.cursorDevice = this.model.getCursorDevice ();

        // Monitor the 'normal' selected parameter page for changes which will be used to trigger
        // the learn mode
        final IParameterBank parameterBank = this.cursorDevice.getParameterBank ();
        for (int i = 0; i < 8; i++)
        {
            final int index = i;
            parameterBank.getItem (i).addValueObserver (Void -> this.handleMapping (index));
        }

        // Add 8 knob and 8 button parameters
        this.parameterProvider = new ReplaceableParameterProvider (16);
        // TODO 5.3.3: use this when switched to Bitwig binding
        // if (this.controls != null)
        // this.setParameterProvider (this.parameterProvider);

        this.rotoDisplay = new RotoControlDisplay (surface, model);

        this.flushDisplay ();
    }


    /** {@inheritDoc} */
    @Override
    public void flushDisplay ()
    {
        this.rotoDisplay.flushDeviceDisplay ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        // Sends only information about devices! Parameter names are stored in the ROTO-CONTROL!
        this.rotoDisplay.updateDeviceDisplay ();
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final IParameter item = this.parameterProvider.get (index);
        if (item != null && item.doesExist ())
            item.setValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        if (!this.cursorDevice.doesExist ())
            return -1;
        final IParameter item = this.parameterProvider.get (index);
        return item != null && item.doesExist () ? item.getValue () : -1;
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event == ButtonEvent.LONG)
            return;
        final IParameter item = this.parameterProvider.get (8 + index);
        if (item != null && item.doesExist ())
            item.setValue (event == ButtonEvent.DOWN ? this.model.getValueChanger ().getUpperBound () - 1 : 0);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final int index = buttonID.ordinal () - ButtonID.ROW1_1.ordinal ();
        final IParameter item = this.parameterProvider.get (8 + index);
        return item != null && item.doesExist () && item.getValue () > 0 ? 127 : 0;
    }


    /**
     * Turn learn mode on/off.
     *
     * @param activateLearn True to activate the learn mode
     */
    public void setParamLearn (final boolean activateLearn)
    {
        this.learnMode = activateLearn;
    }


    /**
     * Bind a parameter.
     * 
     * @param paramIndex The index of the parameter across all pages
     * @param posInPage The position of the knob/switch
     * @param isSwitch True if it is a switch, false if it is a knob
     */
    public void bind (final int paramIndex, final int posInPage, final boolean isSwitch)
    {
        final IParameterList parameterList = this.cursorDevice.getParameterList ();
        final int index = isSwitch ? 8 + posInPage : posInPage;
        this.parameterProvider.set (index, parameterList.getParameters ().get (paramIndex));
    }


    /**
     * If a remote control knob has been moved in Bitwig send the related parameter data to the
     * ROTO-CONTROL if learn mode is active.
     *
     * @param index The index of the remote control (0-7)
     */
    private void handleMapping (final int index)
    {
        if (!this.learnMode)
            return;

        final IParameterBank parameterBank = this.cursorDevice.getParameterBank ();
        final IParameter param = parameterBank.getItem (index);
        if (!param.doesExist ())
            return;

        final int position = parameterBank.getPageBank ().getSelectedItemIndex ();
        if (position < 0)
            return;

        final int paramIndex = position * parameterBank.getPageSize () + index;
        // Already mapped? To prevent multiple learn messages when several value change messages
        // appear
        if (this.lastMappedParameterIndex == paramIndex)
            return;

        final ByteArrayOutputStream out = new ByteArrayOutputStream ();
        out.write ((byte) (paramIndex >> 7 & 0x7F));
        out.write ((byte) (paramIndex & 0x7F));
        for (final byte b: RotoControlDisplay.createHash (param.getName (), 6))
            out.write ((byte) (b & 0x7F));

        // TODO API 20: set correct value for stepped parameters!
        out.write ((byte) 0);

        try
        {
            out.write (RotoControlDisplay.create13ByteTextArray (param.getName ()).getBytes ());
        }
        catch (final IOException ex)
        {
            // Ignore
        }

        // TODO API 20: Add names for stepped values if any

        this.surface.sendSysex (RotoControlMessage.PLUGIN, RotoControlMessage.TR_LEARN_PARAM, out.toByteArray ());

        this.lastMappedParameterIndex = paramIndex;
    }
}
