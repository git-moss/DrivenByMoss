// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.faderfox.ec4.mode;

import de.mossgrabers.controller.faderfox.ec4.EC4Configuration;
import de.mossgrabers.controller.faderfox.ec4.controller.EC4ControlSurface;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.bank.IBank;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameter.TempoParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Abstract base mode for all EC4 modes.
 *
 * @param <B> The specific item contained in the bank
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractEC4Mode<B extends IItem> extends AbstractParameterMode<EC4ControlSurface, EC4Configuration, B>
{
    protected final IParameterProvider bottomRowProvider;

    private final PlayCommand          playCommand;


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     * @param bank A bank from which to take the parameters
     */
    protected AbstractEC4Mode (final String name, final EC4ControlSurface surface, final IModel model, final IBank<B> bank)
    {
        super (name, surface, model, false, bank, EC4ControlSurface.KNOB_IDS);

        this.playCommand = new PlayCommand<> (model, surface);

        final ITransport transport = model.getTransport ();
        this.bottomRowProvider = new CombinedParameterProvider (
                // Column 1
                new FixedParameterProvider (new TempoParameter (model.getValueChanger (), transport, surface)),
                // Column 2
                new FixedParameterProvider (transport.getCrossfadeParameter ()),
                // Column 3
                new FixedParameterProvider (model.getProject ().getCueVolumeParameter ()),
                // Column 4
                new FixedParameterProvider (model.getMasterTrack ().getVolumeParameter ()));
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (row == 3)
        {
            final ModeManager modeManager = this.surface.getModeManager ();

            switch (index)
            {
                case 0:
                    if (event != ButtonEvent.DOWN)
                        return;
                    if (modeManager.isActive (Modes.SESSION))
                        modeManager.restore ();
                    else
                        modeManager.setActive (Modes.SESSION);
                    break;

                case 1:
                    if (event != ButtonEvent.DOWN)
                        return;
                    modeManager.setActive (modeManager.isActive (Modes.TRACK) ? Modes.DEVICE_PARAMS : Modes.TRACK);
                    break;

                case 2:
                    if (event != ButtonEvent.DOWN)
                        return;
                    if (modeManager.isActive (Modes.TRACK_DETAILS))
                        modeManager.restore ();
                    else
                        modeManager.setActive (Modes.TRACK_DETAILS);
                    break;

                case 3:
                    this.playCommand.execute (event, event == ButtonEvent.DOWN ? 127 : 0);
                    break;

                default:
                    // There are no more
                    break;
            }
        }
    }
}
