package minesweeper.gameengine;

import minesweeper.gameengine.cell.Cell;

import java.util.*;

public class MineField {

    private final Cell[][] cellArr;
    private final List<Cell> cellsWithMines;
    private final List<Cell> safeCells;
    private final int minesNumber;
    private boolean minesAreLaid = false;
    private final char unmarkedCellSymbol = '.';
    private final char markedCellSymbol = '*';
    private final char exploredCellWithoutMinesAroundSymbol = '/';
    private final char mineSymbol = 'X';

    public MineField(int size, int minesNumber) {
        this.minesNumber = Math.min(minesNumber, size * size - 1);
        cellArr = new Cell[size][size];
        fillArrWithCells();
        cellsWithMines = new ArrayList<>(minesNumber);
        safeCells = new ArrayList<>(size * size - minesNumber);
    }

    private void fillArrWithCells() {
        int fieldSize = cellArr.length;
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                cellArr[i][j] = new Cell();
            }
        }
    }

    private void layMines(int safeYIndex, int safeXIndex) {
        if (minesAreLaid) {
            return;
        }
        int size = cellArr.length;
        int cellsNumberExceptFirstExplored = size * size - 1;
        List<Cell> allCellsExceptFirstExplored = new ArrayList<>(cellsNumberExceptFirstExplored);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i != safeXIndex || j != safeYIndex) {
                    allCellsExceptFirstExplored.add(cellArr[i][j]);
                }
            }
        }
        Collections.shuffle(allCellsExceptFirstExplored);
        cellsWithMines.addAll(allCellsExceptFirstExplored.subList(0, minesNumber));
        safeCells.addAll(allCellsExceptFirstExplored.subList(minesNumber, cellsNumberExceptFirstExplored));
        for (Cell c : cellsWithMines) {
            c.layMine();
        }
        minesAreLaid = true;
        setNumbersOfMinesAround();
    }

    private void setNumbersOfMinesAround() {
        int size = cellArr.length;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (!cellArr[i][j].isMine()) {
                    int minesAroundThisCell = minesAroundNumber(i, j);
                    cellArr[i][j].setMinesAroundNumber(minesAroundThisCell);
                }
            }
        }
    }

    private int minesAroundNumber(int y, int x) {
        int numberOfMines = 0;
        for (int i = y - 1; i <= y + 1; i++) {
            for (int j = x - 1; j <= x + 1; j++) {
                if (coordinatesAreValid(i, j) && cellArr[i][j].isMine()) {
                    ++numberOfMines;
                }
            }
        }
        return numberOfMines;
    }

    private boolean coordinatesAreValid(int y, int x) {
        int size = cellArr.length;
        return (y >= 0) && (y < size) && (x >= 0) && (x < size);
    }

    public String toString() {
        int size = cellArr.length;
        String result = "\n" + " |123456789|\n"
                             + "-|---------|\n";
        for (int i = 0; i < size; i++) {
            result += (i + 1) + "|";
            for (int j = 0; j < size; j++) {
                Cell cell = cellArr[i][j];
                if (cell.isUnexplored()) {
                    if (cell.isMarked()) {
                        result += markedCellSymbol;
                    } else {
                        result += unmarkedCellSymbol;
                    }
                } else {
                    if (cell.isMine()) {
                        result += mineSymbol;
                    } else {
                        if (cell.thereAreMinesAround()) {
                            result += (char) ('0' + cell.getMinesAroundNumber());
                        } else {
                            result += exploredCellWithoutMinesAroundSymbol;
                        }
                    }
                }
            }
            result += "|\n";
        }
        result += "-|---------|";
        return result;
    }

    public boolean setOrDeleteMark(int y, int x) {
        boolean success;
        --y;
        --x;
        if (!coordinatesAreValid(y, x)) {
            return false;
        }
        if (!cellArr[y][x].isExplored()) {
            success = cellArr[y][x].markOrUnmark();
        } else {
            success = false;
        }
        return success;
    }

    public void exploreCell(int y, int x) {
        --y;
        --x;
        if (!minesAreLaid) {
            layMines(y, x);
        }
        if (coordinatesAreValid(y, x) && cellArr[y][x].isUnexplored()) {
            if (cellArr[y][x].isSafe()) {
                autoExplore(y, x);
            } else {
                cellArr[y][x].explore();
            }
        }
    }

    private void autoExplore(int y, int x) {
        if (coordinatesAreValid(y, x) && cellArr[y][x].isSafe() && cellArr[y][x].isUnexplored()) {
            cellArr[y][x].explore();
            if (cellArr[y][x].getMinesAroundNumber() == 0) {
                autoExplore(y - 1, x - 1);
                autoExplore(y - 1, x);
                autoExplore(y - 1, x + 1);
                autoExplore(y, x - 1);
                autoExplore(y, x + 1);
                autoExplore(y + 1, x - 1);
                autoExplore(y + 1, x);
                autoExplore(y + 1, x + 1);
            }
        }
    }

    public boolean gameIsOver() {
        return minesAreLaid &&
                (allMineCellsAreMarked() && allSafeCellsAreUnmarked()
                        || allSafeCellsExplored()
                        || anyMineCellsExplored());
    }

    public boolean playerWon() {
        return (allMineCellsAreMarked() && allSafeCellsAreUnmarked() || allSafeCellsExplored())
                && allMineCellsUnexplored();
    }

    private boolean allSafeCellsAreUnmarked() {
        return safeCells.stream().noneMatch(Cell::isMarked);
    }

    private boolean allSafeCellsExplored() {
        return safeCells.stream().allMatch(Cell::isExplored);
    }

    private boolean allMineCellsAreMarked() {
        return cellsWithMines.stream().allMatch(Cell::isMarked);
    }

    private boolean anyMineCellsExplored() {
        return cellsWithMines.stream().anyMatch(Cell::isExplored);
    }

    private boolean allMineCellsUnexplored() {
        return !anyMineCellsExplored();
    }
}
