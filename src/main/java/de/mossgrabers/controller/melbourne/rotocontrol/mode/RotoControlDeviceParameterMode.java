// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.melbourne.rotocontrol.mode;

import de.mossgrabers.controller.melbourne.rotocontrol.RotoControlConfiguration;
import de.mossgrabers.controller.melbourne.rotocontrol.controller.RotoControlControlSurface;
import de.mossgrabers.controller.melbourne.rotocontrol.controller.RotoControlMessage;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IParameterList;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;


/**
 * The device parameter mode. The knobs control the value of the parameter on the parameter page.
 * device. Updates all relevant parameters for the mode on the ROTO CONTROl.
 *
 * @author Jürgen Moßgraber
 */
public class RotoControlDeviceParameterMode extends AbstractParameterMode<RotoControlControlSurface, RotoControlConfiguration, IParameter>
{
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
        this.cursorDevice.getParameterBank ().addValueObserver ( (page, index) -> this.handleMapping (page, index));

        // Add 8 knob and 8 button parameters
        this.parameterProvider = new ReplaceableParameterProvider (16);
        // TODO 5.3.3: use this when switched to Bitwig binding
        // if (this.controls != null)
        // this.setParameterProvider (this.parameterProvider);
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

        final IHost host = this.surface.getHost ();
        final int maxNumberOfParameters = parameterList.getMaxNumberOfParameters ();
        if (paramIndex >= maxNumberOfParameters)
        {
            host.error ("Parameter at index " + paramIndex + " is higher than the number of supported parameters (" + maxNumberOfParameters + ").");
            return;
        }

        final int index = isSwitch ? 8 + posInPage : posInPage;
        final List<IParameter> parameters = parameterList.getParameters ();
        final int numParams = parameters.size ();
        if (paramIndex < numParams)
        {
            final IParameter parameter = parameters.get (paramIndex);
            this.parameterProvider.set (index, parameter);

            host.scheduleTask ( () -> {
                // Needs to be send again since Firmware 1.1.3
                this.sendLearnParam (parameter, paramIndex);
            }, 100);
        }
        else
            host.error ("Parameter at index " + paramIndex + " is out of parameter range (" + numParams + ").");
    }


    /**
     * If a remote control knob has been moved in Bitwig send the related parameter data to the
     * ROTO-CONTROL if learn mode is active.
     *
     * @param page The page of the parameter
     * @param index The index of the remote control (0-7)
     */
    private void handleMapping (final int page, final int index)
    {
        if (!this.learnMode)
            return;

        final IParameterList parameterList = this.cursorDevice.getParameterList ();
        final int paramIndex = page * this.cursorDevice.getParameterBank ().getPageSize () + index;

        final IHost host = this.surface.getHost ();

        final int maxNumberOfParameters = parameterList.getMaxNumberOfParameters ();
        if (paramIndex >= maxNumberOfParameters)
        {
            host.error ("Parameter at index " + paramIndex + " is higher than the number of supported parameters (" + maxNumberOfParameters + ").");
            return;
        }

        final List<IParameter> parameters = parameterList.getParameters ();
        final int numParams = parameters.size ();
        if (paramIndex >= numParams)
        {
            host.error ("Parameter at index " + paramIndex + " is out of parameter range (" + numParams + ").");
            return;
        }

        final IParameter param = parameters.get (paramIndex);
        if (param.doesExist ())
            this.sendLearnParam (param, paramIndex);
    }


    private void sendLearnParam (final IParameter param, final int paramIndex)
    {
        // Already mapped? To prevent multiple learn messages when several value change messages
        // appear
        if (this.learnMode && this.lastMappedParameterIndex == paramIndex)
            return;

        // Add parameter index & hash
        final ByteArrayOutputStream out = new ByteArrayOutputStream ();
        out.write ((byte) (paramIndex >> 7 & 0x7F));
        out.write ((byte) (paramIndex & 0x7F));
        final byte [] hash = RotoControlDisplay.createHash (param.getName (), 6);
        for (final byte b: hash)
            out.write ((byte) (b & 0x7F));

        // Not a MACRO
        out.write ((byte) 0);

        // Set correct value for stepped parameters! Max number is 0x18.
        final int numberOfSteps = param.getNumberOfSteps ();
        out.write ((byte) (numberOfSteps >= 0x02 && numberOfSteps <= 0x18 ? numberOfSteps : 0));

        // The value is already in the range of [0..16383]
        final int paramValue = param.getValue ();
        out.write ((byte) (paramValue >> 7 & 0x7F));
        out.write ((byte) (paramValue & 0x7F));

        try
        {
            out.write (RotoControlDisplay.create13ByteTextArray (param.getName ()).getBytes ());

            // Add names for stepped values if any - but only if they are not more than 0x10
            if (numberOfSteps >= 0x02 && numberOfSteps <= 0x10)
            {
                for (int i = 0; i < numberOfSteps; i++)
                    out.write (RotoControlDisplay.create13ByteTextArray ("Option " + (i + 1)).getBytes ());
            }
        }
        catch (final IOException ex)
        {
            // Ignore
        }

        this.surface.sendSysex (RotoControlMessage.PLUGIN, RotoControlMessage.TR_LEARN_PARAM, out.toByteArray ());

        this.lastMappedParameterIndex = paramIndex;
    }
}
