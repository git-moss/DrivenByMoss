// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.mode;

import de.mossgrabers.controller.ni.maschine.core.MaschineColorManager;
import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.FaderConfig;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.mode.device.ParameterMode;
import de.mossgrabers.framework.parameter.IParameter;


/**
 * The parameter mode.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineJamParameterMode extends ParameterMode<MaschineJamControlSurface, MaschineJamConfiguration> implements IMaschineJamMode
{
    private static final FaderConfig FADER_OFF  = new FaderConfig (FaderConfig.TYPE_SINGLE, 0, 0);

    private FaderSlowChange          slowChange = new FaderSlowChange ();


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public MaschineJamParameterMode (final MaschineJamControlSurface surface, final IModel model)
    {
        super (surface, model, true);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        if (!this.cursorDevice.doesExist ())
            return;
        final IParameter item = this.cursorDevice.getParameterBank ().getItem (index);
        if (item != null && item.doesExist ())
            this.slowChange.changeValue (this.surface, item, value);
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        if (this.surface.isSelectPressed ())
            this.cursorDevice.selectPrevious ();
        else
            this.cursorDevice.getParameterBank ().scrollBackwards ();

        this.mvHelper.notifySelectedDeviceAndParameterPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        if (this.surface.isSelectPressed ())
            this.cursorDevice.selectNext ();
        else
            this.cursorDevice.getParameterBank ().scrollForwards ();

        this.mvHelper.notifySelectedDeviceAndParameterPage ();
    }


    /** {@inheritDoc} */
    @Override
    public FaderConfig setupFader (final int index)
    {
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        if (!cursorDevice.doesExist ())
            return FADER_OFF;

        final IParameter parameter = cursorDevice.getParameterBank ().getItem (index);
        if (!parameter.doesExist ())
            return FADER_OFF;

        final int value = this.model.getValueChanger ().toMidiValue (parameter.getValue ());
        return new FaderConfig (FaderConfig.TYPE_SINGLE, MaschineColorManager.PARAM_COLORS.get (index).intValue (), value);
    }
}