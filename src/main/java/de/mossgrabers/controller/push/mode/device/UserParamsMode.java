// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode.device;

import de.mossgrabers.controller.push.controller.Push1Display;
import de.mossgrabers.controller.push.controller.PushColors;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.BaseMode;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
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
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        this.model.getUserParameterBank ().getItem (index).changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;

        final IParameter param = this.model.getUserParameterBank ().getItem (index);
        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (this.surface.getDeleteTriggerId ());
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
        final IParameterBank userParameterBank = this.model.getUserParameterBank ();
        userParameterBank.scrollTo (index * userParameterBank.getPageSize ());
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final int selectedColor = this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_HI : PushColors.PUSH1_COLOR_ORANGE_HI;
        final int existsColor = this.isPush2 ? PushColors.PUSH2_COLOR_YELLOW_LO : PushColors.PUSH1_COLOR_YELLOW_LO;

        final IParameterBank bank = this.model.getUserParameterBank ();
        final int selectedPage = bank.getScrollPosition () / bank.getPageSize ();
        for (int i = 0; i < bank.getPageSize (); i++)
            this.surface.updateTrigger (20 + i, i == selectedPage ? selectedColor : existsColor);
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        final IParameter param = this.model.getUserParameterBank ().getItem (index);
        if (!param.doesExist ())
            return;

        // Toggle between the min and max value
        final int max = this.model.getValueChanger ().getUpperBound () - 1;
        param.setValueImmediatly (param.getValue () < max / 2 ? max : 0);
    }


    /** {@inheritDoc} */
    @Override
    public void updateSecondRow ()
    {
        final ColorManager colorManager = this.model.getColorManager ();
        final int colorOff = colorManager.getColor (AbstractMode.BUTTON_COLOR_OFF);
        final int colorOn = colorManager.getColor (AbstractMode.BUTTON_COLOR_ON);
        final int colorHi = colorManager.getColor (AbstractMode.BUTTON_COLOR_HI);

        final IParameterBank bank = this.model.getUserParameterBank ();
        final int max = this.model.getValueChanger ().getUpperBound () - 1;
        for (int i = 0; i < bank.getPageSize (); i++)
        {
            final IParameter param = this.model.getUserParameterBank ().getItem (i);
            final boolean isHi = param.getValue () > max / 2;
            this.surface.updateTrigger (102 + i, bank.getItem (i).doesExist () ? isHi ? colorHi : colorOn : colorOff);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        // Row 1 & 2
        final IParameterBank bank = this.model.getUserParameterBank ();
        for (int i = 0; i < 8; i++)
        {
            final IParameter param = bank.getItem (i);
            display.setCell (0, i, param.doesExist () ? StringUtils.fixASCII (param.getName ()) : "").setCell (1, i, param.getDisplayedValue (8));
        }

        // Row 3
        display.setBlock (2, 0, "User Parameters");

        // Row 4
        final int selectedPage = bank.getScrollPosition () / bank.getPageSize ();
        for (int i = 0; i < bank.getPageSize (); i++)
            display.setCell (3, i, (i == selectedPage ? Push1Display.SELECT_ARROW : "") + "Page " + (i + 1));
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final double [] bottomMenuColor =
        {
            1,
            1,
            1
        };

        final IValueChanger valueChanger = this.model.getValueChanger ();
        final IParameterBank bank = this.model.getUserParameterBank ();
        final int selectedPage = bank.getScrollPosition () / bank.getPageSize ();
        for (int i = 0; i < bank.getPageSize (); i++)
        {
            final boolean isBottomMenuOn = i == selectedPage;

            final IParameter param = bank.getItem (i);
            final boolean exists = param.doesExist ();
            final String parameterName = exists ? param.getName (9) : "";
            final int parameterValue = valueChanger.toDisplayValue (exists ? param.getValue () : 0);
            final String parameterValueStr = exists ? param.getDisplayedValue (8) : "";
            final boolean parameterIsActive = this.isKnobTouched[i];
            final int parameterModulatedValue = valueChanger.toDisplayValue (exists ? param.getModulatedValue () : -1);

            display.addParameterElement ("", false, "Page " + (i + 1), "USER", bottomMenuColor, isBottomMenuOn, parameterName, parameterValue, parameterValueStr, parameterIsActive, parameterModulatedValue);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected IBank<? extends IItem> getBank ()
    {
        return this.model.getUserParameterBank ();
    }
}