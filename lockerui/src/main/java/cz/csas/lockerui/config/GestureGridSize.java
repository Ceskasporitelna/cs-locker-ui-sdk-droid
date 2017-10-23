package cz.csas.lockerui.config;

import cz.csas.lockerui.error.CsLockerUIError;

/**
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 23/10/2017.
 */
public class GestureGridSize {

    private final int MAX_SIZE = 5;
    private final int MIN_SIZE = 3;
    private int rows;
    private int columns;

    public GestureGridSize(int rows, int columns) {
        if (checkGridSize(rows) || checkGridSize(columns))
            throw new CsLockerUIError(CsLockerUIError.Kind.BAD_GRID_SIZE);
        this.rows = rows;
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    private boolean checkGridSize(Integer gridSize) {
        return gridSize == null || gridSize < MIN_SIZE || gridSize > MAX_SIZE;
    }
}
