// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.mode;

import de.mossgrabers.controller.ni.maschine.core.MaschineColorManager;
import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.FaderConfig;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.mode.device.ProjectParamsMode;
import de.mossgrabers.framework.parameter.IParameter;


/**
 * The user parameter mode.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineJamUserMode extends ProjectParamsMode<MaschineJamControlSurface, MaschineJamConfiguration> implements IMaschineJamMode
{
    private static final FaderConfig FADER_OFF  = new FaderConfig (FaderConfig.TYPE_SINGLE, 0, 0);

    private FaderSlowChange          slowChange = new FaderSlowChange ();


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public MaschineJamUserMode (final MaschineJamControlSurface surface, final IModel model)
    {
        super (surface, model, true, null);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final IParameterBank bank = this.isProjectMode ? this.model.getProject ().getParameterBank () : this.model.getCursorTrack ().getParameterBank ();
        final IParameter item = bank.getItem (index);
        if (item != null && item.doesExist ())
            this.slowChange.changeValue (this.surface, item, value);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.setTouchedKnob (index, isTouched);

        final IParameter param = this.bank.getItem (index);
        if (!param.doesExist ())
            return;

        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);
            param.resetValue ();
        }
        param.touchValue (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        super.selectPreviousItem ();
        if (this.isProjectMode)
            this.mvHelper.notifySelectedProjectParameterPage ();
        else
            this.mvHelper.notifySelectedTrackParameterPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        super.selectNextItem ();
        if (this.isProjectMode)
            this.mvHelper.notifySelectedProjectParameterPage ();
        else
            this.mvHelper.notifySelectedTrackParameterPage ();
    }


    /** {@inheritDoc} */
    @Override
    public FaderConfig setupFader (final int index)
    {
        final IParameter parameter = this.bank.getItem (index);
        if (!parameter.doesExist ())
            return FADER_OFF;

        final int value = this.model.getValueChanger ().toMidiValue (parameter.getValue ());
        return new FaderConfig (FaderConfig.TYPE_SINGLE, this.isProjectMode ? MaschineColorManager.COLOR_WHITE : MaschineColorManager.COLOR_AMBER, value);
    }
}