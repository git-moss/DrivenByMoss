// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.command.trigger;

import de.mossgrabers.controller.ni.maschine.core.command.trigger.EncoderMode;
import de.mossgrabers.controller.ni.maschine.core.controller.EncoderModeManager;
import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.GrooveParameterID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Command to change the groove swing amount.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineJamSwingCommand extends AbstractTriggerCommand<MaschineJamControlSurface, MaschineJamConfiguration>
{
    private final EncoderModeManager<MaschineJamControlSurface, MaschineJamConfiguration> encoderManager;


    /**
     * Constructor.
     *
     * @param encoderManager The encoder manager
     * @param model The model
     * @param surface The surface
     */
    public MaschineJamSwingCommand (final EncoderModeManager<MaschineJamControlSurface, MaschineJamConfiguration> encoderManager, final IModel model, final MaschineJamControlSurface surface)
    {
        super (model, surface);

        this.encoderManager = encoderManager;
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final IParameter parameter = this.model.getGroove ().getParameter (GrooveParameterID.SHUFFLE_RATE);
        parameter.setValue (parameter.getValue () == 0 ? this.model.getValueChanger ().getUpperBound () - 1 : 0);
        this.mvHelper.delayDisplay ( () -> parameter.getName () + ": " + parameter.getDisplayedValue ());
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (this.surface.isSelectPressed ())
        {
            if (event == ButtonEvent.DOWN)
            {
                final IParameter parameter = this.model.getGroove ().getParameter (GrooveParameterID.ENABLED);
                parameter.setValue (parameter.getValue () == 0 ? this.model.getValueChanger ().getUpperBound () - 1 : 0);
                this.mvHelper.delayDisplay ( () -> parameter.getName () + ": " + parameter.getDisplayedValue ());
            }
            return;
        }

        if (event == ButtonEvent.DOWN)
        {
            this.surface.getViewManager ().setTemporary (Views.SHUFFLE);
            this.encoderManager.enableTemporaryEncodeMode (EncoderMode.TEMPORARY_SWING);
        }
        else if (event == ButtonEvent.UP)
        {
            this.surface.getViewManager ().restore ();
            this.encoderManager.disableTemporaryEncodeMode ();
        }
    }
}
