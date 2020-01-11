// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode.device;

import de.mossgrabers.controller.push.controller.Push1Display;
import de.mossgrabers.controller.push.controller.PushColorManager;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.BaseMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for editing user control parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class UserParamsMode extends BaseMode
{
    private final IParameterBank userParameterBank;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public UserParamsMode (final PushControlSurface surface, final IModel model)
    {
        super ("User Controls", surface, model);

        this.isTemporary = false;
        this.userParameterBank = this.model.getUserParameterBank ();
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        this.userParameterBank.getItem (index).changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;

        final IParameter param = this.userParameterBank.getItem (index);
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
        final int position = index * this.userParameterBank.getPageSize ();
        this.userParameterBank.scrollTo (position);
        this.bindCurrentPage ();
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
        {
            final int selectedColor = this.isPush2 ? PushColorManager.PUSH2_COLOR_ORANGE_HI : PushColorManager.PUSH1_COLOR_ORANGE_HI;
            final int existsColor = this.isPush2 ? PushColorManager.PUSH2_COLOR_YELLOW_LO : PushColorManager.PUSH1_COLOR_YELLOW_LO;

            final int selectedPage = this.userParameterBank.getScrollPosition () / this.userParameterBank.getPageSize ();
            return index == selectedPage ? selectedColor : existsColor;
        }

        index = this.isButtonRow (1, buttonID);
        if (index >= 0)
        {
            final IParameter param = this.userParameterBank.getItem (index);
            if (!param.doesExist ())
                return super.getButtonColor (buttonID);

            final int max = this.model.getValueChanger ().getUpperBound () - 1;
            return this.colorManager.getColorIndex (param.getValue () > max / 2 ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON);
        }

        return super.getButtonColor (buttonID);
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        final IParameter param = this.userParameterBank.getItem (index);
        if (!param.doesExist ())
            return;

        // Toggle between the min and max value
        final int max = this.model.getValueChanger ().getUpperBound () - 1;
        param.setValueImmediatly (param.getValue () < max / 2 ? max : 0);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final String [] userPageNames = this.surface.getConfiguration ().getUserPageNames ();

        // Row 1 & 2
        for (int i = 0; i < 8; i++)
        {
            final IParameter param = this.userParameterBank.getItem (i);
            display.setCell (0, i, param.doesExist () ? StringUtils.fixASCII (param.getName ()) : "").setCell (1, i, param.getDisplayedValue (8));
        }

        // Row 3
        display.setBlock (2, 0, "User Parameters");

        // Row 4
        final int pageSize = this.userParameterBank.getPageSize ();
        final int selectedPage = this.userParameterBank.getScrollPosition () / pageSize;
        for (int i = 0; i < pageSize; i++)
            display.setCell (3, i, (i == selectedPage ? Push1Display.SELECT_ARROW : "") + userPageNames[i]);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final IValueChanger valueChanger = this.model.getValueChanger ();
        final String [] userPageNames = this.surface.getConfiguration ().getUserPageNames ();
        final int pageSize = this.userParameterBank.getPageSize ();
        final int selectedPage = this.userParameterBank.getScrollPosition () / pageSize;
        for (int i = 0; i < pageSize; i++)
        {
            final boolean isBottomMenuOn = i == selectedPage;

            final IParameter param = this.userParameterBank.getItem (i);
            final boolean exists = param.doesExist ();
            final String parameterName = exists ? param.getName (9) : "";
            final int parameterValue = valueChanger.toDisplayValue (exists ? param.getValue () : 0);
            final String parameterValueStr = exists ? param.getDisplayedValue (8) : "";
            final boolean parameterIsActive = this.isKnobTouched[i];
            final int parameterModulatedValue = valueChanger.toDisplayValue (exists ? param.getModulatedValue () : -1);

            display.addParameterElement ("", false, userPageNames[i], "USER", isBottomMenuOn ? ColorEx.WHITE : ColorEx.GRAY, isBottomMenuOn, parameterName, parameterValue, parameterValueStr, parameterIsActive, parameterModulatedValue);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected IBank<? extends IItem> getBank ()
    {
        return this.userParameterBank;
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.bindCurrentPage ();
    }


    /**
     * Update the binding to the current page.
     */
    private void bindCurrentPage ()
    {
        for (int i = 0; i < 8; i++)
        {
            final ContinuousID knobID = ContinuousID.get (ContinuousID.KNOB1, i);
            this.surface.getContinuous (knobID).bind (this.userParameterBank.getItem (i));
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        for (int i = 0; i < 8; i++)
        {
            final ContinuousID knobID = ContinuousID.get (ContinuousID.KNOB1, i);
            this.surface.getContinuous (knobID).bind ((IParameter) null);
        }
    }
}