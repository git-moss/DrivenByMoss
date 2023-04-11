// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc.module;

import de.mossgrabers.controller.osc.exception.IllegalParameterException;
import de.mossgrabers.controller.osc.exception.MissingCommandException;
import de.mossgrabers.controller.osc.exception.UnknownCommandException;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IArranger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IMixer;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.osc.IOpenSoundControlWriter;

import java.util.LinkedList;
import java.util.Locale;


/**
 * All layout related commands.
 *
 * @author Jürgen Moßgraber
 */
public class LayoutModule extends AbstractModule
{
    /**
     * Constructor.
     *
     * @param host The host
     * @param model The model
     * @param writer The writer
     */
    public LayoutModule (final IHost host, final IModel model, final IOpenSoundControlWriter writer)
    {
        super (host, model, writer);
    }


    /** {@inheritDoc} */
    @Override
    public String [] getSupportedCommands ()
    {
        return new String []
        {
            "layout",
            "panel",
            "arranger",
            TAG_MIXER
        };
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final String command, final LinkedList<String> path, final Object value) throws IllegalParameterException, UnknownCommandException, MissingCommandException
    {
        switch (command)
        {
            case "layout":
                this.model.getApplication ().setPanelLayout (toString (value).toUpperCase (Locale.US));
                break;

            case "panel":
                final String subCommand = getSubCommand (path);
                final IApplication app = this.model.getApplication ();
                switch (subCommand)
                {
                    case "noteEditor":
                        app.toggleNoteEditor ();
                        break;
                    case "automationEditor":
                        app.toggleAutomationEditor ();
                        break;
                    case "devices":
                        app.toggleDevices ();
                        break;
                    case TAG_MIXER:
                        app.toggleMixer ();
                        break;
                    case "fullscreen":
                        app.toggleFullScreen ();
                        break;
                    default:
                        throw new UnknownCommandException (subCommand);
                }
                break;

            case "arranger":
                final String subCommand2 = getSubCommand (path);
                final IArranger arrange = this.model.getArranger ();
                switch (subCommand2)
                {
                    case "cueMarkerVisibility":
                        arrange.toggleCueMarkerVisibility ();
                        break;
                    case "playbackFollow":
                        arrange.togglePlaybackFollow ();
                        break;
                    case "trackRowHeight":
                        arrange.toggleTrackRowHeight ();
                        break;
                    case "clipLauncherSectionVisibility":
                        arrange.toggleClipLauncher ();
                        break;
                    case "timeLineVisibility":
                        arrange.toggleTimeLine ();
                        break;
                    case "ioSectionVisibility":
                        arrange.toggleIoSection ();
                        break;
                    case "effectTracksVisibility":
                        arrange.toggleEffectTracks ();
                        break;
                    default:
                        throw new UnknownCommandException (subCommand2);
                }
                break;

            case TAG_MIXER:
                final String subCommand3 = getSubCommand (path);
                final IMixer mix = this.model.getMixer ();
                switch (subCommand3)
                {
                    case "clipLauncherSectionVisibility":
                        mix.toggleClipLauncherSectionVisibility ();
                        break;
                    case "crossFadeSectionVisibility":
                        mix.toggleCrossFadeSectionVisibility ();
                        break;
                    case "deviceSectionVisibility":
                        mix.toggleDeviceSectionVisibility ();
                        break;
                    case "sendsSectionVisibility":
                        mix.toggleSendsSectionVisibility ();
                        break;
                    case "ioSectionVisibility":
                        mix.toggleIoSectionVisibility ();
                        break;
                    case "meterSectionVisibility":
                        mix.toggleMeterSectionVisibility ();
                        break;
                    default:
                        throw new UnknownCommandException (subCommand3);
                }
                break;

            default:
                throw new UnknownCommandException (command);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void flush (final boolean dump)
    {
        final IApplication app = this.model.getApplication ();
        this.writer.sendOSC ("/layout", app.getPanelLayout ().toLowerCase (Locale.US), dump);

        final IArranger arrange = this.model.getArranger ();
        this.writer.sendOSC ("/arranger/cueMarkerVisibility", arrange.areCueMarkersVisible (), dump);
        this.writer.sendOSC ("/arranger/playbackFollow", arrange.isPlaybackFollowEnabled (), dump);
        this.writer.sendOSC ("/arranger/trackRowHeight", arrange.hasDoubleRowTrackHeight (), dump);
        this.writer.sendOSC ("/arranger/clipLauncherSectionVisibility", arrange.isClipLauncherVisible (), dump);
        this.writer.sendOSC ("/arranger/timeLineVisibility", arrange.isTimelineVisible (), dump);
        this.writer.sendOSC ("/arranger/ioSectionVisibility", arrange.isIoSectionVisible (), dump);
        this.writer.sendOSC ("/arranger/effectTracksVisibility", arrange.areEffectTracksVisible (), dump);

        final IMixer mix = this.model.getMixer ();
        this.writer.sendOSC ("/mixer/clipLauncherSectionVisibility", mix.isClipLauncherSectionVisible (), dump);
        this.writer.sendOSC ("/mixer/crossFadeSectionVisibility", mix.isCrossFadeSectionVisible (), dump);
        this.writer.sendOSC ("/mixer/deviceSectionVisibility", mix.isDeviceSectionVisible (), dump);
        this.writer.sendOSC ("/mixer/sendsSectionVisibility", mix.isSendSectionVisible (), dump);
        this.writer.sendOSC ("/mixer/ioSectionVisibility", mix.isIoSectionVisible (), dump);
        this.writer.sendOSC ("/mixer/meterSectionVisibility", mix.isMeterSectionVisible (), dump);
    }
}
