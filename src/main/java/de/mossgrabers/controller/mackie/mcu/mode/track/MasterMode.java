// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.mode.track;

import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.controller.mackie.mcu.mode.BaseMode;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for editing the parameters of the master track.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MasterMode extends BaseMode<ITrack>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public MasterMode (final MCUControlSurface surface, final IModel model)
    {
        super ("Master", surface, model);

        final IMasterTrack masterTrack = this.model.getMasterTrack ();
        this.setParameterProvider (new FixedParameterProvider (masterTrack.getVolumeParameter (), masterTrack.getPanParameter (), EmptyParameter.INSTANCE, EmptyParameter.INSTANCE, EmptyParameter.INSTANCE, EmptyParameter.INSTANCE, EmptyParameter.INSTANCE, EmptyParameter.INSTANCE));
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();

        this.setActive (true);
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        super.onDeactivate ();

        this.setActive (false);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;

        if (index == 0)
            this.model.getMasterTrack ().touchVolume (isTouched);
        else if (index == 1)
            this.model.getMasterTrack ().touchPan (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN && row > 0)
        {
            this.surface.getModeManager ().restore ();
            return;
        }

        if (event != ButtonEvent.UP || row > 0)
            return;

        switch (index)
        {
            case 0:
                this.model.getMasterTrack ().resetVolume ();
                break;

            case 1:
                this.model.getMasterTrack ().resetPan ();
                break;

            case 2:
            case 3:
            case 4:
                this.model.getApplication ().toggleEngineActive ();
                break;

            case 6:
                this.model.getProject ().previous ();
                break;

            case 7:
                this.model.getProject ().next ();
                break;

            default:
                // Unused
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        if (!this.surface.getConfiguration ().hasDisplay1 ())
            return;

        this.drawDisplay2 ();

        final ITextDisplay d = this.surface.getTextDisplay ().clear ();
        final String projectName = StringUtils.fixASCII (this.model.getProject ().getName ());
        final IMasterTrack master = this.model.getMasterTrack ();

        final IApplication application = this.model.getApplication ();
        d.setCell (0, 0, "Volume").setCell (0, 1, "Pan").setBlock (0, 1, "Audio Engine:").setCell (0, 4, application.isEngineActive () ? " On" : " Off");
        d.setCell (0, 5, "Prjct:").setBlock (0, 3, projectName);
        d.setCell (1, 0, master.getVolumeStr (6)).setCell (1, 1, master.getPanStr (6)).setBlock (1, 1, application.isEngineActive () ? "  Turn off" : "  Turn on");
        d.setCell (1, 6, " <<").setCell (1, 7, " >>").allDone ();
    }


    private void setActive (final boolean enable)
    {
        final IMasterTrack mt = this.model.getMasterTrack ();
        mt.setVolumeIndication (enable);
        mt.setPanIndication (enable);
    }


    /** {@inheritDoc} */
    @Override
    public void updateKnobLEDs ()
    {
        final IMasterTrack masterTrack = this.model.getMasterTrack ();
        final int upperBound = this.model.getValueChanger ().getUpperBound ();
        this.surface.setKnobLED (0, MCUControlSurface.KNOB_LED_MODE_WRAP, masterTrack.getVolume (), upperBound);
        this.surface.setKnobLED (1, MCUControlSurface.KNOB_LED_MODE_BOOST_CUT, masterTrack.getPan (), upperBound);
        for (int i = 0; i < 6; i++)
            this.surface.setKnobLED (2 + i, MCUControlSurface.KNOB_LED_MODE_WRAP, 0, upperBound);
    }


    /** {@inheritDoc} */
    @Override
    protected void resetParameter (final int index)
    {
        final IMasterTrack masterTrack = this.model.getMasterTrack ();
        switch (index)
        {
            case 0:
                masterTrack.resetVolume ();
                break;
            case 1:
                masterTrack.resetPan ();
                break;
            default:
                // Intentionally empty
                break;
        }
    }
}