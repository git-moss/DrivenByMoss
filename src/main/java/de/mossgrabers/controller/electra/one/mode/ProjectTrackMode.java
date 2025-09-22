// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.mode;

import de.mossgrabers.controller.electra.one.controller.ElectraOneColorManager;
import de.mossgrabers.controller.electra.one.controller.ElectraOneControlSurface;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameter.PlayPositionParameter;
import de.mossgrabers.framework.parameter.TempoParameter;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.EmptyParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.RangeFilterParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode to control project and track parameters.
 *
 * @author Jürgen Moßgraber
 */
public class ProjectTrackMode extends AbstractElectraOneMode
{
    private final ITransport   transport;
    private final IMasterTrack masterTrack;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public ProjectTrackMode (final ElectraOneControlSurface surface, final IModel model)
    {
        super (6, "Project-Track", surface, model);

        this.transport = this.model.getTransport ();
        this.masterTrack = this.model.getMasterTrack ();

        final BankParameterProvider projectParameterProvider = new BankParameterProvider (this.model.getProject ().getParameterBank ());
        final BankParameterProvider trackParameterProvider = new BankParameterProvider (this.model.getCursorTrack ().getParameterBank ());
        final EmptyParameterProvider emptyParameterProvider = new EmptyParameterProvider (1);
        this.setParameterProvider (new CombinedParameterProvider (
                // Row 1
                emptyParameterProvider, new RangeFilterParameterProvider (projectParameterProvider, 0, 4), new FixedParameterProvider (this.masterTrack.getVolumeParameter ()),
                // Row 2
                emptyParameterProvider, new RangeFilterParameterProvider (projectParameterProvider, 4, 4), new FixedParameterProvider (new PlayPositionParameter (model.getValueChanger (), this.transport, surface)),
                // Row 3
                new EmptyParameterProvider (5), new FixedParameterProvider (new TempoParameter (model.getValueChanger (), this.transport, surface)),
                // Row 4
                new EmptyParameterProvider (6),
                // Row 5
                emptyParameterProvider, new RangeFilterParameterProvider (trackParameterProvider, 0, 4), emptyParameterProvider,
                // Row 6
                emptyParameterProvider, new RangeFilterParameterProvider (trackParameterProvider, 4, 4), emptyParameterProvider));
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int column, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        switch (row)
        {
            case 2:
                if (column < 5)
                    this.model.getProject ().getParameterBank ().getPageBank ().selectPage (column);
                break;

            case 3:
                if (column < 5)
                    this.model.getCursorTrack ().getParameterBank ().getPageBank ().selectPage (column);
                else
                    this.transport.tapTempo ();
                break;

            case 4:
                if (column == 5)
                    this.transport.startRecording ();
                break;

            case 5:
                if (column == 5)
                    this.playCommand.execute (ButtonEvent.UP, 127);
                break;

            default:
                // 0 + 1, not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final IParameterBank projectParameterBank = this.model.getProject ().getParameterBank ();
        final IParameterBank trackParameterBank = this.model.getCursorTrack ().getParameterBank ();
        for (int i = 0; i < 8; i++)
        {
            IParameter param = projectParameterBank.getItem (i);
            boolean paramExists = param.doesExist ();
            int row = i / 4;
            final int column = 1 + i % 4;
            this.pageCache.updateElement (row, column, paramExists ? StringUtils.fixASCII (param.getName ()) : "", paramExists ? ColorEx.PINK : ColorEx.BLACK, Boolean.valueOf (paramExists));
            this.pageCache.updateValue (row, column, param.getValue (), paramExists ? StringUtils.optimizeName (StringUtils.fixASCII (param.getDisplayedValue ()), 15) : " ");

            param = trackParameterBank.getItem (i);
            paramExists = param.doesExist ();
            row += 4;
            this.pageCache.updateElement (row, column, paramExists ? StringUtils.fixASCII (param.getName ()) : "", paramExists ? ColorEx.MINT : ColorEx.BLACK, Boolean.valueOf (paramExists));
            this.pageCache.updateValue (row, column, param.getValue (), paramExists ? StringUtils.optimizeName (StringUtils.fixASCII (param.getDisplayedValue ()), 15) : " ");
        }

        final IParameterPageBank projectPageBank = projectParameterBank.getPageBank ();
        final IParameterPageBank trackPageBank = trackParameterBank.getPageBank ();
        for (int column = 0; column < 5; column++)
        {
            // Set project page names
            String paramPage = projectPageBank.getItem (column);
            boolean isSelected = projectPageBank.getSelectedItemIndex () == column;
            boolean pageExists = !paramPage.isBlank ();
            ColorEx color = isSelected ? ElectraOneColorManager.PROJECT_PARAM_PAGE_SELECTED : ElectraOneColorManager.PROJECT_PARAM_PAGE;
            this.pageCache.updateElement (2, column, pageExists ? StringUtils.fixASCII (paramPage) : " ", pageExists ? color : ColorEx.BLACK, Boolean.valueOf (pageExists));

            // Set track page names
            paramPage = trackPageBank.getItem (column);
            isSelected = trackPageBank.getSelectedItemIndex () == column;
            pageExists = !paramPage.isBlank ();
            color = isSelected ? ElectraOneColorManager.TRACK_PARAM_PAGE_SELECTED : ElectraOneColorManager.TRACK_PARAM_PAGE;
            this.pageCache.updateElement (3, column, pageExists ? StringUtils.fixASCII (paramPage) : " ", pageExists ? color : ColorEx.BLACK, Boolean.valueOf (pageExists));
        }

        // Row 3 / 4
        this.pageCache.updateValue (2, 5, 0, this.transport.formatTempo (this.transport.getTempo ()));

        // Maybe support page bank navigation later...
        this.pageCache.updateColor (0, 0, ColorEx.BLACK);
        this.pageCache.updateColor (1, 0, ColorEx.BLACK);
        this.pageCache.updateColor (4, 0, ColorEx.BLACK);
        this.pageCache.updateColor (5, 0, ColorEx.BLACK);
        this.pageCache.updateElement (0, 0, " ", ColorEx.BLACK, Boolean.FALSE);
        this.pageCache.updateElement (1, 0, " ", ColorEx.BLACK, Boolean.FALSE);
        this.pageCache.updateElement (4, 0, " ", ColorEx.BLACK, Boolean.FALSE);
        this.pageCache.updateElement (5, 0, " ", ColorEx.BLACK, Boolean.FALSE);

        // Master
        this.pageCache.updateColor (0, 5, this.masterTrack.getColor ());
        this.pageCache.updateValue (0, 5, this.masterTrack.getVolume (), StringUtils.optimizeName (StringUtils.fixASCII (this.masterTrack.getVolumeStr ()), 15));
        this.pageCache.updateValue (1, 5, 0, StringUtils.optimizeName (StringUtils.fixASCII (this.transport.getBeatText ()), 15));
        this.pageCache.updateElement (1, 5, StringUtils.optimizeName (StringUtils.fixASCII (this.transport.getPositionText ()), 15), null, null);

        // Transport
        this.pageCache.updateColor (4, 5, this.transport.isRecording () ? ElectraOneColorManager.RECORD_ON : ElectraOneColorManager.RECORD_OFF);
        this.pageCache.updateColor (5, 5, this.transport.isPlaying () ? ElectraOneColorManager.PLAY_ON : ElectraOneColorManager.PLAY_OFF);

        this.pageCache.flush ();
    }
}