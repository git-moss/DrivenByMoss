// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.view;

import de.mossgrabers.controller.maschine.MaschineConfiguration;
import de.mossgrabers.controller.maschine.controller.MaschineControlSurface;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractPlayView;


/**
 * The Play view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PlayView extends AbstractPlayView<MaschineControlSurface, MaschineConfiguration> implements PadButtons
{
    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public PlayView (final MaschineControlSurface surface, final IModel model)
    {
        super (surface, model, true);

        final Configuration configuration = this.surface.getConfiguration ();
        configuration.addSettingObserver (AbstractConfiguration.ACTIVATE_FIXED_ACCENT, this::initMaxVelocity);
        configuration.addSettingObserver (AbstractConfiguration.FIXED_ACCENT_VALUE, this::initMaxVelocity);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final IDisplay display = this.surface.getDisplay ();
        switch (index)
        {
            case 0:
                if (this.surface.isPressed (ButtonID.STOP))
                {
                    this.surface.setTriggerConsumed (ButtonID.STOP);
                    this.scales.prevScaleOffset ();
                    display.notify (Scales.BASES[this.scales.getScaleOffset ()]);
                }
                else
                {
                    this.scales.prevScale ();
                    display.notify (this.scales.getScale ().getName ());
                }
                break;
            case 1:
                if (this.surface.isPressed (ButtonID.STOP))
                {
                    this.surface.setTriggerConsumed (ButtonID.STOP);
                    this.scales.nextScaleOffset ();
                    display.notify (Scales.BASES[this.scales.getScaleOffset ()]);
                }
                else
                {
                    this.scales.nextScale ();
                    display.notify (this.scales.getScale ().getName ());
                }
                break;
            case 2:
                if (this.surface.isPressed (ButtonID.STOP))
                {
                    this.surface.setTriggerConsumed (ButtonID.STOP);
                    this.scales.prevScaleLayout ();
                    display.notify (this.scales.getScaleLayout ().getName ());
                }
                else
                    this.onOctaveDown (event);
                break;
            case 3:
                if (this.surface.isPressed (ButtonID.STOP))
                {
                    this.surface.setTriggerConsumed (ButtonID.STOP);
                    this.scales.nextScaleLayout ();
                    display.notify (this.scales.getScaleLayout ().getName ());
                }
                else
                    this.onOctaveUp (event);
                break;
            default:
                // Not used
                break;
        }

        this.update ();
    }


    private void update ()
    {
        this.updateNoteMapping ();
        final MaschineConfiguration config = this.surface.getConfiguration ();
        config.setScale (this.scales.getScale ().getName ());
        config.setScaleBase (Scales.BASES[this.scales.getScaleOffset ()]);
        config.setScaleLayout (this.scales.getScaleLayout ().getName ());
    }
}