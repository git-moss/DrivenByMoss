// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.mode;

import de.mossgrabers.controller.ni.maschine.core.MaschineColorManager;
import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.FaderConfig;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.device.ProjectParamsMode;
import de.mossgrabers.framework.parameter.IParameter;


/**
 * The user parameter mode.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineJamUserMode extends ProjectParamsMode<MaschineJamControlSurface, MaschineJamConfiguration> implements IMaschineJamMode
{
    private static final FaderConfig FADER_OFF = new FaderConfig (FaderConfig.TYPE_SINGLE, 0, 0);


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public MaschineJamUserMode (final MaschineJamControlSurface surface, final IModel model)
    {
        super (surface, model, true, ContinuousID.createSequentialList (ContinuousID.FADER1, 8));
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