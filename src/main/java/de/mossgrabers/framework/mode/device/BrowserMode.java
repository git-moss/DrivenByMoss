// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode.device;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;

import java.util.Optional;


/**
 * The device browser mode. The knobs control the selection of the filter rows.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BrowserMode<S extends IControlSurface<C>, C extends Configuration> extends AbstractParameterMode<S, C, IItem>
{
    /**
     * Constructor. Changes are always relative.
     *
     * @param surface The control surface
     * @param model The model
     */
    public BrowserMode (final S surface, final IModel model)
    {
        super ("Browser", surface, model, false);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final IBrowser browser = this.model.getBrowser ();
        if (browser == null)
            return;
        if (index == 7)
        {
            if (value > 0)
                browser.selectNextResult ();
            else
                browser.selectPreviousResult ();
            return;
        }
        if (value > 0)
            browser.selectNextFilterItem (index);
        else
            browser.selectPreviousFilterItem (index);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);
            final IBrowser browser = this.model.getBrowser ();
            if (browser != null)
                browser.resetFilterColumn (index);
        }
    }


    /** {@inheritDoc} */
    @Override
    public Optional<String> getSelectedItemName ()
    {
        final IBrowser browser = this.model.getBrowser ();
        if (browser == null)
            return Optional.empty ();
        return Optional.ofNullable (browser.getSelectedResult ());
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        final IBrowser browser = this.model.getBrowser ();
        if (browser != null)
            browser.previousContentType ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        final IBrowser browser = this.model.getBrowser ();
        if (browser != null)
            browser.nextContentType ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        final IBrowser browser = this.model.getBrowser ();
        if (browser != null && browser.isActive ())
            browser.stopBrowsing (false);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        final IBrowser browser = this.model.getBrowser ();
        if (browser != null && browser.isActive ())
            browser.stopBrowsing (true);
    }


    /** {@inheritDoc} */
    @Override
    public void selectItem (final int index)
    {
        final IBrowser browser = this.model.getBrowser ();
        if (browser == null)
            return;
        if (index == 7)
            browser.selectNextResult ();
        else
            browser.selectNextFilterItem (index);
    }
}