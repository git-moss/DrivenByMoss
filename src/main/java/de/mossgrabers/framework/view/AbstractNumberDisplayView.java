// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.AbstractView;


/**
 * Abstract implementation of a view which can display a 3 digit number.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractNumberDisplayView<S extends IControlSurface<C>, C extends Configuration> extends AbstractView<S, C>
{
    // @formatter:off

    private static final boolean [][] TWO_COLS_0 =
    {
        { false, false },
        { false, false },
        { false, false },
        { false, false },
        { false, false }
    };

    private static final boolean [][] TWO_COLS_1 =
    {
        { true, false },
        { true, false },
        { true, false },
        { true, false },
        { true, false }
    };

    private static final boolean [][] TWO_COLS_2 =
    {
        { true,  true  },
        { false, true  },
        { true,  true  },
        { true,  false },
        { true,  true  }
    };

    private static final boolean [][] TWO_COLS_3 =
    {
        { true,  true },
        { false, true },
        { true,  true },
        { false, true },
        { true,  true }
    };

    private static final boolean [][] TWO_COLS_4 =
    {
        { true,  false },
        { true,  false },
        { true,  true  },
        { false, true  },
        { false, true  }
    };

    private static final boolean [][] TWO_COLS_5 =
    {
        { true,  true  },
        { true,  false },
        { true,  true  },
        { false, true  },
        { true,  true  }
    };

    private static final boolean [][] TWO_COLS_6 =
    {
        { true, true  },
        { true, false },
        { true, true  },
        { true, true  },
        { true, true  }
    };

    private static final boolean [][] TWO_COLS_7 =
    {
        { true,  true },
        { false, true },
        { false, true },
        { false, true },
        { false, true }
    };

    private static final boolean [][] TWO_COLS_8 =
    {
        { true, true },
        { true, true },
        { true, true },
        { true, true },
        { true, true }
    };

    private static final boolean [][] TWO_COLS_9 =
    {
        { true,  true },
        { true,  true },
        { true,  true },
        { false, true },
        { true,  true }
    };

    private static final boolean [][] THREE_COLS_0 =
    {
        { true, true,  true },
        { true, false, true },
        { true, false, true },
        { true, false, true },
        { true, true,  true }
    };

    private static final boolean [][] THREE_COLS_1 =
    {
        { false, true, false },
        { false, true, false },
        { false, true, false },
        { false, true, false },
        { false, true, false }
    };

    private static final boolean [][] THREE_COLS_2 =
    {
        { true,  true,  true  },
        { false, false, true  },
        { true,  true,  true  },
        { true,  false, false },
        { true,  true,  true  }
    };

    private static final boolean [][] THREE_COLS_3 =
    {
        { true,  true,  true },
        { false, false, true },
        { true,  true,  true },
        { false, false, true },
        { true,  true,  true }
    };

    private static final boolean [][] THREE_COLS_4 =
    {
        { true,  false, false },
        { true,  false, false },
        { true,  true,  true  },
        { false, true,  false },
        { false, true,  false }
    };

    private static final boolean [][] THREE_COLS_5 =
    {
        { true,  true,  true },
        { true,  false, false },
        { true,  true,  true  },
        { false, false, true },
        { true,  true,  true }
    };

    private static final boolean [][] THREE_COLS_6 =
    {
        { true,  true,  true },
        { true,  false, false },
        { true,  true,  true  },
        { true,  false, true },
        { true,  true,  true }
    };

    private static final boolean [][] THREE_COLS_7 =
    {
        { true,  true,  true },
        { false, false, true },
        { false, false, true },
        { false, false, true },
        { false, false, true }
    };

    private static final boolean [][] THREE_COLS_8 =
    {
        { true, true,  true },
        { true, false, true },
        { true, true,  true },
        { true, false, true },
        { true, true,  true }
    };

    private static final boolean [][] THREE_COLS_9 =
    {
        { true,  true,  true },
        { true,  false, true },
        { true,  true,  true },
        { false, false, true },
        { true,  true,  true }
    };

    private static final boolean [][][] TWO_COLS =
    {
        TWO_COLS_0, TWO_COLS_1, TWO_COLS_2, TWO_COLS_3, TWO_COLS_4,
        TWO_COLS_5, TWO_COLS_6, TWO_COLS_7, TWO_COLS_8, TWO_COLS_9
    };

    private static final boolean [][][] THREE_COLS =
    {
        THREE_COLS_0, THREE_COLS_1, THREE_COLS_2, THREE_COLS_3, THREE_COLS_4,
        THREE_COLS_5, THREE_COLS_6, THREE_COLS_7, THREE_COLS_8, THREE_COLS_9
    };

    // @formatter:on

    protected final IPadGrid              padGrid;
    private final int                     textColor1;
    private final int                     textColor2;
    private final int                     backgroundColor;


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param textColor1 The color of the 1st and 3rd digit
     * @param textColor2 The color of 2nd digit
     * @param backgroundColor The background color
     */
    protected AbstractNumberDisplayView (final String name, final S surface, final IModel model, final int textColor1, final int textColor2, final int backgroundColor)
    {
        super (name, surface, model);

        this.textColor1 = textColor1;
        this.textColor2 = textColor2;
        this.backgroundColor = backgroundColor;

        this.padGrid = this.surface.getPadGrid ();
    }


    /** {@inheritDoc}} */
    @Override
    public void drawGrid ()
    {
        // Draw the 3 digit number in the upper part of the grid

        final int value = Math.min (999, Math.max (0, this.getNumber ()));

        // 1st digit (two columns) in color 1
        final int digit1 = value / 100;
        final boolean [] [] first = TWO_COLS[digit1];
        for (int x = 0; x < 2; x++)
        {
            for (int y = 0; y < 5; y++)
                this.padGrid.lightEx (x, y, first[y][x] ? this.textColor1 : this.backgroundColor);
        }

        // 2nd digit (three columns) in color 2
        final int digit2 = value % 100 / 10;
        final boolean [] [] second = THREE_COLS[digit2];
        final boolean show2ndDigit = digit1 > 0 || digit2 > 0;
        for (int x = 0; x < 3; x++)
        {
            for (int y = 0; y < 5; y++)
                this.padGrid.lightEx (2 + x, y, show2ndDigit && second[y][x] ? this.textColor2 : this.backgroundColor);
        }

        // 3rd digit (three columns) in color 1
        final boolean [] [] third = THREE_COLS[value % 10];
        for (int x = 0; x < 3; x++)
        {
            for (int y = 0; y < 5; y++)
                this.padGrid.lightEx (5 + x, y, third[y][x] ? this.textColor1 : this.backgroundColor);
        }

        this.fillBottom ();
    }


    /**
     * Fill the rest of the grid with the background color. Overwrite for additional features.
     */
    protected void fillBottom ()
    {
        for (int x = 0; x < this.padGrid.getCols (); x++)
        {
            for (int y = 5; y < this.padGrid.getRows (); y++)
                this.padGrid.lightEx (x, y, this.backgroundColor);
        }
    }


    /** {@inheritDoc}} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            this.surface.getViewManager ().restore ();
    }


    /**
     * Get the number to display.
     *
     * @return The number in the range of 0 to 999
     */
    protected abstract int getNumber ();
}
