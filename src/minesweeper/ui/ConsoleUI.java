package minesweeper.ui;

import minesweeper.gameengine.MineField;

import java.util.Scanner;

public class ConsoleUI {

    private final int fieldSize = 9;
    private final String commandMine = "mine";
    private final String commandFree = "free";

    public void go() {
        System.out.print("How many mines do you want on the field? > ");
        Scanner scanner = new Scanner(System.in);
        int minesNumber = scanner.nextInt();
        MineField mineField = new MineField(fieldSize, minesNumber);
        System.out.println(mineField);

        while (!mineField.gameIsOver()) {
            System.out.print("Set/unset mines marks or claim a cell as free: > ");
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            String command = scanner.next();
            if (commandMine.equals(command)) {
                mineField.setOrDeleteMark(y, x);
            } else if (commandFree.equals(command)) {
                mineField.exploreCell(y, x);
            }
            System.out.println(mineField);
        }
        if (mineField.playerWon()) {
            System.out.println("Congratulations! You found all the mines!");
        } else {
            System.out.println("You stepped on a mine and failed!");
        }
    }
}
