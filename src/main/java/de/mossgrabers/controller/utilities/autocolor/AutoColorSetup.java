// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.utilities.autocolor;

import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.scale.Scales;


/**
 * Auto coloring of tracks.
 *
 * @author Jürgen Moßgraber
 */
public class AutoColorSetup extends AbstractControllerSetup<IControlSurface<AutoColorConfiguration>, AutoColorConfiguration>
{
    private static final int MAX_TRACKS = 100;

    private final AutoColor  autoColor;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    public AutoColorSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);

        this.colorManager = new ColorManager ();
        this.valueChanger = new TwosComplementValueChanger (128, 1);
        this.configuration = new AutoColorConfiguration (host, this.valueChanger);
        this.autoColor = new AutoColor (this.configuration);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        this.scales = new Scales (this.valueChanger, 0, 128, 128, 1);
        this.scales.setChromatic (true);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();

        ms.enableMainDrumDevice (false);

        ms.setNumDeviceLayers (0);
        ms.setNumDevicesInBank (0);
        ms.setNumDrumPadLayers (0);
        ms.setNumFilterColumnEntries (0);
        ms.setNumMarkers (0);
        ms.setNumParamPages (0);
        ms.setNumParams (0);
        ms.setNumResults (0);
        ms.setNumScenes (0);
        ms.setNumSends (0);

        ms.setHasFlatTrackList (true);
        ms.setHasFullFlatTrackList (true);

        ms.setNumTracks (MAX_TRACKS);

        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, ms);

        this.autoColor.setTrackBank (this.model.getTrackBank ());
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        // Update track colors if Auto Color is enabled in the settings
        this.configuration.addSettingObserver (AutoColorConfiguration.ENABLE_AUTO_COLOR, () -> {
            if (!this.configuration.isEnableAutoColor ())
                return;
            final ITrackBank tb = this.model.getTrackBank ();
            for (int i = 0; i < tb.getPageSize (); i++)
                this.autoColor.matchTrackName (i, tb.getItem (i).getName ());
        });

        // Monitor all color regular expression settings
        final DAWColor [] colors = DAWColor.values ();
        for (int i = 0; i < colors.length; i++)
        {
            final DAWColor color = colors[i];
            this.configuration.addSettingObserver (Integer.valueOf (AutoColorConfiguration.COLOR_REGEX.intValue () + i), () -> this.autoColor.handleRegExChange (color, this.configuration.getColorRegExValue (color)));
        }

        // Add name observers to all tracks
        final ITrackBank tb = this.model.getTrackBank ();

        for (int index = 0; index < tb.getPageSize (); index++)
        {
            final int i = index;
            final ITrack track = tb.getItem (index);
            track.addNameObserver (name -> this.autoColor.matchTrackName (i, name));
            track.addColorObserver (name -> this.autoColor.matchTrackName (i, track.getName ()));
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        // Intentionally empty
    }
}
