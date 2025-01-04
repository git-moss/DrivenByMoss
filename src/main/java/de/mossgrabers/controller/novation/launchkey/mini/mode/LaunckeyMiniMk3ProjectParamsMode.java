// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchkey.mini.mode;

import java.util.Optional;

import de.mossgrabers.controller.novation.launchkey.mini.LaunchkeyMiniMk3Configuration;
import de.mossgrabers.controller.novation.launchkey.mini.controller.LaunchkeyMiniMk3ControlSurface;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.mode.device.ProjectParamsMode;


/**
 * Edit project and track remote control parameters.
 *
 * @author Jürgen Moßgraber
 */
public class LaunckeyMiniMk3ProjectParamsMode extends ProjectParamsMode<LaunchkeyMiniMk3ControlSurface, LaunchkeyMiniMk3Configuration>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public LaunckeyMiniMk3ProjectParamsMode (final LaunchkeyMiniMk3ControlSurface surface, final IModel model)
    {
        super (surface, model, true, ContinuousID.createSequentialList (ContinuousID.DEVICE_KNOB1, 8), () -> false);
    }


    /**
     * Select the previous parameter page.
     */
    public void selectPreviousParameterPage ()
    {
        super.selectPreviousItem ();
    }


    /**
     * Select the next parameter page.
     */
    public void selectNextParameterPage ()
    {
        super.selectNextItem ();
    }


    /**
     * Notify the selected page.
     */
    public void notifyPage ()
    {
        if (this.isProjectMode)
            this.mvHelper.notifySelectedProjectParameterPage ();
        else
            this.mvHelper.notifySelectedTrackParameterPage ();
    }


    /** {@inheritDoc} */
    @Override
    public Optional<String> getSelectedItemName ()
    {
        final String prefix = this.isProjectMode ? "Project " : "Track ";
        final Optional<String> selectedItem = ((IParameterBank) this.getBank ()).getPageBank ().getSelectedItem ();
        if (selectedItem.isPresent ())
            return Optional.of (prefix + "Page: " + selectedItem.get ());
        return Optional.of (prefix + "Page: None");
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        this.toggleMode ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        this.toggleMode ();
    }
}
