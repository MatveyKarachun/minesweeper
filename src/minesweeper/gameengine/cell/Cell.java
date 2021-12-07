package minesweeper.gameengine.cell;

public class Cell {

    private boolean mine = false;
    private CellState state = CellState.UNEXPLORED_AND_UNMARKED;

    private int minesAroundNumber = -1;

    public void layMine() {
        mine = true;
    }

    public boolean isMine() {
        return mine;
    }

    public boolean isSafe() {
        return !mine;
    }

    public boolean setMinesAroundNumber(int minesAroundNumber) {
        boolean success;
        if (!mine && minesAroundNumber >= 0 && minesAroundNumber <= 8) {
            this.minesAroundNumber = minesAroundNumber;
            success = true;
        } else {
            success = false;
        }
        return success;
    }

    public int getMinesAroundNumber() {
        return minesAroundNumber;
    }

    public boolean markOrUnmark() {
        boolean success;
        switch (state) {
            case UNEXPLORED_AND_UNMARKED:
                state = CellState.UNEXPLORED_AND_MARKED;
                success = true;
                break;
            case UNEXPLORED_AND_MARKED:
                state = CellState.UNEXPLORED_AND_UNMARKED;
                success = true;
                break;
            default:
                success = false;
                break;
        }
        return success;
    }

    public boolean isMarked() {
        return state == CellState.UNEXPLORED_AND_MARKED;
    }

    public void explore() {
        state = CellState.EXPLORED;
    }

    public boolean isExplored() {
        return state == CellState.EXPLORED;
    }

    public boolean isUnexplored() {
        return state != CellState.EXPLORED;
    }

    public boolean thereAreMinesAround() {
        return minesAroundNumber > 0;
    }
}