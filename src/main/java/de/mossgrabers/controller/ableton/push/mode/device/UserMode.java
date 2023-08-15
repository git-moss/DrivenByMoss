// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode.device;

import de.mossgrabers.controller.ableton.push.controller.Push1Display;
import de.mossgrabers.controller.ableton.push.controller.PushColorManager;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.controller.ableton.push.mode.BaseMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Optional;


/**
 * Mode for editing user control parameters.
 *
 * @author Jürgen Moßgraber
 */
public class UserMode extends BaseMode<IParameter>
{
    private static final String []      TOP_MENU      =
    {
        "Project",
        " ",
        " ",
        " ",
        " ",
        " ",
        " ",
        " "
    };

    private final BankParameterProvider projectParameterProvider;
    private final BankParameterProvider trackParameterProvider;

    private boolean                     isProjectMode = true;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public UserMode (final PushControlSurface surface, final IModel model)
    {
        super ("Project/Track Controls", surface, model, model.getProject ().getParameterBank ());

        this.projectParameterProvider = new BankParameterProvider (model.getProject ().getParameterBank ());
        this.trackParameterProvider = new BankParameterProvider (model.getCursorTrack ().getParameterBank ());
        this.setParameterProvider (this.projectParameterProvider);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.setTouchedKnob (index, isTouched);

        final IParameter param = this.bank.getItem (index);
        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);
            param.resetValue ();
        }
        param.touchValue (isTouched);
        this.checkStopAutomationOnKnobRelease (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        final IParameterPageBank parameterPageBank = ((IParameterBank) this.bank).getPageBank ();
        if (!parameterPageBank.getItem (index).isBlank ())
            parameterPageBank.selectPage (index);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final int offColor = this.isPushModern ? PushColorManager.PUSH2_COLOR_BLACK : PushColorManager.PUSH1_COLOR_BLACK;

        int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
        {
            final int selectedColor = this.isPushModern ? PushColorManager.PUSH2_COLOR_ORANGE_HI : PushColorManager.PUSH1_COLOR_ORANGE_HI;
            final int existsColor = this.isPushModern ? PushColorManager.PUSH2_COLOR_YELLOW_LO : PushColorManager.PUSH1_COLOR_YELLOW_LO;

            final IParameterPageBank parameterPageBank = ((IParameterBank) this.bank).getPageBank ();
            if (parameterPageBank.getItem (index).isBlank ())
                return offColor;

            final int selectedPage = parameterPageBank.getSelectedItemIndex ();
            return index == selectedPage ? selectedColor : existsColor;
        }

        index = this.isButtonRow (1, buttonID);
        if (index >= 0)
        {
            if (index > 1)
                return offColor;

            final int selectedColor = this.isPushModern ? PushColorManager.PUSH2_COLOR2_WHITE : PushColorManager.PUSH1_COLOR2_WHITE;
            final int existsColor = this.isPushModern ? PushColorManager.PUSH2_COLOR2_GREY_LO : PushColorManager.PUSH1_COLOR2_GREY_LO;
            return index == 0 && this.isProjectMode || index == 1 && !this.isProjectMode ? selectedColor : existsColor;
        }

        return super.getButtonColor (buttonID);
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event == ButtonEvent.UP && index <= 1)
            this.setMode (index == 0);
    }


    private void setMode (final boolean isProjectMode)
    {
        this.isProjectMode = isProjectMode;
        this.switchBanks (this.isProjectMode ? this.model.getProject ().getParameterBank () : this.model.getCursorTrack ().getParameterBank ());
        this.setParameterProvider (this.isProjectMode ? this.projectParameterProvider : this.trackParameterProvider);
        this.bindControls ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final IParameterPageBank parameterPageBank = ((IParameterBank) this.bank).getPageBank ();

        final Optional<ITrack> selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
        final String trackHeader = selectedTrack.isEmpty () ? "None" : selectedTrack.get ().getName ();

        // Row 1 & 2
        for (int i = 0; i < this.bank.getPageSize (); i++)
        {
            final IParameter param = this.bank.getItem (i);
            display.setCell (0, i, param.doesExist () ? StringUtils.fixASCII (param.getName ()) : "").setCell (1, i, param.getDisplayedValue (8));
        }

        // Row 3
        display.setBlock (2, 0, this.isProjectMode ? "Params : Project" : "Params : Track ->");
        if (!this.isProjectMode)
            display.setBlock (2, 1, trackHeader);

        // Row 4
        final int selectedPage = parameterPageBank.getSelectedItemIndex ();
        for (int i = 0; i < parameterPageBank.getPageSize (); i++)
        {
            final String pageName = parameterPageBank.getItem (i);
            if (!pageName.isBlank ())
                display.setCell (3, i, (i == selectedPage ? Push1Display.SELECT_ARROW : "") + pageName);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final IParameterPageBank parameterPageBank = ((IParameterBank) this.bank).getPageBank ();
        final IValueChanger valueChanger = this.model.getValueChanger ();
        final int selectedPage = parameterPageBank.getSelectedItemIndex ();

        final Optional<ITrack> selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
        final String trackHeader = selectedTrack.isEmpty () ? "None" : selectedTrack.get ().getName ();
        final ColorEx trackColor = selectedTrack.isEmpty () ? ColorEx.BLACK : selectedTrack.get ().getColor ();

        for (int i = 0; i < this.bank.getPageSize (); i++)
        {
            final boolean isBottomMenuOn = i == selectedPage;

            final IParameter param = this.bank.getItem (i);
            final boolean exists = param.doesExist ();
            final String parameterName = exists ? param.getName (9) : "";
            final int parameterValue = valueChanger.toDisplayValue (exists ? param.getValue () : 0);
            final String parameterValueStr = exists ? param.getDisplayedValue (8) : "";
            final boolean parameterIsActive = this.isKnobTouched (i);
            final int parameterModulatedValue = valueChanger.toDisplayValue (exists ? param.getModulatedValue () : -1);

            final String bottomMenu = StringUtils.limit (parameterPageBank.getItem (i), 12);
            final String bottomMenuIcon = this.isProjectMode ? "PROJECT" : "TRACK";
            final boolean isTopMenuSelected = i == 0 && this.isProjectMode || i == 1 && !this.isProjectMode;

            final ColorEx bottomMenuColor;
            if (this.isProjectMode)
                bottomMenuColor = isBottomMenuOn ? ColorEx.WHITE : ColorEx.GRAY;
            else
                bottomMenuColor = trackColor;
            display.addParameterElement (i == 1 ? trackHeader : TOP_MENU[i], isTopMenuSelected, bottomMenu, bottomMenuIcon, bottomMenuColor, isBottomMenuOn, parameterName, parameterValue, parameterValueStr, parameterIsActive, parameterModulatedValue);
        }
    }
}